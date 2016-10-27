package org.sjr.babel.persistence;

import java.util.List;

import org.sjr.babel.model.entity.Course;

public interface CourseDao {
	
	public Course getById (int id);
	
	public List<Course> find(String city);
	
	public Course save (Course cour);
	
	public void delete (int id);
	

}
