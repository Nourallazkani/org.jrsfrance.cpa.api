package org.sjr.babel;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

public class TestDeploymentEnvironment {

	@Test
	public void testDbSchema(){
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");		
		EntityManagerFactory emf =  Persistence.createEntityManagerFactory("default");
		emf.close();
	}
}
