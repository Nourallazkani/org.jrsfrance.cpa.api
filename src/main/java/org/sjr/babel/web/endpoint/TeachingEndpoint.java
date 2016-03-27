package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Teaching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeachingEndpoint extends AbstractEndpoint {

	class EducationSummary {
		public int id;
		public String fieldOfStudy, lanuageLevelRequired, city, country;
		public List<Link> links;

		public EducationSummary(Teaching e) {
			this.id = e.getId();
			this.fieldOfStudy = e.getFieldOfStudy().getName();
			this.lanuageLevelRequired = e.getLanguageLevelRequired().getName();
			this.links = Arrays.asList(
					new Link(e.getOrganisation().getName(),	"/organisations/" + e.getOrganisation().getId(), "organisation"),
					new Link(null, "/educations/" + e.getId(), "self")
				);

			if (e.getOrganisation().getAddress() != null) {
				this.city = e.getOrganisation().getAddress().getCity();
				this.country = e.getOrganisation().getAddress().getCountry().getName();
			}
		}
	}

	@RequestMapping(path = "/teachings", method = RequestMethod.GET)
	@Transactional
	public List<EducationSummary> list() {
		return objectStore.find(Teaching.class, "select t from Teaching t ")
				.stream()
				.map(e -> new EducationSummary(e))
				.collect(Collectors.toList());
	}

	@RequestMapping(path = "/teachings/{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> get(@PathVariable int id) {

		Optional<Teaching> e = objectStore.getById(Teaching.class, id);
		if (e.isPresent()) {
			return ResponseEntity.ok(e.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/teachings/{id}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Void> delete(@PathVariable int id) {
		// return deleteIfExists(Education.class, id);

		Optional<Teaching> e = objectStore.getById(Teaching.class, id);
		if (e.isPresent()) {
			objectStore.delete(e.get());
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(path = "/teachings/{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<Void> update(@PathVariable int id, @RequestBody Teaching e) {
		if (e.getId() == null || !(e.getId().equals(id))) {
			return ResponseEntity.badRequest().build();
		} else {
			objectStore.save(e);
			return ResponseEntity.noContent().build();
		}

	}

	@RequestMapping(path = "/teachings", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> create(@RequestBody Teaching e) {
		if (e.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		objectStore.save(e);
		return ResponseEntity.created(URI.create("/teachings/" + e.getId())).build();
	}
}