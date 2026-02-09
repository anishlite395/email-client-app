package com.email.client.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="user_smtp_config")
public class UserSmtpConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "user_id",nullable = false)
	private UserInfo user;
	
	@Column(name = "smtp_host")
	private String smtpHost;
	
	@Column(name = "smtp_port")
	private int smtpPort;
	
	@Column(name = "smtp_username")
	private String smtpUsername;
	
	@Column(name = "encrypted_password",nullable = false)
	private String encryptedPassword;
	
	@Column(name = "tls_enabled")
	private boolean tlsEnabled;
	
	@Column(name = "auth_enabled")
	private boolean authEnabled;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public boolean isTlsEnabled() {
		return tlsEnabled;
	}

	public void setTlsEnabled(boolean tlsEnabled) {
		this.tlsEnabled = tlsEnabled;
	}

	public boolean isAuthEnabled() {
		return authEnabled;
	}

	public void setAuthEnabled(boolean authEnabled) {
		this.authEnabled = authEnabled;
	}
	
	

}
