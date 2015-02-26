package com.octo.monitoring_flux.cep.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.octo.monitoring_flux.shared.MonitoringMessage;

/**
 * Handler to read and process messages.
 *
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class MonitoringMessageProcessor implements Processor {

	/** Logger for this route. */
	private final Logger logger	= LoggerFactory.getLogger(getClass());
	
	/** {@inheritDoc} */
	public void process(Exchange exchange) throws Exception {
		MonitoringMessage mm = (MonitoringMessage) exchange.getOut().getBody();
		logger.info("Message coming from " + mm.getModuleType() + "-" + mm.getModuleId());
		
		// Elastic Search expect a Map as Body
		exchange.getOut().setBody(mm.asMessage());
	}

}
