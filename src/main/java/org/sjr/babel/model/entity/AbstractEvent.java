package org.sjr.babel.model.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sjr.babel.model.component.Address;
import org.sjr.babel.model.component.Contact;
import org.sjr.babel.model.component.MultiLanguageText;
import org.sjr.babel.model.component.Registration;
import org.sjr.babel.model.component.Contact.ContactConverter;
import org.sjr.babel.model.entity.reference.EventType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractEvent extends AbstractEntity {

	public enum Audience {
		REFUGEE, VOLUNTEER
	}

	@Enumerated(EnumType.STRING)
	private Audience audience;

	@Embedded()
	@AttributeOverrides({
		@AttributeOverride(name="defaultText", column=@Column(name="subject_defaultText")),
		@AttributeOverride(name="textAr", column=@Column(name="subject_textAr")),
		@AttributeOverride(name="textEn", column=@Column(name="subject_textEn")),
		@AttributeOverride(name="textPrs", column=@Column(name="subject_textPrs"))
	})
	private MultiLanguageText subject;
	
	@Embedded()
	@AttributeOverrides({
		@AttributeOverride(name="defaultText", column=@Column(name="description_defaultText")),
		@AttributeOverride(name="textAr", column=@Column(name="description_textAr")),
		@AttributeOverride(name="textEn", column=@Column(name="description_textEn")),
		@AttributeOverride(name="textPrs", column=@Column(name="description_textPrs"))
	})
	private MultiLanguageText description;
	
	private String link;

	@Temporal(TemporalType.DATE)
	private Date registrationOpeningDate, registrationClosingDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate, endDate;

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

	public MultiLanguageText getDescription() {
		return description;
	}

	public void setDescription(MultiLanguageText description) {
		this.description = description;
	}

	public MultiLanguageText getSubject() {
		return subject;
	}

	public void setSubject(MultiLanguageText subject) {
		this.subject = subject;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
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

	public void setRegistrations(List<Registration> registratios) {
		this.registrations = registratios;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getRegistrationOpeningDate() {
		return registrationOpeningDate;
	}

	public void setRegistrationOpeningDate(Date registrationOpeningDate) {
		this.registrationOpeningDate = registrationOpeningDate;
	}

	public Date getRegistrationClosingDate() {
		return registrationClosingDate;
	}

	public void setRegistrationClosingDate(Date registrationClosingDate) {
		this.registrationClosingDate = registrationClosingDate;
	}

	@Entity
	@DiscriminatorValue("O-E")
	public static class OrganisationEvent extends AbstractEvent {

		@ManyToOne(optional = false)
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

		@ManyToOne(optional = false)
		private Volunteer volunteer;

		public Volunteer getVolunteer() {
			return volunteer;
		}

		public void setVolunteer(Volunteer volunteer) {
			this.volunteer = volunteer;
		}

	}
}
