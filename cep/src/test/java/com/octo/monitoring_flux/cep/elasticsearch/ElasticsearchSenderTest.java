package com.octo.monitoring_flux.cep.elasticsearch;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.elasticsearch.ElasticsearchConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.elasticsearch.action.get.GetResponse;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test connectivity to elasticsearch
 */
public class ElasticsearchSenderTest extends CamelTestSupport {

    @Test
    @Ignore
    public void testSend2ElasticSearch() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("content", "{ \"top\":\"ok\"}");

        // Insert message into ES
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(ElasticsearchConfiguration.PARAM_OPERATION, ElasticsearchConfiguration.OPERATION_INDEX);
        headers.put(ElasticsearchConfiguration.PARAM_INDEX_NAME, "monitoring");
        headers.put(ElasticsearchConfiguration.PARAM_INDEX_TYPE, "test_to_elasticsearch");
        String indexId = template.requestBodyAndHeaders("direct:start", map, headers, String.class);

        // Check that message is written into ES
        headers.put(ElasticsearchConfiguration.PARAM_OPERATION, ElasticsearchConfiguration.OPERATION_GET_BY_ID);
        GetResponse response = template.requestBodyAndHeaders("direct:start", indexId, headers, GetResponse.class);
        assertNotNull("response should not be null", response);
        assertNotNull("response source should not be null", response.getSource());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start").to("elasticsearch://duduche");
            }
        };
    }
}
