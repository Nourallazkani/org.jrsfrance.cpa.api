package org.sjr.babel.web.endpoint;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.MeetingRequest;
import org.sjr.babel.entity.Volunteer;
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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import util.EncryptionUtil;

@RestController
public class VolunteerEndpoint extends AbstractEndpoint {

	public static class VolunteerSummary {

		public int id;
		public String mailAddress;
		public @JsonProperty(access = Access.WRITE_ONLY) String password;
		public String civility, firstName, lastName, phoneNumber;
		public AddressSummary address;
		public List<String> languages , fieldsOfStudy;
		public Date birthDate;
		public Boolean availableForConversation, availableForInterpreting, availableForSupportInStudies, availableForActivities;
		public String activities;
		
		public VolunteerSummary() {	}
		
		public VolunteerSummary(Volunteer v) {
			this.id = v.getId();
			
			this.civility = safeTransform(v.getCivility(), x -> x.getName());
			this.firstName = v.getFirstName();
			this.lastName = v.getLastName();
			this.birthDate = v.getBirthDate();
			this.mailAddress = v.getMailAddress();
			this.phoneNumber = v.getPhoneNumber();
			
			this.address = safeTransform(v.getAddress(), x -> new AddressSummary(x));
			this.languages = v.getLanguages().stream().map(x -> x.getName()).collect(Collectors.toList());
			this.fieldsOfStudy = v.getFieldsOfStudy().stream().map(x -> x.getName()).collect(Collectors.toList());
			
			this.availableForConversation = v.getAvailableForConversation();
			this.availableForInterpreting = v.getAvailableForInterpreting();
			this.languages = v.getLanguages().stream().map(x->x.getName()).collect(Collectors.toList());
			this.availableForSupportInStudies = v.getAvailableForSupportInStudies();
			this.fieldsOfStudy = v.getFieldsOfStudy().stream().map(x -> x.getName()).collect(Collectors.toList());
			this.availableForActivities = v.getAvailableForActivities();
			this.activities = v.getActivities();
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

	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> updateVolunteer(@PathVariable int id, @RequestBody VolunteerSummary input, @RequestHeader String accessKey) {
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
			
			v.setFirstName(input.firstName);
			v.setLastName(input.lastName);
			v.setMailAddress(input.mailAddress);
			v.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
			v.setPhoneNumber(input.phoneNumber);
			
			if(StringUtils.hasText(input.password)){
				v.getAccount().setPassword(EncryptionUtil.sha256(input.password));
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
