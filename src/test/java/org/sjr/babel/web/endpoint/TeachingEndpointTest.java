package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.sjr.babel.model.entity.Teaching;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.AbstractEndpoint.ContactSummary;
import org.sjr.babel.web.endpoint.TeachingEndpoint.TeachingSummary;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

public class TeachingEndpointTest extends AbstractEndpointTest {
	@Test
	public void testGetMany() throws Exception{
		List<TeachingSummary> results = em.createQuery("select t from Teaching t", Teaching.class)
				.getResultList()
				.stream()
				.map(TeachingSummary::new)
				.collect(Collectors.toList());
		
		String expectedJson = this.jackson.writeValueAsString(results);
	
		mockMvc.perform(get("/teachings"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}

	@Test
	public void testGetOneOk() throws Exception{
		String expectedJson = this.jackson.writeValueAsString(new TeachingSummary(em.find(Teaching.class, 1)));
		mockMvc.perform(get("/teachings/1"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson))
				.andReturn();
	}
	
	@Test
	public void testGetOneNotFound() throws Exception{
		mockMvc.perform(get("/teachings/123")).andExpect(status().isNotFound());
	}
	
	@Test
	public void testPostCreated() throws Exception{
		TeachingSummary input = new TeachingSummary();
		input.registrationOpeningDate = new Date();
		input.registrationClosingDate = new Date();
		input.address = new AddressSummary("1 rue de rivoli", null, "75001", "Paris", "France");
		input.contact = new ContactSummary("John Doe", "x@x.x", "123");
		input.languageLevelRequired = "A1";
		input.licence = true;
		input.master = false;
		input.fieldOfStudy = "Commerce";
		mockMvc.perform(
				post("/teachings")
				.header("accessKey", "O-d6daffe2-01ed-4e40-bf1e-b2b102c873e4")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isCreated());
	}
	
	@Test
	public void testPostUnauthorized() throws Exception{
		TeachingSummary input = new TeachingSummary();
		mockMvc.perform(
				post("/teachings")
				.header("accessKey", "xxx")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testPostTeachingRegistrationCreated() throws JsonProcessingException, Exception{

		mockMvc.perform(post("/teachings/1/registrations")
				.header("accessKey", "R-3b743606-928a-4086-852a-9efd72f83d01")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
	}
	
	@Test
	public void testPostTeachingRegistrationConflict() throws JsonProcessingException, Exception{
		mockMvc.perform(post("/teachings/1/registrations")
				.header("accessKey", "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isConflict());
	}
}
