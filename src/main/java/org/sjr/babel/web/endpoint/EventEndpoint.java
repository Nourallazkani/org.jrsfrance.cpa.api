package org.sjr.babel.web.endpoint;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.sjr.babel.entity.Administrator;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.entity.AbstractEvent;
import org.sjr.babel.entity.AbstractEvent.OrganisationEvent;
import org.sjr.babel.entity.AbstractEvent.VolunteerEvent;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/events")
public class EventEndpoint extends AbstractEndpoint {

	class EventSummary {
		public int id;
		public String subject, description, organizedBy,type;
		public AddressSummary address;
		public Date startDate, endDate;

		public EventSummary(AbstractEvent event) {
			this.id = event.getId();
			this.subject = event.getSubject();
			this.description = event.getDescription();
			this.address = new AddressSummary(event.getAddress());
			this.startDate = event.getStartDate();
			this.endDate = event.getEndDate();
			this.type = event.getType().getName();
			if (event instanceof VolunteerEvent) {
				VolunteerEvent e = (VolunteerEvent) event;
				this.organizedBy = e.getVolunteer().getFullName();
			} else if (event instanceof OrganisationEvent) {
				OrganisationEvent e = (OrganisationEvent) event;
				this.organizedBy = e.getOrganisation().getName();
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
	public List<EventSummary> getWorkshopSuammary(@RequestParam(required = false) String city,
			@RequestParam(required = false) String zipcode) {
		StringBuffer hql = new StringBuffer("select w from Workshop w where 0=0 ");
		HashMap<String, Object> args = new HashMap<>();
		if (city != null && !(city.trim().equals(""))) {
			hql.append("and w.address.city like :city");
			args.put("city", city);
		}
		if (zipcode != null && !(city.trim().equals(""))) {
			hql.append("and w.address.zipcode like :zipcode");
			args.put("zipcode", zipcode);
		}
		return objectStore.find(AbstractEvent.class, hql.toString(), args).stream().map(EventSummary::new)
				.collect(Collectors.toList());
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	@Transactional
	@RolesAllowed({ "ADMIN", "ORGANISATION" })
	public ResponseEntity<?> getWorkshop(@PathVariable int id, @RequestHeader String accessKey) {
		Optional<AbstractEvent> w = objectStore.getById(AbstractEvent.class, id);
		if (w.isPresent()) {
			return hasAccess(accessKey, w.get()) ? ResponseEntity.ok(w.get())
					: ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
	public ResponseEntity<?> updateWorkshop(@RequestParam AbstractEvent w, @PathVariable int id,
			@RequestHeader String accessKey) {
		if (w.getId() == null || !(w.getId() == id)) {
			return ResponseEntity.badRequest().build();
		} else if (hasAccess(accessKey, w)) {
			objectStore.save(w);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	@RolesAllowed({ "ADMIN" })
	public ResponseEntity<?> saveWorkshop(HttpServletRequest req, @RequestHeader String accessKey)
			throws JsonProcessingException, IOException {
		AbstractEvent event = null;
		JsonNode json = jackson.readTree(req.getInputStream());
		if (json.has("organisation")) {
			event = jackson.treeToValue(json, OrganisationEvent.class);
		} else if (json.has("volunteer")) {
			event = jackson.treeToValue(json, VolunteerEvent.class);
		}

		if (event == null) {
			return ResponseEntity.badRequest().build();
		}
		if (event.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		if (!hasAccess(accessKey, event)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		objectStore.save(event);
		return ResponseEntity.created(getUri("/events/" + event.getId())).body(event);
	}

}