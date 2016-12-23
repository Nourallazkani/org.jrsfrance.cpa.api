package org.sjr.babel.web.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
	}

	public static class MailCommand{
		public String recipientName, recipientMailAddress, language;
		public MailType type;
		public Object[] bodyVars;
		
		public MailCommand(MailType type, String recipientName, String recipientMailAddress, String language, Object... bodyVars) {
			super();
			this.recipientName = recipientName;
			this.recipientMailAddress = recipientMailAddress;
			this.language = language;
			this.type = type;
			this.bodyVars = bodyVars;
		}
	}
	
	private JsonNode templates;
	
	public MailHelper() throws JsonProcessingException, IOException {
		ObjectMapper jackson = new ObjectMapper();
		this.templates = jackson.readTree(getClass().getResourceAsStream("/mail-templates.json"));
	}
	
	public static class SendMailOutcome{
		public String body,subject;
		public boolean sent;
	}
	
	public SendMailOutcome send(MailCommand command){

		try {
			JsonNode template = templates.get(command.type.name().toLowerCase().replace("_", "-"));
			
			String from = template.get("from").textValue();
			String subject = template.get("subject").get(command.language).textValue();
			String body;
			if(template.has("body")){
				String bodyTemplate = template.get("body").get(command.language).textValue();
				body = String.format(bodyTemplate, command.bodyVars);
			}
			else{
				String bodyUrl = template.get("bodyUrl").get(command.language).asText();
				
				HttpURLConnection connection = null;
				try{
					connection = (HttpURLConnection) new URL(bodyUrl).openConnection();
					InputStream in = connection.getInputStream();
					body = StreamUtils.copyToString(in, Charset.defaultCharset());
					in.close();
				}
				catch(Exception e){
					body  = null;
				}
				finally 
			    {
					connection.disconnect();   
			    }				
			}
			
			MimeMessage mime = this.sender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mime, true);
			  
			helper.setTo(command.recipientMailAddress);
			helper.setText(body, true);
			helper.setSubject(subject);
			helper.setFrom(from);
			SendMailOutcome response = new SendMailOutcome();
			
			response.body = body;
			response.subject = subject;
			
			if (command.recipientMailAddress == null || command.recipientMailAddress.trim().length() < 12) {
				response.sent = false;
			}
			else{
				this.sender.send(mime);	
				response.sent = true;
			}
			return response;
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}