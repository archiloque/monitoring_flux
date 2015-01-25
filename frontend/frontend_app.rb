require 'sinatra/base'
require 'json'
require_relative 'frontend_helper'

# Simple frontend app: a single page and a single method
class FrontendApp < Sinatra::Base

  register Sinatra::FrontendHelper

  LOG = ::Logger.new(STDOUT)

  configure do
    enable :logging
    set :public_folder, File.dirname(__FILE__) + '/static'
  end

  get '/' do
    redirect '/index.html', 301
  end

  post '/messages' do
    number_of_messages = params['numberOfMessages'].to_i
    time_to_spend = params['timeToSpend'].to_i
    LOG.info("Sending #{number_of_messages} messages to wait [#{time_to_spend}]")

    message_content = {numberOfMessages: number_of_messages, timeToSpend: time_to_spend}
    number_of_messages.times do
      post_message_to_backend(message_content)
    end

    'OK'
  end

end