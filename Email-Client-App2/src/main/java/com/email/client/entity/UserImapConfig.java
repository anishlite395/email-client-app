package com.email.client.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserImapConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private UserInfo user;
	
	private String imapHost;
	
	private int imapPort;
	
	private boolean useSsl;
	
	private String username;
	
	private String encryptedPassword;
	
	private String folder = "INBOX";
	
	private boolean enabled = true;
	
	private Long lastProcessedUid;
	
	
	private LocalDateTime createdAt = LocalDateTime.now();
	private LocalDateTime updatedAt = LocalDateTime.now();
	
	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}

}
