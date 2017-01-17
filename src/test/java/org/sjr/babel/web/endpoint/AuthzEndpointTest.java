package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.MyApplication;
import org.sjr.babel.web.endpoint.AuthzEndpoint.SignInCommand;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {MyApplication.class})
@WebAppConfiguration()
public class AuthzEndpointTest extends AbstractEndpointTest {
	
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
	public void testRefugeeSignInByAccessKeyOk() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.accessKey="R-a871ce00-e7d2-497e-8a4e-d272b8b5b520";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isOk());
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
	public void testSignInByAccessKeyUnauthorized() throws JsonProcessingException, Exception {
		SignInCommand input = new SignInCommand();
		input.accessKey="xxx";
		mockMvc.perform(post("/authentication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isUnauthorized());
	}
	

}
