package com.octo.monitoring_flux.cep;

import org.apache.camel.CamelContext;
import org.junit.Ignore;
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
	
	@Test
	@Ignore
	// Parce que sinon le build finit pas hein...
	public void testRunCamel() throws Exception {
		camelContext.start();
		while(true) {
			Thread.sleep(2000);
		}
		
	}
	
	
	
	

}
