package org.sjr.babel.web.endpoint;

import java.util.Optional;

import org.sjr.babel.entity.AbstractEntity;
import org.sjr.babel.entity.Education;
import org.sjr.babel.persistence.ObjectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public abstract class AbstractEndpoint {

	@Autowired
	protected ObjectStore objectStore;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected ResponseEntity<?> okOrNotFound (Optional<?> o){
		return o.isPresent() ? ResponseEntity.ok(o.get()): ResponseEntity.notFound().build() ;
	} 
	
	protected <T extends AbstractEntity> ResponseEntity<Void> deleteIfExists (Class<T> clazz, int id){
		Optional<T> e = objectStore.getById(clazz, id);
		if (e.isPresent()) {
			objectStore.delete(e.get());
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	public static class Link {
		public String title,href,rel;
		public Link(String title, String href, String rel) {
			this.href = href;
			this.title = title;
			this.rel= rel;
		}
	}
}
