package org.sjr.babel.web.endpoint;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.JpaConfig4Tests;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AcceptOrRefuseRegistrationCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy(@ContextConfiguration(classes = {RestConfiguration.class, JpaConfig4Tests.class}))
public class LearningProgramEndpointTest {

	@Autowired 
	private LearningProgramEndpoint endpoint;
	
	@Test
	@Transactional @Rollback
	public void addRegistrationTestOk(){
		String refugeeAccessKey = "R-3b743606-928a-4086-852a-9efd72f83d01";
		ResponseEntity<?> x = endpoint.addRegistration(5, refugeeAccessKey);
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.CREATED, http);
	}
	
	@Test
	public void addRegistrationTestConflict(){
		ResponseEntity<?> x = endpoint.addRegistration(5, "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.CONFLICT, http);
	}
	
	@Test
	public void addRegistrationTestNotFound(){
		ResponseEntity<?> x = endpoint.addRegistration(100000, "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.NOT_FOUND, http);
	}
	
	@Test
	public void addRegistrationTestForbidden(){
		ResponseEntity<?> x = endpoint.addRegistration(5, "R-unknown");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.FORBIDDEN, http);
	}
	
	// Get Registrations test 
	
	@Test
	public void getInscriptionsOk(){
		ResponseEntity<?> x = endpoint.getInscriptions(5, "O-d6daffe2-01ed-4e40-bf1e-b2b102c873e6");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.OK, http);
	}
	
	@Test
	public void getInscriptionsTestNotFound(){
		ResponseEntity<?> x = endpoint.getInscriptions(10000, "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.NOT_FOUND, http);
	}
	
	@Test
	public void getInscriptionsTestForbidden(){
		ResponseEntity<?> x = endpoint.getInscriptions(5, "R-a871ce00-e7d2-497e-8a4e-d272b8b5b528");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.FORBIDDEN, http);
	}
	
	// Accept or refuse  Registration test
	
	@Test
	public void acceptOrRefuseTestOk(){
		AcceptOrRefuseRegistrationCommand acceptOrRefuseCommand = new AcceptOrRefuseRegistrationCommand() ;
		acceptOrRefuseCommand.accepted = true;
		ResponseEntity<?> x = endpoint.acceptOrRefuse(5, 1, acceptOrRefuseCommand, "O-unknown");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.FORBIDDEN, http);
	}
	
	
	// delete Registration  test 
	
	@Test
	@Transactional @Rollback
	public void deleteTestOk(){
		ResponseEntity<?> x = endpoint.delete(5, "O-d6daffe2-01ed-4e40-bf1e-b2b102c873e6");
		HttpStatus http = x.getStatusCode();
		Assert.assertEquals(HttpStatus.NO_CONTENT, http);
	}
	
	
	
}
