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
import org.sjr.babel.entity.reference.Language;
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
		public Date birthDate ;

		public VolunteerSummary(Volunteer v) {
			this.id = v.getId();
			this.civility = v.getCivility().getName();
			this.firstName = v.getFirstName();
			this.lastName = v.getLastName();
			this.birthDate = v.getBirthDate();
			this.phoneNumber = v.getPhoneNumber();
			this.languages = v.getLanguages().stream().map(x-> x.getName()).collect(Collectors.toList());
		}

	}

	@RequestMapping(path = "/volunteers", method = RequestMethod.GET)
	@Transactional
	public List<VolunteerSummary> list(@RequestParam ( name = "name" , defaultValue = "%",required=false) String name ) {
		Map<String, Object> args = new HashMap<>();
		args.put("name", name );
		return objectStore.find(Volunteer.class, "select v from Volunteer v where v.firstName like :name or v.lastName like :name ",args)
				.stream()
				.map(VolunteerSummary::new)
				.collect(Collectors.toList());
	}
	
	

	@RequestMapping(path = "/volunteers/{id}", method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> getFullVolunteer(@PathVariable int id) {
		/*
		 * //Other way to create this method if
		 * (objectStore.getById(Volunteer.class, id) == null){ return
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
