package org.sjr.babel.web.endpoint;

import java.util.List;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Course;
import org.sjr.babel.entity.Organisation;
import org.sjr.babel.persistence.CourseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseEndpoint {
	
	@Autowired
	private CourseDao dao;
	
	@RequestMapping(path= "courses", method=RequestMethod.GET)
	public List<Course> list(@RequestParam(required=false, name="city") String city ){
		List<Course> coures = dao.find(city);
		return coures;
		
	}
	
	@RequestMapping(path="courses/{id}", method=RequestMethod.GET)
	public Course cr(@PathVariable Integer id ,Model model){
		Course cr = dao.getById(id);
		return cr;
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
	
	

}
