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
import org.sjr.babel.entity.Cursus;
import org.sjr.babel.entity.AbstractEvent;
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
@RequestMapping("cursus") // équivalent à @RequestMapping(path="cursus") et
							// équivalent à @RequestMapping(value="cursus")
public class CursusEndpoint extends AbstractEndpoint {

	class CursusSummary {
		public int id;
		public String level, organisation;
		public AddressSummary address;
		public Date startDate, endDate;
		public ContactSummary contact;

		public CursusSummary(Cursus c) {
			this.id = c.getId();
			this.level = c.getName();
			this.organisation = c.getOrganisation().getName();
			this.address = new AddressSummary(c.getAddress());
			this.startDate = c.getStartDate();
			this.endDate = c.getEndDate();
			this.contact = safeTransform(c.getOrganisation().getContact(), ContactSummary::new);
		}
	}

	private boolean hasAccess(String accessKey, Cursus cursus) {
		if (accessKey.startsWith("A-")) {
			Map<String, Object> args = new HashMap<>();
			args.put("accessKey", accessKey);
			String hql = "select a from Administrator a where a.account.accessKey = :accessKey";
			return objectStore.findOne(Administrator.class, hql, args).isPresent();
		} else {
			return cursus.getOrganisation().getAccount().getAccessKey().equals(accessKey);
		}
	}

	// http://dosjds./cursus?city=Paris
	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public List<CursusSummary> list(
			@RequestParam(required=false, name = "city") String city,
			@RequestParam(required=false, name = "origin") String origin,
			@RequestParam(required=false) Integer levelId) {
		StringBuffer query = new StringBuffer("select c from Cursus c where 0=0 ") ;
		Map<String, Object> args = new HashMap<>();
		if (StringUtils.hasText(city)) {
			args.put("city" , city);
			query.append(" and c.address.city like :city ");
		}
		if (levelId!=null) {
			args.put("levelId" , levelId);
			query.append("and c.level.id = :levelId");
		}
		
		List<Cursus> results = objectStore.find(Cursus.class, query.toString(), args);
		return results.stream()
				.map(CursusSummary::new)
				.collect(Collectors.toList());
	}

	@RequestMapping(path = "{id}", method = RequestMethod.GET)
	@RolesAllowed({ "ADMIN" })
	@Transactional
	public ResponseEntity<?> getCursus(@PathVariable Integer id,
			@RequestParam(defaultValue = "true") boolean withDetails, @RequestHeader String accessKey) {
		// return okOrNotFound(objectStore.getById(Cursus.class, id));
		Optional<Cursus> c = objectStore.getById(Cursus.class, id);
		if (c.isPresent()) {
			if (hasAccess(accessKey, c.get())) {
				Cursus cursus = c.get();
				if (withDetails) {
					cursus.getCourses().size();
				}
				return ResponseEntity.ok().body(cursus);
			}
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.notFound().build();
	}

	@RequestMapping(path = "{id}/summary", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> cursusSummary(@PathVariable Integer id) {
		// return okOrNotFound(objectStore.getById(Cursus.class, id));
		Optional<Cursus> c = objectStore.getById(Cursus.class, id);
		if (c.isPresent()) {
			return ResponseEntity.ok().body(new CursusSummary(c.get()));
		}
		return ResponseEntity.notFound().build();
	}

	@RequestMapping(path = "{id}/courses", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> list(@PathVariable int id) {
		Optional<Cursus> c = objectStore.getById(Cursus.class, id);
		if (c.isPresent()) {
			Cursus cursus = c.get();
			cursus.getCourses().size();
			return ResponseEntity.ok(cursus.getCourses());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "{id}", method = RequestMethod.PUT)
	@Transactional
	@RolesAllowed({ "ADMIN", "ORGANISATION" })
	public ResponseEntity<?> saveCur(@RequestBody Cursus cur, @PathVariable int id, @RequestHeader String accessKey) {
		if (cur.getId() == null || !cur.getId().equals(id)) {
			return ResponseEntity.badRequest().body("Id is not correct!");
		} else if (!hasAccess(accessKey, cur)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		if (cur.getEndDate().before(cur.getStartDate())) {
			return ResponseEntity.badRequest().body(Error.INVALID_DATE_RANGE);
		}
		objectStore.save(cur);
		return ResponseEntity.noContent().build();

	}

	@RequestMapping(path = "{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed({ "ADMIN", "ORGANISATION" })
	public ResponseEntity<?> delete(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<Cursus> c = objectStore.getById(Cursus.class, id);
		if (!c.isPresent()) {
			return ResponseEntity.badRequest().build();
		}
		Cursus cursus = c.get();
		if (!hasAccess(accessKey, c.get())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		if (!cursus.getCourses().isEmpty()) {
			int n = cursus.getCourses().size();
			return ResponseEntity.badRequest().body("This cursus has " + n + " dependant courses");
		}
		objectStore.delete(cursus);
		return ResponseEntity.noContent().build();

	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> save(@RequestBody Cursus c, @RequestHeader String accessKey) {
		if (c.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		if (c.getEndDate().before(c.getStartDate())) {
			return ResponseEntity.badRequest().body(Error.INVALID_DATE_RANGE);
		}
		if (hasAccess(accessKey, c)) {
			objectStore.save(c);
			return ResponseEntity.created(getUri("/cursus/" + c.getId())).body(c);
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

}
