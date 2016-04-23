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
		public String civility, firstName, lastName, phoneNumber;
		public List<String> languages;
		public Date birthDate;

		public VolunteerSummary(Volunteer v) {
			this.id = v.getId();
			this.civility = v.getCivility().getName();
			this.firstName = v.getFirstName();
			this.lastName = v.getLastName();
			this.birthDate = v.getBirthDate();
			this.phoneNumber = v.getPhoneNumber();
			this.languages = v.getLanguages().stream().map(x -> x.getName()).collect(Collectors.toList());
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
			@RequestParam(required = false) Integer languageId, @RequestParam(required = false) String city,
			@RequestParam(required = false) String zipcode) {

		StringBuffer hql = new StringBuffer("select v from Volunteer v join fetch v.languages l  where 0=0 ");
		Map<String, Object> args = new HashMap<>();
		if (languageId != null) {
			hql.append("and l.id = :languageId ");
			args.put("languageId", languageId);
		}
		if (name != null) {
			args.put("name", name);
			hql.append(" and v.firstName like :name or v.lastName like :name ");
		}
		if (city != null) {
			args.put("city", city);
			hql.append(" and v.address.city like :city");
		}
		if (zipcode != null) {
			args.put("zipcode", zipcode);
			hql.append(" and v.address.zipcode like :zipcode");
		}

		return objectStore.find(Volunteer.class, hql.toString(), args).stream()
				.map(VolunteerSummary::new) /*
											 * ou bien .map(x -> new
											 * VolunteerSummary(x))
											 */
				.collect(Collectors.toList());
	}

	@RequestMapping(path = "/{id}/meetingRequests", method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> getMeetingRequests(@PathVariable int id, @RequestHeader String accsessKey) {
		Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
		if (!v.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Volunteer volunteer = v.get();
		if (!hasAccess(accsessKey, volunteer)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else {
			Map<String, Object> args = new HashMap<>();
			args.put("id", id);
			List<MeetingRequest> meetings = objectStore.find(MeetingRequest.class,
					"select mr from MeetingRequests mr where mr.Volunteer_id like :id", args);
			return ResponseEntity.ok(meetings);
		}
	}

	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> getFullVolunteer(@PathVariable int id) {
		/*
		 * //Other way to create this method
		 * if(objectStore.getById(Volunteer.class, id) == null){ return
		 * ResponseEntity.badRequest().build(); } return
		 * ResponseEntity.ok(objectStore.getById(Volunteer.class, id));
		 */
		Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
		if (v.isPresent()) {
			return ResponseEntity.ok(v.get());
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<Void> deleteVolunteer(@PathVariable int id) {
		Optional<Volunteer> v = objectStore.getById(Volunteer.class, id);
		if (v.isPresent()) {
			objectStore.delete(v.get());
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();

	}

	@RequestMapping(path = "/{id}/meetingRequests/{meetingRequestId}", method = RequestMethod.POST)
	@Transactional
	@RolesAllowed({ "ADMIN", "VOLNTEER" })
	public ResponseEntity<?> updateMeetingRequest(@RequestBody MeetingRequest mr, @RequestHeader String accessKey,
			@PathVariable int id, @PathVariable int meetingRequestId) {
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
			objectStore.save(mr);
			return ResponseEntity.ok().build();
		}
	}
	
	
	
	
	@RequestMapping(path = "/volunteers", method = RequestMethod.POST)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> saveVolunteer(@RequestBody Volunteer v) {
		if (v.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		objectStore.save(v);
		return ResponseEntity.created(getUri("/volunteers/" + v.getId())).body(v);
	}

	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.PUT)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> updateVolunteer(@PathVariable int id, @RequestBody Volunteer v) {
		if (v.getId() == null || !(v.getId() == id)) {
			return ResponseEntity.badRequest().build();
		} else {
			objectStore.save(v);
			return ResponseEntity.noContent().build();
		}

	}
}
