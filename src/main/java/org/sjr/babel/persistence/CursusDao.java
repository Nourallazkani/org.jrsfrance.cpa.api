package org.sjr.babel.persistence;

import java.util.List;

import org.sjr.babel.entity.AbstractLearningProgram;

public interface CursusDao {
	
	public List<AbstractLearningProgram> find(String city);
	
	public AbstractLearningProgram getById(int id);

	public AbstractLearningProgram save (AbstractLearningProgram cur);
	
	 void delete (int id);

}
