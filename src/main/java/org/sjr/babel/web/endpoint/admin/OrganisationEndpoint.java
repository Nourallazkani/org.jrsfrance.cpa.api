package org.sjr.babel.web.endpoint.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.sjr.babel.entity.Account;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.web.endpoint.AbstractEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import util.EncryptionUtil;

@RestController("admin_organisationEndpoint")
@RequestMapping("admin")
public class OrganisationEndpoint extends AbstractEndpoint {

	class OrganisationSummary {
		public int id;
		public String name, category;
		public AddressSummary address;
		public ContactSummary contact;

		public OrganisationSummary(Organisation o) {
			this.id = o.getId();
			this.name = o.getName();
			this.category = o.getCategory().getName();
			this.contact = safeTransform(o.getContact(), ContactSummary::new);
			this.address = safeTransform(o.getAddress(), x -> new AddressSummary(x));

		}

	}

	@RequestMapping(path = "organisations", method = RequestMethod.POST)
	@Transactional
	@RolesAllowed("ADMIN")
	public ResponseEntity<?> org(@RequestBody Organisation o) {
		if (o.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		
		Map<String, Object> args = new HashMap<>();
		args.put("mailAddress", o.getMailAddress());
		Optional<Organisation> org = objectStore.findOne(Organisation.class, "select v from Organisation v where v.mailAddress = :mailAddress", args);
		if (org.isPresent()) {
			return ResponseEntity.badRequest().body(Error.MAIL_ADDRESS_ALREADY_EXISTS);
		}
		
		o.setRegistrationDate(new Date());
		
		Account account = new Account();
		
		String password = o.getAccount().getPassword();
		if (password == null || password.equals("")) {
			password = UUID.randomUUID().toString().substring(0, 8);
		}
		account.setPassword(EncryptionUtil.sha256(password));
		account.setAccessKey("O-" + UUID.randomUUID().toString());
		
		o.setAccount(account);
		objectStore.save(o);
		return ResponseEntity.created(getUri("/organisations/" + o.getId())).body(o);

	}

	@RequestMapping(path = "organisations/{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed("ADMIN")
	public ResponseEntity<Void> delete(@PathVariable int id) {

		Optional<Organisation> o = this.objectStore.getById(Organisation.class, id);
		if (o.isPresent()) {
			objectStore.delete(o.get());
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
}