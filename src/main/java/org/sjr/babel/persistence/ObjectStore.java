package org.sjr.babel.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.sjr.babel.model.entity.AbstractEntity;

public interface ObjectStore {

	<T extends AbstractEntity> T save(T entity);

	<T extends AbstractEntity> void delete(T entity);

	<T extends AbstractEntity> void delete(Class<T> clazz, int id);

	<T extends AbstractEntity> Optional<T> getById(Class<T> clazz, int id);
	
	<T> Optional<T>	findOne(Class<T> clazz, String hql	, Map<String, Object> args);
	
	<T extends AbstractEntity> Long count(Class<T> clazz, String hql, Map<String, Object> args);

	<T> List<T> find(Class<T> clazz, String hql);

	<T> List<T> find(Class<T> clazz, String hql, Map<String, Object> args);
	
	<T> List<T> findAll (Class <T> clazz);

}
