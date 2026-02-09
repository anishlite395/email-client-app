package com.email.client.entity;

import org.springframework.web.multipart.MultipartFile;

public class MailRequest {
	
	private UserSmtpConfig smtpConfig;
	private String to;
	private String subject;
	private String body;
	private boolean isHtml;
	private MultipartFile[] attachments;
	
	public UserSmtpConfig getSmtpConfig() {
		return smtpConfig;
	}
	
	public void setSmtpConfig(UserSmtpConfig smtpConfig) {
		this.smtpConfig = smtpConfig;
	}
	
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	

	public MultipartFile[] getAttachments() {
		return attachments;
	}

	public void setAttachments(MultipartFile[] attachments) {
		this.attachments = attachments;
	}

	public boolean isHtml() {
		return isHtml;
	}

	public void setHtml(boolean isHtml) {
		this.isHtml = isHtml;
	}
	
	
	
	
}
