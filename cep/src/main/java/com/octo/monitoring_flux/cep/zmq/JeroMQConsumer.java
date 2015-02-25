/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.octo.monitoring_flux.cep.zmq;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultMessage;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

/**
 * READ messages coming from ZeroMQ.
 *
 * @author clunven
 */
public class JeroMQConsumer extends ScheduledPollConsumer {
	
	/** Logger for this route. */
	private final Logger logger	= LoggerFactory.getLogger(getClass());

	/** Target JeroMQ Component. */
    private final JeroMQEndpoint endpoint;
    
    /** Reading socket. */
    private  ZMQ.Socket zContextSocket;

    /**
     * Initialization through endpoint
     * @param endpoint
     * 		target endpoint
     * @param processor
     * 		target processor
     */
    public JeroMQConsumer(JeroMQEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        // Read endpoint information and create a poller
        if ("PULL".equalsIgnoreCase(endpoint.getSocketType())) {
        	zContextSocket = endpoint.getJeromqContext().createSocket(ZMQ.PULL);
        } else if ("SUBSCRIBE".equalsIgnoreCase(endpoint.getSocketType())) {
        	zContextSocket = endpoint.getJeromqContext().createSocket(ZMQ.SUB);
        } else {
        	throw new IllegalArgumentException("Cannot create a poller correct value are PULL and SUBSCRIBE for socketType");
        }
        // Linger
        zContextSocket.setLinger(endpoint.getLinger());
        logger.info("Connecting to ZeroMQ : " + endpoint.getUrl() );
        // URL
	    zContextSocket.connect(endpoint.getUrl());
	    logger.info("Connection OK");
    }

    /** {@inheritDoc} */
    @Override
    protected int poll() throws Exception {
    	String msg = new String(zContextSocket.recv(0));
    	logger.info("Incoming message from " + endpoint.getUrl());
    	// Create empty exchange
    	Exchange exchange = endpoint.createExchange();
    	Message dataIn = new DefaultMessage();
    	dataIn.setBody(msg);
    	exchange.setOut(dataIn);
    	
    	
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
