package org.sjr.babel.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class MeetingRequest extends AbstractEntity {

	public enum Reason {
		SUPPORT_IN_STUDIES, INTERPRETING, CONVERSATION
	}

	@Temporal(TemporalType.DATE)
	private Date startDate, endDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date postDate, acceptedDate;

	@Enumerated(EnumType.STRING)
	private Reason reason;

	private String additionalInformations;

	private int matchesCount;

	private Address refugeeLocation;

	@ManyToOne
	private Refugee refugee;

	@ManyToOne
	private Volunteer volunteer;

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

	public int getMatchesCount() {
		return matchesCount;
	}

	public void setMatchesCount(int matchesCount) {
		this.matchesCount = matchesCount;
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
}
