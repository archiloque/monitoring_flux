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
            List<String> bundled_message = jedis.blpop(0, "app_queue");
            LOGGER.info("New message");
            String message = bundled_message.get(1);
            LOGGER.info(message);
            JSONObject parsedObject = (JSONObject) JSONValue.parse(message);
            JSONObject parsedObjectHeader = (JSONObject) parsedObject.get("header");
            String correlationId = (String) parsedObjectHeader.get("correlation_id");
            String receivedTimestamp = (String) parsedObjectHeader.get("timestamp");
            String beginTimestamp = rfc339.format(new Date());

            Map<String, Object> beginHeader = new HashMap<>();
            beginHeader.put("message_type", "begin");
            beginHeader.put("correlation_id", correlationId);
            beginHeader.put("received_timestamp", receivedTimestamp);
            beginHeader.put("backend_begin_timestamp", beginTimestamp);

            Map<String, ?> body = (Map<String, ?>) parsedObject.get("body");

            Map<String, Object> monitoringBeginMessage = new HashMap<>();
            monitoringBeginMessage.put("header", beginHeader);
            monitoringBeginMessage.put("params", body);

            sendMonitoringMessage("begin", beginTimestamp, monitoringBeginMessage);

            Map<?, ?> result = null;
            Exception exception = null;

            LOGGER.info("Begin process");
            try {
                result = processMessage(body);
            } catch (Exception e) {
                exception = e;
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
            LOGGER.info("End process");

            Map<String, Object> endHeader = new HashMap<>(beginHeader);
            String endTimestamp = rfc339.format(new Date());
            endHeader.put("backend_end_timestamp", endTimestamp);

            Map<String, Object> monitoringEndMessage = new HashMap<>();
            monitoringEndMessage.put("header", endHeader);
            monitoringEndMessage.put("params", body);
            if (result != null) {
                monitoringEndMessage.put("result", result);
            }
            if (exception != null) {
                monitoringEndMessage.put("exception", exception.getMessage());
            }
            sendMonitoringMessage("end", endTimestamp, monitoringEndMessage);

        }
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
     * Send a message to the monitoring system.
     *
     * @param messageType the message type
     * @param content     the base message content
     */
    private void sendMonitoringMessage(String messageType, String timestamp, Map<String, ?> content) {
        if (timestamp == null) {
            timestamp = rfc339.format(new Date());
        }
        Map additionalContent = new HashMap<>(content);
        Map<String, Object> header = (Map<String, Object>) additionalContent.get("header");
        if (header == null) {
            header = new HashMap<>();
            additionalContent.put("header", header);
        }
        header.put("message_type", messageType);
        header.put("timestamp", timestamp);
        header.put("from", this.getClass().getName());

        blockingQueue.add(additionalContent);
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

}
