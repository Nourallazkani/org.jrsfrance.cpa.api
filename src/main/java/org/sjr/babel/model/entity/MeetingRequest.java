package org.sjr.babel.model.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sjr.babel.model.component.Address;
import org.sjr.babel.model.component.Message;

@Entity
public class MeetingRequest extends AbstractEntity {

	public enum Reason {
		SUPPORT_IN_STUDIES, INTERPRETING, CONVERSATION
	}
	public enum Direction {
		VOLUNTEER_TO_REFUGEE, REFUGEE_TO_VOLUNTEER
	}

	private String dateConstraint;

	@Temporal(TemporalType.TIMESTAMP)
	private Date postDate, acceptationDate, confirmationDate;

	@Enumerated(EnumType.STRING)
	private Reason reason;
	
	@Enumerated(EnumType.STRING)
	private Direction firstContact;

	private String additionalInformations;

	private Address refugeeLocation;

	@ManyToOne
	private Refugee refugee;

	@ManyToOne
	private Volunteer volunteer;
	
	@ManyToMany()
	@JoinTable(name="MeetingRequest_Volunteer", joinColumns=@JoinColumn(name="MeetingRequest_id"), inverseJoinColumns=@JoinColumn(name="Volunteer_id"))
	private Set<Volunteer> matches;
	
	@ElementCollection
	private List<Message> messages; 

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

	public Date getAcceptationDate() {
		return acceptationDate;
	}

	public void setAcceptationDate(Date acceptedDate) {
		this.acceptationDate = acceptedDate;
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
/*
	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
*/

	public Date getConfirmationDate() {
		return confirmationDate;
	}

	public void setConfirmationDate(Date confimationDate) {
		this.confirmationDate = confimationDate;
	}

	public Direction getFirstContact() {
		return firstContact;
	}

	public void setFirstContact(Direction firstContact) {
		this.firstContact = firstContact;
	}
	
	
}
