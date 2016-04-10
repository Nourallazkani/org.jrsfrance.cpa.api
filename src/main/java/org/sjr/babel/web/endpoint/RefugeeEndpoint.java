package org.sjr.babel.web.endpoint;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.sjr.babel.entity.Refugee;
import org.sjr.babel.entity.Volunteer;
import org.sjr.babel.entity.AbstractEvent;
import org.sjr.babel.web.endpoint.VolunteerEndpoint.VolunteerSummary;
import org.sjr.babel.web.endpoint.EventEndpoint.EventSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping(path = "/refugees")
public class RefugeeEndpoint extends AbstractEndpoint {

	class RefugeeSummary {

		public int id;
		public String civility, firstName, lastName, phoneNumber;
		public List<String> languages;
		public Date birthDate;

		public RefugeeSummary(Refugee r) {
			this.id = r.getId();
			this.civility = r.getCivility().getName();
			this.firstName = r.getFirstName();
			this.lastName = r.getLastName();
			this.birthDate = r.getBirthDate();
			this.phoneNumber = r.getPhoneNumber();
			this.languages = r.getLanguageSkills().stream().map(x -> x.getLanguage().getName()).collect(Collectors.toList());
		}

	}



	@RequestMapping( method = RequestMethod.GET)
	@Transactional
	public List<RefugeeSummary> list(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer languageId, 
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String zipcode) {

		StringBuffer hql = new StringBuffer("select r from Refugee r join fetch r.languages l  where 0=0 ");
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

		return objectStore.find(Refugee.class, hql.toString(),args).stream()
				.map(RefugeeSummary::new)
				.collect(Collectors.toList());
	}
	

	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> getFullRefugee(@PathVariable int id) {
		
		Optional<Refugee> r = objectStore.getById(Refugee.class, id);
		if (r.isPresent()) {
		// TODO hasAccess
			return ResponseEntity.ok(r.get());
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	
	@RequestMapping(path = "/{id}/summary", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getRefugeeSummary(@PathVariable int id) {

		Optional<RefugeeSummary> r = objectStore.getById(Refugee.class, id).map(rf -> new RefugeeSummary(rf));
		if (r.isPresent()) {
			return ResponseEntity.ok(r.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	
	
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<Void> deleteRefugee(@PathVariable int id) {
		Optional<Refugee> r = objectStore.getById(Refugee.class, id);
		if (r.isPresent()) {
			// TODO hasAccess
			objectStore.delete(r.get());
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();

	}
	

	@RequestMapping( method = RequestMethod.POST)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> saveRefugee(@RequestBody Refugee r) {
		if (r.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		objectStore.save(r);
		return ResponseEntity.created(getUri("/refugees/" + r.getId())).body(r);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> updateRefugee(@PathVariable int id, @RequestBody Refugee r) {
		if (r.getId() == null || !(r.getId() == id)) {
			return ResponseEntity.badRequest().build();
		} else {
			objectStore.save(r);
			return ResponseEntity.noContent().build();
		}

	}
	
	
}
