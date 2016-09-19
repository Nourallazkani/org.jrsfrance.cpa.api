package org.sjr.babel.web.endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.sjr.babel.entity.Level;
import org.sjr.babel.entity.reference.Civility;
import org.sjr.babel.entity.reference.Country;
import org.sjr.babel.entity.reference.FieldOfStudy;
import org.sjr.babel.entity.reference.Language;
import org.sjr.babel.entity.reference.LanguageLearningProgramType;
import org.sjr.babel.entity.reference.OrganisationCategory;
import org.sjr.babel.entity.reference.ProfessionalLearningProgramDomain;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("referenceData")
public class ReferenceDataEndpoint extends AbstractEndpoint {

	private Map<String, Object> map = new HashMap<>();

	interface Loader{
		Object load();
	}
	
	private Map<String, Loader> loaders = new HashMap<>();
	
	public ReferenceDataEndpoint() {
		loaders.put("countries", ()-> objectStore.findAll(Country.class));
		loaders.put("levels", ()-> objectStore.findAll(Level.class));
		loaders.put("civilities", ()-> objectStore.findAll(Civility.class));
		loaders.put("organisationCategories", ()-> objectStore.findAll(OrganisationCategory.class));
		loaders.put("fieldsOfStudy", ()-> objectStore.findAll(FieldOfStudy.class));
		loaders.put("languages", ()-> objectStore.findAll(Language.class));
		loaders.put("eventTypes", ()-> objectStore.findAll(Language.class));
		loaders.put("languageLearningProgramTypes", ()-> objectStore.findAll(LanguageLearningProgramType.class));
		loaders.put("professionalLearningProgramDomains", ()-> objectStore.findAll(ProfessionalLearningProgramDomain.class));
		loaders.put("cities", ()->{
			Map<String, List<String>> cities = new HashMap<>();
			cities.put("teaching", this.objectStore.find(String.class, "select distinct o.address.locality from Teaching t join t.organisation o"));
			cities.put("languageLearningProgram", this.objectStore.find(String.class, "select distinct address.locality from LanguageLearningProgram"));
			cities.put("professionalLearningProgram", this.objectStore.find(String.class, "select distinct address.locality from ProfessionalLearningProgram"));
			cities.put("workshops", this.objectStore.find(String.class, "select distinct e.address.locality from AbstractEvent e join e.type t where t.stereotype='WORKSHOP'"));
			cities.put("events", this.objectStore.find(String.class, "select distinct e.address.locality from AbstractEvent e join e.type t where t.stereotype is null"));
			return cities;
		});
	}
	
	@PostConstruct
	public void fillMap() {

		for(Map.Entry<String, Loader> loader : this.loaders.entrySet()){
			map.put(loader.getKey(), loader.getValue().load());
		}
		/*
		map.put("countries", objectStore.findAll(Country.class));
		map.put("levels", objectStore.findAll(Level.class));
		map.put("civilities", objectStore.findAll(Civility.class));
		map.put("organisationCategories", objectStore.findAll(OrganisationCategory.class));
		map.put("fieldsOfStudy", objectStore.findAll(FieldOfStudy.class));
		map.put("languages", objectStore.findAll(Language.class));
		map.put("eventTypes", objectStore.findAll(Language.class));
		map.put("languageLearningProgramTypes", objectStore.findAll(LanguageLearningProgramType.class));
		map.put("professionalLearningProgramDomains", objectStore.findAll(ProfessionalLearningProgramDomain.class));
		
		
		Map<String, List<String>> cities = new HashMap<>();
		cities.put("teaching", this.objectStore.find(String.class, "select distinct o.address.locality from Teaching t join t.organisation o"));
		cities.put("languageLearningProgram", this.objectStore.find(String.class, "select distinct address.locality from LanguageLearningProgram"));
		cities.put("professionalLearningProgram", this.objectStore.find(String.class, "select distinct address.locality from ProfessionalLearningProgram"));
		cities.put("workshops", this.objectStore.find(String.class, "select distinct e.address.locality from AbstractEvent e join e.type t where t.stereotype='WORKSHOP'"));
		cities.put("events", this.objectStore.find(String.class, "select distinct e.address.locality from AbstractEvent e join e.type t where t.stereotype is null"));
		
		map.put("cities", cities);*/
	}

	@RequestMapping(method = RequestMethod.GET)
	public Map<String, Object> allLists(@RequestParam(defaultValue="false") boolean noCache) {
		if (map == null || noCache) {
			fillMap();
		}
		return map;
	}

	@RequestMapping(path = "{s}", method = RequestMethod.GET)
	public ResponseEntity<?> oneList(@PathVariable String s, @RequestParam(defaultValue="false") boolean noCache) {
		if (map == null) {
			fillMap();
		}
		else if(noCache){
			this.map.put(s, loaders.get(s).load());
		}
		Object list = map.get(s);
		if (list == null) {
			String body = s + " does not exists, please use : " + String.join(", ", map.keySet());
			return ResponseEntity.status(404).body(body);
		}
		return ResponseEntity.ok(list);
	}

	/*
	 * // à l'ancienne :
	 * 
	 * @RequestMapping(path="/countries",method=RequestMethod.GET) public
	 * List<Country> countries(){ return objectStore.findAll(Country.class); }
	 * 
	 * @RequestMapping(path="/civilities",method=RequestMethod.GET) public
	 * List<Civility> civilities(){ return objectStore.findAll(Civility.class);
	 * }
	 */
}
