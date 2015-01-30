require_relative 'monitoring_base'

# Simple frontend app: a single page and a single method
class FrontendApp < MonitoringBase

  FRONTEND_APP_LOG = ::Logger.new(STDOUT)
  FRONTEND_APP_LOG.progname = FrontendApp.name

  get '/' do
    redirect '/index.html', 301
  end

  post '/messages' do
    number_of_messages = params['numberOfMessages'].to_i
    time_to_spend = params['timeToSpend'].to_i
    FRONTEND_APP_LOG.info{"Sending #{number_of_messages} messages to wait [#{time_to_spend}]"}

    message_content = {numberOfMessages: number_of_messages, timeToSpend: time_to_spend}
    number_of_messages.times do
      post_message_to_backend(message_content)
    end

    'OK'
  end

end