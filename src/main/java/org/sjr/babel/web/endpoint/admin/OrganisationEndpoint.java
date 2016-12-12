package org.sjr.babel.web.endpoint.admin;

import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.sjr.babel.model.entity.Organisation;
import org.sjr.babel.web.endpoint.AbstractEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("admin_organisationEndpoint")
@RequestMapping("admin")
public class OrganisationEndpoint extends AbstractEndpoint {

	@RequestMapping(path = "organisations/{id}", method = RequestMethod.DELETE)
	@Transactional
	@RolesAllowed("ADMIN")
	public ResponseEntity<Void> delete(@PathVariable int id) {

		Optional<Organisation> o = this.objectStore.getById(Organisation.class, id);
		if (o.isPresent()) {
			objectStore.delete(o.get());
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
}