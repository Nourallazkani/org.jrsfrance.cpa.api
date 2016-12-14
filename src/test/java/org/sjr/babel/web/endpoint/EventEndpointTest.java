package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.JpaConfig4Tests;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.sjr.babel.model.entity.AbstractEvent.Audience;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.AbstractEndpoint.ContactSummary;
import org.sjr.babel.web.endpoint.EventEndpoint.EventSummary;
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
public class EventEndpointTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	private ObjectMapper jackson = new ObjectMapper();
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
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
}