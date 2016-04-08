package org.sjr.babel.web.endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.apache.commons.codec.digest.DigestUtils;
import org.sjr.babel.entity.Account;
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

	class OrganisationSummary {
		public int id;
		public String name, category;
		public AddressSummary address;

		public OrganisationSummary(Organisation o) {
			this.id = o.getId();
			this.name = o.getName();
			this.category = o.getCategory().getName();
			/*
			 * if (o.getAddress() != null) { this.city =
			 * o.getAddress().getCity(); this.country =
			 * o.getAddress().getCountry().getName(); }
			 */
			Optional.ofNullable(o.getAddress()).ifPresent(a -> this.address = new AddressSummary(a));

		}

	}

	@RequestMapping(path = "/organisations", method = RequestMethod.GET)
	@Transactional
	public List<OrganisationSummary> list(@RequestParam(name = "name", defaultValue = "%") String name) {
		// List<Organisation> org = dao.find(name);
		Map<String, Object> args = new HashMap<>();
		args.put("n", name);
		List<Organisation> results = objectStore.find(Organisation.class,
				"select o from Organisation o where o.name like :n", args);
		// return results.stream().map(x -> new
		// OrganisationSummary(x)).collect(Collectors.toList());
		return results.stream().map(OrganisationSummary::new).collect(Collectors.toList());

	}

	@RequestMapping(path = "/organisations/{id}", method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> org(@PathVariable Integer id) {
		logger.info("entering org ");
		return okOrNotFound(objectStore.getById(Organisation.class, id));
		// return okOrNotFound(dao.getById(id));
		// return org ==null ? ResponseEntity.notFound().build() :
		// ResponseEntity.ok(org);
	}

	@RequestMapping(path = "/organisations/{id}/summary", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> orgSummary(@PathVariable Integer id) {
		logger.info("entering org ");
		Optional<Organisation> org = objectStore.getById(Organisation.class, id);
		Optional<OrganisationSummary> orgSummary = org.map(x -> new OrganisationSummary(x));
		return okOrNotFound(orgSummary);
	}

	@RequestMapping(path = "/organisations", method = RequestMethod.POST)
	@Transactional
	@RolesAllowed("ADMIN")
	public ResponseEntity<?> org(@RequestBody Organisation o) {
		if (o.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		if (o.getAccount() == null) {
			o.setAccount(new Account());
		}
		String password = o.getAccount().getPassword();
		if (password == null || password.equals("")) {
			password = UUID.randomUUID().toString().substring(0, 8);
		}
		o.getAccount().setPassword(DigestUtils.sha256Hex(password));
		objectStore.save(o);
		return ResponseEntity.created(getUri("/organisations/" + o.getId())).body(o);

	}

	@RequestMapping(path = "/organisations/{id}", method = RequestMethod.PUT)
	@Transactional
	@RolesAllowed("ADMIN")
	public ResponseEntity<?> updateOrg(@RequestBody Organisation o, @PathVariable int id) {
		if (o.getId() == null || !o.getId().equals(id)) {
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		objectStore.save(o);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(path = "/organisations/{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed("ADMIN")
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