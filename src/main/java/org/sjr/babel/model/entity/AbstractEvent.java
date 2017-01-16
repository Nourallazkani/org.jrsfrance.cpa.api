package org.sjr.babel.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import org.sjr.babel.model.Gender;
import org.sjr.babel.model.Status;
import org.sjr.babel.model.component.Address;
import org.sjr.babel.model.component.Contact;
import org.sjr.babel.model.component.Contact.ContactConverter;
import org.sjr.babel.model.component.MultiLanguageText;
import org.sjr.babel.model.component.MultiLanguageText.MultiLanguageTextConverter;
import org.sjr.babel.model.component.Registration;
import org.sjr.babel.model.entity.reference.EventType;
import org.sjr.babel.model.entity.reference.Level;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractEvent extends AbstractEntity {

	public enum Audience {
		REFUGEE, VOLUNTEER
	}

	@Enumerated(EnumType.STRING)
	private Audience audience;

	private String subject, description, link;
	
	@Convert(converter = MultiLanguageTextConverter.class) @Column(columnDefinition = "json", insertable = false, updatable = false)
	private MultiLanguageText subjectI18n, descriptionI18n;
		
	private LocalDate registrationOpeningDate, registrationClosingDate;

	private LocalDateTime startDate, endDate;
	
	private Integer maxAge, minAge;
	
	@Enumerated(EnumType.STRING)
	private Gender genderRestriction;
	
	@Enumerated(EnumType.STRING)
	private Status statusRestriction;

	@ManyToOne
	private Level languageLevelRequired;
	
	@Convert(converter = ContactConverter.class)
	private Contact contact;

	@Embedded
	private Address address;
	
	@ElementCollection
	private List<Registration> registrations;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private EventType type;

	public Audience getAudience() {
		return audience;
	}

	public void setAudience(Audience audience) {
		this.audience = audience;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public MultiLanguageText getSubjectI18n() {
		return subjectI18n;
	}

	public void setSubjectI18n(MultiLanguageText subjectI18n) {
		this.subjectI18n = subjectI18n;
	}

	public MultiLanguageText getDescriptionI18n() {
		return descriptionI18n;
	}

	public void setDescriptionI18n(MultiLanguageText descriptionI18n) {
		this.descriptionI18n = descriptionI18n;
	}

	public LocalDate getRegistrationOpeningDate() {
		return registrationOpeningDate;
	}

	public void setRegistrationOpeningDate(LocalDate registrationOpeningDate) {
		this.registrationOpeningDate = registrationOpeningDate;
	}

	public LocalDate getRegistrationClosingDate() {
		return registrationClosingDate;
	}

	public void setRegistrationClosingDate(LocalDate registrationClosingDate) {
		this.registrationClosingDate = registrationClosingDate;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}

	public Integer getMinAge() {
		return minAge;
	}

	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}

	public Gender getGenderRestriction() {
		return genderRestriction;
	}

	public void setGenderRestriction(Gender genderRestriction) {
		this.genderRestriction = genderRestriction;
	}

	public Status getStatusRestriction() {
		return statusRestriction;
	}

	public void setStatusRestriction(Status statusRestriction) {
		this.statusRestriction = statusRestriction;
	}

	public Level getLanguageLevelRequired() {
		return languageLevelRequired;
	}

	public void setLanguageLevelRequired(Level languageLevelRequired) {
		this.languageLevelRequired = languageLevelRequired;
	}

	public Contact getContact() {
		return contact;
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

	public List<Registration> getRegistrations() {
		return registrations;
	}

	public void setRegistrations(List<Registration> registrations) {
		this.registrations = registrations;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	@Entity
	@DiscriminatorValue("O-E")
	public static class OrganisationEvent extends AbstractEvent {

		@ManyToOne
		private Organisation organisation;

		public Organisation getOrganisation() {
			return organisation;
		}

		public void setOrganisation(Organisation organisation) {
			this.organisation = organisation;
		}

	}

	@Entity
	@DiscriminatorValue("V-E")
	public static class VolunteerEvent extends AbstractEvent {

		@ManyToOne
		private Volunteer volunteer;

		public Volunteer getVolunteer() {
			return volunteer;
		}

		public void setVolunteer(Volunteer volunteer) {
			this.volunteer = volunteer;
		}

	}
}
