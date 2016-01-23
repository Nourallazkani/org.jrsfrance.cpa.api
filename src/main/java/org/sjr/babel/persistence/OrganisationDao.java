package org.sjr.babel.persistence;

import java.util.List;

import org.sjr.babel.entity.Organisation;

public interface OrganisationDao {
	
	Organisation getById( int id);

	List<Organisation> find (String name);
	
	Organisation save (Organisation org);
	
	void delete(Organisation o);

	void delete(int id);
}
