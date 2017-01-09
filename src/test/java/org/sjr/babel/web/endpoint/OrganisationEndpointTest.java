package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
import org.sjr.babel.web.endpoint.AbstractEndpoint.ContactSummary;
import org.sjr.babel.web.endpoint.OrganisationEndpoint.OrganisationSummary;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OrganisationEndpointTest extends AbstractEndpointTest {
	
	@Test
	public void testPostByOrganisationCreated() throws JsonProcessingException, Exception{
		OrganisationSummary input = new OrganisationSummary();
		input.address = new AddressSummary("19 rue Raspail", null, "94200", "Ivry sur seine", "France");
		input.contact = new ContactSummary("nour", "nour@nour.nour", "07123123");
		input.category = "université";
		input.mailAddress = "azerty1234@cpafrance.fr";
		input.password = "azerty";
		input.name = "Humans for womens";

        String jsonWithoutPassword = jackson.writeValueAsString(input); // n'incluera pas le json. A partir de là plus de lien avec la classe RefugeeSummary.
        ObjectNode node = (ObjectNode) jackson.readTree(jsonWithoutPassword);
        node.put("password", "azerty");

        String jsonWithPassword = jackson.writeValueAsString(node);
		
		mockMvc.perform(post("/organisations")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jsonWithPassword))
		.andExpect(status().isCreated());
	}
	
	@Test
	public void testPostByAdminCreated() throws JsonProcessingException, Exception{
		OrganisationSummary input = new OrganisationSummary();
		input.address = new AddressSummary("19 rue Raspail", null, "94200", "Ivry sur seine", "France");
		input.contact = new ContactSummary("nour", "nour@nour.nour", "07123123");
		input.category = "université";
		input.mailAddress = "azerty4567@cpafrance.fr";
		input.name = "Humans for womens";


		mockMvc.perform(post("/organisations")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("accessKey", "A-cfa5be9e-c8dd-477c-9d3d-a3cb452be3f6")
				.content(jackson.writeValueAsString(input)))
		.andExpect(status().isCreated());
	}	
	
	@Test
	public void testPostConflict() throws JsonProcessingException, Exception{
		OrganisationSummary input = new OrganisationSummary();
		input.address = new AddressSummary("27 Rue Saint-Guillaume", null, "75007", "Paris", "France");
		input.contact = new ContactSummary("nour", "nour@nour.nour", "07123123");
		input.category = "Université";
		input.mailAddress = "o@O.o";
		input.password = "azerty";
		input.name = "Science Po";
		
        String jsonWithoutPassword = jackson.writeValueAsString(input); // n'incluera pas le json. A partir de là plus de lien avec la classe RefugeeSummary.
        ObjectNode node = (ObjectNode) jackson.readTree(jsonWithoutPassword);
        node.put("password", "azerty");

        String jsonWithPassword = jackson.writeValueAsString(node);
		
		mockMvc.perform(post("/organisations")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(jsonWithPassword))
		.andExpect(status().isConflict());
	}

}
