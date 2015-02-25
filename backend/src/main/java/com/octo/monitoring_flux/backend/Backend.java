package com.octo.monitoring_flux.backend;

import java.util.Map;

/**
 * Implements a simple backend.
 */
public class Backend extends ApplicationBase {

    /**
     * Project Starting.
     *
     * @param args
     */
    public static void main(String[] args) {
        new Backend();
    }

    /* (non-Javadoc)
     * @see com.octo.monitoring_flux.backend.ApplicationBase#processMessage(java.util.Map)
     */
    @Override
    protected Map<?, ?> processMessage(Map<String, ?> message) throws Exception {
        Integer timeToSleep = (Integer) message.get("timeToSpend");
        Thread.sleep(timeToSleep * 1000);
        return null;
    }
}
