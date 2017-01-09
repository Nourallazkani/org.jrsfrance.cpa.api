package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.VolunteerEndpoint.VolunteerSummary;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class VolunteerEndpointTest extends AbstractEndpointTest {
	
	@Test
	public void testPostConflict() throws JsonProcessingException, Exception{
		VolunteerSummary input = new VolunteerSummary();
		input.address = new AddressSummary("19 rue Raspail", null, "94200", "Ivry sur seine", "France");
		input.firstName = "Nour";
		input.lastName = "Allazkani";
		input.mailAddress = "v@V.v";
		input.password = "azerty";
		
        String jsonWithoutPassword = jackson.writeValueAsString(input); // n'incluera pas le json. A partir de là plus de lien avec la classe RefugeeSummary.
        ObjectNode node = (ObjectNode) jackson.readTree(jsonWithoutPassword);
        node.put("password", "azerty");

        String jsonWithPassword = jackson.writeValueAsString(node);
		
		mockMvc.perform(post("/volunteers")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jsonWithPassword))
		.andExpect(status().isConflict());
	}
	
	@Test
	public void testPostOk() throws JsonProcessingException, Exception{
		VolunteerSummary input = new VolunteerSummary();
		input.address = new AddressSummary("19 rue Raspail", null, "94200", "Ivry sur seine", "France");
		input.firstName = "Nour";
		input.lastName = "Allazkani";
		input.mailAddress = "nn@Nn.n";
		input.password = "azerty";
		
        String jsonWithoutPassword = jackson.writeValueAsString(input); // n'incluera pas le json. A partir de là plus de lien avec la classe RefugeeSummary.
        ObjectNode node = (ObjectNode) jackson.readTree(jsonWithoutPassword);
        node.put("password", "azerty");

        String jsonWithPassword = jackson.writeValueAsString(node);
        
		mockMvc.perform(post("/volunteers")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jsonWithPassword))
		.andExpect(status().isOk());
	}
}
