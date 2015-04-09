/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.octo.monitoring_flux.cep.zmq;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * This class test connectivity to ZeroMQ.
 */
public class JeroMQComponentTest extends CamelTestSupport {

    /**
     * target URL .
     */
    private static final String URL_TEST = "tcp://127.0.0.1:2206";

    /**
     * Sample communication test.
     *
     * @throws Exception
     */
    @Test
    public void testJeroMQComponent() throws Exception {

        MockEndpoint mock = getMockEndpoint("mock:end");

        // Envoi du message
        template.sendBody("direct:start", "{ \"id\":\"1\"}");

        // Asserts
        mock.expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {

                // Send message to 0MQ
                from("direct:start").to("jeromq://" + URL_TEST + "?socketType=PUSH&linger=0").end();

                // Read message from 0MQ and send to mock
                from("jeromq://" + URL_TEST + "?socketType=PULL&linger=0").to("mock:end");
            }
        };
    }
}
