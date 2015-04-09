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
 */
public class JestElasticsearchEndPoint extends DefaultEndpoint {

    /**
     * Http API of ES.
     */
    private String elasticSearchURL = "http://localhost:9300";

    /**
     * IndexName to pick.
     */
    private String indexName = "monitoring";

    /**
     * IndexName to pick.
     */
    private String indexType = "monitoring";

    /**
     * Jest factory.
     */
    private JestClientFactory factory;

    /**
     * ES Client.
     */
    private JestClient client;

    /**
     * Initialize endpoint from component.
     *
     * @param uri       current URI
     * @param component
     */
    public JestElasticsearchEndPoint(String uri, JestElasticsearchComponent component) {
        super(uri, component);
        factory = new JestClientFactory();
    }

    @Override
    public Producer createProducer() throws Exception {
        // Initialisation
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://" + getElasticSearchURL()).multiThreaded(true).build());
        client = factory.getObject();
        return new JestElasticsearchProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("This component can only write into ES.");
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public String getElasticSearchURL() {
        return elasticSearchURL;
    }

    public void setElasticSearchURL(String elasticSearchURL) {
        this.elasticSearchURL = elasticSearchURL;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public JestClient getClient() {
        return client;
    }

    public void setClient(JestClient client) {
        this.client = client;
    }

}
