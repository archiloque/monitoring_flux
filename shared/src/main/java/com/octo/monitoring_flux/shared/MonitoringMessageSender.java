package com.octo.monitoring_flux.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Thread that send messages from a BlockingQueue to ZeroMQ.
 */
public final class MonitoringMessageSender extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringMessageSender.class);

    private final int zMQport;

    private final BlockingQueue<Map<String, ?>> queue = new LinkedTransferQueue<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MonitoringMessageSender(int zMQport) {
        this.zMQport = zMQport;
    }

    public Queue<Map<String, ?>> getQueue() {
        return queue;
    }

    @Override
    public void run() {
        LOGGER.info("Initializing ZeroMQ");
        ZContext zContext = new ZContext(1);
        ZMQ.Socket zContextSocket = zContext.createSocket(ZMQ.PUSH);
        zContextSocket.setLinger(0);
        zContextSocket.bind("tcp://127.0.0.1:" + zMQport);
        LOGGER.info("ZeroMQ initialized");
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
