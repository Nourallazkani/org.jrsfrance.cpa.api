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

@RestController
public class VolunteerEndpoint extends AbstractEndpoint {

	class VolunteerSummary {

		public int id;
		public String civility, firstname, lastname, phoneNumber;
		public AddressSummary address;
		public List<String> languages , fieldsOfStudy;
		public Date birthDate;

		public VolunteerSummary(Volunteer v) {
			this.id = v.getId();
			this.civility = v.getCivility().getName();
			this.firstname = v.getFirstName();
			this.lastname = v.getLastName();
			this.birthDate = v.getBirthDate();
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

	@RequestMapping(path = "/volunteers", method = RequestMethod.GET)
	@Transactional
	public List<VolunteerSummary> list(@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer languageId,
			@RequestParam(required = false) Integer fieldOfStudyId,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String zipcode) {

		StringBuffer hql = new StringBuffer("select distinct v from Volunteer v ");
		// fetch supplémentaire mais seulement si nécessaire
		if (languageId != null) {
			hql.append("left join v.languages l ");
		}
		if (fieldOfStudyId != null) {
			hql.append("left join v.fieldsOfStudy f ");
		}
		hql.append("where 0=0 ");
		Map<String, Object> args = new HashMap<>();
		if (languageId != null) {
			hql.append("and l.id = :languageId ");
			args.put("languageId", languageId);
		}
		if (fieldOfStudyId != null) {
			hql.append("and f.id = :fieldOfStudyId ");
			args.put("fieldOfStudyId", fieldOfStudyId);
		}
		if (StringUtils.hasText(name)) {
			args.put("name", name);
			hql.append("and v.firstName like :name or v.lastName like :name ");
		}
		if (StringUtils.hasText(city)) {
			args.put("city", city);
			hql.append("and v.address.city like :city ");
		}
		if (zipcode != null) {
			args.put("zipcode", zipcode);
			hql.append("and v.address.zipcode like :zipcode");
		}

		return objectStore.find(Volunteer.class, hql.toString(), args).stream()
				.map(VolunteerSummary::new) /*
											 * ou bien .map(x -> new
											 * VolunteerSummary(x))
											 */
				.collect(Collectors.toList());
	}

	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getFullVolunteer(@PathVariable int id) {
		/*
		 * //Other way to create this method
		 * if(objectStore.getById(Volunteer.class, id) == null){ return
		 * ResponseEntity.badRequest().build(); } return
		 * ResponseEntity.ok(objectStore.getById(Volunteer.class, id));
		 */
		Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
		if (v.isPresent()) {
			return ResponseEntity.ok(new VolunteerSummary(v.get()));
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
			// TODO update volunteer
			return ResponseEntity.noContent().build();
		}
	}
	

	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Void> deleteVolunteer(@PathVariable int id) {
		Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
		if (v.isPresent()) {
			objectStore.delete(v.get());
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
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
	public ResponseEntity<?> AcceptMeetingRequest(@RequestBody MeetingRequestAcceptationCommand accepted,
			@RequestHeader String accessKey, @PathVariable int id, @PathVariable int meetingRequestId) {
		Optional<MeetingRequest> meeting = objectStore.getById(MeetingRequest.class, meetingRequestId);
		if (!meeting.isPresent()) {
			// TODO to inform the user that there is no meeting/volunteer found.
			return ResponseEntity.notFound().build();
		} else {
			Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
			if (!v.isPresent()) {
				return ResponseEntity.badRequest().build();
			} else if (!hasAccess(accessKey, v.get())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			MeetingRequest meetingRequest = meeting.get();
			meetingRequest.setAccepted(accepted.accepted);
			if(accepted.accepted){
				for( MeetingRequest m : v.get().getMeetingRequests()){
					if (m.getAccepted() == null){
						m.setAccepted(false);
					}
				}
			}
			
			objectStore.save(meetingRequest);
			return ResponseEntity.ok().build();
		}
	}
}
