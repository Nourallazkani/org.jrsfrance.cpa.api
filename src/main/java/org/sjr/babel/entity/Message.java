package org.sjr.babel.entity;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class Message {
	public enum Direction {
		VOLUNTEER_TO_REFUGEE, REFUGEE_TO_VOLUNTEER
	}
	@ManyToOne   
	private Volunteer volunteer;
	@Enumerated(EnumType.STRING)
	private Direction direction ;
	@NotNull @Size(min=1)
	private String text ;
	private Date postedDate;
	private Date readDate;
	
	
	public Volunteer getVolunteer() {
		return volunteer;
	}
	public void setVolunteer(Volunteer volunteer) {
		this.volunteer = volunteer;
	}
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public String getText() {
		return text;
	}
	public void setText(String message) {
		this.text = message;
	}
	public Date getPostedDate() {
		return postedDate;
	}
	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}
	public Date getReadDate() {
		return readDate;
	}
	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}
	
	
	
	
}
