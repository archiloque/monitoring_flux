package com.octo.monitoring_flux.cep.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.event.map.MapEventBean;
import com.octo.monitoring_flux.shared.MonitoringUtilities;

/**
 * 'CAST' FUNCTION DOES NOT WORK WITH ESPER...
 * MAKE CONVERSION and computing with Java to update engine.
 */
public class ComputeGlobalSlaProcessor implements Processor {
	
	/** Logger */
	private final Logger logger	= LoggerFactory.getLogger(getClass());
	
	/** {@inheritDoc} */
	@Override
	public void process(Exchange exchange) throws Exception {
		MapEventBean events = (MapEventBean) exchange.getIn().getBody();
		String max = (String) events.getProperties().get("maxi");
		String min = (String) events.getProperties().get("mini");
		if (max != null && min != null) {
			long tickmax = MonitoringUtilities.getTimeStampFromRfc339(max);
			long tickmin = MonitoringUtilities.getTimeStampFromRfc339(min);
			long elasped = tickmax - tickmin;
			events.getProperties().put("elasped", elasped);
			logger.info("Recording total elapsed=" + elasped);
		}
		exchange.getOut().setBody(events);
	}

}
