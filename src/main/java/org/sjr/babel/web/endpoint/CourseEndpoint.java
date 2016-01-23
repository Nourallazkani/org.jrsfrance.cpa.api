package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.util.List;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Course;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.persistence.CourseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public  class  CourseEndpoint extends AbstractEndpoint {
	
	@Autowired
	private CourseDao dao;
	
	@RequestMapping(path= "courses", method=RequestMethod.GET)
	public List<Course> list(@RequestParam(required=false, name="city") String city ){
		List<Course> coures = dao.find(city);
		return coures;
		
	}
	
	@RequestMapping(path="courses/{id}", method=RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> cr(@PathVariable Integer id ,Model model){
		return okOrNotFound(dao.getById(id));
	}
	
	@RequestMapping(path="/courses/{id}" , method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> updateOrg(@RequestBody Course cour , @PathVariable int id){
		if(cour.getId()==null || !cour.getId().equals(id)){ 
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		dao.save(cour);
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(path="/courses" , method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<Course> save (@RequestBody Course cour){
		Course afterSave = dao.save(cour);
		return ResponseEntity.created(URI.create("http://localhost:8080/courses/"+afterSave.getId())).body(afterSave);
	}
	
	@RequestMapping (path="/courses/{id}",method = RequestMethod.DELETE)
	@Transactional
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	public void delete (@PathVariable int id){
		dao.delete(id);
	}
	

}
