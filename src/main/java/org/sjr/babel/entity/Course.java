package org.sjr.babel.entity;

import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
@Entity //@Table(name="Course")
public class Course extends AbstractEntity {

	
	private String name;
	@Transient
	private Date startDate;
	@Transient
	private Date endDate;
	@ManyToOne (fetch=FetchType.EAGER) //@JoinColumn(name="cursus_id")
	private Cursus cursus;
	
	@Embedded
	private Address address;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}



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

	public Cursus getCursus() {
		return cursus;
	}

	public void setCursus(Cursus cursus) {
		this.cursus = cursus;
	}

}
