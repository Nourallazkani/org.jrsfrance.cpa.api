package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.Test;
import org.sjr.babel.model.entity.AbstractEvent.Audience;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.AbstractEndpoint.ContactSummary;
import org.sjr.babel.web.endpoint.EventEndpoint.EventSummary;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

public class EventEndpointTest extends AbstractEndpointTest{
	
	@Test
	public void testGetMany() throws JsonProcessingException, Exception{
		mockMvc.perform(get("/events?includePastEvents=true&includeFutureEvents=true")
				.header("Accept-language", "en"))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testPostCreated() throws JsonProcessingException, Exception{
		EventSummary input = new EventSummary();
		input.audience = Audience.VOLUNTEER.name();
		input.address = new AddressSummary("1 rue de Rivoli", null, "75007", "Paris", "France");
		input.contact = new ContactSummary("John Doe", "x@x.x", null);
		
		input.startDate = new Date();
		input.endDate = new Date();
		input.registrationClosingDate = new Date();
		input.registrationOpeningDate = new Date();
		input.subject = "subject";
		input.description = "description";
		
		mockMvc.perform(post("/events")
				.header("accessKey", "O-d6daffe2-01ed-4e40-bf1e-b2b102c873e4")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isCreated());
	}
	
	@Test
	public void testPostEventRegistrationCreated() throws JsonProcessingException, Exception{

		mockMvc.perform(post("/workshops/1/registrations")
				.header("accessKey", "R-3b743606-928a-4086-852a-9efd72f83d01")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
	}
	
	@Test
	public void testPostEventRegistrationConflict() throws JsonProcessingException, Exception{
		mockMvc.perform(post("/workshops/1/registrations")
				.header("accessKey", "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isConflict());
	}
	
	@Test
	public void testPostWorkshopRegistrationCreated() throws JsonProcessingException, Exception{

		mockMvc.perform(post("/events/3/registrations")
				.header("accessKey", "R-3b743606-928a-4086-852a-9efd72f83d01")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
	}
	
	@Test
	public void testPostWorkshopRegistrationConflict() throws JsonProcessingException, Exception{
		mockMvc.perform(post("/events/3/registrations")
				.header("accessKey", "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isConflict());
	}
}