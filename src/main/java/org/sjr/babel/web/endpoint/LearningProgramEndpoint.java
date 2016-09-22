package org.sjr.babel.web.endpoint;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.sjr.babel.entity.AbstractLearningProgram;
import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.LanguageLearningProgram;
import org.sjr.babel.entity.Level;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.entity.ProfessionalLearningProgram;
import org.sjr.babel.entity.reference.LanguageLearningProgramType;
import org.sjr.babel.entity.reference.ProfessionalLearningProgramDomain;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController @RequestMapping(path = "learnings")
public class LearningProgramEndpoint extends AbstractEndpoint {

	public static class LearningProgramSummary {
		public Integer id;
		public String level, organisation;
		public String link;
		public AddressSummary address;
		public Date startDate, endDate, registrationOpeningDate,registrationClosingDate;
		public boolean openForRegistration;
		public ContactSummary contact;
		
		@JsonInclude(value=Include.NON_NULL)
		public String domain, type;
		
		public LearningProgramSummary() {} // for jackson deserialisation
		
		public LearningProgramSummary(AbstractLearningProgram lp) {
			this.id = lp.getId();
			this.level = lp.getLevel().getName();
			this.organisation = lp.getOrganisation().getName();
			this.link = lp.getLink();
			this.address = safeTransform(lp.getAddress(), x -> new AddressSummary(x));
			this.registrationOpeningDate = lp.getRegistrationOpeningDate();
			this.registrationClosingDate = lp.getRegistrationClosingDate();
			this.startDate = lp.getStartDate();
			this.endDate = lp.getEndDate();
			if(lp.getContact()!=null){
				this.contact = new ContactSummary(lp.getContact());
			}
			else if (lp.getOrganisation().getContact() != null) {
				this.contact = new ContactSummary(lp.getOrganisation().getContact());	
			}
			
			if (lp instanceof LanguageLearningProgram){
				LanguageLearningProgram l = (LanguageLearningProgram) lp ;
				this.type = safeTransform(l.getType(), x -> x.getName(), "");
				
			}else if ( lp instanceof ProfessionalLearningProgram) {
				ProfessionalLearningProgram p = (ProfessionalLearningProgram) lp;
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
	public List<LearningProgramSummary> learningPrograms(
			@RequestParam(required=false, name = "city") String city, 
			@RequestParam(required=false) Integer levelId,
			@RequestParam(required=false) Integer domainId,
			@RequestParam(required=false) Integer typeId,
			@RequestParam(required=false) Integer organisationId,
			@RequestParam(defaultValue="false") boolean includePastEvents,
			@RequestParam(defaultValue="true") boolean includeFutureEvents,
			@RequestParam(required=false) Boolean openForRegistration,
			HttpServletRequest req
		) { 
		String path = (String) req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		
		Class<? extends AbstractLearningProgram > targetClass = path.endsWith("language-programs") ? LanguageLearningProgram.class : ProfessionalLearningProgram.class;

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
	public ResponseEntity<?> learningProgram(@PathVariable Integer id) {
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
	public ResponseEntity<?> learningProgram(@PathVariable int id, @RequestHeader String accessKey) {
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
	public ResponseEntity<?> learningProgram(@RequestBody LearningProgramSummary input, @PathVariable int id, @RequestHeader String accessKey) {

		if (input.id == null || !input.id.equals(id)) {
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		if (input.endDate.before(input.startDate)) {
			return ResponseEntity.badRequest().body(Error.INVALID_DATE_RANGE);
		}
		
		Optional<AbstractLearningProgram> _learningProgram = this.objectStore.getById(AbstractLearningProgram.class, id);
		if(!_learningProgram.isPresent()){
			return ResponseEntity.notFound().build();
		}
		
		AbstractLearningProgram learningProgram = _learningProgram.get();
		if (!hasAccess(accessKey, learningProgram)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(learningProgram instanceof LanguageLearningProgram){
			((LanguageLearningProgram)learningProgram).setType(this.refDataProvider.resolve(LanguageLearningProgramType.class, input.type));
		}
		else{
			((ProfessionalLearningProgram)learningProgram).setDomain(this.refDataProvider.resolve(ProfessionalLearningProgramDomain.class, input.domain));
		}
		
		learningProgram.setStartDate(input.startDate);
		learningProgram.setEndDate(input.endDate);
		learningProgram.setRegistrationOpeningDate(input.registrationOpeningDate);
		learningProgram.setRegistrationClosingDate(input.registrationClosingDate);
		learningProgram.setLink(input.link);
		
		learningProgram.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
		learningProgram.setContact(safeTransform(input.contact, x -> x.toContact()));
		
		learningProgram.setLevel(this.refDataProvider.resolve(Level.class, input.level));
		
		objectStore.save(learningProgram);
		return ResponseEntity.noContent().build();

	}
	
	@CrossOrigin
	@RequestMapping(path = {"language-programs", "professional-programs"}, method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> learningProgram(@RequestBody LearningProgramSummary input, HttpServletRequest req, @RequestHeader String accessKey) throws JsonParseException, JsonMappingException, IOException {
		if(input.id!=null){
			return ResponseEntity.badRequest().build();
		}
		if (input.endDate.before(input.startDate)) {
			return ResponseEntity.badRequest().body(Error.INVALID_DATE_RANGE);
		}
		
		Optional<Organisation> o = getOrganisationByAccessKey(accessKey);
		if(!o.isPresent()){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		String path = (String) req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		
		AbstractLearningProgram lp;
		if(path.endsWith("language-programs")){
			LanguageLearningProgram _lp = new LanguageLearningProgram();
			_lp.setType(this.refDataProvider.resolve(LanguageLearningProgramType.class, input.type));
			lp = _lp;
		}
		else{
			ProfessionalLearningProgram _lp = new ProfessionalLearningProgram();
			_lp.setDomain(this.refDataProvider.resolve(ProfessionalLearningProgramDomain.class, input.domain));
			lp = _lp;
		}
		
		lp.setOrganisation(o.get());
		lp.setStartDate(input.startDate);
		lp.setEndDate(input.endDate);
		lp.setRegistrationOpeningDate(input.registrationOpeningDate);
		lp.setRegistrationClosingDate(input.registrationClosingDate);
		lp.setLink(input.link);
		lp.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
		lp.setContact(safeTransform(input.contact, x -> x.toContact()));
		
		lp.setLevel(this.refDataProvider.resolve(Level.class, input.level));
		
		
		objectStore.save(lp);
		input.id = lp.getId();
		
		return ResponseEntity.created(getUri("/language-programs/" + lp.getId())).body(input);
	}
}
