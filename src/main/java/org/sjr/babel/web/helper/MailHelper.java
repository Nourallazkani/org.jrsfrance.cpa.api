package org.sjr.babel.web.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MailHelper {

	@Autowired
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
		public InternetAddress to, replyTo;
		public String language;
		public MailType type;
		public MailBodyVars bodyVars;
		
		public MailCommand() {}
		
		public MailCommand(MailType type, InternetAddress to, String language, MailBodyVars bodyVars) {
			super();
			this.to = to;
			this.language = language;
			this.type = type;
			this.bodyVars = bodyVars;
		}
		public MailCommand(MailType type, InternetAddress to, InternetAddress replyTo, String language, MailBodyVars bodyVars) {
			this(type, to, language, bodyVars);
			this.replyTo = replyTo;
		}		
	}

	public static class SendMailOutcome{
		public InternetAddress to;
		public String body,subject;
		public boolean sent;
	}
	
	private JsonNode templates;
	
	private boolean mock;

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@PostConstruct
	public void initialize() throws JsonProcessingException, IOException{
		if(this.sender.getJavaMailProperties()!=null && this.sender.getJavaMailProperties().containsKey("mock")){
			this.mock = this.sender.getJavaMailProperties().getProperty("mock").equals("true");	
		}
		if(this.mock){
			logger.info("won't send mail, mock=true");
		}

		ObjectMapper jackson = new ObjectMapper();
		this.templates = jackson.readTree(getClass().getResourceAsStream("/mail-templates.json"));
	}
	
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
				if(!StringUtils.hasText(bodyTemplate)){
					try{
						body = new ObjectMapper().writeValueAsString(command.bodyVars);
					}
					catch(Exception e){	}
				}
				else{
					for(Map.Entry<String, Object> entry : command.bodyVars.entrySet()){
						String value = entry.getValue() == null ? "" : entry.getValue().toString();
						String toReplace = "${"+entry.getKey()+"}";
						body = bodyTemplate.replace(toReplace, value);
					}
				}
			}
			else{
				body = bodyTemplate;
			}
			MimeMessage mime = this.sender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mime, true);
			 
			if(command.replyTo!=null){
				helper.setReplyTo(command.replyTo);
			}
			helper.setTo(command.to);
			helper.setText(body, true);
			helper.setSubject(subject);
			helper.setFrom(from);
			if(command.replyTo != null){
				helper.setReplyTo(command.replyTo);
			}
			
			SendMailOutcome response = new SendMailOutcome();
			response.to = command.to;
			response.body = body;
			response.subject = subject;
			
			if (command.to == null || command.to.getAddress().trim().length() < 12) {
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