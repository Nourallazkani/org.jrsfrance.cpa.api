package org.sjr.babel.web.endpoint;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.JpaConfig4Tests;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestConfiguration.class, JpaConfig4Tests.class})
//@ContextHierarchy(@ContextConfiguration(classes = {RestConfiguration.class, JpaConfig4Tests.class}))
//@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@Ignore
public class RefugeeEndpointTest {
	
	@Autowired
	private RefugeeEndpoint endpoint;
	
	@Test
	public void testGetRefugeeSummaryOk(){
		ResponseEntity<?> x = endpoint.search(1, "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.OK, http);
	}
	
	@Test
	public void testGetRefugeeSummaryNotFound(){
		ResponseEntity<?> x = endpoint.search(3, "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.NOT_FOUND, http);
	}
	
	@Test
	public void testGetRefugeeSummaryForbidden(){
		ResponseEntity<?> x = endpoint.search(1, "xxx");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.FORBIDDEN, http);
	}
	/*
	@Test
	public void testGetRefugeeMeetingRequestMessagesOk(){
		ResponseEntity<?> x = endpoint.getMeetingRequestMessages(1, 1, "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.OK, http);
	}
	@Test
	public void testGetRefugeeMeetingRequestMessagesNotFound(){
		ResponseEntity<?> x = endpoint.getMeetingRequestMessages(11010, 900020, "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.NOT_FOUND, http);
	}
	@Test
	public void testGetRefugeeMeetingRequestMessagesForbidden(){
		ResponseEntity<?> x = endpoint.getMeetingRequestMessages(1, 1, "R-a171ce00-e7d2-497e-8a4e-d272b8b5b520");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.FORBIDDEN, http);
	}
	*/
}
