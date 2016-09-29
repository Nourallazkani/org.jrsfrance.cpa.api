package org.sjr.babel.web.endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.entity.reference.OrganisationCategory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
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
		public @JsonProperty(access = Access.WRITE_ONLY) String password;
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
	public List<OrganisationSummary> list(@RequestParam(required=false) Integer categoryId, @RequestParam(required=false) String city) 
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
	public ResponseEntity<?> orgSummary(@PathVariable int id, @RequestHeader String accessKey) {
		
		Optional<Organisation> _organisation = objectStore.getById(Organisation.class, id);
		if(!_organisation.isPresent()){
			return ResponseEntity.notFound().build();
		}
		Organisation organisation = _organisation.get();
		if(!hasAccess(organisation, accessKey)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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

		return ResponseEntity.ok(new OrganisationSummary(organisation));
	}
	
	@RequestMapping(path = "organisations/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> updateOrg(@RequestBody @Valid OrganisationSummary input, BindingResult binding,  @PathVariable int id, @RequestHeader String accessKey) {
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
		
		Map<String, String> errors = errorsAsMap(binding.getFieldErrors());
		if (!errors.isEmpty()) {
			return badRequest(errors);
		}
		
		organisation.setName(input.name);
		organisation.setAddress(safeTransform(input.address, a -> a.toAddress(this.refDataProvider)));
		organisation.setContact(safeTransform(input.contact, c -> c.toContact()));
		
		organisation.setMailAddress(input.mailAddress);
		if (StringUtils.hasText(input.password)) {
			organisation.getAccount().setPassword(EncryptionUtil.sha256(input.password));
		}
		organisation.setAdditionalInformations(input.additionalInformations);
		objectStore.save(organisation);
		return ResponseEntity.noContent().build();
	}
}