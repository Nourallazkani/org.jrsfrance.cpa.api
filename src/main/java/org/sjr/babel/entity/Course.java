package org.sjr.babel.entity;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
@Embeddable//@Entity //@Table(name="Course")
public class Course {

	@Temporal(TemporalType.DATE)
	private Date startDate;
	
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	@ManyToOne
	private Level level;
	
	//@ManyToOne (fetch=FetchType.EAGER) //@JoinColumn(name="cursus_id")
	//private Cursus cursus;
	
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	@Embedded
	@Transient
	private Place address;

	public Place getAddress() {
		return address;
	}

	public void setAddress(Place address) {
		this.address = address;
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
/*
	public Cursus getCursus() {
		return cursus;
	}

	public void setCursus(Cursus cursus) {
		this.cursus = cursus;
	}
	*/

}
