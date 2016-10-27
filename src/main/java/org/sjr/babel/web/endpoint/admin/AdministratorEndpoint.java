package org.sjr.babel.web.endpoint.admin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sjr.babel.model.component.Account;
import org.sjr.babel.model.entity.Administrator;
import org.sjr.babel.model.entity.reference.Civility;
import org.sjr.babel.web.endpoint.AbstractEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import util.EncryptionUtil;

@RestController
public class AdministratorEndpoint extends AbstractEndpoint {

	class AdministratorSummary {
		
		public Integer id;
		@NotNull @Size(min = 1)
		public String civility, firstName, lastName, mailAddress, phoneNumber;
		@JsonProperty(access = Access.WRITE_ONLY)
		public String password;
		
		public AdministratorSummary(Administrator entity) {
			this.id = entity.getId();
			this.civility = entity.getCivility().getName();
			this.firstName = entity.getFirstName();
			this.lastName = entity.getLastName();
			this.mailAddress = entity.getMailAddress();
			this.phoneNumber = entity.getPhoneNumber();
		}
	}

	@RequestMapping(path = "/administrators", method = RequestMethod.GET)
	@Transactional
	public List<AdministratorSummary> list() {
		List<Administrator> adList = objectStore.find(Administrator.class, "select a from Administrator a");
		return adList.stream().map(x -> new AdministratorSummary(x)).collect(Collectors.toList());
	}

	@RequestMapping(path = "/administrators/{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> get(@PathVariable Integer id) {
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
	public ResponseEntity<?> post(@RequestBody AdministratorSummary input) {
		if (input.id != null) {
			return ResponseEntity.badRequest().build();
		}
		
		if (input.password == null || input.password.equals("")) {
			input.password = UUID.randomUUID().toString().substring(0, 8);
		}
		
		Administrator admin = new Administrator();
		
		admin.setFirstName(input.firstName);
		admin.setCivility(this.refDataProvider.resolve(Civility.class, input.civility));
		admin.setLastName(input.lastName);
		admin.setPhoneNumber(input.phoneNumber);
		admin.setAccount(new Account());
		admin.getAccount().setPassword(EncryptionUtil.sha256(input.password));
		admin.getAccount().setAccessKey("A-"+UUID.randomUUID().toString());
		
		objectStore.save(admin);
		input.id = admin.getId();
		return ResponseEntity.created(getUri("/administrators/" + admin.getId())).body(input);
	}
	
	@RequestMapping(path = "/administrators/{id}", method = RequestMethod.PUT)
	@Transactional
	@RolesAllowed("ADMIN")
	public ResponseEntity<?> put(@RequestBody AdministratorSummary input, @PathVariable int id) {
		if (input.id == null || ! (input.id.equals(id)) ) {
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		Optional<Administrator> _admin = this.objectStore.getById(Administrator.class, id);
		if(_admin.isPresent()){
			return notFound();
		}
		Administrator admin = _admin.get();
		admin.setFirstName(input.firstName);
		admin.setCivility(this.refDataProvider.resolve(Civility.class, input.civility));
		admin.setLastName(input.lastName);
		admin.setMailAddress(input.mailAddress);
		admin.setPhoneNumber(input.phoneNumber);
		if(StringUtils.hasText(input.password)){
			admin.getAccount().setPassword(EncryptionUtil.sha256(input.password));
		}
		return ResponseEntity.noContent().build();
		
	}

	@RequestMapping(path = "/administrators/{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed("ADMIN")
	public ResponseEntity<Void> delete(@PathVariable int id) {
		Optional<Administrator> admin = objectStore.getById(Administrator.class, id);
		if (!admin.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		objectStore.delete(admin.get());
		return ResponseEntity.noContent().build();
	}
}
