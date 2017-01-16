package org.sjr.babel.web.endpoint;

import static org.hamcrest.core.StringStartsWith.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.Test;
import org.sjr.babel.model.Gender;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.RefugeeEndpoint.RefugeeSummary;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RefugeeEndpointTest extends AbstractEndpointTest{
		
	@Test
	public void testPostOk() throws JsonProcessingException, Exception{
		RefugeeSummary input = new RefugeeSummary();
		input.address = new AddressSummary("19 rue Raspail", null, "94200", "Ivry sur seine", "France");
		input.birthDate = LocalDate.of(1979, 8, 15);
		input.gender = Gender.MAN.name();
		input.firstName= "Nour";
		input.lastName = "Allazkani";
		input.fieldOfStudy = "Informatique";
		input.mailAddress = "n@n.n";
		
	       
        String jsonWithoutPassword = jackson.writeValueAsString(input); // n'incluera pas le json. A partir de là plus de lien avec la classe RefugeeSummary.
        ObjectNode node = (ObjectNode) jackson.readTree(jsonWithoutPassword);
        node.put("password", "azerty");

        String jsonWithPassword = jackson.writeValueAsString(node);

		mockMvc.perform(post("/refugees")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jsonWithPassword))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.accessKey").value(startsWith("R-")))
		;
	}
	
	@Test
	public void testPostConflict() throws JsonProcessingException, Exception{
		RefugeeSummary input = new RefugeeSummary();
		input.address = new AddressSummary("1 rue de Rivoli", null, "75007", "Paris", "France");
		input.birthDate = LocalDate.of(1979, 8, 15);
		input.gender= Gender.MAN.name();
		input.firstName= "Nour";
		input.lastName = "Allazkani";
		input.fieldOfStudy = "Informatique";
		input.mailAddress = "r@R.r";
		input.password = "azerty";
		
        String jsonWithoutPassword = jackson.writeValueAsString(input); // n'incluera pas le json. A partir de là plus de lien avec la classe RefugeeSummary.
        ObjectNode node = (ObjectNode) jackson.readTree(jsonWithoutPassword);
        node.put("password", "azerty");

        String jsonWithPassword = jackson.writeValueAsString(node);
        
		mockMvc.perform(post("/refugees")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jsonWithPassword))
		.andExpect(status().isConflict());
	}
	

}
