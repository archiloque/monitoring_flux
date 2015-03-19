package com.octo.monitoring_flux.cep.esper;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.espertech.esper.event.map.MapEventBean;

/**
 * Display SLA violation.
 *
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class DisplaySlaViolation  implements Processor {
	
	/** {@inheritDoc} */
	@Override
	public void process(Exchange exchange) throws Exception {
		MapEventBean events = (MapEventBean) exchange.getIn().getBody();
		Double elasped = (Double) events.getProperties().get("elapsedTime");
		String module = (String) events.getProperties().get("moduleType");
		System.out.println(System.currentTimeMillis() +  "=>SLA violation on '" + module + "': " + elasped);
	}
	
}
