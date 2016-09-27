package org.sjr.babel;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.sjr.babel.entity.AbstractEntity.CacheOnStartup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

public class WebAppInitializer implements WebApplicationInitializer
{
	@Configuration 
	@EnableWebMvc 
	@EnableTransactionManagement
	@PropertySource("classpath:conf.properties")
	@ComponentScan
	public static class RestConfiguration extends WebMvcConfigurerAdapter{
		
		@Bean
		public EntityManagerFactory emf(){
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("abcd");
			return emf;
		}
		
		@Bean
		public PlatformTransactionManager txManager(EntityManagerFactory emf){
			return new JpaTransactionManager(emf);
		}

		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry
				.addMapping("/**")
				.allowedHeaders("accessKey", "content-type")
				.allowedMethods("PUT", "POST", "GET", "DELETE")
				.allowedOrigins("*");
			//allowedMethods("POST, PUT, DELETE, GET");//.allowedHeaders("XSRF-TOKEN");
		}
		
		/*
		@Override
		public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
			ObjectMapper jackson = jackson();
			jackson.registerModule(new Hibernate5Module());
			MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(jackson);
			converters.add(converter);
		}*/

	}
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(RestConfiguration.class);
		ctx.setServletContext(servletContext);
		
		ctx.addApplicationListener((ApplicationContextEvent event) -> {
			EntityManagerFactory emf = event.getApplicationContext().getBean(EntityManagerFactory.class);
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Set<EntityType<?>> entities = emf.getMetamodel().getEntities();
			
			/*
			for (EntityType<?> entityType : entities) {
				if (entityType.getJavaType().isAnnotationPresent(Cacheable.class) )
				{
					em.createQuery("select o from "+entityType.getJavaType().getName()+" o").getResultList();
				}
			}*/
			
			
			entities.stream()
				.map(e -> e.getJavaType())
				.filter(c -> c.isAnnotationPresent(Cacheable.class) && c.isAnnotationPresent(CacheOnStartup.class))
				.sorted((c1, c2) -> c1.getAnnotation(CacheOnStartup.class).order() - c2.getAnnotation(CacheOnStartup.class).order())
				.forEach((x)-> em.createQuery("select o from "+x.getName()+" o").getResultList());
			tx.commit();
			em.close();		
		});
		
		DispatcherServlet springServlet = new DispatcherServlet(ctx);
		Dynamic d = servletContext.addServlet("springServlet", springServlet);
		d.setAsyncSupported(true);
		d.addMapping("/");
		d.setLoadOnStartup(0);		
	}
}