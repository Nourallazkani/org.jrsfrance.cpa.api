package org.sjr.babel.entity;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity @Cacheable //@Table(name= "Cursus")
public class Cursus extends AbstractEntity {
	
	private String name;
	
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="organisation_id")
	private Organisation org;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="cursus")
	@JsonIgnore
	private List<Course> courses;
	
	@Embedded
	private Address address;
	@ManyToOne
	private Level level;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Organisation getOrg() {
		return org;
	}

	public void setOrg(Organisation org) {
		this.org = org;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

}
