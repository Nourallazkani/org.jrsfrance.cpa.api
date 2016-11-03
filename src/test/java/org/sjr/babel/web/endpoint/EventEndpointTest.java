package org.sjr.babel.web.endpoint;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.sjr.babel.web.endpoint.EventEndpoint.EventSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy(@ContextConfiguration(classes = RestConfiguration.class))
public class EventEndpointTest {

	@Autowired
	EventEndpoint endPoint;
	
	
	@Test @Ignore
	public void testFullSearch() {
		List<EventSummary> list = endPoint.fullSearch(null, null, null, null, null, true, true, "fr");
	}

}
