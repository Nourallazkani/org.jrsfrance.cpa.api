package org.sjr.babel.web.endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.entity.reference.OrganisationCategory;
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import util.EncryptionUtil;

@RestController
public class OrganisationEndpoint extends AbstractEndpoint {

	class OrganisationSummary {
		public Integer id;
		public String mailAddress;
		public @JsonProperty(access = Access.WRITE_ONLY) String password;
		public String name, category;
		public AddressSummary address;
		public ContactSummary contact;

		public OrganisationSummary(Organisation o) {
			this.id = o.getId();
			this.name = o.getName();
			this.mailAddress = o.getMailAddress();
			this.category = o.getCategory().getName();
			this.contact = safeTransform(o.getContact(), ContactSummary::new);
			this.address = safeTransform(o.getAddress(), x -> new AddressSummary(x));
		}
	}
	
	@RequestMapping(path = {"/organisations", "/libraries"}, method = RequestMethod.GET)
	@Transactional
	public List<OrganisationSummary> list(@RequestParam(required=false) String name, @RequestParam(required=false) String city) 
	{
		StringBuffer query = new StringBuffer("select o from Organisation o join o.category c where 0=0 ");
		Map<String, Object> args = new HashMap<>();
		if(StringUtils.hasText(name)){
			query.append("and o.name like :name ");
			args.put("name", name);	
		}
		if(StringUtils.hasText(city)){
			query.append("and o.address.city like :city ");
			args.put("city", name);	
		}
		if(requestedPathEquals("libraries")){
			query.append("and c.stereotype = :stereotype ");
			args.put("stereotype", OrganisationCategory.Stereotype.LIBRARY);
		}
		
		List<Organisation> results = objectStore.find(Organisation.class, query.toString(), args);

		return results.stream().map(OrganisationSummary::new).collect(Collectors.toList());
	}

	
	private boolean hasAccess(Organisation organisation, String accessKey){
		if (accessKey.startsWith("A-")) {
			Map<String, Object> args = new HashMap<>();
			args.put("ak", accessKey);
			return objectStore
					.findOne(Administrator.class, "select a from Administrator a where a.account.accessKey = :ak", args)
					.isPresent();
		} else {
			return organisation.getAccount().getAccessKey().equals(accessKey);
		}
	}
	
	@RequestMapping(path = "/organisations/{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> orgSummary(@PathVariable int id, @RequestHeader String accessKey) {
		
		Optional<Organisation> _organisation = objectStore.getById(Organisation.class, id);
		if(!_organisation.isPresent()){
			return ResponseEntity.notFound().build();
		}
		Organisation organisation = _organisation.get();
		if(!hasAccess(organisation, accessKey)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		return ResponseEntity.ok(new OrganisationSummary(organisation));
	}
	
	@RequestMapping(path = "organisations/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> updateOrg(@RequestBody OrganisationSummary input, @PathVariable int id, @RequestHeader String accessKey) {
		if (input.id == null || !input.id.equals(id)) {
			return ResponseEntity.badRequest().build();
		}
		Optional<Organisation> _organisation = this.objectStore.getById(Organisation.class, id);
		if(!_organisation.isPresent()){
			return ResponseEntity.notFound().build();
		}
		Organisation organisation = _organisation.get();
		
		if(!hasAccess(organisation, accessKey)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		organisation.setName(input.name);
		organisation.setAddress(safeTransform(input.address, a -> a.toAddress(this.refDataProvider)));
		organisation.setContact(safeTransform(input.contact, c -> c.toContact()));

		organisation.setMailAddress(input.mailAddress);
		if (StringUtils.hasText(input.password)) {
			organisation.getAccount().setPassword(EncryptionUtil.sha256(input.password));
		}
				
		objectStore.save(organisation);
		return ResponseEntity.noContent().build();
	}
}