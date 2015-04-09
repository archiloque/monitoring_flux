package com.octo.monitoring_flux.cep.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.octo.monitoring_flux.shared.MonitoringEvent;

/**
 * Convert from {@link MonitoringEvent} to ElasticSearch MAP.
 *
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class FlattenMapProcessor implements Processor {

    /**
     * {@inheritDoc}
     */
    public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody(((MonitoringEvent) exchange.getIn().getBody()).asMessage());
    }

}
