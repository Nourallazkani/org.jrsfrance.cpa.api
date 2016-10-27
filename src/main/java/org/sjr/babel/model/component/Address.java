package org.sjr.babel.model.component;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.sjr.babel.model.entity.reference.Country;

@Embeddable
public class Address {
	private String street1, street2, postalCode, locality;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Country country;
	
	private Double lat, lng;
	
	private String googleMapId;

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}



	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public String getGoogleMapId() {
		return googleMapId;
	}

	public void setGoogleMapId(String googleMapId) {
		this.googleMapId = googleMapId;
	}
}
