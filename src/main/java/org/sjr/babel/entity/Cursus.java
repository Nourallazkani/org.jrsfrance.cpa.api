package org.sjr.babel.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
public class Cursus extends AbstractEntity {

	private String name;

	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Temporal(TemporalType.DATE)
	private Date endDate;

	@ManyToOne(fetch = FetchType.EAGER)
	private Organisation organisation;

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "cursus")
	@ElementCollection
	@OrderBy("startDate")
	@JsonInclude(Include.NON_EMPTY)
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
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

	@PrePersist
	@PreUpdate
	public void afterPropertiesSet() {
		if (courses != null) {
			this.startDate = this.courses.get(0).getStartDate();
			this.endDate = this.courses.get(courses.size() - 1).getEndDate();
		}
	}
}
