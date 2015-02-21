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
    set :module_type, self.name
    set :module_id, "#{settings.module_type}_#{Socket.gethostname}_#{Process.pid}"

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
    request.monitoring_info[:before_timestamp_string] = timestamp_string
    request.monitoring_info[:correlation_id] = "#{settings.module_id}_#{Time.now.getutc}_#{SecureRandom.uuid}"
    send_monitoring_message(
        'Begin call',
        timestamp_string,
        {
            begin_timestamp: timestamp_string,
        }
    )
  end

  # Send a message after a request is processed
  after do
    timestamp = current_timestamp
    timestamp_string = timestamp_as_string(timestamp)
    send_monitoring_message(
        'End call',
        timestamp_string,
        {
            begin_timestamp: request.monitoring_info[:before_timestamp_string],
            end_timestamp: timestamp_string,
            elapsed_time: current_timestamp - request.monitoring_info[:before_timestamp],

            result: {
                code: response.status,
                content: response.body
            }
        })
  end

  alias_method :query_middle_end_service_base, :query_middle_end_service

  # Add monitoring capabilities to query_middle_end_service
  def query_middle_end_service(method, url, headers = {}, payload)

    before_timestamp = current_timestamp
    before_timestamp_string = timestamp_as_string(before_timestamp)

    real_headers = headers.merge(
        {
            'X-Correlation-id' => request.monitoring_info[:correlation_id],
            'X-Starting-timestamp' => before_timestamp_string,
        })

    common_message =
        {
            begin_timestamp: before_timestamp_string,
            service_url: url,
            headers: real_headers,
            payload: payload
        }

    send_monitoring_message(
        'Begin call middle end service',
        before_timestamp_string,
        common_message
    )
    begin
      result = query_middle_end_service_base(
          method,
          url,
          real_headers,
          payload)
      query_middle_end_service_after(
          common_message,
          {code: result.code, content: result},
          before_timestamp,
          before_timestamp_string
      )
    rescue RestClient::Exception => e
      query_middle_end_service_after(
          common_message,
          {code: e.http_code, content: e.http_body},
          before_timestamp,
          before_timestamp_string
      )
      raise e
    rescue => e
      query_middle_end_service_after(
          common_message,
          {content: e.message},
          before_timestamp,
          before_timestamp_string
      )
      raise e
    end

  end

  private

  def query_middle_end_service_after(common_message, result, before_timestamp, before_timestamp_string)
    after_timestamp = current_timestamp
    after_timestamp_string = timestamp_as_string(after_timestamp)
    send_monitoring_message(
        'End call middle end service',
        after_timestamp_string,
        common_message.merge(
            {
                end_timestamp: after_timestamp_string,
                elapsed_time: after_timestamp - before_timestamp,
                result: result
            })
    )
  end

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
  def send_monitoring_message(message_type,
                              timestamp = current_timestamp,
                              content = {})
    yield_rescued do

      # Will contain env parameters
      env_params = {}
      request.env.each_pair do |key, value|
        # Don't include any rack-specific parameter
        unless key.to_s.start_with?('rack.') || key.to_s.start_with?('sinatra.')
          env_params[key] = value
        end
      end

      # Add the message to the monitoring queue
      settings.monitoring_queue << content.merge(
          {
              correlation_id: request.monitoring_info[:correlation_id],
              timestamp: timestamp,

              module_type: settings.module_type,
              module_id: settings.module_id,
              endpoint: "#{request.env['REQUEST_METHOD']} #{request.env['PATH_INFO']}",
              message_type: message_type,
              params: params,
              env: env_params
          })
    end
  end

  def timestamp_as_string(timestamp)
    timestamp.to_datetime.rfc3339(3)
  end

end
