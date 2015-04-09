package com.octo.monitoring_flux.cep.jest;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection to ElasticSearch using JEST.
 * 
 * @author clunven
 */
public class JestElasticSearchComponent extends DefaultComponent {
    
	/**
     * Logger for this route.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    /** {@inheritDoc} */
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    	
    	// Initialzize Target Endpoint
    	JestElasticSearchEndPoint endpoint = new JestElasticSearchEndPoint(uri, this);

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
