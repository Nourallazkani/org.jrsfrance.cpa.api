package org.sjr.babel.persistence;

import java.util.List;

import org.sjr.babel.entity.Course;

public interface CourseDao {
	
	public Course getById (Integer id);
	
	public List<Course> find(String city);
	
	public Course save (Course cour);
	

}
