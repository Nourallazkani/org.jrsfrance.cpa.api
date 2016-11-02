package org.sjr.babel.web.endpoint;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.sjr.babel.web.endpoint.TeachingEndpoint.TeachingSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy(@ContextConfiguration(classes = RestConfiguration.class))
public class TeachingEndpointTest {
	
	@Autowired
	private TeachingEndpoint endpoint;
	
	@Test
	public void testList() {
		List<TeachingSummary> result = endpoint.search(null, null, null, null, null);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void testGetOK(){
		ResponseEntity<?> x = endpoint.getOne(3);
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.OK, http);
	}
	
	@Test
	public void testGetNotFound(){
		ResponseEntity<?> x = endpoint.getOne(11313);
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.NOT_FOUND, http);
	}
	

}
