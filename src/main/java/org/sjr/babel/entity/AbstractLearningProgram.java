package org.sjr.babel.entity;

import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sjr.babel.entity.Contact.ContactConverter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractLearningProgram extends AbstractEntity {

	private String name, link;

	private Integer groupSize;
	
	@Temporal(TemporalType.DATE)
	private Date registrationOpeningDate, registrationClosingDate;

	@Temporal(TemporalType.DATE)
	private Date startDate, endDate;

	@ManyToOne(fetch = FetchType.EAGER)
	private Organisation organisation;

	@Convert(converter = ContactConverter.class)
	private Contact contact;

	@Embedded
	private Address address;

	@ManyToOne
	private Level level;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Integer getGroupSize() {
		return groupSize;
	}

	public void setGroupSize(Integer groupSize) {
		this.groupSize = groupSize;
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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
}
