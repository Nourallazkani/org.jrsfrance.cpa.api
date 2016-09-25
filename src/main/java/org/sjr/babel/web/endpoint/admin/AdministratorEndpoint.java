package org.sjr.babel.web.endpoint.admin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.apache.commons.codec.digest.DigestUtils;
import org.sjr.babel.entity.Administrator;
import org.sjr.babel.web.endpoint.AbstractEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdministratorEndpoint extends AbstractEndpoint {

	class AdministratorSummary {
		public int id;
		public String role, civility, firstName, lastName, mailAddress, phoneNumber;

		public AdministratorSummary(Administrator ad) {
			this.id = ad.getId();
			this.civility = ad.getCivility().getName();
			this.firstName = ad.getFirstName();
			this.lastName = ad.getLastName();
			this.mailAddress = ad.getMailAddress();
			this.phoneNumber = ad.getPhoneNumber();
		}
	}

	@RequestMapping(path = "/administrators", method = RequestMethod.GET)
	@Transactional
	public List<AdministratorSummary> getAdminsSummary() {
		List<Administrator> adList = objectStore.find(Administrator.class, "select a from Administrator a");
		return adList.stream().map(x -> new AdministratorSummary(x)).collect(Collectors.toList());
	}

	@RequestMapping(path = "/administrators/{id}", method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> admin(@PathVariable Integer id) {
		return okOrNotFound(objectStore.getById(Administrator.class, id));
	}

	@RequestMapping(path = "/administrators/{id}/summary", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> adminSummary(@PathVariable Integer id) {
		Optional<Administrator> admin = objectStore.getById(Administrator.class, id);
		if(!admin.isPresent()){
			ResponseEntity.notFound().build();
		}
		Optional<AdministratorSummary> adminSummary = admin.map(x -> new AdministratorSummary(x));
		return okOrNotFound(adminSummary);
	}

	@RequestMapping(path = "/administrators", method = RequestMethod.POST)
	@Transactional
	@RolesAllowed("ADMIN")
	public ResponseEntity<?> savaAdmin(@RequestBody Administrator ad) {
		if (ad.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		
		String password = ad.getAccount().getPassword();
		if (password == null || password.equals("")) {
			password = UUID.randomUUID().toString().substring(0, 8);
		}
		ad.getAccount().setPassword(DigestUtils.sha256Hex(password));
		objectStore.save(ad);
		return ResponseEntity.created(getUri("/administrators/" + ad.getId())).body(ad);
	}
	
	@RequestMapping(path = "/administrators/{id}", method = RequestMethod.PUT)
	@Transactional
	@RolesAllowed("ADMIN")
	public ResponseEntity<?> updateAdmin(@RequestBody Administrator ad, @PathVariable int id) {
		if (ad.getId() == null || ! (ad.getId().equals(id)) ) {
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		objectStore.save(ad);
		return ResponseEntity.noContent().build();
		
	}

	@RequestMapping(path = "/administrators/{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed("ADMIN")
	public ResponseEntity<Void> deleteAdmin(@PathVariable int id) {
		Optional<Administrator> ad = objectStore.getById(Administrator.class, id);
		if (ad.isPresent()) {
			objectStore.delete(ad.get());
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
}
