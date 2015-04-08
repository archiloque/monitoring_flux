package com.octo.monitoring_flux.cep.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.event.map.MapEventBean;

public class Esper2ElasticSearchProcessor implements Processor {
	
	/** Logger */
	private final Logger logger	= LoggerFactory.getLogger(getClass());
	
	/** {@inheritDoc} */
	@Override
	public void process(Exchange exchange) throws Exception {
		MapEventBean events = (MapEventBean) exchange.getIn().getBody();
		Double avgCnt 		  = (Double) events.getProperties().get("avgCnt");
		String correlationId  = (String) events.getProperties().get("correlationId");
		String moduleType 	  = (String) events.getProperties().get("moduleType");
		Double elapsedTime 	  = (Double) events.getProperties().get("elapsedTime");
		Long elasped		  = (Long) events.getProperties().get("elasped");
		
		Map < String, Object> elasticSearchEvent = new HashMap<>();
		
		if (avgCnt != null) {
			logger.info("Alert Throttling");
			elasticSearchEvent.put("alert_type", "throttling");
			elasticSearchEvent.put("timestamp", System.currentTimeMillis());
			elasticSearchEvent.put("avgCnt", avgCnt);
			elasticSearchEvent.put("correlationId", correlationId);
			
		} else if (moduleType != null) {
			logger.info("Trigger Unitary SLA Violation for component");
			elasticSearchEvent.put("alert_type", "unitary-sla");
			elasticSearchEvent.put("module", moduleType);
			elasticSearchEvent.put("time", elapsedTime);
			elasticSearchEvent.put("timestamp", System.currentTimeMillis());
			
		} else if (elasped != null) {
			logger.info("Global SLA Violation for component");
			elasticSearchEvent.put("alert_type", "global-sla");
			elasticSearchEvent.put("timestamp", System.currentTimeMillis());
			elasticSearchEvent.put("correlationId", correlationId);
			elasticSearchEvent.put("count", elasped);
		}
		
		 
		exchange.getOut().setBody(elasticSearchEvent);
	}

}
