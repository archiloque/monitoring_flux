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

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

/**
 * Represents the component that manages {@link JeroMQEndpoint}
 */
public class JeroMQComponent extends DefaultComponent {
    
    /** {@inheritDoc} */
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    	// Initialzize Target Endpoint
    	JeroMQEndpoint endpoint = new JeroMQEndpoint(uri, this);
    	// Defined URL
    	endpoint.setUrl(remaining);
    	// Properties (introspection over socketType and linger)
        setProperties(endpoint, parameters);
        // Initialize connection
        return endpoint;
    }
}
