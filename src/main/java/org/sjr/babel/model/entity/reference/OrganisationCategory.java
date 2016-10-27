package org.sjr.babel.model.entity.reference;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.sjr.babel.model.entity.AbstractEntity.CacheOnStartup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity @Cacheable @CacheOnStartup(order = 0)
public class OrganisationCategory extends AbstractReferenceEntity {
	public enum Stereotype {
		LIBRARY, UNIVERSITY, NGO
	}
	
	@Enumerated(EnumType.STRING)
	private Stereotype stereotype;

	@Convert(converter=ListConverter.class)
	private List<String> additionalInformations;
	
	public Stereotype getStereotype() {
		return stereotype;
	}

	public void setStereotype(Stereotype stereotype) {
		this.stereotype = stereotype;
	}
	
	public List<String> getAdditionalInformations() {
		return additionalInformations;
	}

	public void setAdditionalInformations(List<String> additionalInformations) {
		this.additionalInformations = additionalInformations;
	}

	@Converter
	public static class ListConverter implements javax.persistence.AttributeConverter<List<String>, String>{

		private static ObjectMapper jackson = new ObjectMapper();
		@Override
		public String convertToDatabaseColumn(List<String> attribute) {
			try {
				return jackson.writeValueAsString(attribute);
			} catch (JsonProcessingException e) {
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<String> convertToEntityAttribute(String dbData) {
			try{
				return jackson.readValue(dbData, List.class);
			}catch(Exception e){
				return null;
			}
		}	
	}
}
