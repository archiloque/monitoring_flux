package com.octo.monitoring_flux.shared;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Thread that send messages from a BlockingQueue to ZeroMQ.
 */
public final class MonitoringMessageSender extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringMessageSender.class);

    /**
     * The unique instance.
     */
    private static MonitoringMessageSender INSTANCE;

    /**
     * Initialize the message sender.
     *
     * @param zMQport the non-null port number to be used by Zero MQ
     */
    public static void initialize(int zMQport) {
        if (INSTANCE == null) {
            INSTANCE = new MonitoringMessageSender(zMQport);
            INSTANCE.start();
        }
    }

    /**
     * Queue used to communicate with the thread sending the messages.
     */
    private final BlockingQueue<Map<String, ?>> queue = new LinkedTransferQueue<>();

    /** Set as attribute to remove memeory leak. */
  	private ZContext zContext;
  	
    /**
     * Zero mq socket
     */
    private final ZMQ.Socket zContextSocket;

    /**
     * Object reader to serialize json messages.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

  

    private MonitoringMessageSender(int zMQport) {
        LOGGER.info("Initializing ZeroMQ on port " + zMQport);
        zContext = new ZContext(1);
        zContextSocket = zContext.createSocket(ZMQ.PUSH);
        zContextSocket.setLinger(0);
        zContextSocket.bind("tcp://127.0.0.1:" + zMQport);
        LOGGER.info("ZeroMQ initialized");
    }

    /**
     * Get the queue to send messages to, must be called after #initialize.
     *
     * @return a non-null queue
     */
    public static Queue<Map<String, ?>> getQueue() {
        if (INSTANCE == null) {
            throw new RuntimeException("Not initialized");
        }
        return INSTANCE.queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Map<String, ?> message = queue.take();
                String messageAsString = objectMapper.writeValueAsString(message);
                LOGGER.info("Send message " + messageAsString);
                zContextSocket.send(messageAsString);

            } catch (InterruptedException | IOException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
