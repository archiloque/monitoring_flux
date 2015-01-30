ENV['APP_REDIS_KEY'] ||= 'app_queue'
ENV['APP_REDIS_PORT'] ||= '6379'
ENV['MONITORING_ZMQ_PORT'] ||= '2200'

require_relative 'lib/frontend_app'
run FrontendApp
