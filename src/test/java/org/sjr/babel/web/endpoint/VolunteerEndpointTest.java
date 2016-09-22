package org.sjr.babel.web.endpoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy(@ContextConfiguration(classes = RestConfiguration.class))
public class VolunteerEndpointTest {

	@Autowired
	private VolunteerEndpoint endpoint;

	@Test
	public void testList() {
		
	}

}
