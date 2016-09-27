package org.sjr.babel.web.endpoint;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sjr.babel.entity.Account;
import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.MeetingRequest;
import org.sjr.babel.entity.Volunteer;
import org.sjr.babel.entity.reference.FieldOfStudy;
import org.sjr.babel.entity.reference.Language;
import org.sjr.babel.web.helper.MailHelper.MailType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import util.EncryptionUtil;

@RestController
public class VolunteerEndpoint extends AbstractEndpoint {

	public static class VolunteerSummary {

		public int id;
		
		public @NotNull String mailAddress;
		public @JsonProperty(access = Access.WRITE_ONLY) String password;
		public @NotNull @Size(min=1) String firstName, lastName;
		public @NotNull @Valid AddressSummary address;
		public String civility, phoneNumber;
		public List<String> languages , fieldsOfStudy;
		public Date birthDate;
		public Boolean availableForConversation, availableForInterpreting, availableForSupportInStudies, availableForActivities;
		public String activities;
		
		public VolunteerSummary() {	}
		
		public VolunteerSummary(Volunteer entity) {
			this.id = entity.getId();
			
			this.civility = safeTransform(entity.getCivility(), x -> x.getName());
			this.firstName = entity.getFirstName();
			this.lastName = entity.getLastName();
			this.birthDate = entity.getBirthDate();
			this.mailAddress = entity.getMailAddress();
			this.phoneNumber = entity.getPhoneNumber();
			
			this.address = safeTransform(entity.getAddress(), x -> new AddressSummary(x));
			this.languages = entity.getLanguages().stream().map(x -> x.getName()).collect(Collectors.toList());
			this.fieldsOfStudy = entity.getFieldsOfStudy().stream().map(x -> x.getName()).collect(Collectors.toList());
			
			this.availableForConversation = entity.getAvailableForConversation();
			this.availableForInterpreting = entity.getAvailableForInterpreting();
			this.languages = entity.getLanguages().stream().map(x -> x.getName()).collect(Collectors.toList());
			this.availableForSupportInStudies = entity.getAvailableForSupportInStudies();
			this.fieldsOfStudy = entity.getFieldsOfStudy().stream().map(x -> x.getName()).collect(Collectors.toList());
			this.availableForActivities = entity.getAvailableForActivities();
			this.activities = entity.getActivities();
		}

	}

	private boolean hasAccess(String accessKey, Volunteer v) {
		if (accessKey.startsWith("A-")) {
			Map<String, Object> args = new HashMap<>();
			args.put("ak", accessKey);
			return objectStore
					.findOne(Administrator.class, "select a from Administrator a where a.account.accessKey = :ak", args)
					.isPresent();
		} else {
			return v.getAccount().getAccessKey().equals(accessKey);
		}
	}
	
	private ResponseEntity<Map<String, Object>> successSignUp(Volunteer volunteer) {
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("name", volunteer.getFullName());
		responseBody.put("profile", "V");
		responseBody.put("accessKey", volunteer.getAccount().getAccessKey());
		responseBody.put("id", volunteer.getId());
		return ResponseEntity.ok(responseBody);
	}
	
	@RequestMapping(path = "volunteers", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> signUp(@RequestBody @Valid VolunteerSummary input, BindingResult binding) throws IOException {
		if(binding.hasErrors()){
			return badRequest(binding);
		}
		
		String query = "select count(x) from Volunteer x where x.mailAddress = :mailAddress";
		Map<String, Object> args = new HashMap<>();
		args.put("mailAddress", input.mailAddress);
		long n = objectStore.count(Volunteer.class, query, args);
		if (n > 0) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Error.MAIL_ADDRESS_ALREADY_EXISTS);
		}
		
		Account account = new Account();
		account.setPassword(EncryptionUtil.sha256(input.password));
		account.setAccessKey("V-" + UUID.randomUUID().toString());
		
		Volunteer volunteer = new Volunteer();
		volunteer.setFirstName(input.firstName);
		volunteer.setLastName(input.lastName);
		volunteer.setMailAddress(input.mailAddress);
		volunteer.setAddress(safeTransform(input.address, x -> x.toAddress(refDataProvider)));	
		
		volunteer.setPhoneNumber(input.phoneNumber);
		if(input.fieldsOfStudy!=null){
			List<FieldOfStudy> fieldsOfStudy = input.fieldsOfStudy.stream()
					.map(x->this.refDataProvider.resolve(FieldOfStudy.class, x))
					.collect(Collectors.toList()); 
			volunteer.setFieldsOfStudy(fieldsOfStudy);
		}
		if(input.languages!=null){
			List<Language> languages = input.languages.stream()
					.map(x->this.refDataProvider.resolve(Language.class, x))
					.collect(Collectors.toList()); 
			volunteer.setLanguages(languages);				
		}
		volunteer.setAccount(account);
		this.objectStore.save(volunteer);
		
		this.mailHelper.send(MailType.VOLUNTEER_SIGN_UP_CONFIRMATION, "fr", volunteer.getMailAddress(), volunteer.getMailAddress(), input.password);
		
		return successSignUp(volunteer);
	}
	
	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getFullVolunteer(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<Volunteer> _v = objectStore.getById(Volunteer.class, id);
		if (_v.isPresent()) {
			Volunteer v = _v.get();
			if (!hasAccess(accessKey, v)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			return ResponseEntity.ok(new VolunteerSummary(v));
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	static class ValidationError{
		public String field;
		public String message;
		
		public ValidationError(FieldError fieldError) {
			super();
			this.field = fieldError.getField();
			this.message = fieldError.getDefaultMessage();
		}
	}
	
	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> updateVolunteer(@PathVariable int id, @RequestBody @Valid VolunteerSummary input, BindingResult binding, @RequestHeader String accessKey) {
		if (input.id!=id) {
			return ResponseEntity.badRequest().build();
		} else {
			Optional<Volunteer> _v = this.objectStore.getById(Volunteer.class, id);
			if (!_v.isPresent()) {
				return ResponseEntity.notFound().build();
			} 
			Volunteer v = _v.get();
			if (!hasAccess(accessKey, v)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			System.out.println(input.firstName);
			System.out.println(binding.getFieldErrorCount());
			if(binding.hasErrors()){
				
				return badRequest(binding);
			}
			
			String query = "select count(x) from Volunteer x where x.mailAddress = :mailAddress and x.id != :id";
			Map<String, Object> args = new HashMap<>();
			args.put("mailAddress", input.mailAddress);
			args.put("id", id);
			long n = objectStore.count(Volunteer.class, query, args);
			if (n > 0) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(Error.MAIL_ADDRESS_ALREADY_EXISTS);
			}
			
			v.setFirstName(input.firstName);
			v.setLastName(input.lastName);
			v.setMailAddress(input.mailAddress);
			v.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
			v.setPhoneNumber(input.phoneNumber);
			
			if(StringUtils.hasText(input.password)){
				v.getAccount().setPassword(EncryptionUtil.sha256(input.password));
				this.mailHelper.send(MailType.VOLUNTEER_UPDATE_PASSWORD_CONFIRMATION, "fr", v.getMailAddress(), v.getMailAddress(), input.password);
			}
			
			v.setAvailableForConversation(input.availableForConversation);
			
			v.setAvailableForInterpreting(input.availableForInterpreting);
			if (input.languages != null && !input.languages.isEmpty()) {
				List<Language> languages = input.languages.stream()
						.filter(StringUtils::hasText)
						.map(x -> this.refDataProvider.resolve(Language.class, x))
						.collect(Collectors.toList()); 
				v.setLanguages(languages);	
			}
			else{
				v.setLanguages(null);
			}
			
			v.setAvailableForSupportInStudies(input.availableForSupportInStudies);
			if (input.fieldsOfStudy != null && !input.fieldsOfStudy.isEmpty()) {
				List<FieldOfStudy> fieldsOfStudy = input.fieldsOfStudy.stream()
						.filter(StringUtils::hasText)
						.map(x -> this.refDataProvider.resolve(FieldOfStudy.class, x))
						.collect(Collectors.toList());
				v.setFieldsOfStudy(fieldsOfStudy);
			}
			else{
				v.setFieldsOfStudy(null);
			}
			
			v.setAvailableForActivities(input.availableForActivities);
			v.setActivities(input.activities);
			
			this.objectStore.save(v);
			
			return ResponseEntity.noContent().build();
		}
	}
	
	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Void> deleteVolunteer(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<Volunteer> _v = objectStore.getById(Volunteer.class, id);
		if (_v.isPresent()) {
			Volunteer v = _v.get();
			if (!hasAccess(accessKey, v)) {
				return ResponseEntity.badRequest().build();
			}
			this.objectStore.delete(v);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

	@RequestMapping(path = "/volunteers/{id}/meeting-requests", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getMeetingRequests(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
		if (!v.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Volunteer volunteer = v.get();
		if (!hasAccess(accessKey, volunteer)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} else {
			List<MeetingRequest> meetings = volunteer.getMeetingRequests();
			return ResponseEntity.ok(meetings.stream().map(MeetingRequestSummary::new).collect(Collectors.toList()));
		}
	}
	
	@RequestMapping(path = "/volunteers/{id}/meeting-requests/{meetingRequestId}", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> AcceptMeetingRequest(@PathVariable int id, @PathVariable int meetingRequestId, @RequestHeader String accessKey) {
		Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
		if (!v.isPresent()) {
			return ResponseEntity.notFound().build();
		} else if (!hasAccess(accessKey, v.get())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		Optional<MeetingRequest> meeting = objectStore.getById(MeetingRequest.class, meetingRequestId);
		if (!meeting.isPresent()) {
			return ResponseEntity.notFound().build();
		} else {
			MeetingRequest meetingRequest = meeting.get();
			if(meetingRequest.getVolunteer()!=null){
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
			
			meetingRequest.setVolunteer(v.get());
			meetingRequest.setAcceptedDate(new Date());
			objectStore.save(meetingRequest);
			
			System.out.println("send mail to "+meetingRequest.getRefugee().getMailAddress());
			return ResponseEntity.ok().build();
		}
	}
}
