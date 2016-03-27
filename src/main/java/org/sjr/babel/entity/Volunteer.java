package org.sjr.babel.entity;

import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.sjr.babel.entity.reference.Civility;
import org.sjr.babel.entity.reference.Language;

@Entity
public class Volunteer extends AbstractEntity {

	private String firstName;
	private String lastName;
	private Date birhtday;
	private String email;
	private String tel;
	private String password;
	private String comments;
	@ManyToMany(fetch=FetchType.LAZY)
	//@JoinTable(name="V_L",joinColumns=@JoinColumn(name="V_id"),inverseJoinColumns=@JoinColumn(name="L_id"))
	private List<Language> languages;
	@ManyToOne(fetch=FetchType.EAGER)
	private Civility civility;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirhtday() {
		return birhtday;
	}

	public void setBirhtday(Date birhtday) {
		this.birhtday = birhtday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	public Civility getCivility() {
		return civility;
	}

	public void setCivility(Civility civility) {
		this.civility = civility;
	}

}
