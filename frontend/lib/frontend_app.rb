require 'rest_client'
require_relative 'monitoring_base'

# Simple frontend app: a single page and a single method
class FrontendApp < MonitoringBase

  FRONTEND_APP_LOG = ::Logger.new(STDOUT)
  FRONTEND_APP_LOG.progname = FrontendApp.name

  get '/' do
    redirect '/index.html', 301
  end

  post '/messages' do

    # Call first service
    begin
      query_middle_end_service(
          :get,
          '/endpoint1',
          {
              content_type: 'application/json',
          }
      )
    rescue => e
      return [500, "KO: #{e}"]
    end

    # Call second service
    number_of_messages = params['numberOfMessages'].to_i
    time_to_spend = params['timeToSpend'].to_i
    begin
      query_middle_end_service(
          :post,
          '/endpoint2',
          {content_type: 'application/json'},
          {
              numberOfMessages: number_of_messages,
              timeToSpend: time_to_spend,
          }
      )
    rescue e
      return [500, "KO: #{e}"]
    end

    # Both services are OK
    'OK'
  end

end