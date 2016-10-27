package org.sjr.babel.persistence.impl;

import java.util.List;
import javax.persistence.TypedQuery;

import org.sjr.babel.model.entity.AbstractLearningProgram;
import org.sjr.babel.persistence.CursusDao;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCursusDaoImpl extends AbstractJpaDao<AbstractLearningProgram> implements CursusDao  {


	@Override	
	public List<AbstractLearningProgram> find(String city){	
		System.out.println(em);
		String hql = "select c from Cursus c where c.address.city like :name";
		TypedQuery<AbstractLearningProgram> query = em.createQuery(hql, AbstractLearningProgram.class);
		System.out.println(query.toString());
		query.setParameter("name", city);
		List<AbstractLearningProgram> results = query.getResultList();
		return results;
		
	}

	@Override
	Class<AbstractLearningProgram> getEntityClass() {
		return AbstractLearningProgram.class;
	}

	
	
	
	/*public static void main(String[] args) {
		Map<String, Object> map = new HashMap<>();
		map.put("n", "%a%");
		map.put("c", "Paris");
		
		for(String s: map.keySet()){
			System.out.println(s+ " : "+map.get(s));
		}
	}*/


	
}

