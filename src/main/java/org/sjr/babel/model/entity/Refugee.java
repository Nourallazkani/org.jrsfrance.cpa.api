package org.sjr.babel.model.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;

import org.sjr.babel.model.Gender;
import org.sjr.babel.model.component.Account;
import org.sjr.babel.model.component.Address;
import org.sjr.babel.model.component.Registration;
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

	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	@Embedded
	private Address address;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Country nationality;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private FieldOfStudy fieldOfStudy;

	@ManyToOne(fetch = FetchType.EAGER)
	private Level hostCountryLanguageLevel ;

	@ManyToMany
	@JoinTable(name = "Refugee_Language", joinColumns = @JoinColumn(name = "Refugee_id"), inverseJoinColumns = @JoinColumn(name = "Language_id"))
	private List<Language> languages;

	@ElementCollection
	@CollectionTable(name="AbstractLearningProgram_registrations")
	@MapKeyJoinColumn(name="AbstractLearningProgram_id")
	private Map<LanguageLearningProgram, Registration> languageLearningProgramRegistrations;
	
	@ElementCollection
	@CollectionTable(name="AbstractLearningProgram_registrations")
	@MapKeyJoinColumn(name="AbstractLearningProgram_id")
	private Map<ProfessionalLearningProgram, Registration> professionalProgramRegistrations;
	

	@ElementCollection
	@CollectionTable(name="AbstractEvent_registrations")
	@MapKeyJoinColumn(name="AbstractEvent_id")
	private Map<AbstractEvent, Registration> eventRegistrations;

	@ElementCollection
	@CollectionTable(name="Teaching_registrations")
	@MapKeyJoinColumn(name="Teaching_id")
	private Map<Teaching, Registration> teachingRegistrations;
	
	
	@OneToMany(mappedBy = "refugee", fetch = FetchType.LAZY)
	private List<MeetingRequest> meetingRequests;
	

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

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
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
	
	public Map<LanguageLearningProgram, Registration> getLanguageLearningProgramRegistrations() {
		return languageLearningProgramRegistrations;
	}

	public void setLanguageLearningProgramRegistrations(
			Map<LanguageLearningProgram, Registration> languageLearningProgramRegistrations) {
		this.languageLearningProgramRegistrations = languageLearningProgramRegistrations;
	}

	public Map<ProfessionalLearningProgram, Registration> getProfessionalProgramRegistrations() {
		return professionalProgramRegistrations;
	}

	public void setProfessionalProgramRegistrations(
			Map<ProfessionalLearningProgram, Registration> professionalProgramRegistrations) {
		this.professionalProgramRegistrations = professionalProgramRegistrations;
	}

	public Map<AbstractEvent, Registration> getEventRegistrations() {
		return eventRegistrations;
	}

	public void setEventRegistrations(Map<AbstractEvent, Registration> eventRegistrations) {
		this.eventRegistrations = eventRegistrations;
	}

	public Map<Teaching, Registration> getTeachingRegistrations() {
		return teachingRegistrations;
	}

	public void setTeachingRegistrations(Map<Teaching, Registration> teachingRegistrations) {
		this.teachingRegistrations = teachingRegistrations;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}
}
