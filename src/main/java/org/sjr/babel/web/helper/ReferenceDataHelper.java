package org.sjr.babel.web.helper;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.sjr.babel.entity.AbstractReferenceEntity;
import org.sjr.babel.entity.Level;
import org.sjr.babel.entity.reference.Country;
import org.sjr.babel.entity.reference.EventType;
import org.sjr.babel.entity.reference.FieldOfStudy;
import org.sjr.babel.entity.reference.Language;
import org.sjr.babel.entity.reference.LanguageLearningProgramType;
import org.sjr.babel.entity.reference.ProfessionalLearningProgramDomain;
import org.sjr.babel.persistence.ObjectStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReferenceDataHelper {
	
	interface Loader{
		Map<String, List<?>> load();
	}
	
	@Autowired
	private ObjectStore objectStore;
	
	private Map<String, Loader> loaders = new HashMap<>();
	
	private Map<String, Map<String, List<?>>> referenceData = new HashMap<>();
	
	
	public Map<String, Map<String, List<?>>> getReferenceData() {
		return referenceData;
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractReferenceEntity> T resolve(Class<T> clazz, String name){
		if (name == null){
			return null;
		}
		List<?> allElements = this.referenceData.get(getRegion(clazz)).get("all");
		
		// special resolver for country, 'name' may be the 2 letters code (exemple : FR for France, IT for Italia, etc...).
		if(clazz.equals(Country.class)){
			return (T) allElements.stream().filter(x -> {
				Country c = (Country) x;
				if (name.length() == 2) {
					return name.equals(c.getIsoCode());
				} else {
					return name.equals(c.getName());
				}
			}).findFirst().get();
		}
		else{
			return (T) allElements.stream().filter(x -> ((AbstractReferenceEntity)x).getName().equals(name)).findFirst().get();
		}
	}
	
	private String getRegion(Class<?> clazz){
		return Introspector.decapitalize(clazz.getSimpleName());	
	}
	
	public ReferenceDataHelper() {
		loaders.put(getRegion(Country.class), ()-> {
			Map<String, List<?>> map = new HashMap<>();
			map.put("all", this.objectStore.findAll(Country.class));
			return map;
		});
		loaders.put(getRegion(Language.class), ()-> {
			Map<String, List<?>> map = new HashMap<>();
			map.put("all", this.objectStore.findAll(Language.class));
			return map;
		});
		
		loaders.put(getRegion(Level.class), ()-> {
			Map<String, List<?>> map = new HashMap<>();
			map.put("all", this.objectStore.findAll(Level.class));
			map.put("languageLearningProgram", this.objectStore.find(Level.class, "select distinct level from LanguageLearningProgram"));
			map.put("professionalLearningProgram", this.objectStore.find(Level.class, "select distinct level from ProfessionalLearningProgram"));
			return map;
		});
		loaders.put(getRegion(FieldOfStudy.class), ()-> {
			Map<String, List<?>> map = new HashMap<>();
			map.put("all", this.objectStore.findAll(FieldOfStudy.class));
			map.put("teaching", this.objectStore.find(FieldOfStudy.class, "select distinct fieldOfStudy from Teaching"));
			return map;
		});
		loaders.put(getRegion(EventType.class), ()-> {
			Map<String, List<?>> map = new HashMap<>();
			map.put("all", this.objectStore.findAll(EventType.class));
			map.put("event", this.objectStore.find(EventType.class, "select distinct type from AbstractEvent"));
			return map;
		});
		loaders.put(getRegion(LanguageLearningProgramType.class), ()-> {
			Map<String, List<?>> map = new HashMap<>();
			map.put("all", this.objectStore.findAll(LanguageLearningProgramType.class));
			map.put("languageLearningProgram", this.objectStore.find(LanguageLearningProgramType.class, "select distinct type from LanguageLearningProgram"));
			return map;
		});
		loaders.put(getRegion(ProfessionalLearningProgramDomain.class), ()-> {
			Map<String, List<?>> map = new HashMap<>();
			map.put("all", this.objectStore.findAll(ProfessionalLearningProgramDomain.class));
			map.put("professionalLearningProgram", this.objectStore.find(ProfessionalLearningProgramDomain.class, "select distinct domain from ProfessionalLearningProgram"));
			return map;
		});
		loaders.put("city", ()->{
			Map<String, List<?>> cities = new HashMap<>();
			cities.put("teaching", this.objectStore.find(String.class, "select distinct o.address.locality from Teaching t join t.organisation o"));
			cities.put("languageLearningProgram", this.objectStore.find(String.class, "select distinct address.locality from LanguageLearningProgram"));
			cities.put("professionalLearningProgram", this.objectStore.find(String.class, "select distinct address.locality from ProfessionalLearningProgram"));
			cities.put("workshop", this.objectStore.find(String.class, "select distinct e.address.locality from AbstractEvent e join e.type t where t.stereotype='WORKSHOP'"));
			cities.put("event", this.objectStore.find(String.class, "select distinct e.address.locality from AbstractEvent e join e.type t where t.stereotype is null"));
			return cities;
		});
	}
	
	@PostConstruct
	public void loadCache() {
		for(Map.Entry<String, Loader> loader : this.loaders.entrySet()){
			String region = loader.getKey();
			Map<String, List<?>> elements = loader.getValue().load();
			referenceData.put(region, elements);
		}
	}
	
	public void loadCache(String region) {
		referenceData.put(region, loaders.get(region).load());
	}
}
