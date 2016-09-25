package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.MeetingRequest;
import org.sjr.babel.entity.MeetingRequest.Reason;
import org.sjr.babel.entity.Refugee;
import org.sjr.babel.entity.Volunteer;
import org.sjr.babel.entity.reference.Country;
import org.sjr.babel.entity.reference.FieldOfStudy;
import org.sjr.babel.entity.reference.Language;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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

	public static class RefugeeSummary {

		public int id;
		public String nationality;
		public String mailAddress;
		public @JsonProperty(access = Access.WRITE_ONLY) String password;
		public String civility, firstName, lastName, phoneNumber;
		public List<String> languages;
		public String fieldOfStudy;
		public Date birthDate;
		
		public RefugeeSummary() {}
		
		public RefugeeSummary(Refugee r) {
			this.id = r.getId();
			this.civility = safeTransform(r.getCivility(), x -> x.getName());
			this.nationality = safeTransform(r.getNationality(), x -> x.getName());
			this.firstName = r.getFirstName();
			this.lastName = r.getLastName();
			this.mailAddress = r.getMailAddress();
			this.birthDate = r.getBirthDate();
			this.phoneNumber = r.getPhoneNumber();
			this.fieldOfStudy = r.getFieldOfStudy().getName();
			this.languages = r.getLanguages().stream().map(x -> x.getName()).collect(Collectors.toList());
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public List<RefugeeSummary> list(@RequestParam(required = false) String name,
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
	public ResponseEntity<?> getRefugeeSummary(@PathVariable int id, @RequestHeader String accessKey) {

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
	public ResponseEntity<?> update(@PathVariable int id, @RequestBody RefugeeSummary input, @RequestHeader String accessKey) {
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
			
			r.setFirstName(input.firstName);
			r.setLastName(input.lastName);
			r.setMailAddress(input.mailAddress);
			r.setPhoneNumber(input.phoneNumber);
			if(StringUtils.hasText(input.password)){
				r.getAccount().setPassword(EncryptionUtil.sha256(input.password));
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
	public ResponseEntity<Void> deleteRefugee(@PathVariable int id, @RequestHeader String accessKey) {
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
	public ResponseEntity<?> createMeetingRequest(@PathVariable int id, @RequestBody MeetingRequestSummary input, @RequestHeader String accessKey) {

		Optional<Refugee> r = objectStore.getById(Refugee.class, id);
		Refugee refugee = r.get();
		if (!r.isPresent()) {
			return ResponseEntity.badRequest().build();
		} else if (!hasAccess(accessKey, refugee)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		MeetingRequest mr = new MeetingRequest();
		mr.setRefugeeLocation(input.refugeeLocation.toAddress(refDataProvider));
		mr.setPostDate(new Date());
		mr.setRefugee(refugee);
		mr.setStartDate(input.startDate);
		mr.setEndDate(input.endDate);
		mr.setReason(input.reason);
		mr.setAdditionalInformations(input.additionalInformations);
		
		Map<String, Object> args = new HashMap<>();
		args.put("available", true);
		List<Volunteer> matches = new ArrayList<>();
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
			matches = new ArrayList<>();
		}
		
		mr.setMatchesCount(matches.size());
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
			return ResponseEntity.notFound().build();
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
}
