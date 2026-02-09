package com.email.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.email.client.entity.UserSmtpConfig;
import com.email.client.repository.UserSmtpConfigRepository;

@Component
public class CurrentUserSmtpResolver {
	
	@Autowired
	private UserSmtpConfigRepository smtpRepo;
	
	public UserSmtpConfig resolve() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String username = auth.getName();
		
		return smtpRepo.findByUsernameNative(username).orElseThrow(
				() -> new RuntimeException("SMTP config not found for user"));
	}

}
