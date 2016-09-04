package org.sjr.babel.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.sjr.babel.entity.reference.ProfessionalLearningProgramDomain;

@Entity
@DiscriminatorValue("P")
public class ProfessionalLearningProgram extends AbstractLearningProgram {
	
	@ManyToOne(fetch = FetchType.EAGER)
	private ProfessionalLearningProgramDomain domain;

	public ProfessionalLearningProgramDomain getDomain() {
		return domain;
	}

	public void setDomain(ProfessionalLearningProgramDomain domain) {
		this.domain = domain;
	}
	
}
