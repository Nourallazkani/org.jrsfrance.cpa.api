package org.sjr.babel.persistence.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
class JpaObjectStoreImpl implements ObjectStore {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(type = PersistenceContextType.TRANSACTION)
	protected EntityManager em;

	@Override
	public <T extends AbstractEntity> T save(T entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity cannot be null");
		}
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
		logger.debug("about to delete " + entity.getClass().getName() + "#" + entity.getId());
		if (em.contains(entity)) {
			this.em.remove(entity);
		} else {
			// bad signal
			this.delete(entity.getClass(), entity.getId());
		}
	}

	@Override
	public <T extends AbstractEntity> void delete(Class<T> clazz, int id) {
		Optional<T> entity = getById(clazz, id);
		if (entity.isPresent()) {
			delete(entity.get());
		}
	}

	@Override // pour l appelant : Organisation o =
				// superdao.getById(Organisation.class, 3);
	public <T extends AbstractEntity> Optional<T> getById(Class<T> clazz, int id) {
		T entity = em.find(clazz, id);
		if (entity != null) {
			return Optional.of(entity);
		} else {
			return Optional.empty();
		}
	}

	// pour l appelant :
	// String hql = "select o from Organisation o where o.address.country.name
	// like :x";
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
	public <T> List<T> find(Class<T> clazz, String hql, Map<String, Object> args) {
		TypedQuery<T> query = em.createQuery(hql, clazz);
		if (args != null) {
			for (String argKey : args.keySet()) {
				Object value = args.get(argKey);
				logger.debug("about to bind param " + argKey + " with value " + value);
				query.setParameter(argKey, value);
			};
		}
		List<T> results = query.getResultList();
		return results;
	}

	@Override
	public <T> List<T> find(Class<T> clazz, String hql) {
		return find(clazz, hql, null);
	}

	@Override
	public <T> List<T> findAll(Class<T> clazz) {
		return find(clazz, "select c from "+clazz.getName()+" c ");
	}

	@Override
	public <T> Optional<T> findOne(Class<T> clazz, String hql, Map<String, Object> args) {
		List<T> items = find(clazz, hql, args);
		if( items.isEmpty()){
			return Optional.empty(); 
		}
		return Optional.of(items.get(0));
	}
	
	@Override
	public <T extends AbstractEntity> Long count(Class<T> clazz, String hql, Map<String, Object> args) {
		TypedQuery<Long> query = em.createQuery(hql, Long.class).setFirstResult(0).setMaxResults(1);
		args.forEach(query::setParameter);
		return query.getSingleResult();
	}

}
