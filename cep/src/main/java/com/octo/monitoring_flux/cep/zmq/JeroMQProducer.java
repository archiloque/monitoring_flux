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
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

/**
 * Message producer to send Data into ZeroMQ 
 *
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class JeroMQProducer extends DefaultProducer {
    
	/** Logger for this route. */
	private final Logger logger	= LoggerFactory.getLogger(getClass());
	    
	/** Current JeroMQ Component. */
    private JeroMQEndpoint endpoint;

    /**
	 * Getter accessor for attribute 'endpoint'.
	 *
	 * @return
	 *       current value of 'endpoint'
	 */
	public JeroMQEndpoint getEndpoint() {
		return endpoint;
	}

	/**
     * Constructor to initialize producer for ZERO (SENDER : PUSH, PUBLISH)
     * @param endpoint
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

    /** {@inheritDoc} */
    public void process(Exchange exchange) throws Exception {
       // Expected String as incoming message
       String message = (String) exchange.getIn().getBody();
       getEndpoint().getzContextSocket().send(message);
    }

}
