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
package com.octo.monitoring_flux.cep.esper;

import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.octo.monitoring_flux.shared.MonitoringEvent;

/**
 * This class test connectivity to ZeroMQ.
 *
 * @author clunven
 */
public class EsperComponentTest extends CamelTestSupport {
	
	/** Sample Messages. */
	private String FRONTEND_BEGIN = "{\"begin_timestamp\":\"2015-02-27T16:45:58.489+01:00\",\"endpoint\":\"POST /messages\",\"module_id\":\"MonitoringBase_octo-clu_36861\",\"module_type\":\"MonitoringBase\",\"correlation_id\":\"MonitoringBase_octo-clu_36861_2015-02-27 15:45:58 UTC_8892ee31-59fe-4597-a8dd-f9cecd2d2742\",\"message_type\":\"Begin call\",\"params\":{\"numberOfMessages\":\"1\",\"timeToSpend\":\"1\"},\"env\":{\"CONTENT_LENGTH\":\"32\",\"CONTENT_TYPE\":\"application/x-www-form-urlencoded; charset=UTF-8\",\"GATEWAY_INTERFACE\":\"CGI/1.1\",\"PATH_INFO\":\"/messages\",\"QUERY_STRING\":\"\",\"REMOTE_ADDR\":\"::1\",\"REMOTE_HOST\":\"localhost\",\"REQUEST_METHOD\":\"POST\",\"REQUEST_URI\":\"http://localhost:9292/messages\",\"SCRIPT_NAME\":\"\",\"SERVER_NAME\":\"localhost\",\"SERVER_PORT\":\"9292\",\"SERVER_PROTOCOL\":\"HTTP/1.1\",\"SERVER_SOFTWARE\":\"WEBrick/1.3.1 (Ruby/2.0.0/2014-05-08)\",\"HTTP_HOST\":\"localhost:9292\",\"HTTP_CONNECTION\":\"keep-alive\",\"HTTP_ACCEPT\":\"*/*\",\"HTTP_ORIGIN\":\"http://localhost:9292\",\"HTTP_X_REQUESTED_WITH\":\"XMLHttpRequest\",\"HTTP_USER_AGENT\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\",\"HTTP_REFERER\":\"http://localhost:9292/index.html\",\"HTTP_ACCEPT_ENCODING\":\"gzip, deflate\",\"HTTP_ACCEPT_LANGUAGE\":\"fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4\",\"HTTP_VERSION\":\"HTTP/1.1\",\"REQUEST_PATH\":\"/messages\"},\"timestamp\":\"2015-02-27T16:45:58.489+01:00\"}";
	
	/** Sample Messages. */
	private String FRONTEND_STARTCALL = "{\"headers\":"
			+ "		{\"content_type\":\"application/json\","
			+ "		 \"X-Correlation-id\":\"MonitoringBase_octo-clu_36861_2015-02-27 15:45:58 UTC_8892ee31-59fe-4597-a8dd-f9cecd2d2742\","
			+ "      \"X-Starting-Timestamp\":\"2015-02-27T16:45:58.503+01:00\"},"
			+ "\"begin_timestamp\":\"2015-02-27T16:45:58.503+01:00\","
			+ "\"module_type\":\"MonitoringBase\",\"message_type\":\"Begin call middle end service\","
			+ "\"params\":{\"numberOfMessages\":\"1\",\"timeToSpend\":\"1\"},"
			+ "\"env\":{\"CONTENT_LENGTH\":\"32\",\"CONTENT_TYPE\":\"application/x-www-form-urlencoded; charset=UTF-8\","
			+ "         \"GATEWAY_INTERFACE\":\"CGI/1.1\",\"PATH_INFO\":\"/messages\",\"QUERY_STRING\":\"\",\"REMOTE_ADDR\":\"::1\","
			+ "         \"REMOTE_HOST\":\"localhost\",\"REQUEST_METHOD\":\"POST\",\"REQUEST_URI\":\"http://localhost:9292/messages\","
			+ "         \"SCRIPT_NAME\":\"\",\"SERVER_NAME\":\"localhost\",\"SERVER_PORT\":\"9292\",\"SERVER_PROTOCOL\":\"HTTP/1.1\","
			+ "         \"SERVER_SOFTWARE\":\"WEBrick/1.3.1 (Ruby/2.0.0/2014-05-08)\",\"HTTP_HOST\":\"localhost:9292\","
			+ "         \"HTTP_CONNECTION\":\"keep-alive\",\"HTTP_ACCEPT\":\"*/*\",\"HTTP_ORIGIN\":\"http://localhost:9292\","
			+ "         \"HTTP_X_REQUESTED_WITH\":\"XMLHttpRequest\",\"HTTP_USER_AGENT\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\","
			+ "         \"HTTP_REFERER\":\"http://localhost:9292/index.html\",\"HTTP_ACCEPT_ENCODING\":\"gzip, deflate\","
			+ "         \"HTTP_ACCEPT_LANGUAGE\":\"fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4\",\"HTTP_VERSION\":\"HTTP/1.1\",\"REQUEST_PATH\":\"/messages\"},"
			+ "\"service_url\":\"/endpoint2\",\"endpoint\":\"POST /messages\",\"module_id\":\"MonitoringBase_octo-clu_36861\","
			+ "\"payload\":{\"numberOfMessages\":1,\"timeToSpend\":1},"
			+ "\"correlation_id\":\"MonitoringBase_octo-clu_36861_2015-02-27 15:45:58 UTC_8892ee31-59fe-4597-a8dd-f9cecd2d2742\","
			+ "\"timestamp\":\"2015-02-27T16:45:58.503+01:00\"}";
	
	private String FRONTEND_ENDCALL = "{\"headers\":"
			+ "		{\"content_type\":\"application/json\","
			+ "      \"X-Correlation-id\":\"MonitoringBase_octo-clu_36861_2015-02-27 15:45:58 UTC_8892ee31-59fe-4597-a8dd-f9cecd2d2742\","
			+ "      \"X-Starting-Timestamp\":\"2015-02-27T16:45:58.503+01:00\"},"
			+ "\"begin_timestamp\":\"2015-02-27T16:45:58.503+01:00\","
			+ "\"module_type\":\"MonitoringBase\",\"message_type\":\"End call middle end service\","
			+ "\"params\":{\"numberOfMessages\":\"1\",\"timeToSpend\":\"1\"},"
			+ "\"env\":{\"CONTENT_LENGTH\":\"32\",\"CONTENT_TYPE\":\"application/x-www-form-urlencoded; charset=UTF-8\","
			+ "        \"GATEWAY_INTERFACE\":\"CGI/1.1\",\"PATH_INFO\":\"/messages\",\"QUERY_STRING\":\"\",\"REMOTE_ADDR\":\"::1\","
			+ "        \"REMOTE_HOST\":\"localhost\",\"REQUEST_METHOD\":\"POST\",\"REQUEST_URI\":\"http://localhost:9292/messages\","
			+ "        \"SCRIPT_NAME\":\"\",\"SERVER_NAME\":\"localhost\",\"SERVER_PORT\":\"9292\",\"SERVER_PROTOCOL\":\"HTTP/1.1\","
			+ "        \"SERVER_SOFTWARE\":\"WEBrick/1.3.1 (Ruby/2.0.0/2014-05-08)\",\"HTTP_HOST\":\"localhost:9292\","
			+ "        \"HTTP_CONNECTION\":\"keep-alive\",\"HTTP_ACCEPT\":\"*/*\",\"HTTP_ORIGIN\":\"http://localhost:9292\","
			+ "        \"HTTP_X_REQUESTED_WITH\":\"XMLHttpRequest\",\"HTTP_USER_AGENT\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\","
			+ "        \"HTTP_REFERER\":\"http://localhost:9292/index.html\",\"HTTP_ACCEPT_ENCODING\":\"gzip, deflate\","
			+ "        \"HTTP_ACCEPT_LANGUAGE\":\"fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4\",\"HTTP_VERSION\":\"HTTP/1.1\",\"REQUEST_PATH\":\"/messages\"},"
			+ "\"result\":{\"code\":200,\"content\":{\"status\":\"OK\"}},"
			+ "\"service_url\":\"/endpoint2\",\"endpoint\":\"POST /messages\",\"module_id\":\"MonitoringBase_octo-clu_36861\","
			+ "\"end_timestamp\":\"2015-02-27T16:45:58.518+01:00\","
			+ "\"payload\":{\"numberOfMessages\":1,\"timeToSpend\":1},"
			+ "\"elapsed_time\":0.015346,"
			+ "\"correlation_id\":\"MonitoringBase_octo-clu_36861_2015-02-27 15:45:58 UTC_8892ee31-59fe-4597-a8dd-f9cecd2d2742\","
			+ "\"timestamp\":\"2015-02-27T16:45:58.518+01:00\"}";
		
	/**
	 * Sample communication test.
	 *
	 * @throws Exception
	 */
	@Test
    public void testJeroMQComponent() throws Exception {
		// read sample as event
		ObjectReader jacksonReader = new ObjectMapper().reader(Map.class);
		MonitoringEvent frontBegin     = new MonitoringEvent(jacksonReader.readValue(FRONTEND_BEGIN));
		MonitoringEvent frontStartCall = new MonitoringEvent(jacksonReader.readValue(FRONTEND_STARTCALL));
		MonitoringEvent frontEndCall   = new MonitoringEvent(jacksonReader.readValue(FRONTEND_ENDCALL));
		
		MockEndpoint mock = getMockEndpoint("mock:end");
		
		// Envoi du message
		template.sendBody("direct:start", frontBegin);
		template.sendBody("direct:start", frontStartCall);
		template.sendBody("direct:start", frontEndCall);
		
		// Asserts
		mock.expectedMinimumMessageCount(0);
        assertMockEndpointsSatisfied();
    }

    /** {@inheritDoc} */
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
    	return new RouteBuilder() {
            public void configure() {
            	
            	// Send message to esper
            	from("direct:start").to("esper://monitoring");
            	
            	// Count messages per second
            	from("esper://monitoring?eql=insert into TicksPerSecond "
            			+ "select correlationId, count(*) as cnt "
            			+ "from " + MonitoringEvent.class.getCanonicalName() + ".win:time_batch(1 sec) "
            			+ "group by correlationId").to("esper://monitoring");
            	
            	// Display results
            	from("esper://monitoring?eql=select correlationId, avg(cnt) as avgCnt, cnt as MsgCnt from TicksPerSecond.win:time(10 sec) "
            			+ "group by correlationId").to("stream:out");
                
            	
            }
        };
    }
}
