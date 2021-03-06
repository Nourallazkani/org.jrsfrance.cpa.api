package org.sjr.babel.model.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.sjr.babel.model.Status;
import org.sjr.babel.model.component.Contact;
import org.sjr.babel.model.component.Contact.ContactConverter;
import org.sjr.babel.model.component.Registration;
import org.sjr.babel.model.entity.reference.FieldOfStudy;
import org.sjr.babel.model.entity.reference.Level;

@Entity
public class Teaching extends AbstractEntity {

	private Boolean master, licence;

	private String link;

	private LocalDate registrationOpeningDate, registrationClosingDate;
	

	private Integer maxAge, minAge;

	@Enumerated(EnumType.STRING)
	private Status statusRestriction;
	
	@ManyToOne
	private Level languageLevelRequired;

	@ManyToOne
	private FieldOfStudy fieldOfStudy;
	
	@ManyToOne
	private Organisation organisation;

	@Convert(converter = ContactConverter.class)
	private Contact contact;

	@ElementCollection
	private List<Registration> registrations;

	public Boolean getMaster() {
		return master;
	}

	public void setMaster(Boolean master) {
		this.master = master;
	}

	public Boolean getLicence() {
		return licence;
	}

	public void setLicence(Boolean licence) {
		this.licence = licence;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
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

	public FieldOfStudy getFieldOfStudy() {
		return fieldOfStudy;
	}

	public void setFieldOfStudy(FieldOfStudy fieldOfStudy) {
		this.fieldOfStudy = fieldOfStudy;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public List<Registration> getRegistrations() {
		return registrations;
	}

	public void setRegistrations(List<Registration> registrations) {
		this.registrations = registrations;
	}
}
