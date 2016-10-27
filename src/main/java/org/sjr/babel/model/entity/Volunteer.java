package org.sjr.babel.model.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
import org.sjr.babel.model.entity.reference.FieldOfStudy;
import org.sjr.babel.model.entity.reference.Language;

@Entity
public class Volunteer extends AbstractEntity {

	private String firstName;
	private String lastName;
	private Date birthDate;
	private String mailAddress;
	private String phoneNumber;
	private Boolean availableForConversation, availableForInterpreting, availableForSupportInStudies, availableForActivities;
	private String activities;

	@Embedded
	private Address address;

	@Embedded
	private Account account;

	@ManyToOne(fetch = FetchType.EAGER)
	private Civility civility;

	@ManyToOne(fetch = FetchType.LAZY)
	private Organisation organisation;

	@ManyToMany
	@JoinTable(joinColumns=@JoinColumn(name="Volunteer_id"), inverseJoinColumns = @JoinColumn(name = "FieldOfStudy_id"))
	private List<FieldOfStudy> fieldsOfStudy;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(joinColumns=@JoinColumn(name="Volunteer_id"), inverseJoinColumns = @JoinColumn(name = "Language_id"))
	private List<Language> languages;

	@ManyToMany()
	@JoinTable(name="MeetingRequest_Volunteer", joinColumns=@JoinColumn(name="Volunteer_id"), inverseJoinColumns=@JoinColumn(name="MeetingRequest_id"))
	private Set<MeetingRequest> meetingRequests;
	
	@OneToMany(mappedBy = "volunteer")
	private Set<MeetingRequest> acceptedMeetingRequests;

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

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Boolean getAvailableForConversation() {
		return availableForConversation;
	}

	public void setAvailableForConversation(Boolean availableForConversation) {
		this.availableForConversation = availableForConversation;
	}

	public Boolean getAvailableForInterpreting() {
		return availableForInterpreting;
	}

	public void setAvailableForInterpreting(Boolean availableForInterpreting) {
		this.availableForInterpreting = availableForInterpreting;
	}

	public Boolean getAvailableForSupportInStudies() {
		return availableForSupportInStudies;
	}

	public void setAvailableForSupportInStudies(Boolean availableForSupportInStudies) {
		this.availableForSupportInStudies = availableForSupportInStudies;
	}

	public Boolean getAvailableForActivities() {
		return availableForActivities;
	}

	public void setAvailableForActivities(Boolean availableForActivities) {
		this.availableForActivities = availableForActivities;
	}

	public String getActivities() {
		return activities;
	}

	public void setActivities(String activities) {
		this.activities = activities;
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

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public List<FieldOfStudy> getFieldsOfStudy() {
		return fieldsOfStudy;
	}

	public void setFieldsOfStudy(List<FieldOfStudy> fieldsOfStudy) {
		this.fieldsOfStudy = fieldsOfStudy;
	}

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	public Set<MeetingRequest> getMeetingRequests() {
		return meetingRequests;
	}

	public void setMeetingRequests(Set<MeetingRequest> meetingRequests) {
		this.meetingRequests = meetingRequests;
	}

	public Set<MeetingRequest> getAcceptedMeetingRequests() {
		return acceptedMeetingRequests;
	}

	public void setAcceptedMeetingRequests(Set<MeetingRequest> acceptedMeetingRequests) {
		this.acceptedMeetingRequests = acceptedMeetingRequests;
	}
	
	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}
}
