package org.sjr.babel.web.endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.sjr.babel.entity.Contact;
import org.sjr.babel.entity.Teaching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping(path = "/teachings")
public class TeachingEndpoint extends AbstractEndpoint {

	class TeachingSummary {
		public int id;
		public String organisation, fieldOfStudy, languageLevelRequired ,contactName,contactPhone,contactMailAddress;
		public AddressSummary address;
		public ContactSummary contact;
		public Boolean master,licence;
		// public List<Link> links;

		public TeachingSummary(Teaching t) {
			this.id = t.getId();
			this.fieldOfStudy = t.getFieldOfStudy().getName();
			this.languageLevelRequired  = t.getLanguageLevelRequired().getName();
			this.organisation = t.getOrganisation().getName();
			this.contact = safeTransform(t.getContact(), ContactSummary::new);
			this.address = safeTransform(t.getOrganisation().getAddress(), AddressSummary::new);
			this.master = t.getMaster();
			this.licence = t.getLicence();
		}
	}
	
	

	@RequestMapping( method = RequestMethod.GET)
	@Transactional
	public List<TeachingSummary> list(
			@RequestParam(required = false) Integer organisationId,
			@RequestParam(required = false) Integer fieldOfStudyId, 
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String zipcode){
		
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
		if (zipcode !=null){
			args.put("zipcode", zipcode);
			hql.append(" and t.organisation.address.zipcode like :zipcode");
		}
		List<TeachingSummary> results = objectStore.find(Teaching.class, hql.toString() , args ).stream().map(e -> new TeachingSummary(e))
				.collect(Collectors.toList());
		
		return results;
	}

	// the new end point

	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> get(@PathVariable int id) {

		Optional<Teaching> t = objectStore.getById(Teaching.class, id);
		if (t.isPresent()) {
			return ResponseEntity.ok(t.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/{id}/summary", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getSummary(@PathVariable int id) {

		Optional<TeachingSummary> t = objectStore.getById(Teaching.class, id).map(te -> new TeachingSummary(te));
		if (t.isPresent()) {
			return ResponseEntity.ok(t.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<Void> delete(@PathVariable int id) {
		// return deleteIfExists(Education.class, id);

		Optional<Teaching> t = objectStore.getById(Teaching.class, id);
		if (t.isPresent()) {
			objectStore.delete(t.get());
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<Void> update(@PathVariable int id, @RequestBody Teaching t) {
		if (t.getId() == null || !(t.getId().equals(id))) {
			return ResponseEntity.badRequest().build();
		} else {
			objectStore.save(t);
			return ResponseEntity.noContent().build();
		}

	}

	@RequestMapping( method = RequestMethod.POST)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> create(@RequestBody Teaching t) {
		if (t.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		objectStore.save(t);
		return ResponseEntity.created(getUri("/teachings/" + t.getId())).body(t);
	}
}