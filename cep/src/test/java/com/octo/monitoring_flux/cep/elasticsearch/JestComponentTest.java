package com.octo.monitoring_flux.cep.elasticsearch;

import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.octo.monitoring_flux.shared.MonitoringEvent;

public class JestComponentTest extends CamelTestSupport {
	
	/** target URL . */
	private static final String URL_TEST = "localhost:9200";
	
	private String MSG = "{\"begin_timestamp\":\"2015-02-27T16:45:58.489+01:00\",\"endpoint\":\"POST /messages\","
			+ "\"module_id\":\"MonitoringBase_octo-clu_36861\",\"module_type\":\"MonitoringBase\",\"correlation_id\":"
			+ "\"MonitoringBase_octo-clu_36861_2015-02-27 15:45:58 UTC_8892ee31-59fe-4597-a8dd-f9cecd2d2742\",\"message_type\":\"Begin call\","
			+ "\"params\":{\"numberOfMessages\":\"1\",\"timeToSpend\":\"1\"},\"env\":"
			+ "{\"CONTENT_LENGTH\":\"32\",\"CONTENT_TYPE\":\"application/x-www-form-urlencoded; charset=UTF-8\",\"GATEWAY_INTERFACE\":\"CGI/1.1\","
			+ "\"PATH_INFO\":\"/messages\",\"QUERY_STRING\":\"\",\"REMOTE_ADDR\":\"::1\",\"REMOTE_HOST\":\"localhost\",\"REQUEST_METHOD\":\"POST\","
			+ "\"REQUEST_URI\":\"http://localhost:9292/messages\",\"SCRIPT_NAME\":\"\",\"SERVER_NAME\":\"localhost\",\"SERVER_PORT\":\"9292\","
			+ "\"SERVER_PROTOCOL\":\"HTTP/1.1\",\"SERVER_SOFTWARE\":\"WEBrick/1.3.1 (Ruby/2.0.0/2014-05-08)\",\"HTTP_HOST\":\"localhost:9292\","
			+ "\"HTTP_CONNECTION\":\"keep-alive\",\"HTTP_ACCEPT\":\"*/*\",\"HTTP_ORIGIN\":\"http://localhost:9292\",\"HTTP_X_REQUESTED_WITH\":"
			+ "\"XMLHttpRequest\",\"HTTP_USER_AGENT\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\","
			+ "\"HTTP_REFERER\":\"http://localhost:9292/index.html\",\"HTTP_ACCEPT_ENCODING\":\"gzip, deflate\",\"HTTP_ACCEPT_LANGUAGE\":\"fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4\","
			+ "\"HTTP_VERSION\":\"HTTP/1.1\",\"REQUEST_PATH\":\"/messages\"},\"timestamp\":\"2015-02-27T16:45:58.489+01:00\"}";
	
	@Test
    public void testJestComponent() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:end");
		ObjectReader jacksonReader = new ObjectMapper().reader(Map.class);
		MonitoringEvent frontBegin     = new MonitoringEvent(jacksonReader.readValue(MSG));
		template.sendBody("direct:start", frontBegin.asMessage());
		
        // Asserts
		mock.expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();
    }

    /** {@inheritDoc} */
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
    	return new RouteBuilder() {
            public void configure() {
                // Send message to ElasticSearch
            	from("direct:start").
            		to("jest://" +URL_TEST + "?indexName=clu&indexType=zeromq_to_elasticsearch").to("mock:end");
            	
            }
        };
    }

}
