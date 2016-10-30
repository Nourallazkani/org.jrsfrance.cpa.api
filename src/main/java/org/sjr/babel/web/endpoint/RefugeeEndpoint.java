package org.sjr.babel.web.endpoint;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.sjr.babel.model.component.Account;
import org.sjr.babel.model.component.Message;
import org.sjr.babel.model.component.Message.Direction;
import org.sjr.babel.model.entity.Administrator;
import org.sjr.babel.model.entity.MeetingRequest;
import org.sjr.babel.model.entity.Refugee;
import org.sjr.babel.model.entity.Volunteer;
import org.sjr.babel.model.entity.MeetingRequest.Reason;
import org.sjr.babel.model.entity.reference.Country;
import org.sjr.babel.model.entity.reference.FieldOfStudy;
import org.sjr.babel.model.entity.reference.Language;
import org.sjr.babel.web.helper.MailHelper.MailType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
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
@RequestMapping(path = "/refugees")
public class RefugeeEndpoint extends AbstractEndpoint {

	static class RefugeeSummary {

		public int id;
		public String nationality;
		public String mailAddress;
		public @JsonProperty(access = Access.WRITE_ONLY) String password;
		public String civility, firstName, lastName, phoneNumber;
		public AddressSummary address;
		public List<String> languages;
		public String fieldOfStudy;
		public Date birthDate;
		
		RefugeeSummary() {}
		
		RefugeeSummary(Refugee entity) {
			this.id = entity.getId();
			this.civility = safeTransform(entity.getCivility(), x -> x.getName());
			this.nationality = safeTransform(entity.getNationality(), x -> x.getName());
			this.firstName = entity.getFirstName();
			this.lastName = entity.getLastName();
			this.mailAddress = entity.getMailAddress();
			this.address = safeTransform(entity.getAddress(), AddressSummary::new);
			this.birthDate = entity.getBirthDate();
			this.phoneNumber = entity.getPhoneNumber();
			this.fieldOfStudy = safeTransform(entity.getFieldOfStudy(), x->x.getName());
			this.languages = entity.getLanguages().stream().map(x -> x.getName()).collect(Collectors.toList());
		}
	}
	
	private ResponseEntity<Map<String, Object>> successSignUp(Refugee refugee) {
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("name", refugee.getFullName());
		responseBody.put("profile", "R");
		responseBody.put("accessKey", refugee.getAccount().getAccessKey());
		responseBody.put("id", refugee.getId());
		return ResponseEntity.ok(responseBody);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> signUp(@RequestBody @Valid RefugeeSummary input, BindingResult binding) throws IOException {

		
		Map<String, String> errors = errorsAsMap(binding.getFieldErrors());
		if(!StringUtils.hasText(input.password)){
			errors.put("password",  "NotNull");
		}
		if(!errors.isEmpty()){
			return ResponseEntity.badRequest().body(errors);
		}
		
		String query = "select count(x) from Refugee x where x.mailAddress = :mailAddress";
		Map<String, Object> args = new HashMap<>();
		args.put("mailAddress", input.mailAddress);
		long n = objectStore.count(Volunteer.class, query, args);
		if (n > 0) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Error.MAIL_ADDRESS_ALREADY_EXISTS);
		}
		
		Account account = new Account();
		account.setPassword(EncryptionUtil.sha256(input.password));
		account.setAccessKey("R" + "-" + UUID.randomUUID().toString());
		
		Refugee refugee = new Refugee();
		refugee.setFirstName(input.firstName);
		refugee.setLastName(input.lastName);
		refugee.setMailAddress(input.mailAddress);
		refugee.setAccount(account);
		refugee.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
		refugee.setPhoneNumber(input.phoneNumber);
		refugee.setFieldOfStudy(safeTransform(input.fieldOfStudy, x -> this.refDataProvider.resolve(FieldOfStudy.class, x)));
		if(input.languages!=null){
			List<Language> languages = input.languages.stream()
					.map(x->this.refDataProvider.resolve(Language.class, x))
					.collect(Collectors.toList()); 
			refugee.setLanguages(languages);				
		}			
			
		this.objectStore.save(refugee);
		
		this.mailHelper.send(MailType.REFUGEE_SIGN_UP_CONFIRMATION, "fr", refugee.getMailAddress(), refugee.getMailAddress(), input.password);
		return  successSignUp(refugee);
		
	}

	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public List<RefugeeSummary> getFullRefugee(@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer languageId, @RequestParam(required = false) String city,
			@RequestParam(required = false) String zipcode) {

		StringBuffer hql = new StringBuffer("select r from Refugee r join fetch r.languageSkills l  where 0=0 ");
		Map<String, Object> args = new HashMap<>();
		if (languageId != null) {
			hql.append("and l.id = :languageId ");
			args.put("languageId", languageId);
		}
		if (name != null) {
			args.put("name", name);
			hql.append(" and r.firstName like :name or r.lastName like :name ");
		}
		if (city != null) {
			args.put("city", city);
			hql.append(" and r.address.city like :city");
		}
		if (zipcode != null) {
			args.put("zipcode", zipcode);
			hql.append(" and r.address.zipcode like :zipcode");
		}

		return objectStore.find(Refugee.class, hql.toString(), args).stream().map(RefugeeSummary::new)
				.collect(Collectors.toList());
	}

	private boolean hasAccess(String accessKey, Refugee r) {
		if (accessKey.startsWith("A-")) {
			Map<String, Object> args = new HashMap<>();
			args.put("ak", accessKey);
			return objectStore
					.findOne(Administrator.class, "select a from Administrator a where a.account.accessKey = :ak", args)
					.isPresent();
		} else {
			return r.getAccount().getAccessKey().equals(accessKey);
		}
	}

	@RequestMapping(path = "{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> search(@PathVariable int id, @RequestHeader String accessKey) {

		Optional<Refugee> r = objectStore.getById(Refugee.class, id);
		if (!r.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		else if (!hasAccess(accessKey, r.get())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		else{
			return ResponseEntity.ok(new RefugeeSummary(r.get()));
		}
	}
	
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> update(@PathVariable int id, @RequestBody @Valid RefugeeSummary input, @RequestHeader String accessKey) {
		if (input.id != id) {
			return ResponseEntity.badRequest().build();
		} else {
			Optional<Refugee> _r = this.objectStore.getById(Refugee.class, id);
			if (!_r.isPresent()) {
				return ResponseEntity.notFound().build();
			} 
			Refugee r = _r.get();
			if (!hasAccess(accessKey, r)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			
			String query = "select count(x) from Refugee x where x.mailAddress = :mailAddress and x.id != :id";
			Map<String, Object> args = new HashMap<>();
			args.put("mailAddress", input.mailAddress);
			args.put("id", id);
			long n = objectStore.count(Volunteer.class, query, args);
			if (n > 0) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(Error.MAIL_ADDRESS_ALREADY_EXISTS);
			}
			
			r.setFirstName(input.firstName);
			r.setLastName(input.lastName);
			r.setMailAddress(input.mailAddress);
			r.setPhoneNumber(input.phoneNumber);
			r.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
			if(StringUtils.hasText(input.password)){
				r.getAccount().setPassword(EncryptionUtil.sha256(input.password));
				this.mailHelper.send(MailType.REFUGEE_UPDATE_PASSWORD_CONFIRMATION, "fr", r.getMailAddress(), r.getMailAddress(), input.password);
			}
			
			r.setNationality(safeTransform(input.nationality, x -> this.refDataProvider.resolve(Country.class, x)));
			r.setFieldOfStudy(safeTransform(input.fieldOfStudy, x -> this.refDataProvider.resolve(FieldOfStudy.class, x)));
			if(input.languages!=null){
				r.getLanguages().clear();
				input.languages.stream().map(x -> this.refDataProvider.resolve(Language.class, x)).forEach(r.getLanguages()::add);
			}
			
			this.objectStore.save(r);
			
			return ResponseEntity.noContent().build();
		}
	}
	
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Void> delete(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<Refugee> _r = objectStore.getById(Refugee.class, id);
		if(!_r.isPresent()){
			return ResponseEntity.notFound().build();
		}
		Refugee r = _r.get();
		if(!hasAccess(accessKey, r)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		objectStore.delete(r);
		return ResponseEntity.noContent().build();
	}

	
	//private ExecutorService executor = Executors.newFixedThreadPool(100);
	
	@RequestMapping(path = "/{id}/meeting-requests", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getMeetingRequests(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<Refugee> r = objectStore.getById(Refugee.class, id);
		if (!r.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Refugee refugee = r.get();
		if (!hasAccess(accessKey, refugee)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} else {
			List<MeetingRequest> meetings = refugee.getMeetingRequests();
			return ResponseEntity.ok(meetings.stream().map(MeetingRequestSummary::new).collect(Collectors.toList()));
		}
	}

	
	@RequestMapping(path = "/{id}/meeting-requests", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> createMeetingRequest(@PathVariable int id, @Valid @RequestBody MeetingRequestSummary input, @RequestHeader String accessKey) {

		Optional<Refugee> r = objectStore.getById(Refugee.class, id);
		Refugee refugee = r.get();
		if (!r.isPresent()) {
			return ResponseEntity.notFound().build();
		} else if (!hasAccess(accessKey, refugee)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		

		MeetingRequest mr = new MeetingRequest();
		mr.setRefugeeLocation(input.refugeeLocation.toAddress(refDataProvider));
		mr.setPostDate(new Date());
		mr.setRefugee(refugee);
		mr.setDateConstraint(input.dateConstraint);
		mr.setReason(input.reason);
		mr.setAdditionalInformations(input.additionalInformations);
		
		Map<String, Object> args = new HashMap<>();
		args.put("available", true);
		Set<Volunteer> matches = new HashSet<>();
		if (Reason.INTERPRETING.equals(input.reason)) {
			String query = "select v from Volunteer v join v.languages l where v.availableForInterpreting = :available and l in :languages";
			args.put("languages", refugee.getLanguages());
			matches.addAll(this.objectStore.find(Volunteer.class, query, args));
			
		} else if (Reason.SUPPORT_IN_STUDIES.equals(input.reason)) {
			String query = "select v from Volunteer v join v.fieldsOfStudy f where v.availableForSupportInStudies = :available and f = :fieldOfStudies";
			args.put("fieldOfStudies", refugee.getFieldOfStudy());
			matches.addAll(this.objectStore.find(Volunteer.class, query, args));
		}
		else{
			matches = new HashSet<>();
		}
		
		mr.setMatches(matches);

		this.objectStore.save(mr);
		for (Volunteer volunteer : matches) {
			String link = String.format("http://localhost:9000/volunteers/meeting-requests?a=a&id=%s&ak=%s", mr.getId(), volunteer.getAccount().getAccessKey());
			System.out.println("send mail to "+volunteer.getFullName()+", link : "+link);
		}
				
		URI uri = getUri("/refugees/" + refugee.getId() + "/meeting-requests/" + mr.getId());
		return ResponseEntity.created(uri).body(new MeetingRequestSummary(mr));
	}
	
	@RequestMapping(path = "/{id}/meeting-requests/{meetingRequestId}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<?> deleteMeetingRequest(@PathVariable int id, @PathVariable int meetingRequestId, @RequestHeader String accessKey) {
		Optional<MeetingRequest> _mr = objectStore.getById(MeetingRequest.class, meetingRequestId);
		if (!_mr.isPresent()) {
			return notFound();
		} else {
			MeetingRequest mr = _mr.get();
			Refugee refugee = mr.getRefugee();
			if (!hasAccess(accessKey, refugee)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			this.objectStore.delete(mr);
			return ResponseEntity.noContent().build();
		}
	}
	
	@RequestMapping (path="/{rId}/meeting-requests/{mId}/messages", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getMeetingRequestMessages (@PathVariable int rId, @PathVariable int mId, @RequestHeader String accessKey){
		Optional<Refugee> _r = objectStore.getById(Refugee.class, rId);
		if (!_r.isPresent()){
			return ResponseEntity.notFound().build();
		}
		Refugee r = _r.get();
		if (!hasAccess(accessKey, r)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		Optional<MeetingRequest> _mr = r.getMeetingRequests().stream().filter(x -> x.getId().equals(mId)).findAny();
		if (!_mr.isPresent()){
			return ResponseEntity.notFound().build();
		}
		MeetingRequest mr = _mr.get();
		List<Message> msgs = mr.getMessages();
		return ResponseEntity.ok(msgs.stream().map(x -> new MessageSummary(mr,x)).collect(Collectors.toList()));
	}
	
	@RequestMapping (path="/{rId}/metting-requests/{mId}/messages", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> postMeetingRequestMessage (@PathVariable int rId, @PathVariable int mId, @RequestHeader String accessKey, @RequestBody @Valid MessageSummary input){
		Date now = new Date();
		Optional<Refugee> _r = objectStore.getById(Refugee.class, rId);
		if (!_r.isPresent()){
			return notFound();
		}
		Refugee r = _r.get();
		if (!hasAccess(accessKey, r)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			/* return forbidden(); */
		}
		Optional<MeetingRequest> _mr = r.getMeetingRequests().stream().filter(x -> x.getId().equals(mId)).findAny();
		if (!_mr.isPresent()){
			return notFound();
		}
		MeetingRequest mr = _mr.get();
		Message m = new Message();
		m.setDirection(Direction.REFUGEE_TO_VOLUNTEER);
		Optional<Volunteer> _to = mr.getMatches().stream().filter(x-> x.getFullName().equals(input.to)).findAny();
		if (!_to.isPresent()){
			return badRequest();
		}
		m.setVolunteer(_to.get());
		m.setPostedDate(now);
		m.setText(input.text);
		mr.getMessages().add(m);
		input.from = r.getFullName();
		input.postedDate = now;
		return created(null, input);
	}
}
