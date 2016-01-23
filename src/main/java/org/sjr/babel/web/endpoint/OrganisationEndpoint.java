package org.sjr.babel.web.endpoint;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.sjr.babel.entity.Organisation;
import org.sjr.babel.persistence.OrganisationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrganisationEndpoint extends AbstractEndpoint{
	
	@Autowired
	private OrganisationDao dao;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(path="/organisations", method=RequestMethod.GET)
	@Transactional
	public List<Organisation> list (@RequestParam(name="name") String name){
		//List<Organisation> org = dao.find(name);
		Map<String, Object> args = new HashMap<>();
		args.put("n", name);
		return superDao.find("select o from Organisation o where o.name like :n", args, Organisation.class);
	}
	
	@RequestMapping(path="/organisations/{id}", method=RequestMethod.GET)
	@Transactional
	public ResponseEntity<?> org(@PathVariable Integer id){
		logger.info("entering org ");
		return okOrNotFound(superDao.getById(Organisation.class, id));
		//return okOrNotFound(dao.getById(id));
		//return org ==null ? ResponseEntity.notFound().build() : ResponseEntity.ok(org);
	} 
	
	@RequestMapping(path="/organisations", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<Organisation> org(@RequestBody Organisation o){
		o = dao.save(o);
		//return ResponseEntity.status(HttpStatus.CREATED).body(o);
		return ResponseEntity.created(URI.create("http://localhost:8080/organisations/"+o.getId())).body(o);
	}
	
	
	@RequestMapping(path="/organisations/{id}" , method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<?> updateOrg(@RequestBody Organisation o , @PathVariable int id){
		if(o.getId()==null || !o.getId().equals(id)){ 
			return ResponseEntity.badRequest().body("Id is not correct!");
		}
		dao.save(o);
		return ResponseEntity.noContent().build();
	}
	
	
	@RequestMapping(path="/organisations/{id}", method = RequestMethod.DELETE)
	@Transactional
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	public void /*ResponseEntity<Void>*/ delete (@PathVariable int id){
		dao.delete( id);
		//return ResponseEntity.noContent().build();
	}
	

}