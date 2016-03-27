package org.sjr.babel.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface CacheOnStartup{
		int order();
		
	}
}
