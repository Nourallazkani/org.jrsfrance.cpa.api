package org.sjr.babel.entity;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Embeddable
public class Address {
	private String street1, street2, zipcode, city;
	
	@ManyToOne
	private Country country;
	
	@Transient
	private Long lat, lng;

	public Address() {
		// TODO Auto-generated constructor stub
	}
	
	
	public Address(String street1, String street2, String zipcode, String city, Country country, Long lat, Long lng) {
		super();
		this.street1 = street1;
		this.street2 = street2;
		this.zipcode = zipcode;
		this.city = city;
		this.country = country;
		this.lat = lat;
		this.lng = lng;
	}



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

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Long getLat() {
		return lat;
	}

	public void setLat(Long lat) {
		this.lat = lat;
	}

	public Long getLng() {
		return lng;
	}

	public void setLng(Long lng) {
		this.lng = lng;
	}
}
