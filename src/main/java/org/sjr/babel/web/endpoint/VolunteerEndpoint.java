package org.sjr.babel.web.endpoint;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sjr.babel.model.component.Account;
import org.sjr.babel.model.entity.MeetingRequest;
import org.sjr.babel.model.entity.MeetingRequest.Direction;
import org.sjr.babel.model.entity.MeetingRequest.Reason;
import org.sjr.babel.model.entity.Volunteer;
import org.sjr.babel.model.entity.reference.FieldOfStudy;
import org.sjr.babel.model.entity.reference.Language;
import org.sjr.babel.web.helper.MailHelper.MailType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
		public @NotNull @Size(min = 1) String firstName, lastName;
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
			return getAdministratorByAccessKey(accessKey).isPresent();
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
		return ok(responseBody);
	}
	
	@RequestMapping(path = "volunteers", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> signUp(@RequestBody @Valid VolunteerSummary input, BindingResult binding) throws IOException {
		
		
		Map<String, String> errors = errorsAsMap(binding.getFieldErrors());
		
		if(!StringUtils.hasText(input.password)){
			errors.put("password", "password cannot be null");
		}
		
		if(!errors.isEmpty()){
			return badRequest(errors);
		}
		
		String query = "select count(x) from Volunteer x where x.mailAddress = :mailAddress";
		Map<String, Object> args = new HashMap<>();
		args.put("mailAddress", input.mailAddress);
		long n = objectStore.count(Volunteer.class, query, args);
		if (n > 0) {
			return conflict(Error.MAIL_ADDRESS_ALREADY_EXISTS);
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
					.map(x -> this.refDataProvider.resolve(FieldOfStudy.class, x))
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
		
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
			@Override
			public void afterCommit() {
				mailHelper.send(MailType.VOLUNTEER_SIGN_UP_CONFIRMATION, "fr", volunteer.getMailAddress(), volunteer.getMailAddress(), input.password);
			}
		});
		
		return successSignUp(volunteer);
	}
	
	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getOne(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<Volunteer> _v = objectStore.getById(Volunteer.class, id);
		if (_v.isPresent()) {
			Volunteer v = _v.get();
			if (!hasAccess(accessKey, v)) {
				return forbidden();
			}
			return ok(new VolunteerSummary(v));
		} else {
			return notFound();
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
	public ResponseEntity<?> update(@PathVariable int id, @RequestBody @Valid VolunteerSummary input, /*BindingResult binding, */@RequestHeader String accessKey) {
		if (input.id!=id) {
			return badRequest();
		} else {
			Optional<Volunteer> _v = this.objectStore.getById(Volunteer.class, id);
			if (!_v.isPresent()) {
				return notFound();
			} 
			Volunteer v = _v.get();
			if (!hasAccess(accessKey, v)) {
				return forbidden();
			}

			String query = "select count(x) from Volunteer x where x.mailAddress = :mailAddress and x.id != :id";
			Map<String, Object> args = new HashMap<>();
			args.put("mailAddress", input.mailAddress);
			args.put("id", id);
			long n = objectStore.count(Volunteer.class, query, args);
			if (n > 0) {
				return conflict(Error.MAIL_ADDRESS_ALREADY_EXISTS);
			}
			
			v.setFirstName(input.firstName);
			v.setLastName(input.lastName);
			v.setMailAddress(input.mailAddress);
			v.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
			v.setPhoneNumber(input.phoneNumber);
			
			if(StringUtils.hasText(input.password)){
				v.getAccount().setPassword(EncryptionUtil.sha256(input.password));
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
					@Override
					public void afterCommit() {
						mailHelper.send(MailType.VOLUNTEER_UPDATE_PASSWORD_CONFIRMATION, "fr", v.getMailAddress(), v.getMailAddress(), input.password);
					}
				});
			}
			
			Date date = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
			if(input.availableForConversation !=null &&  input.availableForConversation.booleanValue()){
				boolean wasNotAvailableForConversationBefore = (v.getAvailableForConversation() == null || !v.getAvailableForConversation());
				
				v.setAvailableForConversation(true);
				if(wasNotAvailableForConversationBefore){
					String q = "select x from MeetingRequest x where x.postDate > :date and x.volunteer is null and x.reason=:reason";
					Map<String, Object> qArs = new HashMap<>();
					qArs.put("date", date);
					qArs.put("reason", Reason.CONVERSATION);
					v.getMeetingRequests().addAll(this.objectStore.find(MeetingRequest.class, q, qArs));
				}
				
			}
			else{
				v.getMeetingRequests().removeIf(x -> Reason.CONVERSATION.equals(x.getReason()));
				v.setAvailableForConversation(input.availableForConversation /*null or false*/);
			}
			
			if(input.availableForInterpreting !=null &&  input.availableForInterpreting.booleanValue() && input.languages != null && !input.languages.isEmpty())
			{
				boolean wasNotAvailableForInterpretingBefore = (v.getAvailableForInterpreting() == null || !v.getAvailableForInterpreting());
				v.setAvailableForInterpreting(true);
				List<Language> languages = input.languages.stream()
						.filter(StringUtils::hasText)
						.map(x -> this.refDataProvider.resolve(Language.class, x))
						.collect(Collectors.toList());
				v.setLanguages(languages);

				if(wasNotAvailableForInterpretingBefore){
					String q = "select x from MeetingRequest x join x.refugee r join r.languages l where x.postDate> :date and x.volunteer is null and x.reason=:reason and l in :languages";
					Map<String, Object> qArs = new HashMap<>();
					qArs.put("date", date);
					qArs.put("reason", Reason.INTERPRETING);
					qArs.put("languages", v.getLanguages());
					v.getMeetingRequests().addAll(this.objectStore.find(MeetingRequest.class, q, qArs));	
				}
			}
			else{
				v.getMeetingRequests().removeIf(x -> Reason.INTERPRETING.equals(x.getReason()));
				v.setAvailableForInterpreting(input.availableForInterpreting /*null or false*/);
			}
			
			
			if(input.availableForSupportInStudies !=null &&  input.availableForSupportInStudies.booleanValue() && input.fieldsOfStudy != null && !input.fieldsOfStudy.isEmpty())
			{
				boolean wasNotAvailableForSupportInStudies = (v.getAvailableForSupportInStudies() == null || !v.getAvailableForSupportInStudies());
				v.setAvailableForSupportInStudies(true);
				List<FieldOfStudy> fieldsOfStudy = input.fieldsOfStudy.stream()
						.filter(StringUtils::hasText)
						.map(x -> this.refDataProvider.resolve(FieldOfStudy.class, x))
						.collect(Collectors.toList());
				v.setFieldsOfStudy(fieldsOfStudy);
								
				if(wasNotAvailableForSupportInStudies){
					String q = "select x from MeetingRequest x join x.refugee r join r.fieldOfStudy f where x.postDate > :date and x.volunteer is null and x.reason=:reason and f in :fieldsOfStudy ";
					Map<String, Object> qArs = new HashMap<>();
					qArs.put("date", date);
					qArs.put("reason", Reason.SUPPORT_IN_STUDIES);
					qArs.put("fieldsOfStudy", v.getFieldsOfStudy());
					v.getMeetingRequests().addAll(this.objectStore.find(MeetingRequest.class, q, qArs));	
				}
			}
			else{
				v.getMeetingRequests().removeIf(x -> Reason.SUPPORT_IN_STUDIES.equals(x.getReason()));
				v.setAvailableForInterpreting(input.availableForSupportInStudies /*null or false*/);
			}
			
			v.setAvailableForActivities(input.availableForActivities);
			v.setActivities(input.activities);
			
			this.objectStore.save(v);
			
			return noContent();
		}
	}
	
	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Void> delete(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<Volunteer> _v = objectStore.getById(Volunteer.class, id);
		if (!_v.isPresent()) {
			return notFound();
		}
		Volunteer v = _v.get();
		if (!hasAccess(accessKey, v)) {
			return forbidden();
		}
		this.objectStore.delete(v);
		return noContent();
	}

	@RequestMapping(path = "/volunteers/{id}/meeting-requests", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getMeetingRequests(@PathVariable int id, @RequestParam(required = false) boolean accepted, @RequestHeader String accessKey) {
		Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
		if (!v.isPresent()) {
			return notFound();
		}
		Volunteer volunteer = v.get();
		if (!hasAccess(accessKey, volunteer)) {
			return forbidden();
		} else {
			
			Set<MeetingRequest> meetings;
			if(!accepted){
				meetings = volunteer.getMeetingRequests().stream().filter(x -> x.getVolunteer() == null).collect(Collectors.toSet());
			}
			else{
				meetings = volunteer.getAcceptedMeetingRequests();
			}
			
			return ok(meetings.stream().map(MeetingRequestSummary::new).collect(Collectors.toList()));
		}
	}
	
	@RequestMapping(path = "/volunteers/{id}/meeting-requests/{mId}", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> acceptMeetingRequest(@PathVariable int id, @PathVariable int mId, @RequestHeader String accessKey, @RequestParam Direction firstContact) {
		Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
		if (!v.isPresent()) {
			return notFound();
		} else if (!hasAccess(accessKey, v.get())) {
			return forbidden();
		}
		
		Optional<MeetingRequest> meeting = objectStore.getById(MeetingRequest.class, mId);
		if (!meeting.isPresent()) {
			return notFound();
		} else {
			MeetingRequest meetingRequest = meeting.get();
			if(meetingRequest.getVolunteer() != null){
				return conflict();
			}
			meetingRequest.setFirstContact(firstContact);
			meetingRequest.setVolunteer(v.get());
			meetingRequest.setAcceptationDate(new Date());
			objectStore.save(meetingRequest);
			
			System.out.println("send mail to "+meetingRequest.getRefugee().getMailAddress());
			return noContent();
		}
	}
	
	@RequestMapping(path = "/volunteers/{id}/meeting-requests/{mId}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<?> cancelMeetingRequest(@PathVariable int id, @PathVariable int mId, @RequestHeader String accessKey) {
		Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
		if (!v.isPresent()) {
			return notFound();
		} else if (!hasAccess(accessKey, v.get())) {
			return forbidden();
		}
		
		Optional<MeetingRequest> meeting = objectStore.getById(MeetingRequest.class, mId);
		if (!meeting.isPresent()) {
			return notFound();
		} else {
			MeetingRequest meetingRequest = meeting.get();
			if(meetingRequest.getVolunteer() != null && meetingRequest.getVolunteer().getId().equals(id)){
				// send email to volunteer.
				meetingRequest.setVolunteer(null);
				meetingRequest.setAcceptationDate(null);
			}
			else{
				meetingRequest.getMatches().removeIf(x -> x.getId().equals(id));
			}
			
			objectStore.save(meetingRequest);
			return noContent();
		}
	}
	
	/*
	@RequestMapping (path="volunteers/{vId}/meeting-requests/{mId}/messages", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getMeetingRequestMessages (@PathVariable int vId, @PathVariable int mId, @RequestHeader String accessKey){
		Optional<Volunteer> _v = objectStore.getById(Volunteer.class, vId);
		if (!_v.isPresent()){
			return notFound();
		}
		Volunteer v = _v.get();
		if (!hasAccess(accessKey, v)){
			return status(HttpStatus.FORBIDDEN);
		}
		Optional<MeetingRequest> _mr = v.getMeetingRequests().stream().filter(x -> x.getId().equals(mId)).findFirst();
		if (!_mr.isPresent()){
			return notFound();
		}
		MeetingRequest mr = _mr.get();
		List<Message> msgs = mr.getMessages();
		return ok(msgs.stream().map(x -> new MessageSummary(mr,x)).collect(Collectors.toList()));
	}
	
	@RequestMapping (path="volunteers/{vId}/meeting-requests/{mId}/messages", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> postMeetingRequestMessage (@PathVariable int vId, @PathVariable int mId, @RequestHeader String accessKey, @Valid @RequestBody MessageSummary input){
		Date now = new Date();
		Optional<Volunteer> _v = objectStore.getById(Volunteer.class, vId);
		if (!_v.isPresent()){
			return notFound();
		}
		Volunteer v = _v.get();
		if (!hasAccess(accessKey, v)){
			return status(HttpStatus.FORBIDDEN);
		}
		Optional<MeetingRequest> _mr = v.getMeetingRequests().stream().filter(x -> x.getId().equals(mId)).findAny();
		if (!_mr.isPresent()){
			return notFound();
		}
		MeetingRequest mr = _mr.get();
		Message m = new Message();
		m.setDirection(Direction.VOLUNTEER_TO_REFUGEE);
		m.setVolunteer(v);
		m.setPostedDate(now);
		m.setText(input.text);
		mr.getMessages().add(m);
		input.from = v.getFullName();
		input.to = mr.getRefugee().getFullName();
		input.postedDate = now;
		return created(null, input);
	}	
	*/
}
