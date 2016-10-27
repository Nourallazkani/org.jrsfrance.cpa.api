package org.sjr.babel.model.component;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;


public class Contact implements Serializable {

	private static final long serialVersionUID = 3271702525941084890L;

	@Converter
	public static class ContactConverter implements javax.persistence.AttributeConverter<Contact, String>{

		private static ObjectMapper jackson = new ObjectMapper();

		@Override
		public String convertToDatabaseColumn(Contact attribute) {
			try {
				return attribute==null ? null : jackson.writeValueAsString(attribute);
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		public Contact convertToEntityAttribute(String dbData) {
			try {
				return dbData==null ? null : jackson.readValue(dbData, Contact.class);
			} catch (IOException e) {
				return null;
			}
		}
		
	}
	private String name, phoneNumber, mailAddress;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

}
