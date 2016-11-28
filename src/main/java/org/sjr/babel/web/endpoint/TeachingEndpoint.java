package org.sjr.babel.web.endpoint;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sjr.babel.model.component.Registration;
import org.sjr.babel.model.entity.AbstractEvent;
import org.sjr.babel.model.entity.Administrator;
import org.sjr.babel.model.entity.Organisation;
import org.sjr.babel.model.entity.Refugee;
import org.sjr.babel.model.entity.Teaching;
import org.sjr.babel.model.entity.reference.FieldOfStudy;
import org.sjr.babel.model.entity.reference.Level;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AcceptOrRefuseRegistrationCommand;
import org.sjr.babel.web.endpoint.AbstractEndpoint.RegistrationSummary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
		public Integer id;
		public String organisation;
		@NotNull @Size(min = 1)
		public String fieldOfStudy, languageLevelRequired;
		public String link;
		public AddressSummary address;
		@NotNull @Valid
		public ContactSummary contact;
		public Boolean master,licence;
		@NotNull
		public Date registrationOpeningDate,registrationClosingDate;
		// public List<Link> links;

		public TeachingSummary(){
			
		}
		public TeachingSummary(Teaching entity) {
			this.id = entity.getId();
			this.fieldOfStudy = entity.getFieldOfStudy().getName();
			this.languageLevelRequired  = entity.getLanguageLevelRequired().getName();
			this.link = entity.getLink();
			this.organisation = entity.getOrganisation().getName();
			this.contact = safeTransform(entity.getContact(), ContactSummary::new);
			this.address = safeTransform(entity.getOrganisation().getAddress(), x -> new AddressSummary(x));
			this.master = entity.getMaster();
			this.licence = entity.getLicence();
			this.registrationOpeningDate = entity.getRegistrationOpeningDate();
			this.registrationClosingDate = entity.getRegistrationClosingDate();
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
	public List<TeachingSummary> search(
			@RequestParam(required = false) Integer organisationId,
			@RequestParam(required = false) Integer fieldOfStudyId, 
			@RequestParam(required=false) Integer levelId,
			@RequestParam(required = false) String city,
			@RequestParam(required=false) Boolean openForRegistration){
		
		StringBuffer hql = new StringBuffer("select t from Teaching t where 0=0 ") ;
		Map<String, Object> args = new HashMap<>();
		if (organisationId != null ) {
			args.put("oId" , organisationId);
			hql.append(" and t.organisation.id = :oId ");
		}
		if (fieldOfStudyId != null ) {
			args.put("fId" , fieldOfStudyId);
			hql.append(" and t.fieldOfStudy.id = :fId ");
		}
		if(levelId!=null){
			args.put("lId", levelId);
			hql.append(" and t.languageLevelRequired.id = :lId ");
		}
		if (city != null && !city.trim().equals("")) {
			args.put("name" , city);
			hql.append(" and  t.organisation.address.locality like :name ");
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
	public ResponseEntity<?> getOne(@PathVariable int id) {

		Optional<TeachingSummary> t = objectStore.getById(Teaching.class, id).map(te -> new TeachingSummary(te));
		if (t.isPresent()) {
			return ResponseEntity.ok(t.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	@Transactional
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
	public ResponseEntity<?> update(@PathVariable int id, @RequestBody @Valid TeachingSummary input, BindingResult binding, @RequestHeader("accessKey") String accessKey) {
		if (input.id ==null || !input.id.equals((id))) {
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

			Map<String, String> errors = errorsAsMap(binding.getFieldErrors());
			
			if(input.registrationClosingDate!=null && input.registrationOpeningDate!=null && input.registrationClosingDate.before(input.registrationOpeningDate)){
				errors.put("registrationOpeningDate", "_");
				errors.put("registrationClosingDate", "_");
			}
			
			if(!errors.isEmpty()){
				return badRequest(errors);
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
	public ResponseEntity<?> create(@RequestBody @Valid TeachingSummary input, BindingResult binding, @RequestHeader("accessKey") String accessKey) {
		if (input.id != null) {
			return ResponseEntity.badRequest().build();
		}

		Optional<Organisation> o = getOrganisationByAccessKey(accessKey);
		if(!o.isPresent()){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}		

		Map<String, String> errors = errorsAsMap(binding.getFieldErrors());
		
		if(input.registrationClosingDate!=null && input.registrationOpeningDate!=null && input.registrationClosingDate.before(input.registrationOpeningDate)){
			errors.put("registrationOpeningDate", "_");
			errors.put("registrationClosingDate", "_");
		}
		
		if(!errors.isEmpty()){
			return badRequest(errors);
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
	
	
	@RequestMapping(path = {"/{id}/registrations"}, method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ORGANISATION" ,"ADMIN"})
	public ResponseEntity<?> getInscriptions (@PathVariable int id, @RequestHeader String accessKey ){
		Optional<Teaching> _T = objectStore.getById(Teaching.class, id);
		if (!_T.isPresent()){
			return notFound();
		}
		if (!hasAccess(accessKey, _T.get())){
			return forbidden();
		}
		List<Registration> registrations = _T.get().getRegistrations();
		List<RegistrationSummary> registrationsSummary = registrations.stream().map(x-> new RegistrationSummary(x)).collect(Collectors.toList());
		return ResponseEntity.ok(registrationsSummary);
	}
	
	
	@RequestMapping(path={"/{id}/registrations"}, method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> addRegistration (@PathVariable int id, @RequestHeader("accessKey") String refugeeAccessKey) {
		Date now = new Date();
		Optional<Teaching> _T = objectStore.getById(Teaching.class, id);
		if (!_T.isPresent()){
			return notFound();
		}
		Optional<Refugee> _r = getRefugeeByAccesskey(refugeeAccessKey);
		if (!_r.isPresent()){
			return forbidden();
		}
		Refugee r = _r.get();
		List<Registration> registrations = _T.get().getRegistrations();
		for (Registration reg : registrations){
			if( r.equals(reg.getRefugee())){
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
		}
		Registration reg = new Registration();
		reg.setAccepted(null);
		reg.setRefugee(r);
		reg.setRegistrationDate(now);
		registrations.add(reg);
		RegistrationSummary regSum = new RegistrationSummary(reg);
		return created(null,regSum);
	}
	
	@RequestMapping(path = {"/{id}/registrations/{rId}"}, method = {RequestMethod.POST,RequestMethod.PATCH})
	@Transactional
	public ResponseEntity<?> acceptOrRefuse(@PathVariable int id, @PathVariable int rId, @RequestBody AcceptOrRefuseRegistrationCommand input,  @RequestHeader String accessKey) {
		Optional<Teaching> _T = this.objectStore.getById(Teaching.class, id);
		if(!_T.isPresent()){
			return ResponseEntity.notFound().build();
		}
		Teaching teaching = _T.get();
		if(!hasAccess(accessKey, teaching))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		Optional<Refugee> _r = this.objectStore.getById(Refugee.class, rId);
		if (!_r.isPresent()){
			return notFound();
		}
		Refugee r = _r.get();
		Optional<Registration> _reg = teaching.getRegistrations().stream()
				.filter(x -> x.getRefugee().getId().equals(r.getId()))
				.findFirst(); // à voir car s'est une fausse function
		if (!_reg.isPresent()){
			return notFound();
		}
		// verification si il y a de changement? (acceptation ou pas)
		Registration reg = _reg.get();
		reg.setAccepted(input.accepted);
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(path = {"/{id}/registrations/{rId}"}, method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<?> cancelRegistration (@PathVariable int id, @PathVariable int rId,  @RequestHeader String accessKey) {
		Optional<Teaching> _T = this.objectStore.getById(Teaching.class, id);
		if(!_T.isPresent()){
			return ResponseEntity.notFound().build();
		}
		Teaching teaching = _T.get();
		if(!hasAccess(accessKey, teaching))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		Optional<Refugee> _r = this.objectStore.getById(Refugee.class, rId);
		if (!_r.isPresent()){
			return notFound();
		}
		Refugee r = _r.get();
		Predicate<Registration> registrationPredicate = x-> x.getRefugee().getId().equals(r.getId());
		if(!teaching.getRegistrations().removeIf(registrationPredicate)){
			return badRequest();
		};
		return noContent();
	}
	
}