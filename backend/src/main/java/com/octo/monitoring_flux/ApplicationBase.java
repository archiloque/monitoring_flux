package com.octo.monitoring_flux;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import redis.clients.jedis.Jedis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Base structure to write a backend application to process messages.
 */
public abstract class ApplicationBase {

    /**
     * BlockingQueue for monitoring messages, they will be sent to ZeroMQ in a separated thread.
     */
    private final BlockingQueue<Map<String, ?>> blockingQueue = new LinkedTransferQueue<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(Backend.class);

    /**
     * Thread pool in charge of processing the message.
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * Format a date in the RFC 339 style.
     */
    private final DateFormat rfc339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    protected ApplicationBase() {
        new MessageSender(blockingQueue).start();
        LOGGER.info("Initializing");
        Jedis jedis = new Jedis("localhost");
        LOGGER.debug(jedis.ping());
        LOGGER.info("Initialized");

        while (true) {
            List<String> bundledMessage = jedis.blpop(0, "app_queue");
            LOGGER.info("Received a message");
            processMessage(bundledMessage.get(1));
        }
    }

    /**
     * Process a message arrived in the queue.
     * @param message the raw message content.
     */
    private void processMessage(String message) {
        LOGGER.info(message);
        String receivedMessageTimestamp = rfc339.format(new Date());

        JSONObject parsedObject = (JSONObject) JSONValue.parse(message);
        MessageHeader messageHeader = new MessageHeader((JSONObject) parsedObject.get("header"));

        Map<String, Object> header = new HashMap<>();
        header.put("correlation_id", messageHeader.correlationId);
        header.put("backend_received_message_timestamp", messageHeader.receivedTimestamp);

        Map<String, ?> body = (Map<String, ?>) parsedObject.get("body");

        sendMonitoringMessage("Received_message", receivedMessageTimestamp, createMonitoringMessage(header, body, null, null));
        executorService.submit(() -> {
            LOGGER.info("Begin processing");
            String beginProcessingTimestamp = rfc339.format(new Date());
            header.put("backend_begin_processing_timestamp", beginProcessingTimestamp);
            sendMonitoringMessage("Begin process", beginProcessingTimestamp, createMonitoringMessage(header, body, null, null));

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
            String endProcessingTimestamp = rfc339.format(new Date());
            header.put("backend_end_processing_timestamp", endProcessingTimestamp);
            sendMonitoringMessage("End process", endProcessingTimestamp, createMonitoringMessage(header, body, result, exception));
            return null;
        });
    }

    /**
     * Implement by application, called when a message is to be processed, must be multi-threaded.
     *
     * @param message a single message.
     * @return the processing result
     * @throws Exception when something goes wrong.
     */
    protected abstract Map<?, ?> processMessage(Map<String, ?> message) throws Exception;

    /**
     * Create a monitoring message, header is cloned.
     * @param header the message header
     * @param params the invocation params
     * @param result the invocation result, nullable
     * @param exception the invocation exception, nullable
     * @return a message ready to be sent
     */
    private Map<String, Object> createMonitoringMessage(
            Map<String, Object> header,
            Map<String, ?> params,
            Map<?, ?> result,
            Exception exception
    ){
        Map<String, Object> message = new HashMap<String, Object>(4);
        if(header != null){
            message.put("header", new HashMap<>(header));
        }
        if(params != null){
            message.put("params", params);
        }
        if(result != null){
            message.put("result", result);
        }
        if(exception != null){
            message.put("exception", exception.getMessage());
        }
        return message;
    }

    /**
     * Send a message to the monitoring system.
     *
     * @param messageType the message type
     * @param content     the base message content
     */
    private void sendMonitoringMessage(String messageType, String timestamp, Map<String, Object> content) {
        if (timestamp == null) {
            timestamp = rfc339.format(new Date());
        }
        Map<String, Object> message = new HashMap<>(content);
        Map<String, Object> header = (Map<String, Object>) message.get("header");
        if (header == null) {
            header = new HashMap<>();
            message.put("header", header);
        }
        header.put("message_type", messageType);
        header.put("timestamp", timestamp);
        header.put("from", this.getClass().getName());

        blockingQueue.add(message);
    }


    /**
     * Thread that send messages from a BlockingQueue to ZeroMQ
     */
    private static final class MessageSender extends Thread {

        private final BlockingQueue<Map<String, ?>> queue;

        private MessageSender(BlockingQueue<Map<String, ?>> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            Logger logger = LoggerFactory.getLogger(MessageSender.class);
            logger.info("Initializing ZeroMQ");
            ZContext zContext = new ZContext(1);
            ZMQ.Socket zContextSocket = zContext.createSocket(ZMQ.PUSH);
            zContextSocket.setLinger(0);
            zContextSocket.bind("tcp://127.0.0.1:2201");
            logger.info("ZeroMQ initialized");
            while (true) {
                try {
                    Map<String, ?> message = queue.take();
                    String messageAsString = new JSONObject(message).toJSONString();
                    LOGGER.info("Send message " + messageAsString);
                    zContextSocket.send(messageAsString);

                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private static final class MessageHeader {

        private final String correlationId;

        private final String receivedTimestamp;

        private MessageHeader(JSONObject jsonObject) {
            correlationId = (String) jsonObject.get("correlation_id");
            receivedTimestamp = (String) jsonObject.get("timestamp");

        }

    }

}
