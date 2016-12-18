package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.Test;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.RefugeeEndpoint.RefugeeSummary;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

public class RefugeeEndpointTest extends AbstractEndpointTest{
		
	@Test
	public void testPostOk() throws JsonProcessingException, Exception{
		RefugeeSummary input = new RefugeeSummary();
		input.address = new AddressSummary("19 rue Raspail", null, "94200", "Ivry sur seine", "France");
		input.birthDate = new Date();
		input.civility = "Mr";
		input.firstName= "Nour";
		input.lastName = "Allazkani";
		input.fieldOfStudy = "Informatique";
		input.mailAddress = "n@n.n";
		input.password = "azerty";
		
		mockMvc.perform(post("/refugees")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testPostConflict() throws JsonProcessingException, Exception{
		RefugeeSummary input = new RefugeeSummary();
		input.address = new AddressSummary("1 rue de Rivoli", null, "75007", "Paris", "France");
		input.birthDate = new Date();
		input.civility = "Mr";
		input.firstName= "Nour";
		input.lastName = "Allazkani";
		input.fieldOfStudy = "Informatique";
		input.mailAddress = "r@R.r";
		input.password = "azerty";
		
		mockMvc.perform(post("/refugees")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isConflict());
	}
	

}
