package com.octo.monitoring_flux.cep.zmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.octo.monitoring_flux.shared.MonitoringEvent;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultMessage;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import java.util.Map;

/**
 * READ messages coming from ZeroMQ.
 */
public class JeroMQConsumer extends ScheduledPollConsumer {

    /**
     * Logger for this route.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Jackson Mapper.
     */
    private final ObjectReader jacksonReader = new ObjectMapper().reader(Map.class);

    /**
     * Target JeroMQ Component.
     */
    private final JeroMQEndpoint endpoint;

    /**
     * Initialization through endpoint.
     *
     * @param endpoint  target endpoint
     * @param processor target processor
     */
    public JeroMQConsumer(JeroMQEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;

        // Read endpoint information and create a poller
        if ("PULL".equalsIgnoreCase(endpoint.getSocketType())) {
            endpoint.setzContextSocket(endpoint.getJeromqContext().createSocket(ZMQ.PULL));
        } else if ("SUBSCRIBE".equalsIgnoreCase(endpoint.getSocketType())) {
            endpoint.setzContextSocket(endpoint.getJeromqContext().createSocket(ZMQ.SUB));
        } else {
            throw new IllegalArgumentException("Cannot create a poller correct value are PULL and SUBSCRIBE for socketType");
        }

        // Linger
        endpoint.getzContextSocket().setLinger(endpoint.getLinger());

        // URL
        endpoint.getzContextSocket().connect(endpoint.getUrl());
        // if no label defined, set default
        if (endpoint.getLabel() == null) endpoint.setLabel(endpoint.getUrl());
        logger.info("ZeroMQ connection ETABLISHED over <" + endpoint.getUrl() + "> labelled as '" + endpoint.getLabel() + "'");
    }

    @Override
    protected int poll() throws Exception {
        String msg = new String(endpoint.getzContextSocket().recv(0));
        logger.info("Incoming message from <" + endpoint.getLabel() + ">");

        // Create empty exchange
        Exchange exchange = endpoint.createExchange();

        // Set IN as RAW message (String)
        Message dataIn = new DefaultMessage();
        dataIn.setBody(msg);
        exchange.setIn(dataIn);

        // Set Marshalled Message as OUT
        Message dataOut = new DefaultMessage();
        dataOut.setBody(new MonitoringEvent(jacksonReader.readValue(msg)));
        exchange.setOut(dataOut);

        try {
            // send message to next processor in the route
            getProcessor().process(exchange);
            return 1; // number of messages polled
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }
}
