package org.sjr.babel.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class MeetingRequest extends AbstractEntity {

	public enum Reason {
		SUPPORT_IN_STUDIES, INTERPRETING, CONVERSATION
	}

	private String dateConstraint;

	@Temporal(TemporalType.TIMESTAMP)
	private Date postDate, acceptedDate;

	@Enumerated(EnumType.STRING)
	private Reason reason;

	private String additionalInformations;

	private Address refugeeLocation;

	@ManyToOne
	private Refugee refugee;

	@ManyToOne
	private Volunteer volunteer;
	
	@ManyToMany()
	@JoinTable(name="MeetingRequest_Volunteer", joinColumns=@JoinColumn(name="MeetingRequest_id"), inverseJoinColumns=@JoinColumn(name="Volunteer_id"))
	private Set<Volunteer> matches;

	public String getDateConstraint() {
		return dateConstraint;
	}

	public void setDateConstraint(String dateConstraint) {
		this.dateConstraint = dateConstraint;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Date getAcceptedDate() {
		return acceptedDate;
	}

	public void setAcceptedDate(Date acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	public Reason getReason() {
		return reason;
	}

	public void setReason(Reason reason) {
		this.reason = reason;
	}

	public String getAdditionalInformations() {
		return additionalInformations;
	}

	public void setAdditionalInformations(String additionalInformations) {
		this.additionalInformations = additionalInformations;
	}

	

	public Address getRefugeeLocation() {
		return refugeeLocation;
	}

	public void setRefugeeLocation(Address refugeeLocation) {
		this.refugeeLocation = refugeeLocation;
	}

	public Refugee getRefugee() {
		return refugee;
	}

	public void setRefugee(Refugee refugee) {
		this.refugee = refugee;
	}

	public Volunteer getVolunteer() {
		return volunteer;
	}

	public void setVolunteer(Volunteer volunteer) {
		this.volunteer = volunteer;
	}

	public Set<Volunteer> getMatches() {
		return matches;
	}

	public void setMatches(Set<Volunteer> matches) {
		this.matches = matches;
	}
}
