package com.email.client.config;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import com.email.client.entity.UserSmtpConfig;

@Component
public class DynamicMailSenderFactory {
	
	private final Map<String,JavaMailSender> cache = new ConcurrentHashMap<>();

	public JavaMailSender get(UserSmtpConfig config,String password) {
		
		return cache.computeIfAbsent(config.getSmtpUsername(), k -> {
			JavaMailSenderImpl sender = new JavaMailSenderImpl();
			sender.setHost(config.getSmtpHost());
			sender.setPort(config.getSmtpPort());
			sender.setUsername(config.getSmtpUsername());
			sender.setPassword(password);
			
			Properties props = sender.getJavaMailProperties();
			props.put("mail.smtp.auth", config.isAuthEnabled());
			props.put("mail.smtp.starttls.enabled", config.isTlsEnabled());
			props.put("mail.transport.protocol", "smtp");
			
			return sender;
		});
		
		
	}
}
