package org.sjr.babel;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.Cacheable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

import org.sjr.babel.model.entity.AbstractEntity.CacheOnStartup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class MyApplication extends WebMvcConfigurerAdapter {
	

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyApplication.class, args);
	}

	private @Autowired EntityManagerFactory emf;
	
	
	@PostConstruct
	public void loadLevel2Cache(){
		Set<EntityType<?>> entities = emf.getMetamodel().getEntities();
		
		EntityManager em = emf.createEntityManager();
		
		// fill level 2 cache so @manyToOne relationships can be eager fetched without additional sql select.
		entities.stream()
			.map(e -> e.getJavaType())
			.filter(c -> c.isAnnotationPresent(Cacheable.class) && c.isAnnotationPresent(CacheOnStartup.class))
			.sorted((c1, c2) -> c1.getAnnotation(CacheOnStartup.class).order() - c2.getAnnotation(CacheOnStartup.class).order())
			.forEach((x)-> em.createQuery("select o from "+x.getName()+" o").getResultList());		
		
		em.close();
	}
}
