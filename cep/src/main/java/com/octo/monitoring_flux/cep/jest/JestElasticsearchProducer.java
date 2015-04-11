package com.octo.monitoring_flux.cep.jest;

import io.searchbox.core.Index;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JestElasticsearchProducer extends DefaultProducer {

    /**
     * Logger for this route.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * endpoint elastic search.
     */
    private JestElasticsearchEndPoint endpoint;

    /**
     * Constructor to initialize producer for ZERO (SENDER : PUSH, PUBLISH)
     */
    public JestElasticsearchProducer(JestElasticsearchEndPoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        logger.info("Elasticsearch Connection SET UP on " + endpoint.getElasticSearchURL());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> map = (Map<String, Object>) exchange.getIn().getBody();
        logger.info("Sending to ES " + map);
        Index index = new Index.Builder(map).index(endpoint.getIndexName()).type(endpoint.getIndexType()).build();
        endpoint.getClient().execute(index);
    }

}
