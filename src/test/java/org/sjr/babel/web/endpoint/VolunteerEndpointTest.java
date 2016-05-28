package org.sjr.babel.web.endpoint;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.WebAppInitializer.ApplicationConfig;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.sjr.babel.web.endpoint.VolunteerEndpoint.VolunteerSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
	@ContextConfiguration(classes=ApplicationConfig.class),
	@ContextConfiguration(classes=RestConfiguration.class)
})
public class VolunteerEndpointTest {

	@Autowired
	private VolunteerEndpoint endpoint;

	@Test
	public void testList() {
		
		List<VolunteerSummary> list = endpoint.list(null, null, null, null);
		
		Assert.assertEquals(3, list.size());
		VolunteerSummary v1 = list.stream().filter(x->x.id==1).findFirst().get();
		Assert.assertEquals(2, v1.languages.size());
		Assert.assertEquals(1, v1.fieldsOfStudy.size());
	}

}
