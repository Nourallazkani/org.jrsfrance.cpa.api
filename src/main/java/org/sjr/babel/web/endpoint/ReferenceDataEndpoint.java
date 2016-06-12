package org.sjr.babel.web.endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.sjr.babel.entity.Level;
import org.sjr.babel.entity.reference.Civility;
import org.sjr.babel.entity.reference.Country;
import org.sjr.babel.entity.reference.FieldOfStudy;
import org.sjr.babel.entity.reference.Language;
import org.sjr.babel.entity.reference.OrganisationCategory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("referenceData")
public class ReferenceDataEndpoint extends AbstractEndpoint{

	private Map<String, List<?>> map = new HashMap<>();
	
	@PostConstruct
	public void fillMap(){
		/*
		// en 1 ligne
		Arrays.asList(Country.class, Civility.class, OrganisationCategory.class, Civility.class, FieldOfStudy.class)
		.forEach(x -> this.map.put(x.getName(), this.objectStore.findAll(x)));
		
		// en deux lignes
		List<Class<? extends AbstractEntity>> list = Arrays.asList(Country.class, Civility.class, OrganisationCategory.class, Civility.class, FieldOfStudy.class);
		list.forEach(x -> this.map.put(x.getName(), this.objectStore.findAll(x)));
		
		*/
		map.put("countries", objectStore.findAll(Country.class));
		map.put("levels", objectStore.findAll(Level.class));
		map.put("civilities", objectStore.findAll(Civility.class));
		map.put("organisationCategories", objectStore.findAll(OrganisationCategory.class));
		map.put("fieldsOfStudy", objectStore.findAll(FieldOfStudy.class));
		map.put("languages", objectStore.findAll(Language.class));
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public Map<String, List<?>> allLists(){
		return map;
	}
	
	@RequestMapping(path="{s}", method=RequestMethod.GET)
	public ResponseEntity<?> oneList(@PathVariable String s){
		List<?> list= map.get(s);
		if(list==null){
			String body = s+" does not exists, please use : "+String.join(", ", map.keySet());
			return ResponseEntity.status(404).body(body);
		}
		return ResponseEntity.ok(list);
	}
	
	/*
	// à l'ancienne :
	
	@RequestMapping(path="/countries",method=RequestMethod.GET)
	public List<Country> countries(){
		return objectStore.findAll(Country.class);
	}
	
	@RequestMapping(path="/civilities",method=RequestMethod.GET)
	public List<Civility> civilities(){
		return objectStore.findAll(Civility.class);
	}*/
}
