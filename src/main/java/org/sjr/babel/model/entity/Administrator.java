package org.sjr.babel.model.entity;

import java.util.UUID;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.PrePersist;

import org.sjr.babel.model.component.Account;

@Entity
public class Administrator extends AbstractEntity {

	private String firstName, lastName, phoneNumber, mailAddress;

	@Embedded
	private Account account;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFullName() {
		return this.firstName + " " + this.lastName;
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	@PrePersist
	public void prePersist() {
		if (this.account == null) {
			setAccount(new Account());
		}
		getAccount().setAccessKey("A-" + UUID.randomUUID().toString());

	}

}
