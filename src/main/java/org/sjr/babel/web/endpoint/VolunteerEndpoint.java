package org.sjr.babel.web.endpoint;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
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

		public VolunteerSummary() {	}
		
		public VolunteerSummary(Volunteer v) {
			this.id = v.getId();
			this.civility = v.getCivility().getName();
			this.firstName = v.getFirstName();
			this.lastName = v.getLastName();
			this.birthDate = v.getBirthDate();
			this.mailAddress = v.getMailAddress();
			this.phoneNumber = v.getPhoneNumber();
			this.address = new AddressSummary(v.getAddress());
			this.languages = v.getLanguages().stream().map(x -> x.getName()).collect(Collectors.toList());
			this.fieldsOfStudy = v.getFieldsOfStudy().stream().map(x -> x.getName()).collect(Collectors.toList());
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
			v.setAddress(input.address.toAddress(this.refDataProvider));
			v.setPhoneNumber(input.phoneNumber);
			if(StringUtils.hasText(input.password)){
				v.getAccount().setPassword(EncryptionUtil.sha256(input.password));
			}
			if (input.languages != null && !input.languages.isEmpty()) {
				v.setLanguages(input.languages.stream().map(x -> this.refDataProvider.resolve(Language.class, x)).collect(Collectors.toList()));	
			}
			if (input.fieldsOfStudy != null && !input.fieldsOfStudy.isEmpty()) {
				v.setFieldsOfStudy(input.fieldsOfStudy.stream().map(x -> this.refDataProvider.resolve(FieldOfStudy.class, x)).collect(Collectors.toList()));
			}
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

	@RequestMapping(path = "/{id}/meetingRequests", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getMeetingRequests(@PathVariable int id, @RequestHeader String accsessKey) {
		Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
		if (!v.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Volunteer volunteer = v.get();
		if (!hasAccess(accsessKey, volunteer)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} else {
			List<MeetingRequest> meetings = volunteer.getMeetingRequests();
			return ResponseEntity.ok(meetings);
		}
	}
	
	public static class MeetingRequestAcceptationCommand {
		public boolean accepted;
	}
	
	@RequestMapping(path = "/{id}/meetingRequests/{meetingRequestId}", method = RequestMethod.POST)
	@Transactional
	@RolesAllowed({ "ADMIN", "VOLUNTEER" })
	public ResponseEntity<?> AcceptMeetingRequest(@PathVariable int id, @PathVariable int meetingRequestId, @RequestBody MeetingRequestAcceptationCommand accepted, @RequestHeader String accessKey) {
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
			meetingRequest.setAccepted(accepted.accepted);
			
			objectStore.save(meetingRequest);
			return ResponseEntity.ok().build();
		}
	}
}
