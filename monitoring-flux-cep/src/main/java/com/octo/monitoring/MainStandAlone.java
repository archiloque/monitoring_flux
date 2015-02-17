package com.octo.monitoring;

import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main for the class.
 */
public class MainStandAlone {

	/** logger. */
    private static Logger LOGGER = LoggerFactory.getLogger(MainStandAlone.class);
    
    /** waiting time */
    private static final long EVERY_10MIN = 600000L;
    
    /** Spring context. */
    private static ApplicationContext springCtx = null; 
    
	/**
	 * Starter.
	 *
	 * @param args
	 * 		no parameter
	 */
	public static void main(String[] args) {
		LOGGER.info("Initializing MONITORING POST PROCESSOR");
		springCtx = new ClassPathXmlApplicationContext("/META-INF/camel-context.xml");
		
		// Get camel conext
		CamelContext camelContext = springCtx.getBean(CamelContext.class);
		try {
			camelContext.start();
			LOGGER.info("Camel context started");
			// Forever loop
			while(true) {
				Thread.sleep(EVERY_10MIN);
			}
		} catch (Exception e) {
			LOGGER.error("An error occured during Camel launch ", e);
			System.exit(-1);
		}
	}
}
