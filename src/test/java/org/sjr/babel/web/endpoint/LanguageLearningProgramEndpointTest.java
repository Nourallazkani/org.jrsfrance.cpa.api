package org.sjr.babel.web.endpoint;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.sjr.babel.model.entity.LanguageLearningProgram;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AcceptOrRefuseRegistrationCommand;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.AbstractEndpoint.ContactSummary;
import org.sjr.babel.web.endpoint.LearningProgramEndpoint.LearningProgramSummary;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

public class LanguageLearningProgramEndpointTest extends AbstractEndpointTest{
	
	// only learning programs open for registrations
	@Test
	public void testGetManyLanguageLearningProgramsOk_strict() throws Exception{
		
		String checkQuery = "select lp from LanguageLearningProgram lp where lp.registrationClosingDate >= :d and lp.registrationOpeningDate <= :d";		
		List<LearningProgramSummary> results = em.createQuery(checkQuery, LanguageLearningProgram.class)
				.setParameter("d", LocalDate.now())
				.getResultList()
				.stream()
				.map(LearningProgramSummary::new)
				.collect(Collectors.toList());
		
		String expectedJson = this.jackson.writeValueAsString(results);
	
		mockMvc.perform(get("/learnings/language-programs").param("openForRegistration", "true"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}
	
	// only learning programs open for registrations and not yet open for registrations
	@Test
	public void testGetManyLanguageLearningProgramsOk_default() throws Exception{
		
		String checkQuery = "select lp from LanguageLearningProgram lp where lp.registrationClosingDate >= :d";
		List<LearningProgramSummary> results = em.createQuery(checkQuery, LanguageLearningProgram.class)
				.setParameter("d", LocalDate.now())
				.getResultList()
				.stream()
				.map(LearningProgramSummary::new)
				.collect(Collectors.toList());
		
		String expectedJson = this.jackson.writeValueAsString(results);
	
		mockMvc.perform(get("/learnings/language-programs").param("includePastEvents", "false").param("includeFutureEvents", "true"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}
	
	// all learning programs for a specific organisation
	@Test
	public void testGetManyLanguageLearningProgramsOk_extended() throws Exception{
		
		String checkQuery = "select lp from LanguageLearningProgram lp where lp.organisation.id = :oId";		
		List<LearningProgramSummary> results = em.createQuery(checkQuery, LanguageLearningProgram.class)
				.setParameter("oId", 1)
				.getResultList()
				.stream()
				.map(LearningProgramSummary::new)
				.collect(Collectors.toList());
		
		String expectedJson = this.jackson.writeValueAsString(results);
	
		mockMvc.perform(get("/learnings/language-programs").param("includeFutureEvents", "true").param("includePastEvents", "true").param("organisationId", "1"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void testGetOneLanguageLearningProgramOk() throws Exception{
		String expectedJson = this.jackson.writeValueAsString(new LearningProgramSummary(em.find(LanguageLearningProgram.class, 1)));
		mockMvc.perform(get("/learnings/language-programs/1"))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void testGetOneLanguageLearningProgramNotFound() throws Exception{
		mockMvc.perform(get("/learnings/language-programs/6"))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void testPostCreated() throws JsonProcessingException, Exception{
		LearningProgramSummary input = new LearningProgramSummary();
		input.address = new AddressSummary("14 rue d'assas", null, "75006", "Paris", "France");
		input.contact = new ContactSummary("lucile fontaine", "x@x.x", null);
		input.level = "A2";
		input.link = "https://www.google.fr/";
		input.groupSize = 5;
		input.type = "Francais pour reprendre des etudes";
		input.startDate = LocalDate.now().plusMonths(1);
		input.endDate = LocalDate.now().plusMonths(2);
		input.registrationOpeningDate = LocalDate.now();
		input.registrationClosingDate = LocalDate.now().plusWeeks(3);
		
		mockMvc.perform(post("/learnings/language-programs")
				.header("accessKey", "O-d6daffe2-01ed-4e40-bf1e-b2b102c873e4")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isCreated());
	}
	
	
	// registrations (created, conflict, forbidden)
	
	@Test
	public void testPostLanguageProgramRegistrationCreated() throws JsonProcessingException, Exception{

		mockMvc.perform(post("/learnings/language-programs/1/registrations")
				.header("accessKey", "R-3b743606-928a-4086-852a-9efd72f83d01")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
	}
	
	@Test
	public void testPostLanguageProgramRegistrationConflict() throws JsonProcessingException, Exception{
		mockMvc.perform(post("/learnings/language-programs/1/registrations")
				.header("accessKey", "R-a871ce00-e7d2-497e-8a4e-d272b8b5b520")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isConflict());
	}
	
	@Test
	public void testPostLanguageProgramRegistrationForbidden() throws JsonProcessingException, Exception{

		mockMvc.perform(post("/learnings/language-programs/1/registrations")
				.header("accessKey", "xxx")
				)
		.andExpect(status().isForbidden());
	}
	
	// registration acceptation or refusal (ok accept, ok refuse, forbidden)

	@Test
	public void testAcceptLanguageProgramRegistrationOk() throws JsonProcessingException, Exception{
		AcceptOrRefuseRegistrationCommand cmd = new AcceptOrRefuseRegistrationCommand();
		cmd.accepted = true;
		mockMvc.perform(post("/learnings/language-programs/1/registrations/1")
				.header("accessKey", "O-d6daffe2-01ed-4e40-bf1e-b2b102c873e5")
				.content(jackson.writeValueAsString(cmd))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isNoContent());
	}
	
	
	@Test
	public void testRejectLanguageProgramRegistrationOk() throws JsonProcessingException, Exception{
		AcceptOrRefuseRegistrationCommand cmd = new AcceptOrRefuseRegistrationCommand();
		cmd.accepted = false;
		mockMvc.perform(post("/learnings/language-programs/1/registrations/1")
				.header("accessKey", "O-d6daffe2-01ed-4e40-bf1e-b2b102c873e5")
				.content(jackson.writeValueAsString(cmd))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isNoContent());
	}
	
	@Test
	public void testAcceptOrRefuseLanguageProgramRegistrationForbidden() throws JsonProcessingException, Exception{
		AcceptOrRefuseRegistrationCommand cmd = new AcceptOrRefuseRegistrationCommand();
		cmd.accepted = true;
		mockMvc.perform(post("/learnings/language-programs/1/registrations/1")
				.header("accessKey", "O-d6daffe2-01ed-4e40-bf1e-b2b102c873e2")
				.content(jackson.writeValueAsString(cmd))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isForbidden());
	}
}
