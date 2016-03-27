package org.sjr.babel.entity.reference;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

import org.sjr.babel.entity.AbstractEntity.CacheOnStartup;
import org.sjr.babel.entity.AbstractReferenceEntity;

@Entity @Cacheable @CacheOnStartup(order = 0)
public class FieldOfStudy extends AbstractReferenceEntity{
	
}
