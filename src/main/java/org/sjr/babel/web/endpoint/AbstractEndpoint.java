package org.sjr.babel.web.endpoint;

import org.springframework.http.ResponseEntity;

public abstract class AbstractEndpoint {

	protected ResponseEntity<?> okOrNotFound (Object o){
		return o == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(o);
	} 
}
