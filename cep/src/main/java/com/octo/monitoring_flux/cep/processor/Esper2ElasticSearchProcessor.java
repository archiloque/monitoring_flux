package com.octo.monitoring_flux.cep.processor;

import com.espertech.esper.event.map.MapEventBean;
import com.octo.monitoring_flux.shared.MonitoringUtilities;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Esper2ElasticsearchProcessor implements Processor {

    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void process(Exchange exchange) throws Exception {
        MapEventBean events = (MapEventBean) exchange.getIn().getBody();
        Double avgCnt = (Double) events.getProperties().get("avgCnt");
        String correlationId = (String) events.getProperties().get("correlationId");
        String moduleType = (String) events.getProperties().get("moduleType");
        Double elapsedTime = (Double) events.getProperties().get("elapsedTime");
        Long elapsed = (Long) events.getProperties().get("elapsed");

        Map<String, Object> elasticSearchEvent = new HashMap<>();

        if (avgCnt != null) {
            logger.info("Alert Throttling");
            elasticSearchEvent.put("alert_type", "throttling");
            elasticSearchEvent.put("timestamp", MonitoringUtilities.getCurrentTimestampAsRfc339());
            elasticSearchEvent.put("avgCnt", avgCnt);
            elasticSearchEvent.put("correlationId", correlationId);

        } else if (moduleType != null) {
            logger.info("Trigger Unitary SLA Violation for component");
            elasticSearchEvent.put("alert_type", "unitary-sla");
            elasticSearchEvent.put("timestamp", MonitoringUtilities.getCurrentTimestampAsRfc339());
            elasticSearchEvent.put("module", moduleType);
            elasticSearchEvent.put("time", elapsedTime);

        } else if (elapsed != null) {
            logger.info("Global SLA Violation for component");
            elasticSearchEvent.put("alert_type", "global-sla");
            elasticSearchEvent.put("timestamp", MonitoringUtilities.getCurrentTimestampAsRfc339());
            elasticSearchEvent.put("correlationId", correlationId);
            elasticSearchEvent.put("count", elapsed);
        }

        exchange.getOut().setBody(elasticSearchEvent);
    }

}
