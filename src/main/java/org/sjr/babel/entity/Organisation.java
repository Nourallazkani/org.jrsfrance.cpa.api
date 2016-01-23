package org.sjr.babel.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity //@Table(name="Organisation")
public class Organisation extends AbstractEntity{
	
	@Basic @Column(name="name")	
	private String  name;
	
	@Embedded
	private Address address;
	
	@Transient
	private String  description;
	
	@Transient
	private String password;
	
	@Transient
	private Boolean modeInscriptionAuto;
	
	@Transient
	private Date registrationDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getModeInscriptionAuto() {
		return modeInscriptionAuto;
	}

	public void setModeInscriptionAuto(Boolean modeInscriptionAuto) {
		this.modeInscriptionAuto = modeInscriptionAuto;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

}