package org.sjr.babel.persistence;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sjr.babel.WebAppInitializer.ApplicationConfig;
import org.sjr.babel.entity.Cursus;
import org.sjr.babel.persistence.impl.JpaCursusDaoImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class JpaCursusDaoImplTest {

	private JpaCursusDaoImpl dao ;
	
	public JpaCursusDaoImplTest() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfig.class);
		this.dao = ctx.getBean(JpaCursusDaoImpl.class);
	}
	
	@Test
	public void testFindCursusByCity() {
		List<Cursus> cs = dao.find("%%");
		for (Cursus c : cs) {
			System.out.println(c.getName());
		}
		Assert.assertEquals(2, cs.size());
	}

	@Test
	public void testFindCursusById() {
		Cursus c = dao.getById(1);
		System.out.println(c.getName());
		
	}

}
