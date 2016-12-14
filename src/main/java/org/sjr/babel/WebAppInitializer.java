package org.sjr.babel;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.Cacheable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.sjr.babel.model.entity.AbstractEntity.CacheOnStartup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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
		
		public RestConfiguration() {
			System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
			System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		}
		
		@Bean
		public EntityManagerFactory emf(){
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
			
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
		}
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		   
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(RestConfiguration.class);
		ctx.setServletContext(servletContext);
		
		Dynamic d = servletContext.addServlet("springServlet", new DispatcherServlet(ctx));
		d.setAsyncSupported(true);
		d.addMapping("/");
		d.setLoadOnStartup(0);		
	}
}