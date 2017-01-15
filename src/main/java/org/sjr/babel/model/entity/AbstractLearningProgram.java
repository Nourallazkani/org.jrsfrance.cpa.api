package org.sjr.babel.model.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import org.sjr.babel.model.GenderRestriction;
import org.sjr.babel.model.StatusRestriction;
import org.sjr.babel.model.component.Address;
import org.sjr.babel.model.component.Contact;
import org.sjr.babel.model.component.Contact.ContactConverter;
import org.sjr.babel.model.component.Registration;
import org.sjr.babel.model.entity.reference.Level;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractLearningProgram extends AbstractEntity {

	private String name, link;

	private Integer groupSize;
	
	private LocalDate registrationOpeningDate, registrationClosingDate;

	private LocalDate startDate, endDate;

	private Integer maxAge, minAge;
	
	@Enumerated(EnumType.STRING)
	private GenderRestriction genderRestriction;
	
	@Enumerated(EnumType.STRING)
	private StatusRestriction statusRestriction;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private Organisation organisation;

	@Convert(converter = ContactConverter.class)
	private Contact contact;

	@Embedded
	private Address address;
	
	@ElementCollection
	private List<Registration> registrations;
	
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

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
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

	public GenderRestriction getGenderRestriction() {
		return genderRestriction;
	}

	public void setGenderRestriction(GenderRestriction genderRestriction) {
		this.genderRestriction = genderRestriction;
	}

	public StatusRestriction getStatusRestriction() {
		return statusRestriction;
	}

	public void setStatusRestriction(StatusRestriction statusRestriction) {
		this.statusRestriction = statusRestriction;
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

	public List<Registration> getRegistrations() {
		return registrations;
	}

	public void setRegistrations(List<Registration> registrations) {
		this.registrations = registrations;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
}
