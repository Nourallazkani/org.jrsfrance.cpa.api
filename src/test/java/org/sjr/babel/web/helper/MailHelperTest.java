package org.sjr.babel.web.helper;

import java.io.IOException;

import javax.mail.internet.InternetAddress;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.SpringConfig;
import org.sjr.babel.SpringConfig4Tests;
import org.sjr.babel.web.helper.MailHelper.MailBodyVars;
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
	
	@Test @Ignore
	public void testSendWithBodyVars() throws JsonProcessingException, IOException {
		ObjectMapper jackson = new ObjectMapper();
		JsonNode templates = jackson.readTree(getClass().getResourceAsStream("/mail-templates.json"));
		for(MailType mt : MailType.values()){
			for(String language : new String[]{"fr", "en", "ar", "prs"}){
				MailBodyVars mb = new MailBodyVars().add("var1", "??").add("var2", null);
				InternetAddress to = new InternetAddress("a@a.fr", "John Doe");
				MailCommand command = new MailCommand(mt, to, language, mb);
				SendMailOutcome resp = helper.send(command);
				
				String templateName = mt.name().toLowerCase().replace("_", "-");
				String expectedSubject = templates.get(templateName).get("subject").get(language).textValue();
				Assert.assertEquals(expectedSubject, resp.subject);
			}
		}
	}
	
	@Test @Ignore
	public void testSendWithoutBodyVars() throws JsonProcessingException, IOException {
		ObjectMapper jackson = new ObjectMapper();
		JsonNode templates = jackson.readTree(getClass().getResourceAsStream("/mail-templates.json"));
		for(MailType mt : MailType.values()){
			for(String language : new String[]{"fr", "en", "ar", "prs"}){
				InternetAddress to = new InternetAddress("a@a.fr", "John Doe");

				MailCommand command = new MailCommand(mt, to, language, null);
				SendMailOutcome resp = helper.send(command);
				
				String templateName = mt.name().toLowerCase().replace("_", "-");
				String expectedSubject = templates.get(templateName).get("subject").get(language).textValue();
				Assert.assertEquals(expectedSubject, resp.subject);
			}
		}
	}
}
