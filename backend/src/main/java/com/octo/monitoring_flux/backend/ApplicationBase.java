package com.octo.monitoring_flux.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.octo.monitoring_flux.shared.MonitoringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base structure to write a backend application to process messages.
 */
public abstract class ApplicationBase {

    /**
     * Monitoring features
     */
    private final MonitoringUtilities monitoringUtilities = new MonitoringUtilities();

    private static final Logger LOGGER = LoggerFactory.getLogger(Backend.class);

    private final ObjectReader mapReader = new ObjectMapper().reader(Map.class);

    /**
     * Thread pool in charge of processing the message.
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    protected ApplicationBase() {
        Properties applicationProperties = new Properties();
        try {
            applicationProperties.load(getClass().getResourceAsStream("/backend.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MonitoringUtilities.initialize(Integer.parseInt(applicationProperties.getProperty("zeromq.port")));
        LOGGER.info("Initializing");
        Jedis jedis = new Jedis("localhost", Integer.parseInt(applicationProperties.getProperty("redis.port")));
        LOGGER.debug(jedis.ping());
        LOGGER.info("Initialized");

        while (true) {
            List<String> bundledMessage = jedis.blpop(0, applicationProperties.getProperty("redis.key"));
            LOGGER.info("Received a message");
            processMessage(bundledMessage.get(1));
        }
    }

    /**
     * Process a message arrived in the queue.
     *
     * @param message the raw message content.
     */
    private void processMessage(String message) {
        LOGGER.info(message);
        String receivedMessageTimestamp = monitoringUtilities.getTimeStampAsRfc339();
        Map<String, Object> parsedMessage;
        try {
            parsedMessage = mapReader.readValue(message);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return;
        }
        MessageHeader messageHeader = new MessageHeader((Map) parsedMessage.get("header"));

        Map<String, Object> header = new HashMap<>();
        String correlationId = messageHeader.correlationId;
        if (correlationId == null) {
            correlationId = monitoringUtilities.createCorrelationId();
        }
        header.put("correlation_id", correlationId);
        header.put("backend_received_message_timestamp", messageHeader.receivedTimestamp);

        Map<String,?> body = (Map<String, ?>) parsedMessage.get("body");

        sendMonitoringMessage(
                "Received_message",
                receivedMessageTimestamp,
                monitoringUtilities.createMonitoringMessage(header, body, null, null, null)
        );
        executorService.submit(() -> {
            LOGGER.info("Begin processing");
            String beginProcessingTimestamp = monitoringUtilities.getTimeStampAsRfc339();
            header.put("backend_begin_processing_timestamp", beginProcessingTimestamp);
            sendMonitoringMessage(
                    "Begin process",
                    beginProcessingTimestamp,
                    monitoringUtilities.createMonitoringMessage(header, body, null, null, null)
            );
            Map<?, ?> result = null;
            Exception exception = null;

            try {
                result = processMessage(body);
            } catch (Exception e) {
                exception = e;
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
            LOGGER.info("End processing");
            String endProcessingTimestamp = monitoringUtilities.getTimeStampAsRfc339();
            header.put("backend_end_processing_timestamp", endProcessingTimestamp);
            sendMonitoringMessage(
                    "End process",
                    endProcessingTimestamp,
                    monitoringUtilities.createMonitoringMessage(header, body, result, null, exception)
            );
            return null;
        });
    }

    /**
     * Implemented by application, called when a message is to be processed, must be multi-threaded.
     *
     * @param message a single message.
     * @return the processing result
     * @throws Exception when something goes wrong.
     */
    protected abstract Map<?, ?> processMessage(Map<String, ?> message) throws Exception;

    /**
     * Send a message to the monitoring system.
     *
     * @param messageType the message type
     * @param timestamp   the message timestamp (may be null)
     * @param content     the base message content
     */
    private void sendMonitoringMessage(String messageType, String timestamp, Map<String, Object> content) {
        monitoringUtilities.sendMonitoringMessage(getClass(), messageType, timestamp, content);
    }

    private static final class MessageHeader {

        private final String correlationId;

        private final String receivedTimestamp;

        private MessageHeader(Map header) {
            correlationId = (String) header.get("correlation_id");
            receivedTimestamp = (String) header.get("timestamp");

        }

    }

}
