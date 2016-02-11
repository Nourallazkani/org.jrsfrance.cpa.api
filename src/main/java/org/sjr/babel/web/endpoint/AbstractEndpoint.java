package org.sjr.babel.web.endpoint;

import org.sjr.babel.persistence.ObjectStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public abstract class AbstractEndpoint {

	@Autowired
	protected ObjectStore objectStore;
	
	protected ResponseEntity<?> okOrNotFound (Object o){
		return o == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(o);
	} 
}
