package org.sjr.babel.entity;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import org.sjr.babel.entity.AbstractEntity.CacheOnStartup;
import org.sjr.babel.entity.Contact.ContactConverter;
import org.sjr.babel.entity.reference.OrganisationCategory;

@Entity // @Table(name="Organisation")
@Cacheable
@CacheOnStartup(order = 1)
public class Organisation extends AbstractEntity {

	@Basic
	private String name, userName;

	@Convert(converter = ContactConverter.class)
	private Contact contact;

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

	public Contact getContact() {
		return contact;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String mailAddress) {
		this.userName = mailAddress;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
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

	@PrePersist
	public void prePersist() {
		if (this.account == null) {
			setAccount(new Account());
		}
		getAccount().setAccessKey("O-" + UUID.randomUUID().toString());

	}

}