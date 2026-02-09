package com.email.client.service;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.email.client.config.CurrentUserSmtpResolver;
import com.email.client.config.DynamicMailSenderFactory;
import com.email.client.entity.UserImapConfig;
//import com.email.client.entity.SmtpConfig;
import com.email.client.entity.UserSmtpConfig;
import com.email.client.jwt.util.CryptoUtil;
import com.email.client.repository.UserImapConfigRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private final DynamicMailSenderFactory mailSenderFactory;
	
	@Autowired
	private CurrentUserSmtpResolver resolver;
	
	@Autowired
	private CryptoUtil crypto;
	
	@Autowired
	private ImapInboxService inboxService;
	
	@Autowired
	private UserImapConfigRepository configRepo;

	public EmailService(DynamicMailSenderFactory mailSenderFactory) {
		this.mailSenderFactory = mailSenderFactory;
	}
	
	/**
	 * 
	 * 
	 * @param smtpConfig -  The Smtp Config from DB for the user
	 * @param to - The Recipients Email Address
	 * @param subject - Email Subject
	 * @param content - Email Content
	 * @throws MessagingException 
	 */
	
	public void sendEmail(String to,String subject,
			              String content,MultipartFile[] attachments,boolean isHtml) throws MessagingException {
		
		UserSmtpConfig cfg = resolver.resolve();
		String password = crypto.decrypt(cfg.getEncryptedPassword());
		
		JavaMailSender mailSender = mailSenderFactory.get(cfg,password);
		UserImapConfig config = configRepo.findByUser_Email(cfg.getSmtpUsername())
				.orElseThrow(() -> new RuntimeException("IMAP config not found"));
		
		//Prepare MimeMessageHelper for Multipart email.
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message,true);
		helper.setFrom(cfg.getSmtpUsername());
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(content,isHtml);
		
		//Add Attachments
		if(attachments != null) {
			for(MultipartFile file:attachments) {
				if(!file.isEmpty()) {
					helper.addAttachment(file.getOriginalFilename(), file);
				}
			}
		}

		mailSender.send(message);
		inboxService.saveToSentFolder(message, config, password);
	}
	

	public void sendSimpleEmail(String to,String subject,String content) {
		
		try {
			UserSmtpConfig cfg = resolver.resolve();
			UserImapConfig config = configRepo.findByUser_Email(cfg.getSmtpUsername())
					.orElseThrow(() -> new RuntimeException("IMAP config not found"));
	        String password = crypto.decrypt(cfg.getEncryptedPassword());

	        JavaMailSender mailSender = mailSenderFactory.get(cfg, password);

	        // Create MIME message (plain text)
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

	        helper.setFrom(cfg.getSmtpUsername());
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(content, false); // false = plain text

	        // Send via SMTP
	        mailSender.send(message);
	        inboxService.saveToSentFolder(message, config, password);
			
		}catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to Send Email "+e);
		}
	}
	
}
