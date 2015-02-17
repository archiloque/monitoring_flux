package com.octo.monitoring.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Behaviour to handle Error.
 */
public class ErrorHandlerProcessor implements Processor {

	/** logger. */
	private static Logger LOGGER = LoggerFactory.getLogger(ErrorHandlerProcessor.class);

	/** {@inheritDoc} */
	@Override
	public void process(Exchange exchange) throws Exception {
		Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
		if (cause != null) {
			LOGGER.error("A technical error has occurred: ", cause);
		}
	}
}
