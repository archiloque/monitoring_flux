require 'sinatra/base'
require 'thread'
require 'ffi-rzmq'
require 'json'
require 'logger'
require 'securerandom'
require 'socket'
require 'redis'

module Sinatra

  # Base structure to write a frontend application that send messages.
  # Monitoring use a queue with a separate thread that send the message to a ZeroMQ queue.
  module FrontendHelper

    MONITORING_LOG = ::Logger.new(STDOUT)

    module Helpers

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
        Time.now.to_datetime.rfc3339(3)
      end

      # Post a message to the backend
      def post_message_to_backend(content)
        timestamp = current_timestamp
        send_monitoring_message('send message', timestamp, {message: content})
        settings.redis.rpush(
            'app_queue',
            JSON.generate(
                {
                    header: {
                        correlation_id: request.env[:correlation_id],
                        timestamp: timestamp
                    },
                    body: content
                }))
      end

      # Send a monitoring message
      # specify a message type and a custom content
      def send_monitoring_message(message_type, timestamp = current_timestamp, content = {})
        yield_rescued do
          options.monitoring_queue << content.merge(
              {
                  header: {
                      message_type: message_type,
                      correlation_id: request.env[:correlation_id],
                      timestamp: timestamp,
                      from: self.class.name
                  },
                  params: params,
                  env: request.env
              })
        end
      end

      # Send a monitoring message about a message
      def monitor_message(message)
        send_monitoring_message('send message', {message: message})
      end

    end

    # Called when the extension is registered
    def self.registered(app)
      app.helpers FrontendHelper::Helpers
      app.set :app_name, app.name
      app.set :host_name, Socket.gethostname
      app.set :process_pid, Process.pid

      # Initialize 0mq
      MONITORING_LOG.info('Initialize ZeroMq')
      zmq_ctx = ZMQ::Context.create(1)
      unless zmq_ctx
        fail 'Failed to create a Context zero mq'
      end
      zmq_socket = zmq_ctx.socket(ZMQ::PUSH)
      error_check(zmq_socket.setsockopt(ZMQ::LINGER, 0))
      result_code = zmq_socket.bind('tcp://127.0.0.1:2200')
      error_check(result_code)
      app.set :zmq_socket, zmq_socket
      MONITORING_LOG.info('ZeroMq initialized')

      # Initialize redis
      MONITORING_LOG.info('Initialize redis')
      app.set :redis, Redis.new
      MONITORING_LOG.debug app.settings.redis.ping
      MONITORING_LOG.info('Redis initialized')

      # Will contain the messages to be sent
      app.set :monitoring_queue, Queue.new

      # Create the thread that send the events to zero mq from the queue
      Thread.new do
        while true
          begin
            message = app.monitoring_queue.pop
            message_json = JSON.generate(message)
            MONITORING_LOG.debug "Sending message [#{message_json}]"
            zmq_socket.send_string(message_json)
            error_check(result_code)
          rescue Exception => e
            MONITORING_LOG.error e
          end
        end
      end

      # Send a message before a request is processed
      app.before do
        timestamp = current_timestamp
        yield_rescued do
          request.env[:correlation_id] = "#{app.host_name}_#{app.app_name}_#{app.process_pid}_#{Time.now.getutc}_#{SecureRandom.uuid}"
          request.env[:frontend_begin_timestamp] = timestamp
        end
        send_monitoring_message 'begin', timestamp
      end

      # Send a message after a request is processed
      app.after do
        timestamp = current_timestamp
        request.env[:frontend_end_timestamp] = timestamp
        send_monitoring_message 'end', timestamp
      end
    end

    private

    # Check if a ZeroMQ operation is successful
    def self.error_check(result_code)
      unless ZMQ::Util.resultcode_ok?(result_code)
        MONITORING_LOG.error "Operation failed, errno [#{ZMQ::Util.errno}] description [#{ZMQ::Util.error_string}]"
      end
    end

  end

  helpers FrontendHelper

end