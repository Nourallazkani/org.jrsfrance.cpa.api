package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.util.ArrayList;
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

import org.sjr.babel.model.component.Contact;
import org.sjr.babel.model.component.MultiLanguageText;
import org.sjr.babel.model.component.Registration;
import org.sjr.babel.model.entity.AbstractEvent;
import org.sjr.babel.model.entity.AbstractEvent.Audience;
import org.sjr.babel.model.entity.AbstractEvent.OrganisationEvent;
import org.sjr.babel.model.entity.AbstractEvent.VolunteerEvent;
import org.sjr.babel.model.entity.Administrator;
import org.sjr.babel.model.entity.Organisation;
import org.sjr.babel.model.entity.Refugee;
import org.sjr.babel.model.entity.Volunteer;
import org.sjr.babel.model.entity.reference.EventType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventEndpoint extends AbstractEndpoint {
	
	public static class EventSummary {
		public Integer id;
		@NotNull @Size(min = 1)
		public String subject, description, audience;
		public String organisedBy, type, link;
		@NotNull @Valid
		public AddressSummary address;
		@NotNull
		public Date startDate, endDate,registrationOpeningDate,registrationClosingDate;
		@NotNull @Valid
		public ContactSummary contact;

		public EventSummary() {	} // for jackson deserialisation
		
		public EventSummary(AbstractEvent entity, String language) {
			this.id = entity.getId();
			
			this.subject = safeTransform(entity.getSubjectI18n(), x -> x.getText(language), entity.getSubject());
			this.description = safeTransform(entity.getDescriptionI18n(), x -> x.getText(language), entity.getDescription());
			
			this.audience = safeTransform(entity.getAudience(), x  -> x.name());
			this.address = safeTransform(entity.getAddress(), x -> new AddressSummary(x));
			this.startDate =entity.getStartDate();
			this.endDate = entity.getEndDate();
			this.registrationOpeningDate = entity.getRegistrationOpeningDate();
			this.registrationClosingDate = entity.getRegistrationClosingDate();
			this.type = safeTransform(entity.getType(), x -> x.getName());
			this.link = entity.getLink();
			this.contact = safeTransform(entity.getContact(), ContactSummary::new);
			if (entity instanceof VolunteerEvent) {
				VolunteerEvent e = (VolunteerEvent) entity;
				this.organisedBy = e.getVolunteer().getFullName();
				if(this.contact == null){
					Contact c = new Contact();
					c.setMailAddress(e.getVolunteer().getMailAddress());
					c.setName(e.getVolunteer().getFullName());
					c.setPhoneNumber(e.getVolunteer().getPhoneNumber());
					this.contact = new ContactSummary(c);
				}
			} else if (entity instanceof OrganisationEvent) {
				OrganisationEvent e = (OrganisationEvent) entity;
				this.organisedBy = e.getOrganisation().getName();
				if(this.contact == null){
					this.contact = new ContactSummary(e.getOrganisation().getContact());
				}
			}
		}
	}
		
	private boolean hasAccess(String accessKey, AbstractEvent event) {
		if (accessKey.startsWith("A-")) {
			Map<String, Object> args = new HashMap<>();
			args.put("accessKey", accessKey);
			String hql = "select a from Administrator a where a.account.accessKey = :accessKey";
			return objectStore.findOne(Administrator.class, hql, args).isPresent();
		} else {
			if (event instanceof VolunteerEvent) {
				VolunteerEvent e = (VolunteerEvent) event;
				return e.getVolunteer().getAccount().getAccessKey().equals(accessKey);
			} else if (event instanceof OrganisationEvent) {
				OrganisationEvent e = (OrganisationEvent) event;
				return e.getOrganisation().getAccount().getAccessKey().equals(accessKey);
			}
			return false;
		}
	}

			
	@RequestMapping(path={"events", "workshops"}, method = RequestMethod.GET)
	@Transactional
	public List<EventSummary> fullSearch(
			@RequestParam(required = false) String city,
			@RequestParam(required = false) Integer organisationId,
			@RequestParam(required = false) Integer volunteerId,
			@RequestParam(required = false) AbstractEvent.Audience audience,
			@RequestParam(required = false) Boolean openForRegistration,
			@RequestParam(defaultValue="false") boolean includePastEvents,
			@RequestParam(defaultValue="true") boolean includeFutureEvents,
			@RequestHeader("Accept-language") String language)
	{
		if(!includePastEvents && !includeFutureEvents){
			return new ArrayList<>();
		}
		
		Class<?> targetClass;
		if(organisationId != null){
			targetClass = OrganisationEvent.class;
		}
		else if(volunteerId != null){
			targetClass = VolunteerEvent.class;
		}
		else{
			targetClass = AbstractEvent.class;
		}
		
		StringBuffer hql = new StringBuffer("select e from ").append(targetClass.getName()).append(" e left join e.type t where 0=0 ");
		HashMap<String, Object> args = new HashMap<>();
		if (city != null && !(city.trim().equals(""))) {
			hql.append("and e.address.locality like :locality ");
			args.put("locality", city);
		}
		if (organisationId != null) {
			hql.append("and e.organisation.id = :organisationId ");
			args.put("organisationId", organisationId);
		}
		if (requestedPathEquals("workshops")) {
			hql.append("and t.stereotype = :stereotype ");
			args.put("stereotype", EventType.Stereotype.WORKSHOP);
		}
		else{
			hql.append("and t.stereotype is null ");
		}
		if (audience != null) {
			hql.append("and e.audience = :audience ");
			args.put("audience", audience);
		}

		Date now = new Date();
		if(openForRegistration != null){
			if(openForRegistration){
				hql.append("and (e.registrationClosingDate >= :d and e.registrationOpeningDate <= :d) ");	
			}
			else{
				hql.append("and (e.registrationClosingDate < :d || e.registrationOpeningDate > :d) ");
			}
			args.put("d", now);
		}
		if(!includeFutureEvents){
			hql.append("and e.startDate <= :d ");
			args.put("d", now);
		}
		if(!includePastEvents){
			hql.append("and e.startDate >= :d ");
			args.put("d", now);
		}
		hql.append("order by e.startDate");
		return objectStore.find(AbstractEvent.class, hql.toString(), args)
				.stream().map(x -> new EventSummary(x, language))
				.collect(Collectors.toList());
	}

	@RequestMapping(path = {"events/{id}", "workshops/{id}"}, method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> summarySearch(@PathVariable int id, @RequestHeader("Accept-language") String language) {

		Optional<EventSummary> w = objectStore.getById(AbstractEvent.class, id).map(ws -> new EventSummary(ws, language));
		if (w.isPresent()) {
			return ResponseEntity.ok(w.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = {"events/{id}", "workshops/{id}"}, method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Void> delete(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<AbstractEvent> w = objectStore.getById(AbstractEvent.class, id);
		if (w.isPresent()) {
			if (hasAccess(accessKey, w.get())) {
				objectStore.delete(w.get());
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.notFound().build();

	}

	@RequestMapping(path = {"events/{id}", "workshops/{id}"}, method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> update(@RequestBody @Valid EventSummary input, BindingResult binding,  @PathVariable int id, @RequestHeader String accessKey) {
		if (input.id == null || !input.id.equals(id)) {
			return ResponseEntity.badRequest().build();
		}
		Optional<AbstractEvent> _event = this.objectStore.getById(AbstractEvent.class, id);
		if(!_event.isPresent()){
			return ResponseEntity.notFound().build();
		}
		
		AbstractEvent event = _event.get();
		if(!hasAccess(accessKey, event))
		{
			return forbidden();
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
		if(!errors.isEmpty()){
			return badRequest(errors);
		}
		
		event.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
		event.setContact(safeTransform(input.contact, x -> x.toContact()));
		event.setType(this.refDataProvider.resolve(EventType.class, input.type));
		
		event.setAudience(Audience.valueOf(input.audience));
		
		if(!input.subject.equals(event.getSubject())){
			event.setSubject(input.subject);
			event.setSubjectI18n(new MultiLanguageText());
		}
		if(!input.description.equals(event.getDescription())){
			event.setDescription(input.description);
			event.setDescriptionI18n(new MultiLanguageText());
		}

		event.setStartDate(input.startDate);
		event.setEndDate(input.endDate);
		event.setLink(input.link);
		event.setRegistrationClosingDate(input.registrationClosingDate);
		event.setRegistrationOpeningDate(input.registrationOpeningDate);
		
		return ResponseEntity.noContent().build();

	}
	
	@RequestMapping(path = {"events", "workshops"}, method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> create(@RequestBody @Valid EventSummary input, BindingResult binding, @RequestHeader String accessKey){
		if (input.id != null) {
			return ResponseEntity.badRequest().build();
		}
		
		boolean isWorkshop = getPath().contains("workshop");
		
		AbstractEvent event;
		if(accessKey.startsWith("O-")){
			OrganisationEvent _event = new OrganisationEvent();
			Optional<Organisation> _o = getOrganisationByAccessKey(accessKey);
			if(!_o.isPresent()){
				return forbidden();
			}
			_event.setOrganisation(_o.get());
			event = _event;
		}
		else if(accessKey.startsWith("V-")){
			VolunteerEvent _event = new VolunteerEvent();
			Optional<Volunteer> _v = getVolunteerByAccessKey(accessKey);
			if(!_v.isPresent()){
				return forbidden();
			}
			_event.setVolunteer(_v.get());
			event = _event;
		}
		else{
			return forbidden();
		}
		
		Map<String, String> errors = errorsAsMap(binding.getFieldErrors());
		
		if(input.startDate != null && input.endDate != null && input.endDate.before(input.startDate)){
			errors.put("startDate", "_");
			errors.put("endDate", "_");
		}
		if(input.registrationClosingDate != null && input.registrationOpeningDate != null && input.registrationClosingDate.before(input.registrationOpeningDate)){
			errors.put("registrationOpeningDate", "_");
			errors.put("registrationClosingDate", "_");
		}
		if(!errors.isEmpty()){
			return badRequest(errors);
		}
		
		event.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
		event.setContact(safeTransform(input.contact, x -> x.toContact()));
		
		if(isWorkshop){
			List<EventType> allEventTypes = this.refDataProvider.getAll(EventType.class);
			EventType workshopEventType = allEventTypes.stream()
					.filter(x -> EventType.Stereotype.WORKSHOP.equals(x.getStereotype()))
					.findFirst()
					.get(); 
			event.setType(workshopEventType);
		}
		else{
			event.setType(this.refDataProvider.resolve(EventType.class, input.type));	
		}
		
		
		event.setAudience(Audience.valueOf(input.audience));
		event.setSubject(input.subject);
		event.setDescription(input.description);
		event.setSubjectI18n(new MultiLanguageText());
		event.setDescriptionI18n(new MultiLanguageText());
		
		event.setLink(input.link);
		event.setRegistrationClosingDate(input.registrationClosingDate);
		event.setRegistrationOpeningDate(input.registrationOpeningDate);
		event.setStartDate(input.startDate);
		event.setEndDate(input.endDate);
		
		this.objectStore.save(event);
		input.id = event.getId();
		URI uri = isWorkshop ? getUri("/workshops/"+event.getId()) : getUri("/events/"+event.getId());
		return ResponseEntity.created(uri).body(input);
	}
	
	@RequestMapping(path = {"events/{id}/registrations", "workshops/{id}/registrations"}, method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ORGANISATION" ,"ADMIN"})
	public ResponseEntity<?> getInscriptions (@PathVariable int id, @RequestHeader String accessKey ){
		Optional<AbstractEvent> _ae = objectStore.getById(AbstractEvent.class, id);
		if (!_ae.isPresent()){
			return notFound();
		}
		if (!hasAccess(accessKey, _ae.get())){
			return forbidden();
		}
		List<Registration> registrations = _ae.get().getRegistrations();
		List<RegistrationSummary> registrationsSummary = registrations.stream().map(x-> new RegistrationSummary(x)).collect(Collectors.toList());
		return ResponseEntity.ok(registrationsSummary);
	}
	
	
	@RequestMapping(path={"events/{id}/registrations" ,"workshops/{id}/registrations"}, method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> addRegistration (@PathVariable int id, @RequestHeader("accessKey") String refugeeAccessKey) {
		Date now = new Date();
		Optional<AbstractEvent> _ae = objectStore.getById(AbstractEvent.class, id);
		if (!_ae.isPresent()){
			return notFound();
		}
		AbstractEvent abstractEvent = _ae.get();

		Optional<Refugee> _r = getRefugeeByAccesskey(refugeeAccessKey);
		if (!_r.isPresent()){
			return forbidden();
		}
		Refugee r = _r.get();

		if(abstractEvent.getRegistrations().stream().anyMatch(x-> x.getRefugee().getId().equals(r.getId()))){
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		
		Registration reg = new Registration();
		reg.setAccepted(null);
		reg.setRefugee(r);
		reg.setRegistrationDate(now);
		abstractEvent.getRegistrations().add(reg);
		return created(null, new RegistrationSummary(reg));
	}
	
	@RequestMapping(path = {"events/{id}/registrations/{rId}", "workshops/{id}/registrations/{rId}"}, method = {RequestMethod.POST,RequestMethod.PATCH})
	@Transactional
	public ResponseEntity<?> acceptOrRefuse(@PathVariable int id, @PathVariable int rId, @RequestBody AcceptOrRefuseRegistrationCommand input,  @RequestHeader String accessKey) {
		Optional<AbstractEvent> _event = this.objectStore.getById(AbstractEvent.class, id);
		if(!_event.isPresent()){
			return ResponseEntity.notFound().build();
		}
		AbstractEvent event = _event.get();
		if(!hasAccess(accessKey, event))
		{
			return forbidden();
		}
		Optional<Refugee> _r = this.objectStore.getById(Refugee.class, rId);
		if (!_r.isPresent()){
			return notFound();
		}
		Refugee r = _r.get();
		Optional<Registration> _reg = event.getRegistrations().stream()
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
	
	@RequestMapping(path = {"events/{id}/registrations/{rId}", "workshops/{id}/registrations/{rId}"}, method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<?> cancelRegistration (@PathVariable int id, @PathVariable int rId,  @RequestHeader String accessKey) {
		Optional<AbstractEvent> _event = this.objectStore.getById(AbstractEvent.class, id);
		if(!_event.isPresent()){
			return notFound();
		}
		AbstractEvent event = _event.get();
		if(!hasAccess(accessKey, event))
		{
			return forbidden();
		}
		Optional<Refugee> _r = this.objectStore.getById(Refugee.class, rId);
		if (!_r.isPresent()){
			return notFound();
		}
		Refugee r = _r.get();
		
		if(!event.getRegistrations().removeIf(x -> x.getRefugee().getId().equals(r.getId()))){
			return badRequest();
		}
	
		return noContent();
	}	
}
