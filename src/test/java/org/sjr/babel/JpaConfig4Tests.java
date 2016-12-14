package org.sjr.babel;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;

import org.sjr.babel.model.entity.AbstractEntity.CacheOnStartup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig4Tests {
	
	@Bean
	public EntityManagerFactory emf() throws Exception{
		EntityManagerFactory emf =  Persistence.createEntityManagerFactory("test");
		
		EntityManager em = emf.createEntityManager();
		
		Set<EntityType<?>> entities = emf.getMetamodel().getEntities();
		
		// fill level 2 cache so @manyToOne relationships can eager fetched without additional sql select.
		entities.stream()
			.map(e -> e.getJavaType())
			.filter(c -> c.isAnnotationPresent(Cacheable.class) && c.isAnnotationPresent(CacheOnStartup.class))
			.sorted((c1, c2) -> c1.getAnnotation(CacheOnStartup.class).order() - c2.getAnnotation(CacheOnStartup.class).order())
			.forEach((x)-> em.createQuery("select o from "+x.getName()+" o").getResultList());
		em.close();
		return emf;
	}
}
