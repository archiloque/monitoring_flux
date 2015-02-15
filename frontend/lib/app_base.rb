require 'sinatra/base'
require 'json'
require 'logger'
require 'rest-client'

# Base for a frontend Sinatra application with redis messages
class AppBase < Sinatra::Base

  MIDDLE_END_PORT = 'MIDDLE_END_PORT'
  APP_BASE_LOG = ::Logger.new(STDOUT)
  APP_BASE_LOG.progname = AppBase.name

  configure do
    enable :logging
    set :public_folder, File.join(File.dirname(__FILE__), '..', 'static')
    set :middle_end_port, ENV[MIDDLE_END_PORT]

    RestClient.log= 'stdout'
  end

  # Call a service of the middle end server
  def query_middle_end_service(method, url, headers = {}, payload)
    if payload && payload.is_a?(Hash)
      payload = payload.to_json

    end
    RestClient::Request.execute(
        :method => method,
        :url => "http://localhost:#{settings.middle_end_port}#{url}",
        :headers => headers,
        :payload => payload)
  end

end
