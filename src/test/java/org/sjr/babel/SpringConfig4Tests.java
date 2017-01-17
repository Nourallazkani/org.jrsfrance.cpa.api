package org.sjr.babel;

import java.util.Properties;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;

import org.sjr.babel.SpringConfig.MailSettings;
import org.sjr.babel.model.entity.AbstractEntity.CacheOnStartup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class SpringConfig4Tests {
	
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
	
	@Bean
	public JavaMailSenderImpl javaMailSender(Environment env){
		JavaMailSenderImpl javaMailsender = new JavaMailSenderImpl();
		javaMailsender.setHost(env.getProperty("mail.smtp.host"));
		javaMailsender.setProtocol(env.getProperty("mail.smtp.protocol"));
		javaMailsender.setDefaultEncoding("UTF-8");
		javaMailsender.setPort(env.getProperty("mail.smtp.port", Integer.class));
		javaMailsender.setUsername(env.getProperty("mail.smtp.username"));
		javaMailsender.setPassword(env.getProperty("mail.smtp.password"));
		javaMailsender.setJavaMailProperties(new Properties());
		javaMailsender.getJavaMailProperties().put("mock", "true");
		return javaMailsender;
	}
}
