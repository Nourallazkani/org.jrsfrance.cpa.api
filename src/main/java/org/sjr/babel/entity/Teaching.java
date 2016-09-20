package org.sjr.babel.entity;

import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sjr.babel.entity.Contact.ContactConverter;
import org.sjr.babel.entity.reference.FieldOfStudy;

@Entity
public class Teaching extends AbstractEntity {
	
	private Boolean master, licence;
	
	private String link;
	
	@Temporal(TemporalType.DATE)
	private Date registrationOpeningDate, registrationClosingDate;
	
	@ManyToOne
	private FieldOfStudy fieldOfStudy;

	@ManyToOne
	private Level languageLevelRequired;

	@ManyToOne
	private Organisation organisation;

	@Convert(converter = ContactConverter.class)
	private Contact contact;


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

	public FieldOfStudy getFieldOfStudy() {
		return fieldOfStudy;
	}

	public void setFieldOfStudy(FieldOfStudy fieldOfStudy) {
		this.fieldOfStudy = fieldOfStudy;
	}

	public Level getLanguageLevelRequired() {
		return languageLevelRequired;
	}

	public void setLanguageLevelRequired(Level languageLevelRequired) {
		this.languageLevelRequired = languageLevelRequired;
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
}
