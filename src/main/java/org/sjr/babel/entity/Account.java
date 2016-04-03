package org.sjr.babel.entity;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;

import org.apache.commons.codec.digest.DigestUtils;import com.fasterxml.jackson.databind.ser.impl.FailingSerializer;

@Embeddable
public class Account {

	private String password;
	
	@Column(updatable=false)
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

	@PrePersist
	public void beforeSave() {
		if (this.accessKey == null || this.accessKey.equals("")) {
			setAccessKey(UUID.randomUUID().toString());
		}
	}
}
