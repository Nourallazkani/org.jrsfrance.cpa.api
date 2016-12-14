package org.sjr.babel.web.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sjr.babel.JpaConfig4Tests;
import org.sjr.babel.WebAppInitializer.RestConfiguration;
import org.sjr.babel.web.helper.MailHelper.MailType;
import org.sjr.babel.web.helper.MailHelper.SendMailOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestConfiguration.class, JpaConfig4Tests.class})
public class MailHelperTest {

	@Autowired
	private MailHelper helper;
	
	@Test
	public void testSend() {
		SendMailOutcome resp = helper.send(MailType.REFUGEE_SIGN_UP_CONFIRMATION, "fr", "a@x.fr", "x", "y");
		assertEquals("Bienvenue sur le site CPA", resp.subject);
	}

}
