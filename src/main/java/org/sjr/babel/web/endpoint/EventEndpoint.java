package org.sjr.babel.web.endpoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.sjr.babel.entity.AbstractEvent;
import org.sjr.babel.entity.AbstractEvent.Audience;
import org.sjr.babel.entity.AbstractEvent.OrganisationEvent;
import org.sjr.babel.entity.AbstractEvent.VolunteerEvent;
import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.Contact;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.entity.Volunteer;
import org.sjr.babel.entity.reference.EventType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventEndpoint extends AbstractEndpoint {

	public static class EventSummary {
		public int id;
		public String subject, description, organisedBy,type, audience,link;
		public AddressSummary address;
		public Date startDate, endDate, registrationOpeningDate,registrationClosingDate;
		public ContactSummary contact;

		public EventSummary() {	} // for jackson deserialisation
		
		public EventSummary(AbstractEvent event) {
			this.id = event.getId();
			this.subject = event.getSubject();
			this.description = event.getDescription();
			this.audience = event.getAudience().name();
			this.address = safeTransform(event.getAddress(), x -> new AddressSummary(x));
			this.startDate = event.getStartDate();
			this.endDate = event.getEndDate();
			this.registrationOpeningDate = event.getRegistrationOpeningDate();
			this.registrationClosingDate = event.getRegistrationClosingDate();
			this.type = safeTransform(event.getType(), x -> x.getName());
			this.link = event.getLink();
			this.contact = safeTransform(event.getContact(), ContactSummary::new);
			if (event instanceof VolunteerEvent) {
				VolunteerEvent e = (VolunteerEvent) event;
				this.organisedBy = e.getVolunteer().getFullName();
				if(this.contact == null){
					Contact c = new Contact();
					c.setMailAddress(e.getVolunteer().getMailAddress());
					c.setName(e.getVolunteer().getFullName());
					c.setPhoneNumber(e.getVolunteer().getPhoneNumber());
					this.contact = new ContactSummary(c);
				}
			} else if (event instanceof OrganisationEvent) {
				OrganisationEvent e = (OrganisationEvent) event;
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

	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public List<EventSummary> events(
			@RequestParam(required = false) String city,
			@RequestParam(required = false) EventType.Stereotype stereotype,
			@RequestParam(required = false) Integer organisationId,
			@RequestParam(required = false) Integer volunteerId,
			@RequestParam(required = false) AbstractEvent.Audience audience,
			@RequestParam(required = false) Boolean openForRegistration,
			@RequestParam(defaultValue="false") boolean includePastEvents,
			@RequestParam(defaultValue="true") boolean includeFutureEvents
		) 
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
			hql.append("and e.organisation.id = :organisationId");
			args.put("organisationId", organisationId);
		}
		if (stereotype != null) {
			hql.append("and t.stereotype = :stereotype ");
			args.put("stereotype", stereotype);
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
		return objectStore.find(AbstractEvent.class, hql.toString(), args).stream().map(EventSummary::new)
				.collect(Collectors.toList());
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ADMIN", "ORGANISATION" })
	public ResponseEntity<?> getWorkshop(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<AbstractEvent> w = objectStore.getById(AbstractEvent.class, id);
		if (w.isPresent()) {
			return hasAccess(accessKey, w.get()) ? ResponseEntity.ok(w.get()) : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} else
			return ResponseEntity.notFound().build();
	}

	@RequestMapping(path = "/{id}/summary", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> getWorkshopSummary(@PathVariable int id) {

		Optional<EventSummary> w = objectStore.getById(AbstractEvent.class, id).map(ws -> new EventSummary(ws));
		if (w.isPresent()) {
			return ResponseEntity.ok(w.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<Void> deleteWorkshop(@PathVariable int id, @RequestHeader String accessKey) {
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

	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	@Transactional
	@RolesAllowed("ADMIN")
	public ResponseEntity<?> update(@RequestBody EventSummary input,  @PathVariable int id, @RequestHeader String accessKey) {
		if(input.id!=id){
			return ResponseEntity.badRequest().build();
		}
		Optional<AbstractEvent> _event = this.objectStore.getById(AbstractEvent.class, id);
		if(!_event.isPresent()){
			return ResponseEntity.notFound().build();
		}
		
		AbstractEvent event = _event.get();
		if(!hasAccess(accessKey, event))
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		event.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
		event.setContact(safeTransform(input.contact, x -> x.toContact()));
		event.setType(this.refDataProvider.resolve(EventType.class, input.type));
		
		event.setAudience(Audience.valueOf(input.audience));
		event.setDescription(input.description);
		event.setEndDate(event.getEndDate());
		event.setLink(input.link);
		event.setRegistrationClosingDate(input.registrationClosingDate);
		event.setRegistrationOpeningDate(input.registrationOpeningDate);
		event.setStartDate(input.startDate);
		event.setSubject(input.subject);
		
		return ResponseEntity.noContent().build();

	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> save(@RequestBody EventSummary input, @RequestHeader String accessKey){
		if (input.id > 0) {
			return ResponseEntity.badRequest().build();
		}
		
		AbstractEvent event;
		if(accessKey.startsWith("O-")){
			OrganisationEvent _event = new OrganisationEvent();
			Optional<Organisation> _o = getOrganisationByAccessKey(accessKey);
			if(!_o.isPresent()){
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			_event.setOrganisation(_o.get());
			event = _event;
		}
		else if(accessKey.startsWith("V-")){
			VolunteerEvent _event = new VolunteerEvent();
			Optional<Volunteer> _v = getVolunteerByAccessKey(accessKey);
			if(!_v.isPresent()){
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			_event.setVolunteer(_v.get());
			event = _event;
		}
		else{
			return ResponseEntity.badRequest().build();
		}
		
		event.setAddress(safeTransform(input.address, x -> x.toAddress(this.refDataProvider)));
		event.setContact(safeTransform(input.contact, x -> x.toContact()));
		event.setType(this.refDataProvider.resolve(EventType.class, input.type));
		
		event.setAudience(Audience.valueOf(input.audience));
		event.setDescription(input.description);
		event.setEndDate(event.getEndDate());
		event.setLink(input.link);
		event.setRegistrationClosingDate(input.registrationClosingDate);
		event.setRegistrationOpeningDate(input.registrationOpeningDate);
		event.setStartDate(input.startDate);
		event.setSubject(input.subject);
		
		
		this.objectStore.save(event);
		return ResponseEntity.created(getUri("/events/" + event.getId())).body(event);
	}

}
