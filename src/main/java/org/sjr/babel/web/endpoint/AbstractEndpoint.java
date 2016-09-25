package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.sjr.babel.entity.AbstractEntity;
import org.sjr.babel.entity.Address;
import org.sjr.babel.entity.Contact;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.entity.Volunteer;
import org.sjr.babel.entity.reference.Country;
import org.sjr.babel.persistence.ObjectStore;
import org.sjr.babel.web.helper.ReferenceDataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public abstract class AbstractEndpoint {

	@Autowired
	protected ObjectStore objectStore;
	
	@Autowired
	protected ReferenceDataHelper refDataProvider;
	
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
	
	@Autowired
	private HttpServletRequest currentRequest;
	
	protected boolean requestedPathEquals(String path){
		String p = (String)currentRequest.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		if(p.startsWith("/")){
			p = p.substring(1);
		}
		return p.equals(path);
	}
	
	protected URI getUri(String path){
		HttpRequest httpRequest = new ServletServerHttpRequest(currentRequest);
		return UriComponentsBuilder.fromHttpRequest(httpRequest).path(path).build().toUri();
	}
	
	protected Optional<Organisation> getOrganisationByAccessKey(String accessKey){
		Map<String, Object> args= new HashMap<>();
		args.put("accessKey", accessKey);
		return this.objectStore.findOne(Organisation.class, "select o from Organisation o where o.account.accessKey=:accessKey", args);
	}
	
	protected Optional<Volunteer> getVolunteerByAccessKey(String accessKey){
		Map<String, Object> args= new HashMap<>();
		args.put("accessKey", accessKey);
		return this.objectStore.findOne(Volunteer.class, "select v from Volunteer v where v.account.accessKey=:accessKey", args);
	}
	
	@JsonIgnoreProperties(ignoreUnknown=true /*for formatted_address*/ )
	protected static class AddressSummary {
		public String street1, street2, postalCode, locality;
		public String country="France";
		public Double lat,lng; 
		
		public AddressSummary(){}
		
		public AddressSummary(Address a) {
			
			this.street1 = a.getStreet1();
			this.street2 = a.getStreet2();
			this.postalCode = a.getPostalCode();
			this.lat = a.getLat();
			this.lng = a.getLng();
			
			this.locality = a.getLocality();
			this.country = safeTransform(a.getCountry(), x -> x.getName());
		}
		
		public Address toAddress(ReferenceDataHelper referenceDataProvider){
			Address address = new Address();
			address.setStreet1(this.street1);
			address.setStreet2(this.street2);
			address.setPostalCode(this.postalCode);
			address.setLocality(this.locality);
			address.setLat(this.lat);
			address.setLng(this.lng);
			
			address.setCountry(referenceDataProvider.resolve(Country.class, this.country));
			return address;
		}
	}
	
	protected static class ContactSummary{ // same as contact for the time being, may differ later.
		public String name, mailAddress, phoneNumber;

		public ContactSummary(){}
		
		public ContactSummary(Contact contact) {
			this.name = contact.getName();
			this.phoneNumber = contact.getPhoneNumber();
			this.mailAddress = contact.getMailAddress();
		}
		
		public Contact toContact(){
			Contact c = new Contact();
			c.setMailAddress(this.mailAddress);
			c.setName(this.name);
			c.setPhoneNumber(this.phoneNumber);
			return c;
		}
	}
	
	// this function transform something (input) in something else based on a function provided by the caller, but only if the input is not null 
	protected static <T, U> U safeTransform(T input, Function<T, U> transformer){
		return safeTransform(input, transformer, null);
	}
	
	protected static <T, R> R  safeTransform(T input, Function<T, R> transformer, R defaultValue){
		return input!=null ? transformer.apply(input) : defaultValue;
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
