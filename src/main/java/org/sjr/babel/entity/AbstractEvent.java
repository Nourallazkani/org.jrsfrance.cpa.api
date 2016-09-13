package org.sjr.babel.entity;

import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
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

import org.sjr.babel.entity.Contact.ContactConverter;
import org.sjr.babel.entity.reference.EventType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractEvent extends AbstractEntity {

	public enum Audience {REFUGEE, VOLUNTEER}
	
	@Enumerated(EnumType.STRING)
	private Audience audience;
	
	private String description, subject, link;

	private boolean openForRegistration;

	@Convert(converter = ContactConverter.class)
	private Contact contact;
	
	@Embedded
	private Address address;

	@ManyToOne(fetch = FetchType.EAGER)
	private EventType type;

	@Temporal(TemporalType.DATE)
	private Date registrationStartDate, startDate, endDate;

	public Audience getAudience() {
		return audience;
	}

	public void setAudience(Audience audience) {
		this.audience = audience;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
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
	

	public Date getRegistrationStartDate() {
		return registrationStartDate;
	}

	public void setRegistrationStartDate(Date registrationStartDate) {
		this.registrationStartDate = registrationStartDate;
	}

	public boolean isOpenForRegistration() {
		return openForRegistration;
	}

	public void setOpenForRegistration(boolean openForRegistration) {
		this.openForRegistration = openForRegistration;
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
