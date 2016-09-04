package org.sjr.babel.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.sjr.babel.entity.reference.LanguageLearningProgramType;

@Entity
@DiscriminatorValue("L")
public class LanguageLearningProgram extends AbstractLearningProgram {
	
	@ManyToOne(fetch = FetchType.EAGER)
	private LanguageLearningProgramType type;

	public LanguageLearningProgramType getType() {
		return type;
	}

	public void setType(LanguageLearningProgramType type) {
		this.type = type;
	}
	
}
