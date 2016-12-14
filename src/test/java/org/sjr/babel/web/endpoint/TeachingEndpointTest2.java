package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.JpaConfig4Tests;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.sjr.babel.model.entity.Teaching;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.AbstractEndpoint.ContactSummary;
import org.sjr.babel.web.endpoint.TeachingEndpoint.TeachingSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration()
@ContextConfiguration(classes = {RestConfiguration.class, JpaConfig4Tests.class})
//@ContextHierarchy(@ContextConfiguration(classes = {RestConfiguration.class, JpaConfig4Tests.class}))
//@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class TeachingEndpointTest2 {
	
	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	private ObjectMapper jackson = new ObjectMapper();
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

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
	
	@PersistenceContext
	private EntityManager em;

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
}
