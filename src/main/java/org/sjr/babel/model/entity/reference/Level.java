package org.sjr.babel.model.entity.reference;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

import org.sjr.babel.model.entity.AbstractEntity.CacheOnStartup;

@Entity @Cacheable @CacheOnStartup(order = 0)
public class Level extends AbstractReferenceEntity {

	private String description;
	
	//private Level next, previous;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
/*
	public Level getNext() {
		return next;
	}

	public void setNext(Level next) {
		this.next = next;
	}

	public Level getPrevious() {
		return previous;
	}

	public void setPrevious(Level previous) {
		this.previous = previous;
	} */
}
