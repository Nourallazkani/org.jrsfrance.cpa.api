package org.sjr.babel.entity.reference;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

import org.sjr.babel.entity.AbstractEntity.CacheOnStartup;
import org.sjr.babel.entity.AbstractReferenceEntity;

@Entity @Cacheable @CacheOnStartup(order = 0)
public class Country extends AbstractReferenceEntity {

	private String isoCode;

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

}
