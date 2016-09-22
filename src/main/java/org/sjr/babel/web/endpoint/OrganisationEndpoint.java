package org.sjr.babel.web.endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Organisation;
import org.sjr.babel.entity.reference.OrganisationCategory.Stereotype;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
		public ContactSummary contact;

		public OrganisationSummary(Organisation o) {
			this.id = o.getId();
			this.name = o.getName();
			this.category = o.getCategory().getName();
			this.contact = safeTransform(o.getContact(), ContactSummary::new);
			this.address = safeTransform(o.getAddress(), x -> new AddressSummary(x));

		}

	}

	@RequestMapping(path = "/organisations", method = RequestMethod.GET)
	@Transactional
	public List<OrganisationSummary> list(
			@RequestParam(required=false) String name,
			@RequestParam(required=false) String city,
			@RequestParam(required=false) Stereotype stereotype
			) 
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
		if(stereotype!=null){
			query.append("and c.stereotype = :stereotype ");
			args.put("stereotype", stereotype);
		}
		
		List<Organisation> results = objectStore.find(Organisation.class, query.toString(), args);
		// return results.stream().map(x -> new
		// OrganisationSummary(x)).collect(Collectors.toList());
		return results.stream().map(OrganisationSummary::new).collect(Collectors.toList());

	}

	@RequestMapping(path = "/organisations/{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> orgSummary(@PathVariable Integer id) {
		logger.info("entering org ");
		Optional<Organisation> org = objectStore.getById(Organisation.class, id);
		
		Optional<OrganisationSummary> orgSummary = org.map(x -> new OrganisationSummary(x));
		return okOrNotFound(orgSummary);
	}
	
	@RequestMapping(path = "organisations/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> updateOrg(@RequestBody Organisation o, @PathVariable int id) {
		if (o.getId() == null || !o.getId().equals(id)) {
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		objectStore.save(o);
		return ResponseEntity.noContent().build();
	}
}