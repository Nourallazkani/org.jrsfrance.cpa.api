package org.sjr.babel.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.sjr.babel.entity.AbstractEntity;

public interface ObjectStore {

	<T extends AbstractEntity> T save(T entity);

	<T extends AbstractEntity> void delete(T entity);

	<T extends AbstractEntity> void delete(Class<T> clazz, int id);

	<T extends AbstractEntity> Optional<T> getById(Class<T> clazz, int id, Function<T, ?>... fetchs);
	
	<T extends AbstractEntity> Optional<T>	findOne(Class<T> clazz, String hql	, Map<String, Object> args);

	/**
	 * abcd
	 * 
	 * @param hql
	 * 			xya
	 * @param args
	 * @param clazz
	 * @return
	 */

	<T extends AbstractEntity> List<T> find(Class<T> clazz, String hql);

	/**
	 * 
	 * @param clazz
	 * @param hql
	 * @param paramValue
	 * @return
	 */
	<T extends AbstractEntity> List<T> find(Class<T> clazz, String hql, Object paramValue);

	<T extends AbstractEntity> List<T> find(Class<T> clazz, String hql, Map<String, Object> args);
	
	<T extends AbstractEntity> List<T> findAll (Class <T> clazz);

}
