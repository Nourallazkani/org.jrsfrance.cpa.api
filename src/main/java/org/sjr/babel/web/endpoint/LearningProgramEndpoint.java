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
import org.sjr.babel.entity.ProfessionalLearningProgram;
import org.sjr.babel.web.endpoint.AbstractEndpoint.AddressSummary;
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
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping(path = "learnings")
public class LearningProgramEndpoint extends AbstractEndpoint {

	class LearningProgramSummary {
		public int id;
		public String level, organisation;
		public AddressSummary address;
		public Date startDate, endDate, registrationStartDate;
		public boolean openForRegistration;
		public ContactSummary contact;
		
		@JsonInclude(value=Include.NON_NULL)
		public String domain, type;
		
		public LearningProgramSummary(AbstractLearningProgram lp) {
			this.id = lp.getId();
			this.level = lp.getLevel().getName();
			this.organisation = lp.getOrganisation().getName();
			this.address = safeTransform(lp.getAddress(), x -> new AddressSummary(x, true));
			this.registrationStartDate = lp.getRegistrationStartDate();
			this.openForRegistration = lp.isOpenForRegistration();
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
		if(openForRegistration!=null){
			args.put("openForRegistration" , openForRegistration.booleanValue());
			query.append("and c.openForRegistration= :openForRegistration ");			
		}
		Date now = new Date();
		if(!includeFutureEvents){
			query.append("and c.startDate <= :d ");
			args.put("d", now);
		}
		if(!includePastEvents){
			query.append("and c.startDate >= :d ");
			args.put("d", now);
		}
		
		List<? extends AbstractLearningProgram> results = objectStore.find(targetClass, query.toString(), args);
		return results.stream()
				.map(LearningProgramSummary::new)
				.collect(Collectors.toList());
	}

	@RequestMapping(path = {"language-programs/{id}", "professional-programs/{id}"}, method = RequestMethod.GET)
	@RolesAllowed({ "ADMIN" })
	@Transactional
	public ResponseEntity<?> learningProgram(@PathVariable Integer id, @RequestParam(defaultValue = "true") boolean withDetails, @RequestHeader String accessKey) {
		// return okOrNotFound(objectStore.getById(Cursus.class, id));
		Optional<AbstractLearningProgram> c = objectStore.getById(AbstractLearningProgram.class, id);
		if (c.isPresent()) {
			if (hasAccess(accessKey, c.get())) {
				AbstractLearningProgram lp = c.get();
				if (withDetails) {
					lp.getCourses().size();
				}
				return ResponseEntity.ok().body(lp);
			}
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.notFound().build();
	}

	@RequestMapping(path = {"language-programs/{id}/summary", "professional-programs/{id}/summary"}, method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> learningProgram(@PathVariable Integer id) {
		// return okOrNotFound(objectStore.getById(Cursus.class, id));
		Optional<AbstractLearningProgram> c = objectStore.getById(AbstractLearningProgram.class, id);
		if (c.isPresent()) {
			return ResponseEntity.ok().body(new LearningProgramSummary(c.get()));
		}
		return ResponseEntity.notFound().build();
	}

	@RequestMapping(path = "{id}/courses", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> list(@PathVariable int id) {
		Optional<AbstractLearningProgram> c = objectStore.getById(AbstractLearningProgram.class, id);
		if (c.isPresent()) {
			AbstractLearningProgram cursus = c.get();
			cursus.getCourses().size();
			return ResponseEntity.ok(cursus.getCourses());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = {"language-programs/{id}", "professional-programs/{id}"}, method = RequestMethod.PUT)
	@Transactional
	@RolesAllowed({ "ADMIN", "ORGANISATION" })
	public ResponseEntity<?> learningProgram(@RequestBody AbstractLearningProgram cur, @PathVariable int id, @RequestHeader String accessKey) {
		if (cur.getId() == null || !cur.getId().equals(id)) {
			return ResponseEntity.badRequest().body("Id is not correct!");
		} else if (!hasAccess(accessKey, cur)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		if (cur.getEndDate().before(cur.getStartDate())) {
			return ResponseEntity.badRequest().body(Error.INVALID_DATE_RANGE);
		}
		objectStore.save(cur);
		return ResponseEntity.noContent().build();

	}

	@RequestMapping(path = {"language-programs/{id}", "professional-programs/{id}"}, method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed({ "ADMIN", "ORGANISATION" })
	public ResponseEntity<?> learningProgram(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<AbstractLearningProgram> c = objectStore.getById(AbstractLearningProgram.class, id);
		if (!c.isPresent()) {
			return ResponseEntity.badRequest().build();
		}
		AbstractLearningProgram cursus = c.get();
		if (!hasAccess(accessKey, c.get())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		if (!cursus.getCourses().isEmpty()) {
			int n = cursus.getCourses().size();
			return ResponseEntity.badRequest().body("This cursus has " + n + " dependant courses");
		}
		objectStore.delete(cursus);
		return ResponseEntity.noContent().build();

	}

	@RequestMapping(path = {"language-programs", "professional-programs"}, method = RequestMethod.POST)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> learningProgram(HttpServletRequest req, @RequestHeader String accessKey) throws JsonParseException, JsonMappingException, IOException {
		
		String path = (String) req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		
		Class<? extends AbstractLearningProgram > targetClass = path.endsWith("language-programs")  ? LanguageLearningProgram.class : ProfessionalLearningProgram.class;
		
		AbstractLearningProgram lp = this.jackson.readValue(req.getInputStream(), targetClass);
		if (lp.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		if (lp.getEndDate().before(lp.getStartDate())) {
			return ResponseEntity.badRequest().body(Error.INVALID_DATE_RANGE);
		}
		if (hasAccess(accessKey, lp)) {
			objectStore.save(lp);
			return ResponseEntity.created(getUri("/cursus/" + lp.getId())).body(lp);
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

}
