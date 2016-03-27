package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Course;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "courses")
public class CourseEndpoint extends AbstractEndpoint {

	@RequestMapping(method = RequestMethod.GET)
	public List<Course> list(@RequestParam(required = false, name = "city") String ci) {
		return objectStore.find(Course.class, "select c from Course c where c.address.city like :c", ci);

	}

	@RequestMapping(path = "{id}", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> cr(@PathVariable Integer id, Model model) {
		return okOrNotFound(objectStore.getById(Course.class, id));
	}

	@RequestMapping(path = "{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> updateOrg(@RequestBody Course cour, @PathVariable int id) {
		if (cour.getId() == null || !cour.getId().equals(id)) {
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		objectStore.save(cour);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> save(@RequestBody Course cour) {
		if (cour.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		Course afterSave = objectStore.save(cour);
		return ResponseEntity.created(URI.create("http://localhost:8080/courses/" + afterSave.getId())).body(afterSave);
	}

	@RequestMapping(path = "{id}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Void> delete(@PathVariable int id) {
		Optional<Course> c = objectStore.getById(Course.class, id);
		if (c.isPresent()) {
			objectStore.delete(c.get());
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();

	}

}
