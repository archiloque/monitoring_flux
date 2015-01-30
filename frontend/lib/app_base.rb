require 'sinatra/base'
require 'json'
require 'logger'
require 'redis'

# Base for a frontend Sinatra application with redis messages
class AppBase < Sinatra::Base

  APP_REDIS_KEY = 'APP_REDIS_KEY'
  APP_REDIS_PORT = 'APP_REDIS_PORT'

  APP_BASE_LOG = ::Logger.new(STDOUT)
  APP_BASE_LOG.progname = AppBase.name

  configure do
    enable :logging
    set :public_folder, File.join(File.dirname(__FILE__), '..', 'static')
    # Initialize redis
    APP_BASE_LOG.info{'Initialize redis'}
    APP_BASE_LOG.info{ "Redis will connect on port [#{ENV[APP_REDIS_PORT]}]"}
    set :redis, Redis.new(:port => ENV[APP_REDIS_PORT])
    set :redis_key, ENV[APP_REDIS_KEY]
    APP_BASE_LOG.info{ "Pinging Redis: [#{settings.redis.ping}]"}
    APP_BASE_LOG.info{'Redis initialized'}
  end

  # Post a message to the backend
  # body the message body
  # header the message header
  def post_message_to_backend(body, header = {})
    settings.redis.rpush(
        settings.redis_key,
        JSON.generate(
            {
                header: header,
                body: body
            }))
  end

end
