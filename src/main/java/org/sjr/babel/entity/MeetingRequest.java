package org.sjr.babel.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

@Entity
public class MeetingRequest extends AbstractEntity {
	
	enum Reason{}

	private Date startDate, endDate;
	private Boolean accepted;
	
	@Enumerated(EnumType.STRING)
	private Reason reason;
	
	@ManyToOne
	private Refugee refugee;
	
	@ManyToOne
	private Volunteer volunteer;
	
	public Reason getReason() {
		return reason;
	}
	public void setReason(Reason reason) {
		this.reason = reason;
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
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startTime) {
		this.startDate = startTime;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endTime) {
		this.endDate = endTime;
	}
	public Boolean getAccepted() {
		return accepted;
	}
	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}
	
	
	
	

}
