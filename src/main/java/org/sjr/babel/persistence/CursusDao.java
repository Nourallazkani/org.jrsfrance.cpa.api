package org.sjr.babel.persistence;

import java.util.List;

import org.sjr.babel.entity.Cursus;

public interface CursusDao {
	
	public List<Cursus> find(String city);
	
	public Cursus getById(int id);

	public Cursus save (Cursus cur);
	
	 void delete (int id);

}
