package org.sjr.babel.web.endpoint;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.transaction.Transactional;

import org.sjr.babel.entity.Cursus;
import org.sjr.babel.persistence.CursusDao;
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
public class CursusEndpoint  {
	
	@Autowired
	private CursusDao dao;
	
	public CursusEndpoint() {
		System.out.println("inside counst");
	}

	//http://dosjds./cursus?city=Paris
	@RequestMapping(path="cursus", method=RequestMethod.GET)
	public List<Cursus> list(@RequestParam(name="city") String city){
		List<Cursus> cur = dao.find(city);
		return cur;
	}
	
	@RequestMapping(path="cursus/{id}", method=RequestMethod.GET)
	public Cursus cursus(@PathVariable Integer id ,Model model){
		return dao.getById(id);
	}
	
	@RequestMapping(path="/cursus/{id}", method= RequestMethod.PUT)
	@Transactional
	public  ResponseEntity<?> saveCur (@RequestBody Cursus cur, @PathVariable int id){
		if(cur.getId()==null || !cur.getId().equals(id)){ 
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		dao.save(cur);
		return ResponseEntity.noContent().build();
	}
}
