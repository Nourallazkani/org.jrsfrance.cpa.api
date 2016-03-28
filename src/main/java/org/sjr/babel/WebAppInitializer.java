package org.sjr.babel;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.sjr.babel.entity.AbstractEntity.CacheOnStartup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

public class WebAppInitializer implements WebApplicationInitializer
{
	@Configuration 
	@ComponentScan(basePackages="org.sjr.babel.persistence") 
	@EnableTransactionManagement
	public static class ApplicationConfig{
		
		@Bean
		public EntityManagerFactory xyz(){
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("abcd");
			EntityManager em = emf.createEntityManager();
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
			
			
			//Arrays.asList("Country", "OrganisationCategory", "Civility","Language","Organisation","FieldOfStudy","Level").forEach(x->em.createQuery("select o from "+x+" o").getResultList());
			//em.createQuery("select c from Country c").getResultList();
			//em.createQuery("select o from Organisation o").getResultList();
			
			em.close();
			return emf;
		}
		
		@Bean
		public PlatformTransactionManager txManager(EntityManagerFactory emf){
			return new JpaTransactionManager(emf);
		}
		
	}
	
	@Configuration 
	@EnableWebMvc 
	@Import(ApplicationConfig.class)
	@ComponentScan(basePackages="org.sjr.babel.web.endpoint")
	public static class RestConfiguration extends WebMvcConfigurerAdapter{
		@Override
		public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
			configurer.enable();
		}

	}
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(RestConfiguration.class);
		ctx.setServletContext(servletContext);
		
		DispatcherServlet springServlet = new DispatcherServlet(ctx);
		
		Dynamic d = servletContext.addServlet("springServlet", springServlet);
		d.addMapping("/");
		d.setLoadOnStartup(0);		
	}

}