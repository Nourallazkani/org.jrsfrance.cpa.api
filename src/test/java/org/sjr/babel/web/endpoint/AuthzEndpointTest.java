package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.JpaConfig4Tests;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.sjr.babel.web.endpoint.AuthzEndpoint.SignInCommand;
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
public class AuthzEndpointTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	private ObjectMapper jackson = new ObjectMapper();
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	@Test
	public void testOrganisationSignInByMailAddressOk() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.mailAddress = "o@o.O";
		input.realm = "O";
		input.password = "azerty";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.accessKey").value("O-d6daffe2-01ed-4e40-bf1e-b2b102c873e4"));
	}
	
	@Test
	public void testRefugeeSignInByMailAddressOk() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.mailAddress = "r@r.R";
		input.realm = "R";
		input.password = "azerty";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isOk());
	}	
	
	@Test
	public void testVolunteerSignInByMailAddressOk() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.mailAddress = "v@V.v";
		input.realm = "V";
		input.password = "azerty";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isOk());
	}		
	
	@Test
	public void testOrganisationSignInByMailAddressUnauthorized() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.mailAddress = "x@x.x";
		input.realm = "O";
		input.password = "azerty";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testRefugeeSignInByMailAddressUnauthorized() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.mailAddress = "x@x.x";
		input.realm = "R";
		input.password = "azerty";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isUnauthorized());
	}	
	
	@Test
	public void testVolunteerSignInByMailAddressUnauthorized() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.mailAddress = "x@x.x";
		input.realm = "V";
		input.password = "azerty";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isUnauthorized());
	}		
	
	@Test
	public void testOrganisationSignInByAccessKeyOk() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.accessKey="O-d6daffe2-01ed-4e40-bf1e-b2b102c873e4";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testOrganisationSignInByAccessKeyUnauthorized() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.accessKey="xxx";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testRefugeeSignInByAccessKeyOk() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.accessKey="R-a871ce00-e7d2-497e-8a4e-d272b8b5b520";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testRefugeeSignInByAccessKeyUnauthorized() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.accessKey="xxx";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testVolunteerSignInByAccessKeyOk() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.accessKey="V-41eed0a4-0bbb-4594-a1cf-f8ab3ff810ec";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testVolunteerSignInByAccessKeyUnauthorized() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.accessKey="xxx";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isUnauthorized());
	}	

}
