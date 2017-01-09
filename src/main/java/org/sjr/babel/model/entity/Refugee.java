package org.sjr.babel.model.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.sjr.babel.model.component.Account;
import org.sjr.babel.model.component.Address;
import org.sjr.babel.model.entity.reference.Civility;
import org.sjr.babel.model.entity.reference.Country;
import org.sjr.babel.model.entity.reference.FieldOfStudy;
import org.sjr.babel.model.entity.reference.Language;
import org.sjr.babel.model.entity.reference.Level;

@Entity
public class Refugee extends AbstractEntity {

	private String firstName, lastName, mailAddress, phoneNumber;

	private LocalDate registrationDate, birthDate;

	@Embedded
	private Account account;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private Civility civility;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Country nationality;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private FieldOfStudy fieldOfStudy;

	@Embedded
	private Address address;

	@OneToMany(mappedBy = "refugee", fetch = FetchType.LAZY)
	private List<MeetingRequest> meetingRequests;

	@ManyToMany
	@JoinTable(name = "Refugee_Language", joinColumns = @JoinColumn(name = "Refugee_id"), inverseJoinColumns = @JoinColumn(name = "Language_id"))
	private List<Language> languages;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Level hostCountryLanguageLevel ;
	
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

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
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

	public Country getNationality() {
		return nationality;
	}

	public void setNationality(Country nationality) {
		this.nationality = nationality;
	}

	public FieldOfStudy getFieldOfStudy() {
		return fieldOfStudy;
	}

	public void setFieldOfStudy(FieldOfStudy fieldOfStudy) {
		this.fieldOfStudy = fieldOfStudy;
	}

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}
	
	public Level getHostCountryLanguageLevel() {
		return hostCountryLanguageLevel;
	}

	public void setHostCountryLanguageLevel(Level hostCountryLanguageLevel) {
		this.hostCountryLanguageLevel = hostCountryLanguageLevel;
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
}
