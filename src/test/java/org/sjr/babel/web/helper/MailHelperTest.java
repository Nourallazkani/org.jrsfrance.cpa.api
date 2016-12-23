package org.sjr.babel.web.helper;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.SpringConfig4Tests;
import org.sjr.babel.SpringConfig;
import org.sjr.babel.web.helper.MailHelper.MailCommand;
import org.sjr.babel.web.helper.MailHelper.MailType;
import org.sjr.babel.web.helper.MailHelper.SendMailOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringConfig.class, SpringConfig4Tests.class})
public class MailHelperTest {

	@Autowired
	private MailHelper helper;
	
	@Test
	public void testSend() {
		MailCommand command = new MailCommand(MailType.REFUGEE_SIGN_UP_CONFIRMATION, "Alaric", "a@a.fr", "fr", "a@a.fr", "password");
		SendMailOutcome resp = helper.send(command);
		assertEquals("Bienvenue sur le site CPA", resp.subject);
	}

}
