package com.octo.monitoring_flux.shared;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Send monitoring messages.
 */
public class MonitoringMessenger {

    private final Queue<Map<String, ?>> monitoringMessageQueue;

    /**
     * The module type
     */
    private final String moduleType;


    /**
     * The module id.
     */
    private final String moduleId;

    public MonitoringMessenger(String moduleType, String moduleId, int zMQPort) {
        MonitoringMessageSender.initialize(zMQPort);
        monitoringMessageQueue = MonitoringMessageSender.getQueue();
        this.moduleType = moduleType;
        this.moduleId = moduleId;
    }

    /**
     * Send a message to the monitoring system.
     *
     * @param correlationId  the correlation id.
     * @param endPoint       the endpoint.
     * @param messageType    the message type.
     * @param timestamp      the timestamp.
     * @param beginTimestamp optional: the timestamp of the beginning of the current action
     * @param endTimestamp   optional: the timestamp of the end of the current action
     * @param elapsedTime    optional: the elapsed time of the current action in second
     * @param params         optional: the current parameters
     * @param headers        optional: the current headers
     * @param result         optional: the result of current action
     * @param initialContent optional: the initial content
     */
    public void sendMonitoringMessage(
            String correlationId,
            String endPoint,
            String messageType,
            String timestamp,
            String beginTimestamp,
            String endTimestamp,
            Double elapsedTime,
            Object params,
            Object headers,
            Object result,
            Map<String, Object> initialContent) {

        Map<String, Object> message = new HashMap<>((initialContent == null) ? Collections.EMPTY_MAP : initialContent);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_CORRELATION_ID, correlationId);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_ENDPOINT, endPoint);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_MODULE_TYPE, moduleType);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_MODULE_ID, moduleId);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_MESSAGE_TYPE, messageType);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_TIMESTAMP, timestamp);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_BEGIN_TIMESTAMP, beginTimestamp);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_END_TIMESTAMP, endTimestamp);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_ELAPSED_TIME, elapsedTime);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_PARAMS, params);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_HEADERS, headers);
        addIfNotNull(message, MonitoringMessagesKeys.MONITORING_MESSAGE_RESULT, result);
        monitoringMessageQueue.add(message);
        monitoringMessageQueue.add(message);
    }

    /**
     * Add a value to a Map if the value is not null.
     *
     * @param content a non-null Map.
     * @param key     the non-null value key.
     * @param value   the nullable value
     */
    private void addIfNotNull(Map<String, Object> content, String key, Object value) {
        if (value != null) {
            content.put(key, value);
        }
    }

}
