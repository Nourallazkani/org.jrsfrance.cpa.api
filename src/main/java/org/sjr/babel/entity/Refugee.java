package org.sjr.babel.entity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sjr.babel.entity.reference.Civility;
import org.sjr.babel.entity.reference.FieldOfStudy;
import org.sjr.babel.entity.reference.Language;

@Entity
public class Refugee extends AbstractEntity {

	private String firstName, lastName, mailAddress, phoneNumber;

	@Temporal(TemporalType.DATE)
	private Date birthDate, registrationDate;

	@Embedded
	private Account account;

	@ManyToOne
	private Language firstLanguage;

	@ManyToOne(fetch = FetchType.EAGER)
	private Civility civility;

	@Embedded
	private Address address;

	@OneToMany(mappedBy = "refugee", fetch = FetchType.LAZY)
	private List<MeetingRequest> meetingRequests;

	@ElementCollection(fetch = FetchType.LAZY)
	private List<LanguageSkill> languageSkills;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = "fieldOfStudy_id"))
	private List<FieldOfStudy> fieldsOfStudy;

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

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
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

	public Civility getCivility() {
		return civility;
	}

	public void setCivility(Civility civility) {
		this.civility = civility;
	}

	public Language getFirstLanguage() {
		return firstLanguage;
	}

	public void setFirstLanguage(Language firstLanguage) {
		this.firstLanguage = firstLanguage;
	}

	public List<LanguageSkill> getLanguageSkills() {
		return languageSkills;
	}

	public void setLanguageSkills(List<LanguageSkill> languageSkills) {
		this.languageSkills = languageSkills;
	}

	public List<FieldOfStudy> getFieldsOfStudy() {
		return fieldsOfStudy;
	}

	public void setFieldsOfStudy(List<FieldOfStudy> fieldsOfStudy) {
		this.fieldsOfStudy = fieldsOfStudy;
	}

	public List<MeetingRequest> getMeetingRequests() {
		return meetingRequests;
	}

	public void setMeetingRequests(List<MeetingRequest> meetingRequests) {
		this.meetingRequests = meetingRequests;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

	@PrePersist
	public void prePersist() {
		if (this.account == null) {
			setAccount(new Account());
		}
		getAccount().setAccessKey("R-" + UUID.randomUUID().toString());

	}

}
