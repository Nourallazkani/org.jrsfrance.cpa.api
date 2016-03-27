package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Education;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EducationEndpoint extends AbstractEndpoint {

	class EducationSummary {
		public int id;
		public String fieldOfStudy, lanuageLevelRequired, city, country;
		// public String organisation ;
		public List<Link> links;

		public EducationSummary(Education e) {
			this.id = e.getId();
			this.fieldOfStudy = e.getFieldOfStudy().getName();
			this.lanuageLevelRequired = e.getLanguageLevelRequired().getName();
			// this.organisation= new Link(e.getOrganisation().getName(),
			// "/organisations/"+e.getOrganisation().getId());
			this.links = Arrays.asList(new Link(e.getOrganisation().getName(),
					"/organisations/" + e.getOrganisation().getId(), "organisation"),
					new Link(null, "/educations/" + e.getId(), "self"));

			if (e.getOrganisation().getAddress() != null) {
				this.city = e.getOrganisation().getAddress().getCity();
				this.country = e.getOrganisation().getAddress().getCountry().getName();
			}

		}
	}

	@RequestMapping(path = "/educations", method = RequestMethod.GET)
	@Transactional
	public List<EducationSummary> list() {
		return objectStore.find(Education.class,"select e from Education e ").stream()
				.map(e -> new EducationSummary(e)).collect(Collectors.toList());
	}

	@RequestMapping(path = "/educations/{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> get(@PathVariable int id) {

		Optional<Education> e = objectStore.getById(Education.class, id);
		if (e.isPresent()) {
			return ResponseEntity.ok(e.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/educations/{id}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Void> delete(@PathVariable int id) {
		// return deleteIfExists(Education.class, id);

		Optional<Education> e = objectStore.getById(Education.class, id);
		if (e.isPresent()) {
			objectStore.delete(e.get());
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/educations/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<Void> update(@PathVariable int id, @RequestBody Education e) {
		if (e.getId() == null || !(e.getId().equals(id))) {
			return ResponseEntity.badRequest().build();
		} else {
			objectStore.save(e);
			return ResponseEntity.noContent().build();
		}

	}

	@RequestMapping(path = "/educations", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> create(@RequestBody Education e) throws URISyntaxException {
		if (e.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		objectStore.save(e);
		return ResponseEntity.created(new URI("/educations/" + e.getId())).build();
	}

	/*
	 * un GET sur educations doit retourner toutes les educations. un GET sur
	 * educations/1 doit retourner la education #1. un DELETE sur educations/1
	 * doit supprimer la education #1. un PUT(update) sur educations/1 doit
	 * mettre a jour l'education #1. un POST sur educations doit doit
	 * enregistrer une nouvelle education.
	 */

}
