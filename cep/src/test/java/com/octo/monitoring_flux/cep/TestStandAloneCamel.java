package com.octo.monitoring_flux.cep;

import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ServiceStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/camel-context.xml"} )
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TestStandAloneCamel {
 
	@Autowired
    protected CamelContext camelContext;
	
	@Produce(uri = "seda:monitoring")
	protected ProducerTemplate template;
	 
	@Test
	public void testRunCamel() throws Exception {
		camelContext.start();
		Assert.assertEquals(ServiceStatus.Started, camelContext.getStatus());
	    while(true) {
	    	// Send a dummy message
	    	template.sendBodyAndHeader("BODY", "foo", "bar");
	    	Thread.sleep(1000);
	    }
	   
	}
	
	

}
