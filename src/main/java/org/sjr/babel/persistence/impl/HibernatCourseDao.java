package org.sjr.babel.persistence.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.sjr.babel.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HibernatCourseDao {
	
	//@PersistenceContext
	//private EntityManager em;
	
	@Autowired
	private EntityManagerFactory emf;

	public HibernatCourseDao() {
	}
	
	public List<Course> findCourseByCity (String city){
		EntityManager em = emf.createEntityManager();
		String hql = "select c from Course c where (c.address.city like :name)";
		TypedQuery<Course> query = em.createQuery(hql, Course.class);
		query.setParameter("name", city);
		List<Course> Cour = query.getResultList();
		return Cour;
	}
	
	public List<Course> findCourseBylevelId_City( String levelId, String city){
		EntityManager em = emf.createEntityManager();
		String hql = "select cour from Course cour where (cour.name like :levelId and c.adress.city like :city)";
		TypedQuery<Course> query = em.createQuery(hql, Course.class);
		query.setParameter("levelId", levelId);
		query.setParameter("city", city);
		List<Course> cour = query.getResultList();
		return cour;
	}
	
	public List<Course> findCourseById (Integer id){
		EntityManager em = emf.createEntityManager();
		String hql = "select cour from Course cour where cour.id like :id";
		TypedQuery<Course> query = em.createQuery(hql, Course.class);
		query.setParameter("id", id);
		List<Course> cour = query.getResultList();
		return cour;
	}
	
	}
