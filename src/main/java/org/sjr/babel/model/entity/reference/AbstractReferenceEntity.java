package org.sjr.babel.model.entity.reference;

import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;

import org.sjr.babel.model.component.MultiLanguageText;
import org.sjr.babel.model.component.MultiLanguageText.MultiLanguageTextConverter;
import org.sjr.babel.model.entity.AbstractEntity;

@MappedSuperclass
public abstract class AbstractReferenceEntity extends AbstractEntity {

	private String name;
	
	@Convert(converter = MultiLanguageTextConverter.class)
	private MultiLanguageText nameI18n;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MultiLanguageText getNameI18n() {
		return nameI18n;
	}

	public void setNameI18n(MultiLanguageText nameI18n) {
		this.nameI18n = nameI18n;
	}
	
	
	
	/*
	@Entity @Cacheable //@Table(name="Country")
	public static class Country extends AbstractReferenceEntity {}
	
	@Entity @Cacheable
	public static class Language extends AbstractReferenceEntity {}
	
	@Entity @Cacheable
	public static class Civility extends AbstractReferenceEntity {}
	
	@Entity @Cacheable
	public static class OrganisationCategory extends AbstractReferenceEntity{}
	*/
}
