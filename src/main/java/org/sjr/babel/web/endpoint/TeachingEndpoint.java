package org.sjr.babel.web.endpoint;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.Level;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.entity.Teaching;
import org.sjr.babel.entity.reference.FieldOfStudy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping(path = "/teachings") @CrossOrigin
public class TeachingEndpoint extends AbstractEndpoint {

	public static class TeachingSummary {
		public int id;
		public String organisation, fieldOfStudy, languageLevelRequired, link;
		public AddressSummary address;
		public ContactSummary contact;
		public Boolean master,licence;
		public Date registrationOpeningDate,registrationClosingDate;
		// public List<Link> links;

		public TeachingSummary(){
			
		}
		public TeachingSummary(Teaching t) {
			this.id = t.getId();
			this.fieldOfStudy = t.getFieldOfStudy().getName();
			this.languageLevelRequired  = t.getLanguageLevelRequired().getName();
			this.link = t.getLink();
			this.organisation = t.getOrganisation().getName();
			this.contact = safeTransform(t.getContact(), ContactSummary::new);
			this.address = safeTransform(t.getOrganisation().getAddress(), x -> new AddressSummary(x));
			this.master = t.getMaster();
			this.licence = t.getLicence();
			this.registrationOpeningDate = t.getRegistrationOpeningDate();
			this.registrationClosingDate = t.getRegistrationClosingDate();
		}
	}
	
	private boolean hasAccess(String accessKey, Teaching teaching) {
		if (accessKey.startsWith("A-")) {
			Map<String, Object> args = new HashMap<>();
			args.put("accessKey", accessKey);
			String hql = "select a from Administrator a where a.account.accessKey = :accessKey";
			return objectStore.findOne(Administrator.class, hql, args).isPresent();
		} else {
			return teaching.getOrganisation().getAccount().getAccessKey().equals(accessKey);
		}
	}

	@RequestMapping( method = RequestMethod.GET)
	@Transactional
	public List<TeachingSummary> list(
			@RequestParam(required = false) Integer organisationId,
			@RequestParam(required = false) Integer fieldOfStudyId, 
			@RequestParam(required = false) String city,
			@RequestParam(required=false) Boolean openForRegistration){
		
		StringBuffer hql = new StringBuffer("select t from Teaching t where 0=0 ") ;
		Map<String, Object> args = new HashMap<>();
		if (organisationId != null ) {
			args.put("oId" , organisationId);
			hql.append(" and t.organisation.id = :oId");
		}
		if (fieldOfStudyId != null ) {
			args.put("fId" , fieldOfStudyId);
			hql.append(" and t.fieldOfStudy.id = :fId");
		}
		if (city != null && !city.trim().equals("")) {
			args.put("name" , city);
			hql.append(" and  t.organisation.address.locality like :name");
		}
		Date now = new Date();
		if(openForRegistration != null){
			if(openForRegistration){
				hql.append("and (t.registrationClosingDate >= :d and t.registrationOpeningDate <= :d) ");	
			}
			else{
				hql.append("and (t.registrationClosingDate < :d || t.registrationOpeningDate > :d) ");
			}
			args.put("d", now);
		}
		
		List<TeachingSummary> results = objectStore.find(Teaching.class, hql.toString() , args )
				.stream()
				.map(e -> new TeachingSummary(e))
				.collect(Collectors.toList());
		
		return results.stream().collect(Collectors.toList());
	}

	@RequestMapping(path = "{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getSummary(@PathVariable int id) {

		Optional<TeachingSummary> t = objectStore.getById(Teaching.class, id).map(te -> new TeachingSummary(te));
		if (t.isPresent()) {
			return ResponseEntity.ok(t.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<Void> delete(@PathVariable int id) {
		// return deleteIfExists(Education.class, id);

		Optional<Teaching> t = objectStore.getById(Teaching.class, id);
		if (t.isPresent()) {
			objectStore.delete(t.get());
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<Void> update(@PathVariable int id, @RequestBody TeachingSummary input, @RequestHeader("accessKey") String accessKey) {
		if (input.id != id) {
			return ResponseEntity.badRequest().build();
		} else {
			Optional<Teaching> _teaching = this.objectStore.getById(Teaching.class, id);
			if(!_teaching.isPresent()){
				return ResponseEntity.notFound().build();
			}
			
			Teaching teaching = _teaching.get();
			
			if(!hasAccess(accessKey, teaching)){
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			
			teaching.setContact(safeTransform(input.contact, x -> x.toContact()));
			
			teaching.setLicence(input.licence);
			teaching.setMaster(input.master);
			teaching.setLink(input.link);
			teaching.setRegistrationOpeningDate(input.registrationOpeningDate);
			teaching.setRegistrationClosingDate(input.registrationClosingDate);			
			
			teaching.setLanguageLevelRequired(this.refDataProvider.resolve(Level.class, input.languageLevelRequired));
			teaching.setFieldOfStudy(this.refDataProvider.resolve(FieldOfStudy.class, input.fieldOfStudy));

			objectStore.save(teaching);
			return ResponseEntity.noContent().build();
		}

	}

	@RequestMapping( method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> create(@RequestBody TeachingSummary input, @RequestHeader("accessKey") String accessKey) {
		if (input.id > 0) {
			return ResponseEntity.badRequest().build();
		}

		Optional<Organisation> o = getOrganisationByAccessKey(accessKey);
		if(!o.isPresent()){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}		
		
		//objectStore.save(t);
		Teaching teaching = new Teaching();
		teaching.setOrganisation(o.get());
		teaching.setContact(safeTransform(input.contact, x -> x.toContact()));
		
		teaching.setLicence(input.licence);
		teaching.setMaster(input.master);
		teaching.setLink(input.link);
		
		teaching.setLanguageLevelRequired(this.refDataProvider.resolve(Level.class, input.languageLevelRequired));
		teaching.setFieldOfStudy(this.refDataProvider.resolve(FieldOfStudy.class, input.fieldOfStudy));
		teaching.setRegistrationOpeningDate(input.registrationOpeningDate);
		teaching.setRegistrationClosingDate(input.registrationClosingDate);
		
		objectStore.save(teaching);
		
		input.id = teaching.getId();
		input.address = new AddressSummary(teaching.getOrganisation().getAddress());
		input.organisation = teaching.getOrganisation().getName();
		return ResponseEntity.created(getUri("/teachings/" + teaching.getId())).body(input);
	}
}