package com.octo.monitoring_flux.cep.processor;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.octo.monitoring_flux.shared.MonitoringEvent;

/**
 * Convert incoming String into {@link MonitoringEvent}
 * 
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class MarshallProcessor  implements Processor {

	/**
	 * Logger for the json.
	 */
	private ObjectReader jacksonReader = new ObjectMapper().reader(Map.class);
	
	/** {@inheritDoc} */
	public void process(Exchange exchange) throws Exception {
		exchange.getOut().setBody(new MonitoringEvent(jacksonReader.readValue((String) exchange.getIn().getBody())));
	}

}
