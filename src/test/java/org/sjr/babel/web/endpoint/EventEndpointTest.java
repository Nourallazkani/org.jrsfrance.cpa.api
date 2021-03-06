package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.sjr.babel.model.entity.AbstractEvent;
import org.sjr.babel.model.entity.AbstractEvent.Audience;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.AbstractEndpoint.ContactSummary;
import org.sjr.babel.web.endpoint.EventEndpoint.EventSummary;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

public class EventEndpointTest extends AbstractEndpointTest{
	
	
	@Test 	// only events open for registrations
	public void testGetManyRefugeeEvents_strict() throws Exception{
		String checkQuery = "select e from AbstractEvent e where e.registrationOpeningDate >= :d and e.registrationOpeningDate <= :d and e.audience = :audience and e.type.stereotype = null";
		List<EventSummary> results = em.createQuery(checkQuery, AbstractEvent.class)
				.setParameter("d", LocalDate.now())
				.setParameter("audience", Audience.REFUGEE)
				.getResultList()
				.stream()
				.map(x-> new EventSummary(x, "fr"))
				.collect(Collectors.toList());
		
		String expectedJson = this.jackson.writeValueAsString(results);
	
		mockMvc.perform(get("/events")
				.param("openForRegistration", "true")
				.param("audience", "REFUGEE")
				.header("Accept-language", "fr"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}

	// only events open for registrations and not yet open for registrations
	@Test
	public void testGetManyRefugeeEvents_default() throws Exception{
		String checkQuery = "select e from AbstractEvent e where e.registrationClosingDate >= :d and e.audience = :audience and e.type.stereotype = null";
		List<EventSummary> results = em.createQuery(checkQuery, AbstractEvent.class)
				.setParameter("d", LocalDate.now())
				.setParameter("audience", Audience.REFUGEE)
				.getResultList()
				.stream()
				.map(x -> new EventSummary(x, "fr"))
				.collect(Collectors.toList());
		
		String expectedJson = this.jackson.writeValueAsString(results);
	
		mockMvc.perform(get("/events").param("includePastEvents", "false").param("includeFutureEvents", "true")
				.param("audience", "REFUGEE")
				.header("Accept-language", "fr"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}
	
	// all events for a specific organisation
	@Test
	public void testGetManyRefugeeEvents_extended() throws Exception{
		String checkQuery = "select e from AbstractEvent e where e.organisation.id = :id and e.type.stereotype is null";
		List<EventSummary> results = em.createQuery(checkQuery, AbstractEvent.class)
				.setParameter("id", 5)
				.getResultList()
				.stream()
				.map(x-> new EventSummary(x, "fr"))
				.collect(Collectors.toList());
		
		String expectedJson = this.jackson.writeValueAsString(results);
	
		mockMvc.perform(get("/events").param("includePastEvents", "true").param("includeFutureEvents", "true").param("organisationId", "5")
				.header("Accept-language", "fr"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
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

		mockMvc.perform(post("/events/3/registrations")
				.header("accessKey", "R-3b743606-928a-4086-852a-9efd72f83d01")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
	}
	
	@Test
	public void testPostEventRegistrationConflict() throws JsonProcessingException, Exception{
		mockMvc.perform(post("/events/3/registrations")
				.header("accessKey", "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isConflict());
	}
}