package org.sjr.babel.entity;

import javax.persistence.Embeddable;

@Embeddable
public class Account {

	private String password;
	private String accessKey;
	private String role;


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
