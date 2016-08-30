package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.sjr.babel.entity.AbstractEntity;
import org.sjr.babel.entity.Address;
import org.sjr.babel.entity.Contact;
import org.sjr.babel.persistence.ObjectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractEndpoint {

	@Autowired
	protected ObjectStore objectStore;
	
	@Autowired
	protected ObjectMapper jackson;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected ResponseEntity<?> okOrNotFound (Optional<?> o){
		return o.isPresent() ? ResponseEntity.ok(o.get()): ResponseEntity.notFound().build() ;
	} 
	
	protected <T extends AbstractEntity> ResponseEntity<Void> deleteIfExists (Class<T> clazz, int id){
		Optional<T> e = objectStore.getById(clazz, id);
		if (e.isPresent()) {
			objectStore.delete(e.get());
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	/*
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Link {
		public String title,href,rel;
		public Link(String title, String href, String rel) {
			this.href = href;
			this.title = title;
			this.rel= rel;
		}
	}*/
	
	@Autowired
	private HttpServletRequest currentRequest;
	
	protected HttpServletRequest currentRequest(){
		return currentRequest;
	}
	
	protected URI getUri(String path){
		HttpRequest httpRequest = new ServletServerHttpRequest(currentRequest());
		return UriComponentsBuilder.fromHttpRequest(httpRequest).path(path).build().toUri();
	}
	
	protected static class AddressSummary {
		public String street1, street2, zipcode, city, country;
		public Double lat,lng; 
		public AddressSummary(Address a ) {
			this.street1 = a.getStreet1();
			this.street2 = a.getStreet2();
			this.zipcode = a.getZipcode();
			this.city = a.getCity();
			this.country = a.getCountry().getName();
			this.lat = a.getLat();
			this.lng = a.getLng();
		}
	}
	
	protected static class ContactSummary{ // same as contact for the time being, may differ later.
		public String name, mailAddress, phoneNumber;

		public ContactSummary(Contact contact) {
			this.name = contact.getName();
			this.phoneNumber = contact.getPhoneNumber();
			this.mailAddress = contact.getMailAddress();
		}
	}
	
	// this function transform something (input) in something else based on a function provided by the caller, but only if the input is not null 
	protected <T, U> U safeTransform(T input, Function<T, U> transformer){
		return input!=null ? transformer.apply(input) : null;
	}
	
	public static class Error {
		
		public static final Error INVALID_DATE_RANGE = new Error("Date.Order.error","The Strating date must be befor the ending one");
		public static final Error MAIL_ADDRESS_ALREADY_EXISTS = new Error("mailAddress.already.exsists", null) ;	
		public static final Error VOLUNTEER_BUSY = new Error("Volunteer.Busy", "Sorry the Volunteer is busy in this time, try another");
		public String key, defaultMessage;

		Error(String key, String defaultMessage) {
			super();
			this.key = key;
			this.defaultMessage = defaultMessage;
		}
	}
	
	public static class LocalizableObjectSummary<T>{
		public T item;
		public long distanceFromOrigin;
		public LocalizableObjectSummary(T item, long distanceFromOrigin) {
			super();
			this.item = item;
			this.distanceFromOrigin = distanceFromOrigin;
		}
	}
	
}
