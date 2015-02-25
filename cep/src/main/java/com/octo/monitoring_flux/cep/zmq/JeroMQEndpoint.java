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

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.zeromq.ZContext;

/**
 * EndPoint representing a ZERO MQ Endpoint through JeroMQ.
 *
 * @author clunven
 */
public class JeroMQEndpoint extends DefaultEndpoint {

	/** Availables METHODS. */
	private enum SOCKETS { PUSH, PULL, SUBSCRIBE};
	
	/** Socket ZeroMQ. */
	private ZContext jeromqContext;
	
	/** Availables are PUSH, PULL, SUBSCRIBE. */
	private String socketType = String.valueOf(SOCKETS.PULL);
	
	/** defined LInger. */
	private int linger = 0;
	
	/** Target zMQ URL. */
	private String url;
	
	/**
     * Default constructor.
     */
    public JeroMQEndpoint() {}

    /**
     * Initialize endpoint from consumer.
     
     * @param uri
     * 		current URI
     * @param component
     */
    public JeroMQEndpoint(String uri, JeroMQComponent component) {
        super(uri, component);
        jeromqContext = new ZContext(1);
        // Component got other properties not used immesiately but inherit like 
    }

	/**
	 * Getter accessor for attribute 'jeromqContext'.
	 *
	 * @return
	 *       current value of 'jeromqContext'
	 */
	public ZContext getJeromqContext() {
		return jeromqContext;
	}

	/**
	 * Setter accessor for attribute 'jeromqContext'.
	 * @param jeromqContext
	 * 		new value for 'jeromqContext '
	 */
	public void setJeromqContext(ZContext jeromqContext) {
		this.jeromqContext = jeromqContext;
	}

	/** {@inheritDoc} */
    public Producer createProducer() throws Exception {
        return new JeroMQProducer(this);
    }

    /** {@inheritDoc} */
    public Consumer createConsumer(Processor processor) throws Exception {
        return new JeroMQConsumer(this, processor);
    }
	
    /**
	 * Getter accessor for attribute 'socketType'.
	 *
	 * @return
	 *       current value of 'socketType'
	 */
	public String getSocketType() {
		return socketType;
	}

	/**
	 * Setter accessor for attribute 'socketType'.
	 * @param socketType
	 * 		new value for 'socketType '
	 */
	public void setSocketType(String socketType) {
		this.socketType = socketType;
	}

	/**
	 * Getter accessor for attribute 'linger'.
	 *
	 * @return
	 *       current value of 'linger'
	 */
	public int getLinger() {
		return linger;
	}

	/**
	 * Setter accessor for attribute 'linger'.
	 * @param linger
	 * 		new value for 'linger '
	 */
	public void setLinger(int linger) {
		this.linger = linger;
	}

	/**
	 * Getter accessor for attribute 'url'.
	 *
	 * @return
	 *       current value of 'url'
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Setter accessor for attribute 'url'.
	 * @param url
	 * 		new value for 'url '
	 */
	public void setUrl(String url) {
		this.url = url;
	}

    /** {@inheritDoc} */
    public boolean isSingleton() {
        return true;
    }
}
