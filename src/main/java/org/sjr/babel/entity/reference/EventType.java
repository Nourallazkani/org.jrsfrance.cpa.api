package org.sjr.babel.entity.reference;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.sjr.babel.entity.AbstractEntity.CacheOnStartup;
import org.sjr.babel.entity.AbstractReferenceEntity;

@Entity @Cacheable @CacheOnStartup(order = 0)
public class EventType extends AbstractReferenceEntity {
	public enum Stereotype {
		WORKSHOP
	}
	
	@Enumerated(EnumType.STRING)
	private Stereotype stereotype;

	public Stereotype getStereotype() {
		return stereotype;
	}

	public void setStereotype(Stereotype stereotype) {
		this.stereotype = stereotype;
	}

}
