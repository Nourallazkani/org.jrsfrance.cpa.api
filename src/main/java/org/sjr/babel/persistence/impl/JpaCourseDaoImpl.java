package org.sjr.babel.persistence.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import org.sjr.babel.entity.Course;
import org.sjr.babel.persistence.CourseDao;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCourseDaoImpl extends AbstractJpaDao<Course> implements CourseDao {

	@PersistenceContext(type = PersistenceContextType.TRANSACTION)
	EntityManager em;


	@Override
	public List<Course> find(String city) {
		String hql = " select cr from Course cr where cr.address.city like :city";
		TypedQuery<Course> query = em.createQuery(hql, Course.class);
		query.setParameter("city", city);
		List<Course> resultList = query.getResultList();
		return resultList;

	}


	@Override
	Class<Course> getEntityClass() {
		return Course.class;
	}
}
