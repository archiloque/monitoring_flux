package com.octo.monitoring_flux.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.octo.monitoring_flux.shared.MonitoringMessagesKeys;
import com.octo.monitoring_flux.shared.MonitoringMessenger;
import com.octo.monitoring_flux.shared.MonitoringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.octo.monitoring_flux.shared.MonitoringUtilities.formatDateAsRfc339;
import static com.octo.monitoring_flux.shared.MonitoringUtilities.getCurrentTimestamp;
import static com.octo.monitoring_flux.shared.MonitoringUtilities.getCurrentTimestampAsRfc339;

/**
 * Base structure to write a backend application to process messages.
 */
public abstract class ApplicationBase {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Backend.class);

    /**
     * Monitoring features.
     */
    private MonitoringMessenger monitoringMessenger;

    /**
     * Value of the redis key to read from.
     */
    private String redisKey;

    /**
     * Thread pool in charge of processing the message.
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * Configuration information.
     */
    private Properties applicationProperties = new Properties();

    /**
     * Connection redis.
     */
    private Jedis redisConnection;

    protected ApplicationBase() {
        loadConfiguration();
        initRedisConnection();
        initZeroMQConnection();

        // Start
        while (true) {
            // Pop message from queue
            List<String> bundledMessage = redisConnection.blpop(0, redisKey);
            LOGGER.info("Received a message");
            processMessage(bundledMessage.get(1));
        }
    }

    /**
     * Load configuration file (properties)
     */
    protected void loadConfiguration() {
        try {
            applicationProperties.load(getClass().getResourceAsStream("/backend.properties"));
            LOGGER.info("Configuration file < backend.properties > is loaded");
        } catch (IOException e) {
            LOGGER.error("Cannot parse file backend.properties, check existence and rights", e);
            System.exit(-3);
        }
    }

    /**
     * Connect to redis
     */
    protected void initRedisConnection() {
        String redisHost = "localhost";
        int redisPort = Integer.parseInt(applicationProperties.getProperty("redis.port"));
        LOGGER.info("Initializing Redis connection on <" + redisHost + ":" + redisPort + ">");
        try {
            redisKey = applicationProperties.getProperty("redis.key");
            redisConnection = new Jedis(redisHost, redisPort);
            LOGGER.debug("Try to ping Redis : " + redisConnection.ping());
            LOGGER.info("Redis connection ETABLISHED");
        } catch (RuntimeException re) {
            LOGGER.error("Cannot connect to REDIS check it's started and available on " + redisHost + ":" + redisPort, re);
            System.exit(-1);
        }
    }

    /**
     * Connect to zeroMQ and initialize sender
     */
    protected void initZeroMQConnection() {
        int zmqPort = Integer.parseInt(applicationProperties.getProperty("zeromq.port"));
        try {
            String moduleType = applicationProperties.getProperty("app.name");
            String moduleId = moduleType + "." + ManagementFactory.getRuntimeMXBean().getName();
            monitoringMessenger = new MonitoringMessenger(moduleType, moduleId, zmqPort);
            LOGGER.info("ZeroMQ connection ETABLISHED");
        } catch (RuntimeException re) {
            LOGGER.error("Cannot connect to ZeroMQ check it's started and available on port " + zmqPort, re);
            System.exit(-2);
        }
    }

    /**
     * Process a message arrived in the queue.
     *
     * @param message the raw message content.
     */
    private void processMessage(String message) {
        LOGGER.info(message);
        ParsedMessage parsedMessage;
        try {
            parsedMessage = new ParsedMessage(message);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return;
        }
        sendMonitoringMessage(parsedMessage, "Received message", parsedMessage.receivedMessageTimestampAsString, null, null, null, null);
        // Java8 yeah...
        executorService.submit(() -> {
            LOGGER.info("Begin processing");
            String beginProcessingTimestampAsString = getCurrentTimestampAsRfc339();
            Map<String, Object> moreInfo = new HashMap<>();
            moreInfo.put("begin_processing_timestamp", beginProcessingTimestampAsString);
            sendMonitoringMessage(parsedMessage, "Start processing", beginProcessingTimestampAsString, null, null, null, null);
            Map<String, Object> result = new HashMap<>();

            try {
                Map<?, ?> resultContent = processMessage(parsedMessage.body);
                result.put("Status", "OK");
                result.put("Content", resultContent);
            } catch (Exception e) {
                result.put("Status", "KO");
                result.put("Content", e.getMessage());
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }

            LOGGER.info("End processing");
            Date endProcessingTimestamp = getCurrentTimestamp();
            String endProcessingTimestampAsString = formatDateAsRfc339(endProcessingTimestamp);
            Double elapsedTime = ((double) (endProcessingTimestamp.getTime() - parsedMessage.receivedMessageTimestamp.getTime())) / 1000;
            sendMonitoringMessage(
                    parsedMessage,
                    "End processing",
                    endProcessingTimestampAsString,
                    endProcessingTimestampAsString,
                    elapsedTime,
                    result,
                    moreInfo);
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
     * Send a message to the monitoring
     *
     * @param parsedMessage  the parsed message.
     * @param messageType    the message type.
     * @param timestamp      the timestamp.
     * @param endTimestamp   optional: the timestamp of the end of the current action
     * @param elapsedTime    optional: the elapsed time of the current action in second
     * @param result         optional: the result of current action
     * @param initialContent optional: the initial content
     */
    private void sendMonitoringMessage(
            ParsedMessage parsedMessage,
            String messageType,
            String timestamp,
            String endTimestamp,
            Double elapsedTime,
            Object result,
            Map<String, Object> initialContent) {
        monitoringMessenger.sendMonitoringMessage(
                parsedMessage.correlationId,
                redisKey,
                messageType,
                timestamp,
                parsedMessage.receivedMessageTimestampAsString,
                endTimestamp,
                elapsedTime,
                parsedMessage.body,
                parsedMessage.header,
                result,
                initialContent
        );
    }

    /**
     * The parsed message.
     */
    private static final class ParsedMessage {

        /**
         * Jackson Mapper.
         */
        private final ObjectReader mapReader = new ObjectMapper().reader(Map.class);

        /**
         * Timestamp when message is received.
         */
        private final Date receivedMessageTimestamp = getCurrentTimestamp();

        /**
         * Timestamp when message is received as a String.
         */
        private final String receivedMessageTimestampAsString = formatDateAsRfc339(receivedMessageTimestamp);

        /**
         * Message's correlation id.
         */
        private final String correlationId;

        /**
         * Message's header.
         */
        private final Map header;

        /**
         * Message's body.
         */
        private final Map<String, ?> body;

        private ParsedMessage(String message) throws IOException {
            Map<String, Object> parsedMessage = mapReader.readValue(message);
            header = (Map) parsedMessage.get("header");
            body = (Map<String, ?>) parsedMessage.get("body");
            correlationId = (header.containsKey(MonitoringMessagesKeys.MONITORING_MESSAGE_CORRELATION_ID)) ? MonitoringUtilities.createCorrelationId() : (String) header.get(MonitoringMessagesKeys.MONITORING_MESSAGE_CORRELATION_ID);

        }
    }

}
