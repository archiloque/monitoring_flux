require 'logger'
require 'json'
require 'ffi-rzmq'
require 'elasticsearch'

ENV['ELASTICSEARCH_PORT'] ||= '9200'
ENV['ELASTICSEARCH_INDEX'] ||= 'monitoring'

# Read messages from zeromq and push them directly to elastic search in "monitoring" index
class ZeromqToElasticsearch

  LOGGER = ::Logger.new(STDOUT)

  def error_check(result_code)
    if ZMQ::Util.resultcode_ok?(result_code)
      true
    else
      LOGGER.error "Operation failed, errno [#{ZMQ::Util.errno}] description [#{ZMQ::Util.error_string}]"
      false
    end
  end

  def initialize(port)
    unless port
      raise 'You should specify a port number for ZeroMq'
    end
    LOGGER.info 'Starting'

    LOGGER.info "Initialize ElasticSearch at port [#{ENV['ELASTICSEARCH_PORT']}]"
    elasticsearch_client = Elasticsearch::Client.new host: "localhost:#{ENV['ELASTICSEARCH_PORT']}",  log: true
    LOGGER.info elasticsearch_client.info
    LOGGER.info 'ElasticSearch initialized'

    LOGGER.info "Initialize ZeroMq, port is [#{port}]"
    ctx = ZMQ::Context.create(1)
    pull_sock = ctx.socket(ZMQ::PULL)
    error_check(pull_sock.setsockopt(ZMQ::LINGER, 0))
    result_code = pull_sock.connect("tcp://127.0.0.1:#{port}")
    unless result_code
      exit -1
    end
    LOGGER.info 'ZeroMq initialized'

    LOGGER.info 'Started'

    message = ''
    elasticsearch_index = ENV['ELASTICSEARCH_INDEX']
    while true
      result_code = pull_sock.recv_string(message)
      if error_check(result_code)
        LOGGER.debug "Read [#{message}"
        json_message = JSON.parse(message)
        elasticsearch_client.create(
            {
                index: elasticsearch_index,
                body: json_message,
                type: 'zeromq_to_elasticsearch'
            })
      end

    end
  end

end

ZeromqToElasticsearch.new(ARGV[0])
