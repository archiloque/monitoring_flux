require_relative 'app_base'

require 'thread'
require 'ffi-rzmq'
require 'securerandom'
require 'socket'

class Sinatra::Request

  # Hash to store custom monitoring info
  def monitoring_info
    @monitoring_info ||= {}
  end

end

# Add monitoring-related features to AppBase
# Use a Queue with a separate thread that send the message to a ZeroMQ queue.
class MonitoringBase < AppBase

  MONITORING_LOG = ::Logger.new(STDOUT)
  MONITORING_LOG.progname = MonitoringBase.name

  # Environment parameter name for ZeroMQ port
  MONITORING_ZMQ_PORT = 'MONITORING_ZMQ_PORT'

  private

  # Check if a ZeroMQ operation is successful
  def self.error_check(result_code)
    unless ZMQ::Util.resultcode_ok?(result_code)
      MONITORING_LOG.error { "Operation failed, errno [#{ZMQ::Util.errno}] description [#{ZMQ::Util.error_string}]" }
    end
  end

  public

  configure do
    set :app_name, name
    set :host_name, Socket.gethostname
    set :process_pid, Process.pid

    # Initialize 0mq
    MONITORING_LOG.info { 'Initialize ZeroMq' }
    zmq_ctx = ZMQ::Context.create(1)
    unless zmq_ctx
      fail 'Failed to create a Context zero mq'
    end
    zmq_socket = zmq_ctx.socket(ZMQ::PUSH)
    self.error_check(zmq_socket.setsockopt(ZMQ::LINGER, 0))
    zmq_bind_config = "tcp://127.0.0.1:#{ENV[MONITORING_ZMQ_PORT]}"
    MONITORING_LOG.info { "Binding ZeroMq at [#{zmq_bind_config}]" }
    result_code = zmq_socket.bind(zmq_bind_config)
    error_check(result_code)
    set :zmq_socket, zmq_socket
    MONITORING_LOG.info('ZeroMq initialized')

    # Will contain the messages to be sent
    set :monitoring_queue, Queue.new

    # Create the thread that send the events to zero mq from the queue
    Thread.new do
      while true
        begin
          message = settings.monitoring_queue.pop
          message_json = JSON.generate(message)
          MONITORING_LOG.debug { "Sending message [#{message_json}]" }
          zmq_socket.send_string(message_json)
          error_check(result_code)
        rescue Exception => e
          MONITORING_LOG.error e
        end
      end
    end
  end

  # Send a message before a request is processed
  before do
    timestamp = current_timestamp
    timestamp_string = timestamp_as_string(timestamp)
    request.monitoring_info[:before_timestamp] = timestamp
    request.env[:correlation_id] = "#{settings.host_name}_#{settings.app_name}_#{settings.process_pid}_#{Time.now.getutc}_#{SecureRandom.uuid}"
    request.env[:frontend_begin_timestamp] = timestamp_string
    send_monitoring_message 'Begin call', timestamp_string
  end

  # Send a message after a request is processed
  after do
    timestamp = current_timestamp
    timestamp_string = timestamp_as_string(timestamp)
    request.env[:frontend_end_timestamp] = timestamp_string
    request.env[:frontend_time_elapsed] = current_timestamp - request.monitoring_info[:before_timestamp]
    send_monitoring_message 'End call', timestamp_string, {code: response.status, content: response.body}
  end

  alias_method :query_middle_end_service_base, :query_middle_end_service

  # Add monitoring capabilities to query_middle_end_service
  def query_middle_end_service(method, url, headers = {}, payload)
    before_timestamp = current_timestamp
    before_timestamp_string = timestamp_as_string(before_timestamp)
    common_message = {url: url, headers: headers, payload: payload}
    send_monitoring_message(
        'calling_middle_end_service',
        before_timestamp_string,
        common_message
    )
    begin
      result = query_middle_end_service_base(
          method,
          url,
          headers.merge(
              {
                  'X-Correlation-id' => request.env[:correlation_id],
                  'X-Starting-timestamp' => before_timestamp_string,

              }),
          payload)
      after_timestamp = current_timestamp
      after_timestamp_string = timestamp_as_string(after_timestamp)
      send_monitoring_message(
          'calling_middle_end_service',
          after_timestamp_string,
          common_message.merge(
              {
                  result: {code: result.code, content: result},
                  before: before_timestamp_string,
                  after: after_timestamp_string,
                  elapsed: after_timestamp - before_timestamp
              })
      )
    rescue => e
      after_timestamp = current_timestamp
      after_timestamp_string = timestamp_as_string(after_timestamp)
      send_monitoring_message(
          'calling_middle_end_service',
          after_timestamp_string,
          common_message.merge(
              {
                  result: {code: e.http_code, content: e.http_body},
                  before: before_timestamp_string,
                  after: after_timestamp_string,
                  elapsed: after_timestamp - before_timestamp
              })
      )
      raise e
    end

  end

  private

  # Execute a block and rescue any exception.
  # Enable to isolate monitoring code from business code.
  def yield_rescued
    begin
      yield
    rescue Exception => e
      MONITORING_LOG.error e
    end
  end

  # Get current timestamp
  def current_timestamp
    Time.now
  end

  # Send a monitoring message
  # specify a message type and a custom content
  def send_monitoring_message(message_type, timestamp = current_timestamp, content = {})
    yield_rescued do

      # Will contain env parameters
      env_params = {}
      request.env.each_pair do |key, value|
        # Don't include any rack-specific parameter
        unless key.to_s.start_with? 'rack.'
          env_params[key] = value
        end
      end

      # Add the message to the monitoring queue
      settings.monitoring_queue << content.merge(
          {
              header: {
                  message_type: message_type,
                  correlation_id: request.env[:correlation_id],
                  timestamp: timestamp,
                  from: self.class.name,
              },
              params: params,
              env: env_params,
          })
    end
  end

  def timestamp_as_string(timestamp)
    timestamp.to_datetime.rfc3339(3)
  end

end
