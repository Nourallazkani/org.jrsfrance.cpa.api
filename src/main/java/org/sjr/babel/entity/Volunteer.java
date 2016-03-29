package org.sjr.babel.entity;

import java.sql.Date;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.sjr.babel.entity.reference.Civility;
import org.sjr.babel.entity.reference.Language;

@Entity
public class Volunteer extends AbstractEntity {

	private String firstName;
	private String lastName;
	private Date birthDate;
	private String mailAddress;
	private String phoneNumber;
	private String comments;
	
	@Embedded
	private Account account;
	
	
	@ManyToMany(fetch=FetchType.LAZY) 
	@JoinTable(inverseJoinColumns=@JoinColumn(name="language_id"))
	private List<Language> languages;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Civility civility;

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

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String email) {
		this.mailAddress = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	public Civility getCivility() {
		return civility;
	}

	public void setCivility(Civility civility) {
		this.civility = civility;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	
}
