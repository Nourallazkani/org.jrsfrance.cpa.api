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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sjr.babel.entity.reference.Civility;
import org.sjr.babel.entity.reference.FieldOfStudy;
import org.sjr.babel.entity.reference.Language;

@Entity
public class Volunteer extends AbstractEntity {

	private String firstName;
	private String lastName;
	private Date birthDate;
	private String mailAddress;
	private String phoneNumber;
	private String comments;

	@Temporal(TemporalType.DATE)
	private Date registrationDate;

	@Embedded
	private Address address;

	@Embedded
	private Account account;

	@ElementCollection
	private List<Availability> availabilities;

	@ManyToOne(fetch = FetchType.EAGER)
	private Civility civility;

	@ManyToOne(fetch = FetchType.LAZY)
	private Organisation organisation;

	@ManyToMany
	@JoinTable(inverseJoinColumns = @JoinColumn(name = "fieldOfStudy_id"))
	private List<FieldOfStudy> fieldsOfStudy;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = "language_id"))
	private List<Language> languages;

	@OneToMany(mappedBy = "volunteer")
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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

	public List<Availability> getAvailabilities() {
		return availabilities;
	}

	public void setAvailabilities(List<Availability> availabilities) {
		this.availabilities = availabilities;
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

	public List<MeetingRequest> getMeetingRequests() {
		return meetingRequests;
	}

	public void setMeetingRequests(List<MeetingRequest> meetingRequests) {
		this.meetingRequests = meetingRequests;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

	// @PrePersist see AuthEndpoint::signUp
	public void prePersist() {
		if (this.account == null) {
			setAccount(new Account());
		}
		getAccount().setAccessKey("V-" + UUID.randomUUID().toString());

	}

}
