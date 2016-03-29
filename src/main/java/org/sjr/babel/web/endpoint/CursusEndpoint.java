package org.sjr.babel.web.endpoint;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import org.sjr.babel.entity.Cursus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

		public CursusSummary(Cursus c) {
			this.id = c.getId();
			this.level = c.getName();
			this.organisation = c.getOrg().getName();
			this.address = new  AddressSummary(c.getAddress());
			this.startDate = c.getStartDate();
			this.endDate = c.getEndDate();
		}
	}

	// http://dosjds./cursus?city=Paris
	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public List<CursusSummary> list(@RequestParam(name = "city", defaultValue = "%") String city) {
		return objectStore.find(Cursus.class, "select c from Cursus c where c.address.city like ?", city).stream()
				.map(x -> new CursusSummary(x)).collect(Collectors.toList());
	}

	@RequestMapping(path = "{id}", method = RequestMethod.GET)
	@RolesAllowed({"ADMIN"})
	@Transactional
	public ResponseEntity<?> cursus(@PathVariable Integer id,@RequestParam(defaultValue="true") boolean withDetails) {
		// return okOrNotFound(objectStore.getById(Cursus.class, id));
		System.out.println(withDetails);
		Optional<Cursus> c = objectStore.getById(Cursus.class, id);
		if (c.isPresent()) {
			Cursus cursus = c.get();
			if (withDetails)
			{
				cursus.getCourses().size();
			}
			return ResponseEntity.ok().body(cursus);
		}
		return ResponseEntity.notFound().build();
	}
	
	
	@RequestMapping(path = "{id}/summary", method = RequestMethod.GET)
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
	@RolesAllowed({"ADMIN"})
	public ResponseEntity<?> saveCur(@RequestBody Cursus cur, @PathVariable int id) {
		if (cur.getId() == null || !cur.getId().equals(id)) {
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		objectStore.save(cur);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(path = "{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed({"ADMIN"})
	public ResponseEntity<?> delete(@PathVariable int id) {
		Optional<Cursus> c = objectStore.getById(Cursus.class, id);
		if (c.isPresent()) {
			Cursus cursus = c.get();
			if (!cursus.getCourses().isEmpty()) {
				int n = cursus.getCourses().size();
				return ResponseEntity.badRequest().body("This cursus has " + n + " dependant courses");
			}
			objectStore.delete(cursus);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.badRequest().build();
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	@RolesAllowed({"ADMIN"})
	public ResponseEntity<?> save(@RequestBody Cursus c) {
		if (c.getId() != null) {
			return ResponseEntity.badRequest().build();
		}

		objectStore.save(c);
		return ResponseEntity.created(getUri("/cursus/" + c.getId())).body(c);
	}

}
