package org.sjr.babel.web.endpoint;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sjr.babel.model.StatusRestriction;
import org.sjr.babel.model.component.Registration;
import org.sjr.babel.model.entity.Administrator;
import org.sjr.babel.model.entity.Organisation;
import org.sjr.babel.model.entity.Refugee;
import org.sjr.babel.model.entity.Teaching;
import org.sjr.babel.model.entity.reference.FieldOfStudy;
import org.sjr.babel.model.entity.reference.Level;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@RestController @RequestMapping(path = "/teachings") @CrossOrigin
public class TeachingEndpoint extends AbstractEndpoint {

	public static class TeachingSummary {
		public Integer id;
		public String organisation;
		@NotNull @Size(min = 1)
		public String fieldOfStudy;
		public String link;
		public AddressSummary address;
		@NotNull @Valid
		public ContactSummary contact;
		public Boolean master,licence;
		@NotNull
		public LocalDate registrationOpeningDate,registrationClosingDate;

		// restrictions
		public String languageLevelRequired;
		public Integer minAge, maxAge;
		public String statusRestriction;

		@JsonInclude(value=Include.NON_NULL)
		public Boolean alreadyRegisterd;
		
		public TeachingSummary(){
			
		}
		public TeachingSummary(Teaching entity) {
			this.id = entity.getId();
			this.fieldOfStudy = entity.getFieldOfStudy().getName();
			this.link = entity.getLink();
			this.organisation = entity.getOrganisation().getName();
			this.contact = safeTransform(entity.getContact(), ContactSummary::new);
			this.address = safeTransform(entity.getOrganisation().getAddress(), x -> new AddressSummary(x));
			this.master = entity.getMaster();
			this.licence = entity.getLicence();
			this.registrationOpeningDate = entity.getRegistrationOpeningDate();
			this.registrationClosingDate = entity.getRegistrationClosingDate();
			
			// restrictions
			this.languageLevelRequired = safeTransform(entity.getLanguageLevelRequired(), x -> x.getName());
			this.minAge = entity.getMinAge();
			this.maxAge = entity.getMaxAge();
			this.statusRestriction = safeTransform(entity.getStatusRestriction(), x -> x.name());
			
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
		
		if(openForRegistration != null){
			if(openForRegistration){
				hql.append("and (t.registrationClosingDate >= :d and t.registrationOpeningDate <= :d) ");	
			}
			else{
				hql.append("and (t.registrationClosingDate < :d || t.registrationOpeningDate > :d) ");
			}
			args.put("d", LocalDate.now());
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
			return ok(t.get());
		} else {
			return notFound();
		}
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Void> delete(@PathVariable int id) {
		Optional<Teaching> t = objectStore.getById(Teaching.class, id);
		if (t.isPresent()) {
			objectStore.delete(t.get());
			return noContent();
		} else {
			return notFound();
		}
	}
	
	
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> update(@PathVariable int id, @RequestBody @Valid TeachingSummary input, BindingResult binding, @RequestHeader("accessKey") String accessKey) {
		if (input.id ==null || !input.id.equals((id))) {
			return badRequest();
		} else {
			Optional<Teaching> _teaching = this.objectStore.getById(Teaching.class, id);
			if(!_teaching.isPresent()){
				return notFound();
			}
			
			Teaching entity = _teaching.get();
			
			if(!hasAccess(accessKey, entity)){
				return unauthorized();
			}

			Map<String, String> errors = errorsAsMap(binding.getFieldErrors());
			
			if(input.registrationClosingDate!=null && input.registrationOpeningDate!=null && input.registrationClosingDate.isBefore(input.registrationOpeningDate)){
				errors.put("registrationOpeningDate", "_");
				errors.put("registrationClosingDate", "_");
			}
			
			if(!errors.isEmpty()){
				return badRequest(errors);
			}
			
			entity.setContact(safeTransform(input.contact, x -> x.toContact()));
			entity.setFieldOfStudy(this.refDataProvider.resolve(FieldOfStudy.class, input.fieldOfStudy));
			entity.setLicence(input.licence);
			entity.setMaster(input.master);
			entity.setLink(input.link);
			entity.setRegistrationOpeningDate(input.registrationOpeningDate);
			entity.setRegistrationClosingDate(input.registrationClosingDate);			
			
			// restrictions
			entity.setLanguageLevelRequired(this.refDataProvider.resolve(Level.class, input.languageLevelRequired));
			entity.setMinAge(input.minAge);
			entity.setMaxAge(input.maxAge);
			entity.setStatusRestriction(StringUtils.hasText(input.statusRestriction) ? StatusRestriction.valueOf(input.statusRestriction) : null);
			
			objectStore.save(entity);
			return noContent();
		}

	}

	@RequestMapping( method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> create(@RequestBody @Valid TeachingSummary input, BindingResult binding, @RequestHeader("accessKey") String accessKey) {
		if (input.id != null) {
			return badRequest();
		}

		Optional<Organisation> o = getOrganisationByAccessKey(accessKey);
		if(!o.isPresent()){
			return unauthorized();
		}		

		Map<String, String> errors = errorsAsMap(binding.getFieldErrors());
		
		if(input.registrationClosingDate!=null && input.registrationOpeningDate!=null && input.registrationClosingDate.isBefore(input.registrationOpeningDate)){
			errors.put("registrationOpeningDate", "_");
			errors.put("registrationClosingDate", "_");
		}
		
		if(!errors.isEmpty()){
			return badRequest(errors);
		}
		
		//objectStore.save(t);
		Teaching entity = new Teaching();
		entity.setOrganisation(o.get());
		entity.setContact(safeTransform(input.contact, x -> x.toContact()));
		
		entity.setLicence(input.licence);
		entity.setMaster(input.master);
		entity.setLink(input.link);
		
		entity.setFieldOfStudy(this.refDataProvider.resolve(FieldOfStudy.class, input.fieldOfStudy));
		entity.setRegistrationOpeningDate(input.registrationOpeningDate);
		entity.setRegistrationClosingDate(input.registrationClosingDate);
		
		// restrictions
		entity.setLanguageLevelRequired(this.refDataProvider.resolve(Level.class, input.languageLevelRequired));
		entity.setMinAge(input.minAge);
		entity.setMaxAge(input.maxAge);
		entity.setStatusRestriction(StringUtils.hasText(input.statusRestriction) ? StatusRestriction.valueOf(input.statusRestriction) : null);
		
		objectStore.save(entity);
		
		input.id = entity.getId();
		input.address = new AddressSummary(entity.getOrganisation().getAddress());
		input.organisation = entity.getOrganisation().getName();
		return created(getUri("/teachings/" + entity.getId()), input);
	}
	
	
	@RequestMapping(path = {"/{id}/registrations"}, method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getInscriptions (@PathVariable int id, @RequestHeader String accessKey ){
		Optional<Teaching> _teaching = objectStore.getById(Teaching.class, id);
		if (!_teaching.isPresent()){
			return notFound();
		}
		if (!hasAccess(accessKey, _teaching.get())){
			return forbidden();
		}
		List<Registration> registrations = _teaching.get().getRegistrations();
		List<RegistrationSummary> registrationsSummary = registrations.stream().map(x-> new RegistrationSummary(x)).collect(Collectors.toList());
		return ok(registrationsSummary);
	}
	
	
	@RequestMapping(path={"/{id}/registrations"}, method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> addRegistration (@PathVariable int id, @RequestHeader("accessKey") String refugeeAccessKey) {
		
		Optional<Teaching> _teaching = objectStore.getById(Teaching.class, id);
		if (!_teaching.isPresent()){
			return notFound();
		}
		Teaching teaching = _teaching.get();
		Optional<Refugee> _r = getRefugeeByAccesskey(refugeeAccessKey);
		if (!_r.isPresent()){
			return forbidden();
		}
		Refugee r = _r.get();
		if(teaching.getRegistrations().stream().anyMatch(x -> x.getRefugee().getId().equals(r.getId()))){
			return conflict();
		}

		Registration reg = new Registration();
		reg.setRefugee(r);
		reg.setRequestDate(LocalDate.now());
		teaching.getRegistrations().add(reg);
		return created(getUri(getPath()+"/"+r.getId()), new RegistrationSummary(reg));
	}
	
	@RequestMapping(path = {"/{id}/registrations/{rId}"}, method = {RequestMethod.POST,RequestMethod.PATCH})
	@Transactional
	public ResponseEntity<?> acceptOrRefuse(@PathVariable int id, @PathVariable int rId, @RequestBody AcceptOrRefuseRegistrationCommand input,  @RequestHeader String accessKey) {
		Optional<Teaching> _teaching = this.objectStore.getById(Teaching.class, id);
		if(!_teaching.isPresent()){
			return notFound();
		}
		Teaching teaching = _teaching.get();
		if(!hasAccess(accessKey, teaching))
		{
			return forbidden();
		}
		Optional<Refugee> _refugee = this.objectStore.getById(Refugee.class, rId);
		if (!_refugee.isPresent()){
			return notFound();
		}
		Refugee refugee = _refugee.get();

		if (teaching.getRegistrations().stream().anyMatch(x -> x.getRefugee().getId().equals(refugee.getId()))){
			return notFound();
		}
		Registration reg = new Registration();
		reg.setAccepted(input.accepted);
		reg.setDecisionDate(LocalDate.now());
		return noContent();
	}
	
	@RequestMapping(path = {"/{id}/registrations/{rId}"}, method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<?> cancelRegistration (@PathVariable int id, @PathVariable int rId,  @RequestHeader String accessKey) {
		Optional<Teaching> _teaching = this.objectStore.getById(Teaching.class, id);
		if(!_teaching.isPresent()){
			return notFound();
		}
		Teaching teaching = _teaching.get();
		if(!hasAccess(accessKey, teaching))
		{
			return forbidden();
		}
		Optional<Refugee> _refugee = this.objectStore.getById(Refugee.class, rId);
		if (!_refugee.isPresent()){
			return notFound();
		}
		Refugee refugee = _refugee.get();
		if(!teaching.getRegistrations().removeIf(x -> x.getRefugee().getId().equals(refugee.getId()))){
			return badRequest();
		};
		return noContent();
	}
	
}