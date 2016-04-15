package org.sjr.babel.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.sjr.babel.entity.reference.FieldOfStudy;

@Entity
public class Teaching extends AbstractEntity {
	@ManyToOne
	private FieldOfStudy fieldOfStudy;

	private String contactName, contactPhone, contactMailAddress;

	private Date registrationStartDate;

	private boolean openForRegistration;

	@ManyToOne
	private Level languageLevelRequired;

	@ManyToOne
	private Organisation organisation;
	private Boolean master;
	private Boolean licence;
	private String link;

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactMailAddress() {
		return contactMailAddress;
	}

	public void setContactMailAddress(String contactMailAddress) {
		this.contactMailAddress = contactMailAddress;
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

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
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

	public void setLanguageLevelRequired(Level languageLevelRequierd) {
		this.languageLevelRequired = languageLevelRequierd;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
