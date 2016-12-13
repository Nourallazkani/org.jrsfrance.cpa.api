package org.sjr.babel.model.component;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;

//@Embeddable
public class MultiLanguageText implements Serializable {

	private static final long serialVersionUID = 2003082058174806956L;

	@Converter()
	public static class MultiLanguageTextConverter implements javax.persistence.AttributeConverter<MultiLanguageText, String>{

		private static ObjectMapper jackson = new ObjectMapper();

		@Override
		public String convertToDatabaseColumn(MultiLanguageText attribute) {
			try {
				return attribute==null ? null : jackson.writeValueAsString(attribute);
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		public MultiLanguageText convertToEntityAttribute(String dbData) {
			try {
				return dbData==null ? null : jackson.readValue(dbData, MultiLanguageText.class);
			} catch (IOException e) {
				return null;
			}
		}
		
	}
	
	private String textEn, textPrs, textAr;

	public String getTextEn() {
		return textEn;
	}

	public void setTextEn(String textEn) {
		this.textEn = textEn;
	}

	public String getTextPrs() {
		return textPrs;
	}

	public void setTextPrs(String textPrs) {
		this.textPrs = textPrs;
	}

	public String getTextAr() {
		return textAr;
	}

	public void setTextAr(String textAr) {
		this.textAr = textAr;
	}

	public String getText(String language) {
		String text = null;
		if("en".equals(language)){
			text = this.textEn;
		}
		else if("ar".equals(language)){
			text = this.textAr;
		}
		else if("prs".equals(language)){
			text = this.textPrs;
		}
		
		return text;
	}
}
