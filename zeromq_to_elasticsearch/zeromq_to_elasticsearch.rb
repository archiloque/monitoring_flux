require 'logger'
require 'json'
require 'ffi-rzmq'
require 'elasticsearch'

# Read messages from zeromq and push them directly to elastic search in "monitoring" index
class ZeromqToElasticsearch

  def error_check(result_code)
    if ZMQ::Util.resultcode_ok?(result_code)
      true
    else
      @logger.error "Operation failed, errno [#{ZMQ::Util.errno}] description [#{ZMQ::Util.error_string}]"
      false
    end
  end

  def initialize(port)
    unless port
      raise 'You should specify a port number for ZeroMq'
    end
    @logger = ::Logger.new(STDOUT)
    @logger.info 'Starting'

    @logger.info 'Initialize ElasticSearch'
    @elastic_search = Elasticsearch::Client.new log: true
    @logger.info @elastic_search.info
    @logger.info 'ElasticSearch initialized'

    @logger.info "Initialize ZeroMq, port is [#{port}]"
    ctx = ZMQ::Context.create(1)
    pull_sock = ctx.socket(ZMQ::PULL)
    error_check(pull_sock.setsockopt(ZMQ::LINGER, 0))
    result_code = pull_sock.connect("tcp://127.0.0.1:#{port}")
    unless result_code
      exit -1
    end
    @logger.info 'ZeroMq initialized'

    @logger.info 'Started'

    message = ''
    while true
      result_code = pull_sock.recv_string(message)
      if error_check(result_code)
        @logger.debug "Read [#{message}"
        json_message = JSON.parse(message)
        @elastic_search.create(
            {
                index: 'monitoring',
                body: json_message,
                type: 'zeromq_to_elsticsearch'
            })
      end

    end
  end

end

ZeromqToElasticsearch.new(ARGV[0])
