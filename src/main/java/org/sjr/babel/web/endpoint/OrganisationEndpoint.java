package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Organisation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrganisationEndpoint extends AbstractEndpoint {

	@RequestMapping(path = "/organisations", method = RequestMethod.GET)
	@Transactional
	public List<Organisation> list(@RequestParam(name = "name") String name) {
		// List<Organisation> org = dao.find(name);
		Map<String, Object> args = new HashMap<>();
		args.put("n", name);
		List<Organisation> results = objectStore.find(Organisation.class, "select o from Organisation o where o.name like :n", args);

		System.out.println(getUri("abcd"));
		return results;
	}

	@RequestMapping(path = "/organisations/{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> org(@PathVariable Integer id) {
		logger.info("entering org ");
		return okOrNotFound(objectStore.getById(Organisation.class, id));
		// return okOrNotFound(dao.getById(id));
		// return org ==null ? ResponseEntity.notFound().build() : ResponseEntity.ok(org);
	}

	@RequestMapping(path = "/organisations", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> org(@RequestBody Organisation o) {
		if (o.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		objectStore.save(o);
		return ResponseEntity.created(URI.create("/organisations/" + o.getId())).body(o);
	}

	@RequestMapping(path = "/organisations/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> updateOrg(@RequestBody Organisation o, @PathVariable int id) {
		if (o.getId() == null || !o.getId().equals(id)) {
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		objectStore.save(o);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(path = "/organisations/{id}", method = RequestMethod.DELETE)
	@Transactional
	/// @ResponseStatus(code=HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> delete(@PathVariable int id) {
		// dao.delete( id);
		Optional<Organisation> o = objectStore.getById(Organisation.class, id);
		if (o.isPresent()) {
			objectStore.delete(o.get());
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
		// return ResponseEntity.noContent().build();
	}

}