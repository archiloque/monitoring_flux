package com.octo.monitoring_flux.cep.esper;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.espertech.esper.event.map.MapEventBean;

/**
 * Display entity related to message (average /s)
 */
public class DisplayMsgCount implements Processor {

    private static int secondTick = 0;

    @Override
    public void process(Exchange exchange) throws Exception {
        MapEventBean events = (MapEventBean) exchange.getIn().getBody();
        Long messageInLastSecond = (Long) events.getProperties().get("MsgCnt");
        Double averageMsgPerSecond = (Double) events.getProperties().get("avgCnt");
        secondTick++;
        System.out.println(secondTick + "s: #msg<" + messageInLastSecond + "> average msg/s over last 10s <" + averageMsgPerSecond + ">");
    }

}
