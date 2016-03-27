package org.sjr.babel.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.sjr.babel.entity.reference.FieldOfStudy;
@Entity
public class Teaching extends AbstractEntity {
	@ManyToOne
	private FieldOfStudy fieldOfStudy;
	
	@ManyToOne
	private Level languageLevelRequired;
	
	@ManyToOne
	private Organisation organisation;
	private Boolean master;
	private Boolean licence;
	private String link;


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

	public boolean isMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}

	public boolean isLicence() {
		return licence;
	}

	public void setLicence(boolean licence) {
		this.licence = licence;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
