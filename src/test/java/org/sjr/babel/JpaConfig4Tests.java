package org.sjr.babel;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig4Tests {
	
	@Bean
	public EntityManagerFactory emf() throws Exception{
		EntityManagerFactory emf =  Persistence.createEntityManagerFactory("test");
		/*
		ObjectMapper jackson = new ObjectMapper();
		
		JsonNode json = jackson.readTree(JpaConfig4Tests.class.getResourceAsStream("/data4tests.json"));
		
		Iterator<Entry<String, JsonNode>> fields = json.fields();
		
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		while(fields.hasNext()){
			Entry<String, JsonNode> entry = fields.next();
			String nodeName = entry.getKey();
			Class<?> clazz = Class.forName("[L"+nodeName+";");
			Object[] elements = (Object[]) jackson.treeToValue(entry.getValue(), clazz);
			for (Object object : elements) {
				em.persist(object);
			}
		}
		em.getTransaction().commit();
		em.close();
		*/
		return emf;
	}
}
