package com.octo.monitoring_flux.shared;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

/**
 * Utilities for monitoring
 */
public class MonitoringUtilities {

    /**
     * A rfc-399 formatter for dates.
     */
    private final DateFormat rfc339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private final String localhost;

    private static Queue<Map<String, ?>> MONITORING_MESSAGE_QUEUE;

    public static void initialize(int zMQPort) {
        MonitoringMessageSender MONITORING_MESSAGE_SENDER = new MonitoringMessageSender(zMQPort);
        MONITORING_MESSAGE_QUEUE = MONITORING_MESSAGE_SENDER.getQueue();
        MONITORING_MESSAGE_SENDER.start();
    }

    public MonitoringUtilities() {
        String localHostCandidate = "";
        try {
            localHostCandidate = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException ignored) {
        }
        localhost = localHostCandidate;
    }

    /**
     * Get the current timestamp in a rfc-399 format.
     */
    public String getTimeStampAsRfc339() {
        return rfc339.format(new Date());
    }

    /**
     * Create a new correlation id;
     */
    public String createCorrelationId() {
        return localhost + "_" + getTimeStampAsRfc339() + "_" + UUID.randomUUID();
    }

    /**
     * Send a message to the monitoring system.
     *
     * @param sender      the sender
     * @param messageType the message type
     * @param timestamp   the message timestamp (may be null)
     * @param content     the base message content
     */
    public void sendMonitoringMessage(Class sender, String messageType, String timestamp, Map<String, Object> content) {
        if (timestamp == null) {
            timestamp = getTimeStampAsRfc339();
        }
        Map<String, Object> message = new HashMap<>(content);
        Map<String, Object> header = (Map<String, Object>) message.get("header");
        if (header == null) {
            header = new HashMap<>();
            message.put("header", header);
        }
        header.put("message_type", messageType);
        header.put("timestamp", timestamp);
        header.put("from", sender.getName());

        MONITORING_MESSAGE_QUEUE.add(message);
    }

    /**
     * Create a monitoring message, header is cloned.
     *
     * @param header    the message header
     * @param params    the invocation params
     * @param response    the invocation response, nullable
     * @param env       the environment parameters
     * @param exception the invocation exception, nullable
     * @return a message ready to be sent
     */
    public Map<String, Object> createMonitoringMessage(
            Map<String, ?> header,
            Object params,
            Object response,
            Map<String, ?> env,
            Exception exception
    ) {
        Map<String, Object> message = new HashMap<String, Object>(4);
        if (header != null) {
            message.put("header", new HashMap<>(header));
        }
        if (params != null) {
            message.put("params", params);
        }
        if (response != null) {
            message.put("response", response);
        }
        if (env != null) {
            message.put("env", env);
        }
        if (exception != null) {
            message.put("exception", exception.getMessage());
        }
        return message;
    }


}
