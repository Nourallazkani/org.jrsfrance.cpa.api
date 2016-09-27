package org.sjr.babel.web.helper;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MailHelper {

	private JavaMailSenderImpl sender;
	
	private ObjectMapper jackson = new ObjectMapper();
	
	public enum MailType{
		REFUGEE_SIGN_UP_CONFIRMATION, 
		REFUGEE_UPDATE_PASSWORD_CONFIRMATION,
		REFUGEE_RESET_PASSWORD,
		VOLUNTEER_SIGN_UP_CONFIRMATION,
		VOLUNTEER_UPDATE_PASSWORD_CONFIRMATION,
		VOLUNTEER_RESET_PASSWORD,
	}
	
	public static class MailMessageTemplate{
		public String from;
		public String subject;
		public String body;
		public String bodyUrl;
	}
	
	private Map<MailType, String> mailTemplates = new HashMap<>();
	
	@Autowired
	public MailHelper(Environment env) {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setDefaultEncoding("utf-8");
		
		sender.setHost(env.getProperty("mail.smtp.host"));
		sender.setPort(env.getProperty("mail.smtp.port", Integer.class));
		sender.setProtocol(env.getProperty("mail.smtp.protocol"));
		sender.setUsername(env.getProperty("mail.smtp.username"));
		sender.setPassword(env.getProperty("mail.smtp.password"));
		
		this.sender = sender;
		
		String prefix = env.getProperty("mail.template.location");
		this.mailTemplates.put(MailType.REFUGEE_SIGN_UP_CONFIRMATION, prefix+"/refugee-sign-up-confirmation.%s.json");
		this.mailTemplates.put(MailType.REFUGEE_UPDATE_PASSWORD_CONFIRMATION, prefix+"/refugee-update-password-confirmation.%s.json");
		this.mailTemplates.put(MailType.REFUGEE_RESET_PASSWORD, prefix+"/refugee-reset-password.%s.json");
		this.mailTemplates.put(MailType.VOLUNTEER_SIGN_UP_CONFIRMATION, prefix+"/volunteer-sign-up-confirmation.%s.json");
		this.mailTemplates.put(MailType.VOLUNTEER_UPDATE_PASSWORD_CONFIRMATION, prefix+"/volunteer-update-password-confirmation.%s.json");
		this.mailTemplates.put(MailType.VOLUNTEER_RESET_PASSWORD, prefix+"/volunteer-reset-password.%s.json");
	}
	
	public void send(MailType mailType, String language, String to, Object... args){
		if (to == null || to.trim().length() < 12) {
			return;
		}
		try {
			String urlTemplate = this.mailTemplates.get(mailType);
			
			URL url = new URL(String.format(urlTemplate, language));
			MailMessageTemplate template = this.jackson.readValue(url, MailMessageTemplate.class);
			
			MimeMessage mime = this.sender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mime, true);
			  
			helper.setTo(to);
			helper.setText(String.format(template.body, args), true);
			helper.setSubject(template.subject);
			helper.setFrom(template.from);
			this.sender.send(mime);
			
		} catch (IOException | MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}