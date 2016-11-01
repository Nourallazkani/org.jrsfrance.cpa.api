package org.sjr.babel.web.endpoint;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
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
public class VolunteerEndpointTest {

	@Autowired
	private VolunteerEndpoint endpoint;

	@Test
	public void testGetVolunteerSummaryOk() {
		ResponseEntity<?> x = endpoint.getOne(1, "V-41eed0a4-0bbb-4594-a1cf-f8ab3ff810ec");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.OK, http);
	}
	@Test
	public void testGetVolunteerSummaryNotFound() {
		ResponseEntity<?> x = endpoint.getOne(2000, "V-41eed0a4-0bbb-4594-a1cf-f8ab3ff810ec");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.NOT_FOUND, http);
	}
	@Test
	public void testGetVolunteerSummaryForbidden() {
		ResponseEntity<?> x = endpoint.getOne(1, "V-11eed0a4-0bbb-4594-a1cf-f8ab3ff810ec");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.FORBIDDEN, http);
	}
	/*
	@Test
	public void testGetVolunteerMeetingRequestMessagesOK() {
		ResponseEntity<?> x = endpoint.getMeetingRequestMessages(1, 1, "V-41eed0a4-0bbb-4594-a1cf-f8ab3ff810ec");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.OK, http);
	}
	*/

}
