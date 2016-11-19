package org.sjr.babel.model.component;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.sjr.babel.model.entity.Refugee;

@Embeddable
public class Registration {

	@ManyToOne // because a refugee may have more than one inscription, and an inscription must be done by one and only one refugee(but is that guarantee that a refugee cannot register twice in the same event ?)
	private Refugee refugee;
	private Date registrationDate;
	private Boolean Accepted;
	

	public Refugee getRefugee() {
		return refugee;
	}

	public void setRefugee(Refugee refugee) {
		this.refugee = refugee;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Boolean getAccepted() {
		return Accepted;
	}

	public void setAccepted(Boolean accepted) {
		Accepted = accepted;
	}

}
