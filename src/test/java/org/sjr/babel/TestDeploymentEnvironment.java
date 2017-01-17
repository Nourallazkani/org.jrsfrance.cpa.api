package org.sjr.babel;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class TestDeploymentEnvironment {


	@Test 
	public void testDbSchema() throws Exception{

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		
		JsonNode datasource =  mapper.readTree(getClass().getResourceAsStream("/application.yaml")).get("spring").get("datasource");
		
		System.out.println(datasource);
		Map<String, String> hibernateProperties = new HashMap<>();
		hibernateProperties.put("hibernate.connection.driver_class", datasource.get("driver-class-name").asText());
		hibernateProperties.put("hibernate.connection.username", datasource.get("username").asText());
		hibernateProperties.put("hibernate.connection.password", datasource.get("password").asText());
		hibernateProperties.put("hibernate.connection.url", datasource.get("url").asText());
		
		
		StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().applySettings(hibernateProperties).build();
	    MetadataSources metadataSources = new MetadataSources( standardRegistry );

	    ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
		for (BeanDefinition bd : scanner.findCandidateComponents("org.sjr.babel.model")){
			metadataSources.addAnnotatedClassName(bd.getBeanClassName());
		}
		
		
		Metadata metadata = metadataSources.getMetadataBuilder().applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE ).build();
	    
	    SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
	    /*
	    Metamodel metamodel = sessionFactory.createEntityManager().getEntityManagerFactory().getMetamodel();
	    System.out.println(metamodel.getClass());
	    logger.info("schema ok ({} entities, {} embeddables)", metadata.getEntityBindings().size(), metamodel.getEmbeddables().size());
		*/
	    sessionFactory.close();


	}
}
