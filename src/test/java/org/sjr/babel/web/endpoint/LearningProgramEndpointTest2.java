package org.sjr.babel.web.endpoint;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.sjr.babel.model.entity.LanguageLearningProgram;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.AbstractEndpoint.ContactSummary;
import org.sjr.babel.web.endpoint.LearningProgramEndpoint.LearningProgramSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestConfiguration.class, JpaConfig4Tests.class})
//@ContextHierarchy(@ContextConfiguration(classes = {RestConfiguration.class, JpaConfig4Tests.class}))
//@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class LearningProgramEndpointTest2 {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	private ObjectMapper jackson = new ObjectMapper();
	
	@PersistenceContext
	private EntityManager em;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	@Test
	public void testGetMany() throws Exception{
		List<LearningProgramSummary> results = em.createQuery("select lp from LearningProgram lp", LanguageLearningProgram.class)
				.getResultList()
				.stream()
				.map(LearningProgramSummary::new)
				.collect(Collectors.toList());
		
		String expectedJson = this.jackson.writeValueAsString(results);
	
		mockMvc.perform(get("/learnings/language-programs"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}

	@Test
	public void testGetOneOk() throws Exception{
		String expectedJson = this.jackson.writeValueAsString(new LearningProgramSummary(em.find(LanguageLearningProgram.class, 1)));
		mockMvc.perform(get("/learnings/language-programs/1"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson))
				.andReturn();
	}
	
	@Test
	public void testPostCreated() throws JsonProcessingException, Exception{
		LearningProgramSummary input = new LearningProgramSummary();
		input.address = new AddressSummary("14 rue d'assas", null, "75006", "Paris", "France");
		input.contact = new ContactSummary("lucile fontaine", "x@x.x", null);
		input.level = "A2";
		input.link = "https://www.google.fr/";
		input.groupSize = 5;
		input.domain = "";
		input.type = "français pour reprendre l'étude";
		input.startDate = new Date();
		input.endDate = new Date();
		input.registrationClosingDate = new Date();
		input.registrationOpeningDate = new Date();
		
		mockMvc.perform(post("/learnings/language-programs")
				.header("accessKey", "O-d6daffe2-01ed-4e40-bf1e-b2b102c873e4")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isCreated());
	}
}
