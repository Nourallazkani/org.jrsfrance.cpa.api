package org.sjr.babel.model.entity.reference;

import javax.persistence.MappedSuperclass;

import org.sjr.babel.model.entity.AbstractEntity;

@MappedSuperclass
public abstract class AbstractReferenceEntity extends AbstractEntity {

	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
