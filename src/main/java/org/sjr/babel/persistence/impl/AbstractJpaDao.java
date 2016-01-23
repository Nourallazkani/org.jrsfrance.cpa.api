package org.sjr.babel.persistence.impl;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.sjr.babel.entity.AbstractEntity;
public abstract class AbstractJpaDao<T extends AbstractEntity> {

	@PersistenceContext(type = PersistenceContextType.TRANSACTION)
	protected EntityManager em;

	public T save(T entity) {
		System.out.println("i am about to save "+entity);
		if (entity.getId() != null) {
			if (em.contains(entity)) {
				// lorsque lobjet est connu de l entity manager, tout les
				// changement sont detecte par l entity manager
				// et ce dernier synchronisera la base (si necessaire) just
				// avant le commit de la transaction.
			} else
				entity = em.merge(entity);
		} else {
			em.persist(entity);
		}
		return entity;
	}

	public T getById(int id) {
		T entity = em.find(getEntityClass(), id);
		return entity;
	}

	public void delete(T entity) {
		
		if (em.contains(entity)) {
			em.remove(entity);
		} else
		{
			T managedEntity = getById(entity.getId());
			delete(managedEntity);
		}
	}


	public void delete(int id) {
		//em.remove(em.find(getEntityClass(), id));
		T entity = getById(id);
		delete(entity);
		
	}
	
	abstract Class<T> getEntityClass();
}
