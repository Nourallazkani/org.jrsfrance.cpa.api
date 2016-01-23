package org.sjr.babel;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

public class WebAppInitializer implements WebApplicationInitializer
{
	@Configuration @ComponentScan(basePackages="org.sjr.babel.persistence") @EnableTransactionManagement
	public static class ApplicationConfig{
		@Bean
		public EntityManagerFactory xyz(){
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("abcd");
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
/*
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Configuration @EnableWebMvc @ComponentScan
	public static class RestConfiguration extends WebMvcConfigurerAdapter{
		
		@Bean
		public EntityManagerFactory xyz(){
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("abcd");
			return emf;
		}
		
		
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[]{RestConfiguration.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}
}
*/