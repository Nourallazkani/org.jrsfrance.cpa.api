package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.sjr.babel.model.entity.AbstractEvent;
import org.sjr.babel.model.entity.AbstractEvent.Audience;
import org.sjr.babel.model.entity.AbstractLearningProgram;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.AbstractEndpoint.ContactSummary;
import org.sjr.babel.web.endpoint.EventEndpoint.EventSummary;
import org.sjr.babel.web.endpoint.LearningProgramEndpoint.LearningProgramSummary;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

public class EventEndpointTest extends AbstractEndpointTest{
	
	
	// events for refugees : including open for registration and not open yet for registrations
	@Test
	public void testGetMany1() throws Exception{
		String checkQuery = "select e from AbstractEvent e where e.registrationClosingDate >= :d and e.audience = :audience";
		List<EventSummary> results = em.createQuery(checkQuery, AbstractEvent.class)
				.setParameter("d", LocalDate.now()).setParameter("audience", Audience.REFUGEE)
				.getResultList()
				.stream()
				.map(x-> new EventSummary(x, "fr"))
				.collect(Collectors.toList());
		
		String expectedJson = this.jackson.writeValueAsString(results);
	
		mockMvc.perform(get("/events").param("includePastEvents", "false").param("includeFutureEvents", "true")
				.param("audience", "REFUGEE")//.param("openForRegistration", "true")
				.header("Accept-language", "fr"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}
	
	
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
		
		input.startDate = LocalDateTime.now();
		input.endDate = LocalDateTime.now();
		input.registrationClosingDate = LocalDate.now();
		input.registrationOpeningDate = LocalDate.now();
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