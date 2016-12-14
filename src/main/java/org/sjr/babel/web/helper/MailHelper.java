package org.sjr.babel.web.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
	}

	private JsonNode templates;
	
	@Autowired
	public MailHelper(Environment env) throws JsonProcessingException, IOException {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setDefaultEncoding("UTF-8");
		
		sender.setHost(env.getProperty("mail.smtp.host"));
		sender.setPort(env.getProperty("mail.smtp.port", Integer.class));
		sender.setProtocol(env.getProperty("mail.smtp.protocol"));
		sender.setUsername(env.getProperty("mail.smtp.username"));
		sender.setPassword(env.getProperty("mail.smtp.password"));
		
		this.sender = sender;
		ObjectMapper jackson = new ObjectMapper();
		this.templates = jackson.readTree(getClass().getResourceAsStream("/mail-templates.json"));
	}
	
	public static class SendMailResponse{
		public String body,subject;
		public boolean sent;
	}
	
	public SendMailResponse send(MailType mailType, String language, String to, Object... args){

		try {
			JsonNode template = templates.get(mailType.name().toLowerCase().replace("_", "-"));
			
			String from = template.get("from").textValue();
			String subject = template.get("subject").get(language).textValue();
			String body;
			if(template.has("body")){
				String bodyTemplate = template.get("body").get(language).textValue();
				body = String.format(bodyTemplate, args);
			}
			else{
				String bodyUrl = template.get("bodyUrl").get(language).asText();
				
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
			  
			helper.setTo(to);
			helper.setText(body, true);
			helper.setSubject(subject);
			helper.setFrom(from);
			SendMailResponse response = new SendMailResponse();
			
			response.body = body;
			response.subject = subject;
			
			if (to == null || to.trim().length() < 12) {
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