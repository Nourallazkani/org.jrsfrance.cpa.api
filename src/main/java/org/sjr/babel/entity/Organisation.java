package org.sjr.babel.entity;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.sjr.babel.entity.AbstractEntity.CacheOnStartup;
import org.sjr.babel.entity.reference.OrganisationCategory;

@Entity // @Table(name="Organisation")
@Cacheable
@CacheOnStartup(order = 1)
public class Organisation extends AbstractEntity {

	@Basic
	@Column(name = "name")
	private String name;

	private String contact, phoneNumber, mailAddress;

	@Embedded
	private Address address;

	@Embedded
	private Account account;

	@ManyToOne(fetch = FetchType.EAGER)
	private OrganisationCategory category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public OrganisationCategory getCategory() {
		return category;
	}

	public void setCategory(OrganisationCategory category) {
		this.category = category;
	}

}