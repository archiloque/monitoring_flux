ENV['MIDDLE_END_PORT'] ||= '8086'
ENV['MONITORING_ZMQ_PORT'] ||= '2200'

require './lib/frontend_app'
run FrontendApp
