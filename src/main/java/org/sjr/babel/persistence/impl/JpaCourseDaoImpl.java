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
public class JpaCourseDaoImpl implements CourseDao {

	@PersistenceContext(type = PersistenceContextType.TRANSACTION)
	EntityManager em;


	@Override
	public Course getById(Integer id) {
		Course result = em.find(Course.class, id);
		return result;
	}

	@Override
	public List<Course> find(String city) {
		String hql = " select cr from Course cr where cr.address.city like :city";
		TypedQuery<Course> query = em.createQuery(hql, Course.class);
		query.setParameter("city", city);
		List<Course> resultList = query.getResultList();
		return resultList;

	}

	@Override
	public Course save(Course cour) {
		if (cour.getId() != null) {
			if (em.contains(cour)) {
				// lorsque lobjet est connu de l entity manager, tout les changement sont detecte par l entity manager
				// et ce dernier synchronisera la base (si necessaire) just avant le commit de la transaction.
			} else
				cour = em.merge(cour);
		} else {
			em.persist(cour);
		}
		return cour;
	}

}
