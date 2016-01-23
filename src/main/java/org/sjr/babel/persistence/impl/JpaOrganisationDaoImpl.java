package org.sjr.babel.persistence.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import org.sjr.babel.entity.Organisation;
import org.sjr.babel.persistence.OrganisationDao;
import org.springframework.stereotype.Repository;

@Repository
public class JpaOrganisationDaoImpl extends AbstractJpaDao<Organisation> implements OrganisationDao {

	@Override
	public List<Organisation> find(String name) {
		String hql = "select o from Organisation o where o.address.country.name like :name";
		TypedQuery<Organisation> query = em.createQuery(hql, Organisation.class);
		query.setParameter("name", name);
		List<Organisation> org = query.getResultList();
		return org;
	}

	@Override
	Class<Organisation> getEntityClass() {
		return Organisation.class;
	}


	

}
