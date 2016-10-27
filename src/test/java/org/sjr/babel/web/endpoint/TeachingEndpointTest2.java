package org.sjr.babel.web.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration()
@ContextHierarchy(@ContextConfiguration(classes = RestConfiguration.class))
public class TeachingEndpointTest2 {
	
	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	
	@Test
	public void testGetTeachings() throws Exception{
		mockMvc.perform(get("/teachings")).andExpect(status().isOk());
	}
	
	@Test
	public void testGetTeachingOk() throws Exception{
		mockMvc.perform(get("/teachings/1")).andExpect(status().isOk());
	}
	
	@Test
	public void testGetTeachingNotFound() throws Exception{
		mockMvc.perform(get("/teachings/123")).andExpect(status().isNotFound());
	}
}
