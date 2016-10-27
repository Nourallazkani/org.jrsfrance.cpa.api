package org.sjr.babel.model.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.sjr.babel.model.entity.reference.ProfessionalLearningProgramDomain;

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
