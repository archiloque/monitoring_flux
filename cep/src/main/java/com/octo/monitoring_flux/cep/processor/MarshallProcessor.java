package com.octo.monitoring_flux.cep.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.octo.monitoring_flux.shared.MonitoringEvent;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;

/**
 * Convert incoming String into {@link MonitoringEvent}.
 */
public class MarshallProcessor implements Processor {

    /**
     * Logger for the json.
     */
    private ObjectReader jacksonReader = new ObjectMapper().reader(Map.class);

    public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody(new MonitoringEvent(jacksonReader.readValue((String) exchange.getIn().getBody())));
    }

}
