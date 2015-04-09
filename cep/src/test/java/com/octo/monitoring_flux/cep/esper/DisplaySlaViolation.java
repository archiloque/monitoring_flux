package com.octo.monitoring_flux.cep.esper;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.espertech.esper.event.map.MapEventBean;

/**
 * Display SLA violation.
 */
public class DisplaySlaViolation implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        MapEventBean events = (MapEventBean) exchange.getIn().getBody();
        Double elapsed = (Double) events.getProperties().get("elapsedTime");
        String module = (String) events.getProperties().get("moduleType");
        System.out.println(System.currentTimeMillis() + "=>SLA violation on '" + module + "': " + elapsed);
    }

}
