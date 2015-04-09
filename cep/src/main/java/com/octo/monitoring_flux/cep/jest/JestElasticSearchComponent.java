package com.octo.monitoring_flux.cep.jest;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Connection to Elasticsearch using JEST.
 */
public class JestElasticsearchComponent extends DefaultComponent {

    /**
     * Logger for this route.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

        // Initialize Target Endpoint
        JestElasticsearchEndPoint endpoint = new JestElasticsearchEndPoint(uri, this);

        // Parsing Parameters
        endpoint.setElasticSearchURL(remaining);

        if (parameters.containsKey("indexName")) {
            endpoint.setIndexName((String) parameters.get("indexName"));
        }
        if (parameters.containsKey("indexType")) {
            endpoint.setIndexType((String) parameters.get("indexType"));
        }

        logger.info("Param Es : url=" + endpoint.getElasticSearchURL() +
                " index=" + endpoint.getIndexName() + " type=" + endpoint.getIndexType());

        // Properties (introspection over socketType and linger)
        setProperties(endpoint, parameters);

        // returning instance
        return endpoint;
    }


}
