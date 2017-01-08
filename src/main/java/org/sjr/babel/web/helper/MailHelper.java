package org.sjr.babel.web.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.sjr.babel.SpringConfig.MailSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MailHelper {

	private JavaMailSenderImpl sender;
		
	public enum MailType{
		REFUGEE_SIGN_UP_CONFIRMATION, 
		REFUGEE_UPDATE_PASSWORD_CONFIRMATION,
		REFUGEE_RESET_PASSWORD,
		
		VOLUNTEER_SIGN_UP_CONFIRMATION,
		VOLUNTEER_UPDATE_PASSWORD_CONFIRMATION,
		VOLUNTEER_RESET_PASSWORD,
		
		ORGANISATION_SIGN_UP_CONFIRMATION,
		ORGANISATION_UPDATE_PASSWORD_CONFIRMATION,
		ORGANISATION_RESET_PASSWORD,
		
		TEACHING_REGISTRATION_REQUEST,
		TEACHING_REGISTRATION_REQUEST_ACCEPTED,
		TEACHING_REGISTRATION_REQUEST_NOT_ACCEPTED,
		
		LANGUAGE_LEARNING_PROGRAM_REGISTRATION_REQUEST,
		LANGUAGE_LEARNING_PROGRAM_REGISTRATION_REQUEST_ACCEPTED,
		LANGUAGE_LEARNING_PROGRAM_REGISTRATION_REQUEST_NOT_ACCEPTED,
		
		PROFESSIONAL_LEARNING_PROGRAM_REGISTRATION_REQUEST,
		PROFESSIONAL_LEARNING_PROGRAM_REGISTRATION_REQUEST_ACCEPTED,
		PROFESSIONAL_LEARNING_PROGRAM_REGISTRATION_REQUEST_NOT_ACCEPTED,
		
		WORKSHOP_REGISTRATION_REQUEST,
		WORKSHOP_REGISTRATION_REQUEST_ACCEPTED,
		WORKSHOP_REGISTRATION_REQUEST_NOT_ACCEPTED,
		
		EVENT_REGISTRATION_REQUEST,
		EVENT_REGISTRATION_REQUEST_ACCEPTED,
		EVENT_REGISTRATION_REQUEST_NOT_ACCEPTED,
		
		MEETING_REQUEST_ACCEPTED,
		MEETING_REQUEST_CANCELED
	}


	@SuppressWarnings("serial")
	public static class MailBodyVars extends HashMap<String, Object>{
		public MailBodyVars add(String key, Object value){
			put(key, value);
			return this;
		}
	}
	public static class MailCommand{
		public String recipientName, recipientMailAddress, language;
		public MailType type;
		public MailBodyVars bodyVars;
		
		public MailCommand() {}
		
		public MailCommand(MailType type, String recipientName, String recipientMailAddress, String language, MailBodyVars bodyVars) {
			super();
			this.recipientName = recipientName;
			this.recipientMailAddress = recipientMailAddress;
			this.language = language;
			this.type = type;
			this.bodyVars = bodyVars;
		}
	}

	public static class SendMailOutcome{
		public InternetAddress to;
		public String body,subject;
		public boolean sent;
	}
	
	private JsonNode templates;
	
	private boolean mock;
	
	@Autowired
	public MailHelper(MailSettings mailSettings) throws JsonProcessingException, IOException {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setDefaultEncoding(mailSettings.defaultEncoding);
		sender.setHost(mailSettings.smtpHost);
		sender.setPort(mailSettings.port);
		sender.setProtocol(mailSettings.smtpProtocol);
		sender.setUsername(mailSettings.smtpUsername);
		sender.setPassword(mailSettings.smtpPassword);
		
		this.sender = sender;
		this.mock = mailSettings.mock;
		ObjectMapper jackson = new ObjectMapper();
		this.templates = jackson.readTree(getClass().getResourceAsStream("/mail-templates.json"));
	}
	
	@SuppressWarnings("el-syntax")
	public SendMailOutcome send(MailCommand command){

		try {
			JsonNode template = templates.get(command.type.name().toLowerCase().replace("_", "-"));
			String from = template.get("from").textValue();
			String subject = template.get("subject").get(command.language).textValue();
			String bodyTemplate;
			
			
			if(template.has("bodyUrl")){
				String bodyUrl = template.get("bodyUrl").get(command.language).asText();
				
				HttpURLConnection connection = null;
				try{
					connection = (HttpURLConnection) new URL(bodyUrl).openConnection();
					InputStream in = connection.getInputStream();
					bodyTemplate = StreamUtils.copyToString(in, Charset.defaultCharset());
					in.close();
				}
				catch(Exception e){
					e.printStackTrace();
					bodyTemplate  = null;
				}
				finally 
			    {
					connection.disconnect();   
			    }
			}
			else{
				bodyTemplate = template.get("body").get(command.language).textValue();
			}
			String body = null;
			if(command.bodyVars != null && !command.bodyVars.isEmpty()){
				for(Map.Entry<String, Object> entry : command.bodyVars.entrySet()){
					body = bodyTemplate.replace(String.format("${%s}", entry.getKey()), entry.getValue().toString());
				}
			}
			else{
				body = bodyTemplate;
			}
			MimeMessage mime = this.sender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mime, true);
			 
			InternetAddress to = new InternetAddress(String.format("%s <%s>", command.recipientName, command.recipientMailAddress));
			helper.setTo(to);
			helper.setText(body, true);
			helper.setSubject(subject);
			helper.setFrom(from);
			
			SendMailOutcome response = new SendMailOutcome();
			response.to = to;
			response.body = body;
			response.subject = subject;
			
			if (command.recipientMailAddress == null || command.recipientMailAddress.trim().length() < 12) {
				response.sent = false;
			}
			else{
				if(!this.mock){
					this.sender.send(mime);						
				}
				response.sent = true;
			}
			return response;
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}