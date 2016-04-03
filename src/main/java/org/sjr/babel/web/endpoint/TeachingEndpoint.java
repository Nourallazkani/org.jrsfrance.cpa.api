package org.sjr.babel.web.endpoint;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.sjr.babel.entity.Teaching;
import org.sjr.babel.entity.reference.FieldOfStudy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeachingEndpoint extends AbstractEndpoint {

	class TeachingSummary {
		public int id;
		public String organisation, fieldOfStudy, lanuageLevelRequired;
		public AddressSummary address;
		// public List<Link> links;

		public TeachingSummary(Teaching e) {
			this.id = e.getId();
			this.fieldOfStudy = e.getFieldOfStudy().getName();
			this.lanuageLevelRequired = e.getLanguageLevelRequired().getName();
			this.organisation = e.getOrganisation().getName();
			if (e.getOrganisation().getAddress() != null) {
				this.address = new AddressSummary(e.getOrganisation().getAddress());
			}
		}
	}

	@RequestMapping(path = "/teachings", method = RequestMethod.GET)
	@Transactional
	public List<TeachingSummary> list(
			@RequestParam(required = false) Integer organisationId,
			@RequestParam(required = false) Integer fieldOfStudyId, 
			@RequestParam(required = false) String city) {
		
		StringBuffer hql = new StringBuffer("select t from Teaching t where 0=0 ") ;
		Map<String, Object> args = new HashMap<>();
		if (organisationId != null ) {
			args.put("oId" , organisationId);
			hql.append(" and t.organisation.id = :oId");
		}
		if (fieldOfStudyId != null ) {
			args.put("fId" , fieldOfStudyId);
			hql.append(" and t.fieldOfStudy.id = :fId");
		}
		if (city != null && !city.trim().equals("")) {
			args.put("name" , city);
			hql.append(" and  t.organisation.address.city like :name");
		}
		return objectStore.find(Teaching.class, hql.toString() , args ).stream().map(e -> new TeachingSummary(e))
				.collect(Collectors.toList());
	}

	// the new end point

	@RequestMapping(path = "/teachings/{id}", method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> get(@PathVariable int id) {

		Optional<Teaching> e = objectStore.getById(Teaching.class, id);
		if (e.isPresent()) {
			return ResponseEntity.ok(e.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/teachings/{id}/summary", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getSummary(@PathVariable int id) {

		Optional<TeachingSummary> e = objectStore.getById(Teaching.class, id).map(t -> new TeachingSummary(t));
		if (e.isPresent()) {
			return ResponseEntity.ok(e.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/teachings/{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<Void> delete(@PathVariable int id) {
		// return deleteIfExists(Education.class, id);

		Optional<Teaching> e = objectStore.getById(Teaching.class, id);
		if (e.isPresent()) {
			objectStore.delete(e.get());
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/teachings/{id}", method = RequestMethod.PUT)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<Void> update(@PathVariable int id, @RequestBody Teaching e) {
		if (e.getId() == null || !(e.getId().equals(id))) {
			return ResponseEntity.badRequest().build();
		} else {
			objectStore.save(e);
			return ResponseEntity.noContent().build();
		}

	}

	@RequestMapping(path = "/teachings", method = RequestMethod.POST)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> create(@RequestBody Teaching e) {
		if (e.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		objectStore.save(e);
		return ResponseEntity.created(getUri("/teachings/" + e.getId())).body(e);
	}
}