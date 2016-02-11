package org.sjr.babel.persistence;

import java.util.List;
import java.util.Map;

import org.sjr.babel.entity.AbstractEntity;

public interface ObjectStore {

	<T extends AbstractEntity> T save(T entity);
	
	<T extends AbstractEntity> void delete(T entity);
	
	<T extends AbstractEntity> void delete(Class<T> clazz, int id);
	
	<T extends AbstractEntity> T getById(Class<T> clazz, int id);
	
	/**
	 * abcd
	 * @param hql xya
	 * @param args
	 * @param clazz
	 * @return
	 */
	<T extends AbstractEntity> List<T> find(String hql ,Map<String, Object> args, Class<T> clazz );

	<T extends AbstractEntity> List<T> find(String hql , Class<T> clazz );
	
}
