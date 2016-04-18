package org.sjr.babel.entity;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class Appointment extends AbstractEntity {
	
	@ManyToOne
	private Refugee refugee;
	
	@ManyToOne
	private Volunteer volunteer;
	
	private int startTime, endTime;
	private Boolean accepted;
	
	
	
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
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public int getEndTime() {
		return endTime;
	}
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	public Boolean getAccepted() {
		return accepted;
	}
	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}
	
	
	
	

}
