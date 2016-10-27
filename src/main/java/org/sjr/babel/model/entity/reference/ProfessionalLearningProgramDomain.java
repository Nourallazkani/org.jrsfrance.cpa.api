package org.sjr.babel.model.entity.reference;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

import org.sjr.babel.model.entity.AbstractEntity.CacheOnStartup;

@Entity @Cacheable @CacheOnStartup(order = 0)
public class ProfessionalLearningProgramDomain extends AbstractReferenceEntity {
	
}
