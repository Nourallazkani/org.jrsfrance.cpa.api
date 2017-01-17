package org.sjr.babel;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TestDeploymentEnvironment {


	@Test
	public void testDbSchema() throws Exception{

		
		Map<String, String> dbConnectionPropertiesMapping = new HashMap<>();
		dbConnectionPropertiesMapping.put("javax.persistence.jdbc.driver", "hibernate.connection.driver_class");
		dbConnectionPropertiesMapping.put("javax.persistence.jdbc.user", "hibernate.connection.username");
		dbConnectionPropertiesMapping.put("javax.persistence.jdbc.password", "hibernate.connection.password");
		dbConnectionPropertiesMapping.put("javax.persistence.jdbc.url", "hibernate.connection.url");
		
		Map<String, String> hibernateProperties= new HashMap<>();
		hibernateProperties.put("hibernate.connection.driver_class", org.postgresql.Driver.class.getName());
		hibernateProperties.put("hibernate.hbm2ddl.auto", "validate");
		hibernateProperties.put("hibernate.dialect", PostgreSQL95Dialect.class.getName());
		
		DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document doc = b.parse(getClass().getResourceAsStream("/META-INF/persistence.xml"));
		
		
		Element persistenceXmlRootElement = doc.getDocumentElement();
		XPath xPath = XPathFactory.newInstance().newXPath();
		for(Map.Entry<String, String> entry : dbConnectionPropertiesMapping.entrySet()){
			String jpaPropertyName = entry.getKey();
			Node node = (Node)xPath.evaluate("//persistence-unit[@name='default']/properties/*[@name='"+jpaPropertyName+"']", persistenceXmlRootElement, XPathConstants.NODE);
			String jpaPropertyValue = node.getAttributes().item(1).getNodeValue();
			String hibernatePropertyName= entry.getValue();
			hibernateProperties.put(hibernatePropertyName, jpaPropertyValue);
		}

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
