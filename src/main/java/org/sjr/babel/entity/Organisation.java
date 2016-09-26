package org.sjr.babel.entity;

import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.sjr.babel.entity.AbstractEntity.CacheOnStartup;
import org.sjr.babel.entity.Contact.ContactConverter;
import org.sjr.babel.entity.reference.OrganisationCategory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity // @Table(name="Organisation")
@Cacheable
@CacheOnStartup(order = 1)
public class Organisation extends AbstractEntity {

	@Basic
	private String name, mailAddress;

	@Convert(converter = ContactConverter.class)
	private Contact contact;

	@Embedded
	private Address address;

	@Embedded
	private Account account;

	@ManyToOne(fetch = FetchType.EAGER)
	private OrganisationCategory category;
	
	@Convert(converter=MapConverter.class)
	private Map<String, String> additionalInformations;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Contact getContact() {
		return contact;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public OrganisationCategory getCategory() {
		return category;
	}

	public void setCategory(OrganisationCategory category) {
		this.category = category;
	}

	public Map<String, String> getAdditionalInformations() {
		return additionalInformations;
	}

	public void setAdditionalInformations(Map<String, String> additionalInformations) {
		this.additionalInformations = additionalInformations;
	}
	

	@Converter
	public static class MapConverter implements javax.persistence.AttributeConverter<Map<String,String>, String>{

		private static ObjectMapper jackson = new ObjectMapper();
		@Override
		public String convertToDatabaseColumn(Map<String, String> attribute) {
			try {
				return jackson.writeValueAsString(attribute);
			} catch (JsonProcessingException e) {
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, String> convertToEntityAttribute(String dbData) {
			try{
				return jackson.readValue(dbData, Map.class);
			}catch(Exception e){
				return null;
			}
		}
	}
}