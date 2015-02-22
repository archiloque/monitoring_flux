package com.octo.monitoring_flux.shared;

/**
 * Keys to be used in a monitoring message.
 */
public interface MonitoringMessagesKeys {

    static final String MONITORING_MESSAGE_CORRELATION_ID = "correlation_id";
    static final String MONITORING_MESSAGE_TIMESTAMP = "timestamp";

    static final String MONITORING_MESSAGE_MODULE_TYPE = "module_type";
    static final String MONITORING_MESSAGE_MODULE_ID = "module_id";
    static final String MONITORING_MESSAGE_ENDPOINT = "endpoint";
    static final String MONITORING_MESSAGE_MESSAGE_TYPE = "message_type";

    static final String MONITORING_MESSAGE_BEGIN_TIMESTAMP = "begin_timestamp";
    static final String MONITORING_MESSAGE_END_TIMESTAMP = "end_timestamp";
    static final String MONITORING_MESSAGE_ELAPSED_TIME = "elapsed_time";
    static final String MONITORING_MESSAGE_PARAMS = "params";
    static final String MONITORING_MESSAGE_HEADERS = "headers";
    static final String MONITORING_MESSAGE_RESULT = "result";
}
