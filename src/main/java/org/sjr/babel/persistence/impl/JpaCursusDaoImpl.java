package org.sjr.babel.persistence.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import org.sjr.babel.entity.Cursus;
import org.sjr.babel.persistence.CursusDao;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCursusDaoImpl extends AbstractJpaDao<Cursus> implements CursusDao  {


	@Override	
	public List<Cursus> find(String city){	
		System.out.println(em);
		String hql = "select c from Cursus c where c.address.city like :name";
		TypedQuery<Cursus> query = em.createQuery(hql, Cursus.class);
		System.out.println(query.toString());
		query.setParameter("name", city);
		List<Cursus> results = query.getResultList();
		return results;
		
	}

	@Override
	Class<Cursus> getEntityClass() {
		return Cursus.class;
	}



	
}

