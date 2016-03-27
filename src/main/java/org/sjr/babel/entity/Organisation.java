package org.sjr.babel.entity;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.sjr.babel.entity.reference.OrganisationCategory;

@Entity //@Table(name="Organisation")
@Cacheable
public class Organisation extends AbstractEntity{
	
	@Basic @Column(name="name")	
	private String  name;
	
	@Embedded
	private Address address;
	
	private String password;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private OrganisationCategory category;
	
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public OrganisationCategory getCategory() {
		return category;
	}

	public void setCategory(OrganisationCategory category) {
		this.category = category;
	}

}