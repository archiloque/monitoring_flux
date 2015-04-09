package com.octo.monitoring_flux.cep.jest;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * Camel component using JEST.
 *
 * @author clunven
 */
public class JestElasticSearchEndPoint extends DefaultEndpoint {

	/** Http API of ES. */
	private String elasticSearchURL = "http://localhost:9300";
	
	/** IndexName to pick. */
	private String indexName = "monitoring";
	
	/** IndexName to pick. */
	private String indexType = "monitoring";
	
	/** Jest factory. */
	private JestClientFactory factory;
	
	/** ES Client. */
	private JestClient client;
	
	/**
     * Initialize endpoint from component.
     *
     * @param uri current URI
     * @param component
     */
    public JestElasticSearchEndPoint(String uri, JestElasticSearchComponent component) {
        super(uri, component);
        factory = new JestClientFactory();
    }
    
	@Override
	public Producer createProducer() throws Exception {
		// Initialisation
		factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://" + getElasticSearchURL()).multiThreaded(true).build());
		client = factory.getObject();
		return new JestElasticSearchProducer(this);
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		throw new UnsupportedOperationException("This component can only write into ES.");
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	/**
	 * Getter accessor for attribute 'elasticSearchURL'.
	 *
	 * @return
	 *       current value of 'elasticSearchURL'
	 */
	public String getElasticSearchURL() {
		return elasticSearchURL;
	}

	/**
	 * Setter accessor for attribute 'elasticSearchURL'.
	 * @param elasticSearchURL
	 * 		new value for 'elasticSearchURL '
	 */
	public void setElasticSearchURL(String elasticSearchURL) {
		this.elasticSearchURL = elasticSearchURL;
	}

	/**
	 * Getter accessor for attribute 'indexName'.
	 *
	 * @return
	 *       current value of 'indexName'
	 */
	public String getIndexName() {
		return indexName;
	}

	/**
	 * Setter accessor for attribute 'indexName'.
	 * @param indexName
	 * 		new value for 'indexName '
	 */
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	
	/**
	 * Getter accessor for attribute 'indexType'.
	 *
	 * @return
	 *       current value of 'indexType'
	 */
	public String getIndexType() {
		return indexType;
	}

	/**
	 * Setter accessor for attribute 'indexType'.
	 * @param indexType
	 * 		new value for 'indexType '
	 */
	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	/**
	 * Getter accessor for attribute 'client'.
	 *
	 * @return
	 *       current value of 'client'
	 */
	public JestClient getClient() {
		return client;
	}

	/**
	 * Setter accessor for attribute 'client'.
	 * @param client
	 * 		new value for 'client '
	 */
	public void setClient(JestClient client) {
		this.client = client;
	}

}
