package org.sjr.babel.web.endpoint;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sjr.babel.model.component.Registration;
import org.sjr.babel.model.entity.AbstractLearningProgram;
import org.sjr.babel.model.entity.Administrator;
import org.sjr.babel.model.entity.LanguageLearningProgram;
import org.sjr.babel.model.entity.Organisation;
import org.sjr.babel.model.entity.ProfessionalLearningProgram;
import org.sjr.babel.model.entity.Refugee;
import org.sjr.babel.model.entity.reference.LanguageLearningProgramType;
import org.sjr.babel.model.entity.reference.Level;
import org.sjr.babel.model.entity.reference.ProfessionalLearningProgramDomain;
import org.springframework.http.HttpStatus;
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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController @RequestMapping(path = "learnings")
public class LearningProgramEndpoint extends AbstractEndpoint {
	

	static class LearningProgramSummary {
		public Integer id;
		@NotNull @Size(min = 1)
		public String level;
		public String organisation, link;
		public Integer groupSize;
		@NotNull @Valid
		public AddressSummary address;
		@NotNull
		public Date startDate, endDate, registrationOpeningDate,registrationClosingDate;
		public boolean openForRegistration;
		@NotNull @Valid
		public ContactSummary contact;
		
		
		@JsonInclude(value=Include.NON_NULL)
		public String domain, type;
		
		LearningProgramSummary() {} // for jackson deserialisation
		
		LearningProgramSummary(AbstractLearningProgram entity) {
			this.id = entity.getId();
			this.level = entity.getLevel().getName();
			this.organisation = entity.getOrganisation().getName();
			this.link = entity.getLink();
			this.groupSize = entity.getGroupSize();
			this.address = safeTransform(entity.getAddress(), x -> new AddressSummary(x));
			this.registrationOpeningDate = entity.getRegistrationOpeningDate();
			this.registrationClosingDate = entity.getRegistrationClosingDate();
			this.startDate = entity.getStartDate();
			this.endDate = entity.getEndDate();
			if(entity.getContact()!=null){
				this.contact = new ContactSummary(entity.getContact());
			}
			else if (entity.getOrganisation().getContact() != null) {
				this.contact = new ContactSummary(entity.getOrganisation().getContact());	
			}
			
			if (entity instanceof LanguageLearningProgram){
				LanguageLearningProgram l = (LanguageLearningProgram) entity ;
				this.type = safeTransform(l.getType(), x -> x.getName(), "");
				
			}else if ( entity instanceof ProfessionalLearningProgram) {
				ProfessionalLearningProgram p = (ProfessionalLearningProgram) entity;
				this.domain = safeTransform(p.getDomain(), x -> x.getName(), "");
			}
			
		}
	}

	private boolean hasAccess(String accessKey, AbstractLearningProgram cursus) {
		if (accessKey.startsWith("A-")) {
			Map<String, Object> args = new HashMap<>();
			args.put("accessKey", accessKey);
			String hql = "select a from Administrator a where a.account.accessKey = :accessKey";
			return objectStore.findOne(Administrator.class, hql, args).isPresent();
		} else {
			return cursus.getOrganisation().getAccount().getAccessKey().equals(accessKey);
		}
	}
	
	@RequestMapping(path = {"language-programs", "professional-programs"} ,method = RequestMethod.GET)
	@Transactional
	public List<LearningProgramSummary> search(
			@RequestParam(required=false, name = "city") String city, 
			@RequestParam(required=false) Integer levelId,
			@RequestParam(required=false) Integer domainId,
			@RequestParam(required=false) Integer typeId,
			@RequestParam(required=false) Integer organisationId,
			@RequestParam(defaultValue="false") boolean includePastEvents,
			@RequestParam(defaultValue="true") boolean includeFutureEvents,
			@RequestParam(defaultValue="false") boolean includeClosedEvents,
			@RequestParam(required=false) Boolean openForRegistration
		) { 
		
		Class<? extends AbstractLearningProgram > targetClass = requestedPathEquals("learnings/language-programs") ? LanguageLearningProgram.class : ProfessionalLearningProgram.class;

		StringBuffer query = new StringBuffer("select c from ").append(targetClass.getSimpleName()).append(" c where 0=0 ") ;
		Map<String, Object> args = new HashMap<>();
		if (StringUtils.hasText(city)) {
			args.put("city" , city);
			query.append(" and c.address.locality like :city ");
		}
		if (levelId!=null) {
			args.put("levelId" , levelId);
			query.append("and c.level.id = :levelId ");
		}
		if (organisationId != null) {
			args.put("organisationId", organisationId);
			query.append("and c.organisation.id = :organisationId ");
		}
		if (typeId != null && targetClass.equals(LanguageLearningProgram.class)) {
			args.put("typeId", typeId);
			query.append("and c.type.id = :typeId ");
		}
		if (domainId != null && targetClass.equals(ProfessionalLearningProgram.class)) {
			args.put("domainId", domainId);
			query.append("and c.domain.id = :domainId ");
		}		
		Date now = new Date();	
		
		if(openForRegistration != null){
			if(openForRegistration){
				query.append("and (c.registrationClosingDate >= :d and c.registrationOpeningDate <= :d) ");	
			}
			else{
				query.append("and (c.registrationClosingDate < :d || c.registrationOpeningDate > :d) ");
			}
			args.put("d", now);
		}
		
		if(!includeFutureEvents){
			query.append("and c.startDate <= :d ");
			args.put("d", now);
		}
		if(!includeClosedEvents){
			query.append("and c.registrationClosingDate >= :d ");
			args.put("d", now);
		}
		if(!includePastEvents){
			query.append("and c.endDate >= :d ");
			args.put("d", now);
		}
		
		query.append("order by c.startDate");
		List<? extends AbstractLearningProgram> results = objectStore.find(targetClass, query.toString(), args);
		return results.stream()
				.map(LearningProgramSummary::new)
				.collect(Collectors.toList());
	}

	@RequestMapping(path = {"language-programs/{id}", "professional-programs/{id}"}, method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getOne(@PathVariable Integer id) {
		// return okOrNotFound(objectStore.getById(Cursus.class, id));
		Optional<AbstractLearningProgram> c = objectStore.getById(AbstractLearningProgram.class, id);
		if (c.isPresent()) {
			return ResponseEntity.ok().body(new LearningProgramSummary(c.get()));
		}
		return ResponseEntity.notFound().build();
	}

	@RequestMapping(path = {"language-programs/{id}", "professional-programs/{id}"}, method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed({ "ADMIN", "ORGANISATION" })
	public ResponseEntity<?> delete(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<AbstractLearningProgram> _lp = objectStore.getById(AbstractLearningProgram.class, id);
		if (!_lp.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		AbstractLearningProgram lp = _lp.get();
		if (!hasAccess(accessKey, lp)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		objectStore.delete(lp);
		return ResponseEntity.noContent().build();

	}

	
	@RequestMapping(path = {"language-programs/{id}", "professional-programs/{id}"}, method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> update(@RequestBody @Valid LearningProgramSummary input, BindingResult binding, @PathVariable int id, @RequestHeader String accessKey) {

		if (input.id == null || !input.id.equals(id)) {
			return badRequest("Id is not correct!");
		}
		
		Optional<AbstractLearningProgram> _learningProgram = this.objectStore.getById(AbstractLearningProgram.class, id);
		if(!_learningProgram.isPresent()){
			return ResponseEntity.notFound().build();
		}
		
		AbstractLearningProgram entity = _learningProgram.get();
		if (!hasAccess(accessKey, entity)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		Map<String, String> errors = errorsAsMap(binding.getFieldErrors());
		
		if(input.startDate !=null && input.endDate!=null && input.endDate.before(input.startDate)){
			errors.put("startDate", "_");
			errors.put("endDate", "_");
		}
		if(input.registrationClosingDate!=null && input.registrationOpeningDate!=null && input.registrationClosingDate.before(input.registrationOpeningDate)){
			errors.put("registrationOpeningDate", "_");
			errors.put("registrationClosingDate", "_");
		}
		if(requestedPathEquals("learnings/language-programs") && !StringUtils.hasText(input.type)){
			errors.put("type", "_");
		}
		if(requestedPathEquals("learnings/professional-programs") && !StringUtils.hasText(input.domain)){
			errors.put("domain", "_");
		}		
		if(!errors.isEmpty()){
			return badRequest(errors);
		}
		
		if(entity instanceof LanguageLearningProgram){
			((LanguageLearningProgram)entity).setType(this.refDataProvider.resolve(LanguageLearningProgramType.class, input.type));
		}
		else{
			((ProfessionalLearningProgram)entity).setDomain(this.refDataProvider.resolve(ProfessionalLearningProgramDomain.class, input.domain));
		}
		
		entity.setStartDate(input.startDate);
		entity.setEndDate(input.endDate);
		entity.setGroupSize(input.groupSize);
		entity.setRegistrationOpeningDate(input.registrationOpeningDate);
		entity.setRegistrationClosingDate(input.registrationClosingDate);
		entity.setLink(input.link);
		
		entity.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
		entity.setContact(safeTransform(input.contact, x -> x.toContact()));
		
		entity.setLevel(this.refDataProvider.resolve(Level.class, input.level));
		
		objectStore.save(entity);
		return ResponseEntity.noContent().build();

	}
	
	@CrossOrigin
	@RequestMapping(path = {"language-programs", "professional-programs"}, method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> create(@RequestBody @Valid LearningProgramSummary input, BindingResult binding, @RequestHeader String accessKey) throws JsonParseException, JsonMappingException, IOException {
		if(input.id!=null){
			return badRequest();
		}
		
		Optional<Organisation> o = getOrganisationByAccessKey(accessKey);
		if(!o.isPresent()){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Map<String, String> errors = errorsAsMap(binding.getFieldErrors());
		
		if(input.startDate !=null && input.endDate!=null && input.endDate.before(input.startDate)){
			errors.put("startDate", "_");
			errors.put("endDate", "_");
		}
		if(input.registrationClosingDate!=null && input.registrationOpeningDate!=null && input.registrationClosingDate.before(input.registrationOpeningDate)){
			errors.put("registrationOpeningDate", "_");
			errors.put("registrationClosingDate", "_");
		}
		if(requestedPathEquals("learnings/language-programs") && !StringUtils.hasText(input.type)){
			errors.put("type", "_");
		}
		if(requestedPathEquals("learnings/professional-programs") && !StringUtils.hasText(input.domain)){
			errors.put("domain", "_");
		}		
		if(!errors.isEmpty()){
			return badRequest(errors);
		}
		
		AbstractLearningProgram entity;
		
		if(requestedPathEquals("learnings/language-programs")){
			LanguageLearningProgram _lp = new LanguageLearningProgram();
			_lp.setType(this.refDataProvider.resolve(LanguageLearningProgramType.class, input.type));
			entity = _lp;
		}
		else{
			ProfessionalLearningProgram _lp = new ProfessionalLearningProgram();
			_lp.setDomain(this.refDataProvider.resolve(ProfessionalLearningProgramDomain.class, input.domain));
			entity = _lp;
		}
		
		entity.setOrganisation(o.get());
		entity.setStartDate(input.startDate);
		entity.setEndDate(input.endDate);
		entity.setGroupSize(input.groupSize);
		entity.setRegistrationOpeningDate(input.registrationOpeningDate);
		entity.setRegistrationClosingDate(input.registrationClosingDate);
		entity.setLink(input.link);
		entity.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
		entity.setContact(safeTransform(input.contact, x -> x.toContact()));
		
		entity.setLevel(this.refDataProvider.resolve(Level.class, input.level));
		
		
		objectStore.save(entity);
		input.id = entity.getId();
		
		return ResponseEntity.created(getUri("/language-programs/" + entity.getId())).body(input);
	}
	
	@RequestMapping(path = {"language-programs/{id}/registrations", "professional-programs/{id}/registrations"}, method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ORGANISATION" ,"ADMIN"})
	public ResponseEntity<?> getInscriptions (@PathVariable int id, @RequestHeader String accessKey ){
		Optional<AbstractLearningProgram> alp = objectStore.getById(AbstractLearningProgram.class, id);
		if (!alp.isPresent()){
			return notFound();
		}
		if (!hasAccess(accessKey, alp.get())){
			return forbidden();
		}
		List<Registration> registrations = alp.get().getRegistrations();
		List<RegistrationSummary> registrationsSummary = registrations.stream().map(x-> new RegistrationSummary(x)).collect(Collectors.toList());
		return ResponseEntity.ok(registrationsSummary);
	}
	
	@RequestMapping(path={"language-programs/{id}/registrations" ,"professional-programs/{id}/registrations"}, method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> addRegistration (@PathVariable int id, @RequestHeader("accessKey") String refugeeAccessKey) {
		Date now = new Date();
		Optional<AbstractLearningProgram> _lp = objectStore.getById(AbstractLearningProgram.class, id);
		if (!_lp.isPresent()){
			return notFound();
		}
		AbstractLearningProgram lp = _lp.get();
		Optional<Refugee> _r = getRefugeeByAccesskey(refugeeAccessKey);
		if (!_r.isPresent()){
			return forbidden();
		}
		Refugee r = _r.get();
		
		if(lp.getRegistrations().stream().anyMatch(x -> x.getRefugee().getId().equals(r.getId()))){
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		
		/*
		List<Registration> registrations = _lp.get().getRegistrations();
		for (Registration reg : registrations){
			if( r.equals(reg.getRefugee())){
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
		}*/
		Registration reg = new Registration();
		reg.setAccepted(null);
		reg.setRefugee(r);
		reg.setRegistrationDate(now);
		lp.getRegistrations().add(reg);
		return created(null,new RegistrationSummary(reg));
	}
	
	@RequestMapping(path = {"language-programs/{id}/registrations/{rId}", "professional-programs/{id}/registrations/{rId}"}, method = {RequestMethod.POST,RequestMethod.PATCH})
	@Transactional
	public ResponseEntity<?> acceptOrRefuse(@PathVariable int id, @PathVariable int rId, @RequestBody AcceptOrRefuseRegistrationCommand input,  @RequestHeader String accessKey) {
		Optional<AbstractLearningProgram> _learningProgram = this.objectStore.getById(AbstractLearningProgram.class, id);
		if(!_learningProgram.isPresent()){
			return ResponseEntity.notFound().build();
		}
		AbstractLearningProgram learningProgram = _learningProgram.get();
		if(!hasAccess(accessKey, learningProgram))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		Optional<Refugee> _r = this.objectStore.getById(Refugee.class, rId);
		if (!_r.isPresent()){
			return notFound();
		}
		Refugee r = _r.get();
		Optional<Registration> _reg = learningProgram.getRegistrations().stream()
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
	
}
