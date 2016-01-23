
package org.sjr.babel.persistence.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import org.sjr.babel.entity.Address;
import org.sjr.babel.entity.Country;
import org.sjr.babel.entity.Cursus;
import org.sjr.babel.entity.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class HibernateOrganisationDao {

	@PersistenceContext(type=PersistenceContextType.TRANSACTION)
	private EntityManager em;
	
	public HibernateOrganisationDao() {
		System.out.println("inside ctor");
	}

	public List<Organisation> findOrganisationsByName(String name){
		String hql = "select o from Organisation o where o.address.country.name like :name";
		TypedQuery<Organisation> query = em.createQuery(hql, Organisation.class);
		query.setParameter("name", name);
		List<Organisation> org = query.getResultList();
		return org;
	} 
	
	
	public List<Organisation> findCursusById(Integer id) {
		String hql = "select o from Organisation o where o.id like :id";
		TypedQuery<Organisation> list = em.createQuery(hql, Organisation.class);
		list.setParameter("id", id);
		List<Organisation> o = list.getResultList();
		return o;
		}

	
	public void addOrganisation (Organisation o){
		em.persist(o);
		em.close();
	}
	
	

}
