package org.sjr.babel.web.helper;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.SpringConfig;
import org.sjr.babel.SpringConfig4Tests;
import org.sjr.babel.web.helper.MailHelper.MailCommand;
import org.sjr.babel.web.helper.MailHelper.MailType;
import org.sjr.babel.web.helper.MailHelper.SendMailOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringConfig.class, SpringConfig4Tests.class})
public class MailHelperTest {

	@Autowired
	private MailHelper helper;
	
	@Test
	public void testSend() throws JsonProcessingException, IOException {
		ObjectMapper jackson = new ObjectMapper();
		JsonNode templates = jackson.readTree(getClass().getResourceAsStream("/mail-templates.json"));
		for(MailType mt : MailType.values()){
			for(String language : new String[]{"fr", "en", "ar", "prs"}){
				MailCommand command = new MailCommand(mt, "Alaric", "a@a.fr", language, null);
				SendMailOutcome resp = helper.send(command);
				
				String templateName = mt.name().toLowerCase().replace("_", "-");
				String expectedSubject = templates.get(templateName).get("subject").get(language).textValue();
				Assert.assertEquals(expectedSubject, resp.subject);
			}
		}
	}
}
