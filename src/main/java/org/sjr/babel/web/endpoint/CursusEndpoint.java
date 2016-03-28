package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Cursus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cursus") // équivalent à @RequestMapping(path="cursus") et équivalent à @RequestMapping(value="cursus")
public class CursusEndpoint extends AbstractEndpoint {

	// http://dosjds./cursus?city=Paris
	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public List<Cursus> list(@RequestParam(name = "city") String city) {
		return objectStore.find(Cursus.class, "select c from Cursus c where c.address.city like ?", city);
	}

	@RequestMapping(path = "{id}", method = RequestMethod.GET)
	public ResponseEntity<?> cursus(@PathVariable Integer id) {
		
		// return okOrNotFound(objectStore.getById(Cursus.class, id));
		Optional<Cursus> c = objectStore.getById(Cursus.class, id);
		if (c.isPresent()) {
			return ResponseEntity.ok().body(c.get());
		}
		return ResponseEntity.notFound().build();

	}

	@RequestMapping(path = "{id}", method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> saveCur(@RequestBody Cursus cur, @PathVariable int id) {
		if (cur.getId() == null || !cur.getId().equals(id)) {
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		objectStore.save(cur);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(path = "{id}", method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<?> delete(@PathVariable int id) {
		Optional<Cursus> c = objectStore.getById(Cursus.class, id);
		if (c.isPresent()) {
			Cursus cursus = c.get();
			if (!cursus.getCourses().isEmpty()) {
				int n = cursus.getCourses().size();
				return ResponseEntity.badRequest().body("This cursus has "+n+" dependant courses");
			}
			objectStore.delete(cursus);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.badRequest().build();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<?> save (@RequestBody Cursus c){
		if(c.getId()!= null){
			return ResponseEntity.badRequest().build();
		}
		objectStore.save(c);
		return ResponseEntity.created(getUri("/cursus/"+c.getId())).body(c);
	}
	
}
