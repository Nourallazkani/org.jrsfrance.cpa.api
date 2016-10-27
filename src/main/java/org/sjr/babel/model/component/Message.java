package org.sjr.babel.model.component;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.sjr.babel.model.entity.Volunteer;

@Embeddable
public class Message {
	public enum Direction {
		VOLUNTEER_TO_REFUGEE, REFUGEE_TO_VOLUNTEER
	}

	private String text;
	private Date postedDate, readDate;

	@Enumerated(EnumType.STRING)
	private Direction direction;
	
	@ManyToOne
	private Volunteer volunteer;

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
