package org.sjr.babel.web.endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sjr.babel.model.component.Account;
import org.sjr.babel.model.entity.Administrator;
import org.sjr.babel.model.entity.Organisation;
import org.sjr.babel.model.entity.reference.OrganisationCategory;
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

	static class OrganisationSummary {
		public Integer id;
		@NotNull @Size(min = 1)
		public String name, mailAddress;
		public /*@JsonProperty(access = Access.WRITE_ONLY)*/ String password;
		@NotNull @Size(min = 1)
		public String category;
		@NotNull @Valid
		public AddressSummary address;
		@NotNull @Valid
		public ContactSummary contact;
		public Map<String, String> additionalInformations;
		
		OrganisationSummary() {}
		
		OrganisationSummary(Organisation entity) {
			this.id = entity.getId();
			this.name = entity.getName();
			this.mailAddress = entity.getMailAddress();
			this.category = entity.getCategory().getName();
			this.contact = safeTransform(entity.getContact(), ContactSummary::new);
			this.address = safeTransform(entity.getAddress(), x -> new AddressSummary(x));
			this.additionalInformations = entity.getAdditionalInformations();
		}
	}
	
	@RequestMapping(path = {"/organisations", "/libraries"}, method = RequestMethod.GET)
	@Transactional
	public List<OrganisationSummary> search(@RequestParam(required=false) Integer categoryId, @RequestParam(required=false) String city) 
	{
		StringBuffer query = new StringBuffer("select o from Organisation o join o.category c where 0=0 ");
		Map<String, Object> args = new HashMap<>();

		if(StringUtils.hasText(city)){
			query.append("and o.address.locality like :locality ");
			args.put("locality", city);	
		}
		if(requestedPathEquals("libraries")){
			query.append("and c.stereotype = :stereotype ");
			args.put("stereotype", OrganisationCategory.Stereotype.LIBRARY);
		}
		else if(categoryId!=null){
			query.append("and c.id = :categoryId ");
			args.put("categoryId", categoryId);
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
	public ResponseEntity<?> getOne(@PathVariable int id, @RequestHeader String accessKey) {
		
		Optional<Organisation> _organisation = objectStore.getById(Organisation.class, id);
		if(!_organisation.isPresent()){
			return notFound();
		}
		Organisation organisation = _organisation.get();
		if(!hasAccess(organisation, accessKey)){
			return forbidden();
		}
		
		if(organisation.getCategory().getAdditionalInformations()!=null && !organisation.getCategory().getAdditionalInformations().isEmpty()){
			if(organisation.getAdditionalInformations()==null){
				organisation.setAdditionalInformations(new HashMap<>());
			}
			for(String key : organisation.getCategory().getAdditionalInformations()){
				
				if(!organisation.getAdditionalInformations().containsKey(key)){
					organisation.getAdditionalInformations().put(key, null);
				}
			}
		}

		return ok(new OrganisationSummary(organisation));
	}
	
	@RequestMapping(path = "organisations", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> create(@RequestBody @Valid OrganisationSummary input, @RequestHeader(required = false) String accessKey) {
		
		String query = "select o from Organisation o where lower(o.mailAddress) = lower(:mailAddress)";
		Map<String, Object> args= new HashMap<>();
		args.put("mailAddress", input.mailAddress);
		if(this.objectStore.findOne(Organisation.class, query, args).isPresent()){
			return conflict();
		}
		
		Account account = new Account();
		if(StringUtils.hasText(input.password)){
			account.setPassword(EncryptionUtil.sha256(input.password));
		}
		else if (StringUtils.hasText(accessKey)) {
			if(!getAdministratorByAccessKey(accessKey).isPresent()){
				return forbidden();
			}
			
			account.setAccessKey("O-" + UUID.randomUUID().toString());
			account.setPassword(EncryptionUtil.sha256(UUID.randomUUID().toString().substring(0, 8)));
		}
		
		
		Organisation organisation = new Organisation();
		
		organisation.setName(input.name);
		organisation.setAddress(input.address.toAddress(this.refDataProvider));
		organisation.setContact(input.contact.toContact());
		organisation.setMailAddress(input.mailAddress);
		
		organisation.setAccount(account);
		
		organisation.setAdditionalInformations(input.additionalInformations);
		objectStore.save(organisation);
		input.id = organisation.getId();
		return created(getUri("organisations/"+input.id), input);
	}
	
	@RequestMapping(path = "organisations/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> update(@RequestBody @Valid OrganisationSummary input, @PathVariable int id, @RequestHeader String accessKey) {
		if (input.id == null || !input.id.equals(id)) {
			return badRequest();
		}
		Optional<Organisation> _organisation = this.objectStore.getById(Organisation.class, id);
		if(!_organisation.isPresent()){
			return notFound();
		}
		Organisation organisation = _organisation.get();
		
		if(!hasAccess(organisation, accessKey)){
			return forbidden();
		}
		
		organisation.setName(input.name);
		organisation.setAddress(input.address.toAddress(this.refDataProvider));
		organisation.setContact(input.contact.toContact());
		
		organisation.setMailAddress(input.mailAddress);
		if (StringUtils.hasText(input.password)) {
			organisation.getAccount().setPassword(EncryptionUtil.sha256(input.password));
		}
		organisation.setAdditionalInformations(input.additionalInformations);
		objectStore.save(organisation);
		return noContent();
	}
	
	@RequestMapping(path = "organisations/{id}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Void> delete(@PathVariable int id, @RequestHeader String accessKey) {

		Optional<Organisation> o = this.objectStore.getById(Organisation.class, id);
		if (!o.isPresent()) {
			return notFound();
		}
		if(!getAdministratorByAccessKey(accessKey).isPresent()){
			return unauthorized();
		}
		objectStore.delete(o.get());
		return notFound();
	}	
}