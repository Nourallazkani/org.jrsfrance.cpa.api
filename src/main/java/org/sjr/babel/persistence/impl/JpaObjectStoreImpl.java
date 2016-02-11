package org.sjr.babel.persistence.impl;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import org.sjr.babel.entity.AbstractEntity;
import org.sjr.babel.persistence.ObjectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class JpaObjectStoreImpl implements ObjectStore {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@PersistenceContext(type = PersistenceContextType.TRANSACTION)
	protected EntityManager em;

	@Override
	public <T extends AbstractEntity> T save(T entity) {
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

	@Override
	public <T extends AbstractEntity> void delete(T entity) {
		logger.debug("about to delete "+ entity.getClass().getName()+ "#"+entity.getId());
		if( em.contains(entity)){
			this.em.remove(entity);
		} else {
			// bad signal
			this.delete(entity.getClass(), entity.getId());
		}
	}

	@Override
	public <T extends AbstractEntity> void delete(Class<T> clazz, int id) {
		T entity = getById(clazz, id);
		delete(entity);
	}

	@Override // pour l appelant : Organisation o = superdao.getById(Organisation.class, 3);
	public <T extends AbstractEntity> T getById(Class<T> clazz, int id) {
		T entity = em.find(clazz, id);
		return entity;
	}

	// pour l appelant :
	// String hql = "select o from Organisation o where o.address.country.name like :x";
	// Map<String, Object> args = new HashMap<String, Object>();
	// args.put("x", "France");
	// List<Organisation> orgs = find(hql, args, Organisation.class);
	//
	//
	/**
	 * 
	 * 
	 */
	@Override
	public <T extends AbstractEntity> List<T> find(String hql, Map<String, Object> args, Class<T> clazz) {
		TypedQuery<T> query = em.createQuery(hql, clazz);
		if (args != null) {
			for( String argKey : args.keySet()){
				Object value = args.get(argKey);
				logger.debug("about to bind param "+ argKey+" with value "+ value);
				query.setParameter(argKey, value);
			};
		}
		List<T> results = query.getResultList();
		return results;
	}

	@Override
	public <T extends AbstractEntity> List<T> find(String hql, Class<T> clazz) {
		return find(hql, null, clazz);
	}

}
