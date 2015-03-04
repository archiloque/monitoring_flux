package com.octo.monitoring_flux.cep.zmq;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

/**
 * Message producer to send Data into ZeroMQ.
 */
public class JeroMQProducer extends DefaultProducer {

    /**
     * Logger for this route.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * urrent JeroMQ Component.
     */
    private JeroMQEndpoint endpoint;

    public JeroMQEndpoint getEndpoint() {
        return endpoint;
    }

    /**
     * Constructor to initialize producer for ZERO (SENDER : PUSH, PUBLISH)
     */
    public JeroMQProducer(JeroMQEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;

        // Read endpoint information and create a poller
        if ("PUSH".equalsIgnoreCase(endpoint.getSocketType())) {
            endpoint.setzContextSocket(endpoint.getJeromqContext().createSocket(ZMQ.PUSH));
        } else if ("PUBLISH".equalsIgnoreCase(endpoint.getSocketType())) {
            endpoint.setzContextSocket(endpoint.getJeromqContext().createSocket(ZMQ.PUB));
        } else {
            throw new IllegalArgumentException("Cannot create a poller correct value are PULL and SUBSCRIBE for socketType");
        }

        // if no label defined, set default
        if (endpoint.getLabel() == null) endpoint.setLabel(endpoint.getUrl());

        // Linger
        endpoint.getzContextSocket().setLinger(endpoint.getLinger());

        // URL
        endpoint.getzContextSocket().bind(endpoint.getUrl());
        logger.info("ZeroMQ binding ETABLISHED over <" + endpoint.getUrl() + "> labelled as '" + endpoint.getLabel() + "'");
    }

    public void process(Exchange exchange) throws Exception {
        // Expected String as incoming message
        String message = (String) exchange.getIn().getBody();
        getEndpoint().getzContextSocket().send(message);
    }

}
