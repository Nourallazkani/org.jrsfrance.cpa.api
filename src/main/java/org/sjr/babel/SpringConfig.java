package org.sjr.babel;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;

import org.sjr.babel.model.entity.AbstractEntity.CacheOnStartup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration 
@EnableWebMvc 
@EnableTransactionManagement
@PropertySource("classpath:conf.properties")
@ComponentScan
public class SpringConfig extends WebMvcConfigurerAdapter {
	
	public static class MailSettings{
		public String defaultEncoding;
		public String smtpHost;
		public int port;
		public String smtpProtocol;
		public String smtpUsername;
		public String smtpPassword;
		public boolean mock;
	}
	
	public SpringConfig() {
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		ObjectMapper jackson = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(jackson);
		converters.add(jsonConverter);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
			.addMapping("/**")
			.allowedHeaders("accessKey", "content-type")
			.allowedMethods("PUT", "POST", "GET", "DELETE")
			.allowedOrigins("*");
	}
	
	@Bean
	public EntityManagerFactory emf(){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		
		EntityManager em = emf.createEntityManager();
		
		Set<EntityType<?>> entities = emf.getMetamodel().getEntities();
		
		// fill level 2 cache so @manyToOne relationships can be eager fetched without additional sql select.
		entities.stream()
			.map(e -> e.getJavaType())
			.filter(c -> c.isAnnotationPresent(Cacheable.class) && c.isAnnotationPresent(CacheOnStartup.class))
			.sorted((c1, c2) -> c1.getAnnotation(CacheOnStartup.class).order() - c2.getAnnotation(CacheOnStartup.class).order())
			.forEach((x)-> em.createQuery("select o from "+x.getName()+" o").getResultList());
		
		em.close();	
		
		return emf;
	}
	
	@Bean
	public PlatformTransactionManager txManager(EntityManagerFactory emf){
		return new JpaTransactionManager(emf);
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
		javaMailsender.getJavaMailProperties().put("mock", "false");
		return javaMailsender;
	}

}
