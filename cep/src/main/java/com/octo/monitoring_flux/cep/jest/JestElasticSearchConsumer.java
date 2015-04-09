package com.octo.monitoring_flux.cep.jest;

import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

/**
 * Consume Data from ES (from..) : do not use
 * 
 * @deprecated
 * @author clunven
 */
public class JestElasticSearchConsumer extends ScheduledPollConsumer {
    
    /**
     * Mandatory Constructor.
     *
     * @param endpoint  
     * 		target endpoint
     * @param processor 
     * 		target processor
     */
    public JestElasticSearchConsumer(JestElasticSearchEndPoint endpoint, Processor processor) {
        super(endpoint, processor);
    	throw new UnsupportedOperationException("This component can only write into ElasticSearhc, not pool from it");
    }

	@Override
	protected int poll() throws Exception {
		throw new UnsupportedOperationException("This component can only write into ElasticSearhc, not pool from it");
	}
}
