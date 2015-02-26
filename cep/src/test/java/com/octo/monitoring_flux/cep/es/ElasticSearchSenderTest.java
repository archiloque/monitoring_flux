package com.octo.monitoring_flux.cep.es;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.elasticsearch.ElasticsearchConfiguration;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.elasticsearch.action.get.GetResponse;
import org.junit.Test;

public class ElasticSearchSenderTest extends CamelTestSupport {

	    @Test
	    public void testSend2ElasticSearch() throws Exception {
			Map<String,String> map=new HashMap<String,String>();
			map.put("content","{ \"top\":\"ok\"}");
		    Map<String,Object> headers=new HashMap<String,Object>();
		    headers.put(ElasticsearchConfiguration.PARAM_OPERATION, ElasticsearchConfiguration.OPERATION_INDEX);
			headers.put(ElasticsearchConfiguration.PARAM_INDEX_NAME,"monitoring");
			headers.put(ElasticsearchConfiguration.PARAM_INDEX_TYPE,"cep_to_elasticsearch");
			
			String indexId= template.requestBodyAndHeaders("direct:start", map, headers,String.class);
			headers.put(ElasticsearchConfiguration.PARAM_OPERATION,ElasticsearchConfiguration.OPERATION_GET_BY_ID);
			GetResponse response=template.requestBodyAndHeaders("direct:start",indexId,headers,GetResponse.class);
			assertNotNull("response should not be null",response);
			assertNotNull("response source should not be null",response.getSource());
			System.out.println(response.getIndex());
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
