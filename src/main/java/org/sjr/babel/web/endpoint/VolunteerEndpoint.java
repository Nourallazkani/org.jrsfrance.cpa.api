package org.sjr.babel.web.endpoint;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.sjr.babel.entity.Volunteer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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



	@RequestMapping(path = "/volunteers", method = RequestMethod.GET)
	@Transactional
	public List<VolunteerSummary> list(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer languageId, 
			@RequestParam(required = false) String city,
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

		return objectStore.find(Volunteer.class, hql.toString(),args).stream()
				.map(VolunteerSummary::new) /*
											 * ou bien .map(x -> new
											 * VolunteerSummary(x))
											 */
				.collect(Collectors.toList());
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
