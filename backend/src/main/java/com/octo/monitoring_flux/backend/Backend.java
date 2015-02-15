package com.octo.monitoring_flux.backend;

import java.util.Map;

/**
 * Implement a simple backend.
 */
public class Backend extends ApplicationBase {

    public static void main( String[] args ) {
        new Backend();
    }

    @Override
    protected Map<?, ?> processMessage(Map<String, ?> message) throws Exception {
        Integer timeToSleep = (Integer) message.get("timeToSpend");
        Thread.sleep(timeToSleep * 1000);
        return null;
    }
}
