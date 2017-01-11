package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sjr.babel.model.component.Address;
import org.sjr.babel.model.component.Contact;
import org.sjr.babel.model.component.Registration;
import org.sjr.babel.model.entity.AbstractEntity;
import org.sjr.babel.model.entity.Administrator;
import org.sjr.babel.model.entity.MeetingRequest;
import org.sjr.babel.model.entity.MeetingRequest.Reason;
import org.sjr.babel.model.entity.Organisation;
import org.sjr.babel.model.entity.Refugee;
import org.sjr.babel.model.entity.Volunteer;
import org.sjr.babel.model.entity.reference.Country;
import org.sjr.babel.persistence.ObjectStore;
import org.sjr.babel.web.helper.MailHelper;
import org.sjr.babel.web.helper.ReferenceDataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

public abstract class AbstractEndpoint {

	@Autowired
	protected ObjectStore objectStore;
	
	@Autowired
	protected ReferenceDataHelper refDataProvider;
	
	@Autowired
	protected MailHelper mailHelper;
	
	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected ResponseEntity<?> okOrNotFound (Optional<?> o){
		return o.isPresent() ? ResponseEntity.ok(o.get()): ResponseEntity.notFound().build() ;
	} 
	
	protected <T> ResponseEntity<T> ok(T body){
		return ResponseEntity.ok(body) ;
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
	

	protected <T> ResponseEntity<T> created(URI newResourceUri, T newResource){
		if (newResourceUri==null) {
			return ResponseEntity.status(HttpStatus.CREATED).body(newResource);
		}else {
			return ResponseEntity.created(newResourceUri).body(newResource);
		} 
	}

	protected ResponseEntity<Void> noContent(){
		return ResponseEntity.noContent().build();
	} 
	
	protected ResponseEntity<Void> badRequest(){
		return badRequest(null);
	}
	
	protected <T> ResponseEntity<T> badRequest(T body){
		return ResponseEntity.badRequest().body(body);
	}
	
	protected ResponseEntity<Void> unauthorized(){
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
	
	protected ResponseEntity<Void> forbidden(){
		return forbidden(null);
	}
	
	protected <T> ResponseEntity<T> forbidden(T body){
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
	}
	
	protected ResponseEntity<Void> notFound(){
		return ResponseEntity.notFound().build();
	}
	
	protected ResponseEntity<Void> conflict(){
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	
	protected <T> ResponseEntity<T> conflict(T body){
		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}
	
	@Autowired
	private HttpServletRequest currentRequest;
	
	protected boolean requestedPathEquals(String path){
		String p = getPath();
		if(p.startsWith("/")){
			p = p.substring(1);
		}
		return p.equals(path);
	}
	
	protected String getPath(){
		return (String) this.currentRequest.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
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
	
	protected Optional<Administrator> getAdministratorByAccessKey(String accessKey){
		Map<String, Object> args= new HashMap<>();
		args.put("accessKey", accessKey);
		return this.objectStore.findOne(Administrator.class, "select a from Administrator a where a.account.accessKey=:accessKey", args);
	}
	
	protected Optional<Volunteer> getVolunteerByAccessKey(String accessKey){
		Map<String, Object> args= new HashMap<>();
		args.put("accessKey", accessKey);
		return this.objectStore.findOne(Volunteer.class, "select v from Volunteer v where v.account.accessKey=:accessKey", args);
	}
	
	protected Optional<Refugee> getRefugeeByAccesskey (String accessKey ){
		Map<String,Object> args = new HashMap<>();
		args.put("accessKey", accessKey);
		String hql = "select r from Refugee r where r.account.accessKey like :accessKey";
		return objectStore.findOne(Refugee.class, hql, args);
	}
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }
	
	@JsonIgnoreProperties({"formattedAddress", "formatted_address"})
	protected static class AddressSummary {
		public String street1, street2;
		@NotNull @Size(min = 1)
		public String postalCode, locality;
		public String country="France";
		public Double lat,lng;
		public String googleMapId;
		
		public AddressSummary(){}
		
		public AddressSummary(String street1, String street2, String postalCode, String locality, String country) {
			super();
			this.street1 = street1;
			this.street2 = street2;
			this.postalCode = postalCode;
			this.locality = locality;
			this.country = country;
		}

		public AddressSummary(Address a) {
			
			this.street1 = a.getStreet1();
			this.street2 = a.getStreet2();
			this.postalCode = a.getPostalCode();
			this.lat = a.getLat();
			this.lng = a.getLng();
			this.googleMapId = a.getGoogleMapId();
			
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
			address.setGoogleMapId(this.googleMapId);
			address.setCountry(referenceDataProvider.resolve(Country.class, this.country));
			return address;
		}
	}
	
	protected static class ContactSummary{ // same as contact for the time being, may differ later.
		@NotNull @Size(min = 1)
		public String name, mailAddress;
		public String phoneNumber;

		public ContactSummary(){}
		
		public ContactSummary(String name, String mailAddress, String phoneNumber) {
			super();
			this.name = name;
			this.mailAddress = mailAddress;
			this.phoneNumber = phoneNumber;
		}

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
	
	static class RegistrationSummary{
		public ContactSummary refugee;
		public Date acceptationDate;
		public Boolean accepted;
		
		public RegistrationSummary() {}
		
		public RegistrationSummary(Registration r) {
			Contact contact = new Contact();
			contact.setMailAddress(r.getRefugee().getMailAddress());
			contact.setName(r.getRefugee().getFullName());
			contact.setPhoneNumber(r.getRefugee().getPhoneNumber());
			this.refugee = new ContactSummary(contact);
			this.acceptationDate = r.getRegistrationDate();
			this.accepted = r.getAccepted();
		}	
	}
	
	static class AcceptOrRefuseRegistrationCommand {
		public boolean accepted;
	}

	/*
	protected static class MessageSummary {
		@NotNull @Size(min = 1)
		public String text ;
		@JsonProperty(access = Access.READ_ONLY)
		public Date postedDate;
		@JsonProperty(access = Access.READ_ONLY)
		public String from;
		public String to;

		public MessageSummary(){}
		
		public MessageSummary(MeetingRequest mr, Message msg) {
			this.text = msg.getText();
			this.postedDate = msg.getPostedDate();
			if (Direction.VOLUNTEER_TO_REFUGEE.equals(msg.getDirection())) {
				this.from = msg.getVolunteer().getFullName();
				this.to = mr.getRefugee().getFullName();
			} else if (Direction.REFUGEE_TO_VOLUNTEER.equals(msg.getDirection())) {
				this.from = mr.getRefugee().getFullName();
				this.to = msg.getVolunteer().getFullName();
			}
		}
	}*/
	
	protected static class MeetingRequestSummary{
		
		public Integer id;
		public ContactSummary refugee; 
		public ContactSummary volunteer;
		@NotNull
		public Reason reason;
		public String additionalInformations;
		public String dateConstraint;
		@NotNull
		public AddressSummary refugeeLocation;
		
		public @JsonInclude(JsonInclude.Include.NON_NULL) String fieldOfStudy;
		public @JsonInclude(JsonInclude.Include.NON_EMPTY) List<String> languages;
		
		public Date postDate, acceptationDate, confirmationDate;
	
		public MeetingRequestSummary() {}
		
		public MeetingRequestSummary(MeetingRequest entity){
			this.id = entity.getId(); 
			this.reason = entity.getReason();
			this.dateConstraint = entity.getDateConstraint();
			this.additionalInformations = entity.getAdditionalInformations();
			if(Reason.INTERPRETING.equals(this.reason) && entity.getRefugee().getLanguages() != null){
				this.languages=entity.getRefugee().getLanguages().stream().map(x->x.getName()).collect(Collectors.toList());
			}
			if(Reason.SUPPORT_IN_STUDIES.equals(this.reason) && entity.getRefugee().getFieldOfStudy() != null){
				this.fieldOfStudy = entity.getRefugee().getFieldOfStudy().getName();
			}
			this.refugee = new ContactSummary();
			this.refugee.name = entity.getRefugee().getFullName();
			this.refugeeLocation = safeTransform(entity.getRefugeeLocation(), x -> new AddressSummary(x));
			this.refugee.mailAddress = entity.getRefugee().getMailAddress();
			this.refugee.phoneNumber = entity.getRefugee().getPhoneNumber();
			
			if(entity.getVolunteer() != null){
				this.volunteer = new ContactSummary();
				this.volunteer.name = entity.getVolunteer().getFullName();
				this.volunteer.mailAddress = entity.getVolunteer().getMailAddress();
				this.volunteer.phoneNumber = entity.getVolunteer().getPhoneNumber();	
			}
			this.postDate = entity.getPostDate();
			this.acceptationDate = entity.getAcceptationDate();
			this.confirmationDate = entity.getConfirmationDate();
		}
	}
	
	
	// this function transform something (input) in something else based on a function provided by the caller, but only if the input is not null 
	protected static <T, U> U safeTransform(T input, Function<T, U> transformer){
		return safeTransform(input, transformer, null);
	}
	
	protected static <T, R> R  safeTransform(T input, Function<T, R> transformer, R defaultValue){
		if(input == null){
			return defaultValue;
		}
		else{
			R ret = transformer.apply(input);
			return ret != null ? ret : defaultValue;
		}
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
	
	protected Map<String, String> errorsAsMap(List<FieldError> errors){
		return errors.stream().collect(Collectors.toMap(x -> x.getField(), x -> x.getCode()));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> validationErrorsHandler(MethodArgumentNotValidException ex){
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		Map<String, String> errorsMap = errorsAsMap(fieldErrors);
		return ResponseEntity.badRequest().body(errorsMap);
	}
	
	protected void afterTx(Runnable callback){
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
			@Override
			public void afterCommit() {
				callback.run();
			}
		});
	}
}
