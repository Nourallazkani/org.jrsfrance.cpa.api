package org.sjr.babel.web.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

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
	}

	public static class MailCommand{
		public String recipientName, recipientMailAddress, language;
		public MailType type;
		public Object[] bodyVars;
		
		public MailCommand() {}
		
		public MailCommand(MailType type, String recipientName, String recipientMailAddress, String language, Object... bodyVars) {
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
	
	public SendMailOutcome send(MailCommand command){

		try {
			JsonNode template = templates.get(command.type.name().toLowerCase().replace("_", "-"));
			String from = template.get("from").textValue();
			String subject = template.get("subject").get(command.language).textValue();
			String body;
			if(template.has("bodyUrl")){
				String bodyUrl = template.get("bodyUrl").get(command.language).asText();
				
				HttpURLConnection connection = null;
				try{
					connection = (HttpURLConnection) new URL(bodyUrl).openConnection();
					InputStream in = connection.getInputStream();
					body = StreamUtils.copyToString(in, Charset.defaultCharset());
					in.close();
				}
				catch(Exception e){
					e.printStackTrace();
					body  = null;
				}
				finally 
			    {
					connection.disconnect();   
			    }
			}
			else{
				String bodyTemplate = template.get("body").get(command.language).textValue();
				body = command.bodyVars.length>0 ? String.format(bodyTemplate, command.bodyVars) : bodyTemplate;
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