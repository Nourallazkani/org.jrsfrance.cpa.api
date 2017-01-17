package org.sjr.babel.web.endpoint;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.sjr.babel.MyApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {MyApplication.class})
@TestPropertySource("classpath:application-for-tests.properties")
@WebAppConfiguration()
public abstract class AbstractEndpointTest {
	
	
	@Autowired
	protected WebApplicationContext context;

	protected MockMvc mockMvc;

	
	protected ObjectMapper jackson = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	
	
	@PersistenceContext
	protected EntityManager em;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

}
