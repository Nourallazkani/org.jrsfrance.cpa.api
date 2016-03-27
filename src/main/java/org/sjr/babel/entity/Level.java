package org.sjr.babel.entity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

import org.sjr.babel.entity.AbstractEntity.CacheOnStartup;

@Entity @Cacheable @CacheOnStartup(order = 0)
public class Level extends AbstractEntity {

	private String name, description;
	
	//private Level next, previous;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
