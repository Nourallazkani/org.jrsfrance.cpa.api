package org.sjr.babel.web.endpoint;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.sjr.babel.JpaConfig4Tests;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration()
@ContextConfiguration(classes = {RestConfiguration.class, JpaConfig4Tests.class})
//@ContextHierarchy(@ContextConfiguration(classes = {RestConfiguration.class, JpaConfig4Tests.class}))
//@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class AbstractEndpointTest {
	
	@Autowired
	public WebApplicationContext context;

	public MockMvc mockMvc;

	public ObjectMapper jackson = new ObjectMapper();
	
	@PersistenceContext
	public EntityManager em;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

}
