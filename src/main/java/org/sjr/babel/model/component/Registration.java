package org.sjr.babel.model.component;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.sjr.babel.model.entity.Refugee;

@Embeddable
public class Registration {

	@ManyToOne(optional = false) // because a refugee may have more than one inscription, and an
				// inscription must be done by one and only one refugee(but is
				// that guarantee that a refugee cannot register twice in the
				// same event ?)
	private Refugee refugee;
	
	@Column(insertable = false)
	private LocalDate decisionDate;

	@Column(updatable = false)
	private LocalDate requestDate;
	
	@Column(insertable = false)
	private Boolean accepted;

	public Refugee getRefugee() {
		return refugee;
	}

	public void setRefugee(Refugee refugee) {
		this.refugee = refugee;
	}

	public LocalDate getDecisionDate() {
		return decisionDate;
	}

	public void setDecisionDate(LocalDate decisionDate) {
		this.decisionDate = decisionDate;
	}

	public LocalDate getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(LocalDate requestDate) {
		this.requestDate = requestDate;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

}
