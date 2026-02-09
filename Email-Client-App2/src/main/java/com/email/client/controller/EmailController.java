package com.email.client.controller;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.email.client.dto.DraftRequest;
import com.email.client.entity.Drafts;
import com.email.client.entity.Email;
import com.email.client.entity.EmailRequestDto;
import com.email.client.entity.MailRequest;
import com.email.client.service.DraftsService;
import com.email.client.service.EmailService;
import com.email.client.service.ImapInboxService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/email")
public class EmailController {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private ImapInboxService imapInboxService;
	
	@Autowired
	private DraftsService draftsService;
	
	@PostMapping("/send/multipart")
	public String send(@ModelAttribute MailRequest mailRequest) throws MessagingException {
		emailService.sendEmail(
				mailRequest.getTo(),
				mailRequest.getSubject(),
				mailRequest.getBody(),
				mailRequest.getAttachments(),
				mailRequest.isHtml());
		
		return "Email Sent";
	}
	
	@PostMapping("/send")
	public String sendJson(@RequestBody MailRequest mailRequest) {
		emailService.sendSimpleEmail(mailRequest.getTo(), mailRequest.getSubject(), mailRequest.getBody());
		return "Email Sent Successfully";
	}
	
	@GetMapping("/inbox")
	public List<EmailRequestDto> getEmails(Authentication auth){
		String email = auth.getName();
		List<EmailRequestDto> emails =  imapInboxService.readInboxByEmail(email);
		System.out.println("All Emails: "+emails.size());
		return emails;
	}
	
	@GetMapping("/inbox/{uid}")
	public ResponseEntity<EmailRequestDto> getSingleEmail(@PathVariable Long uid,Authentication auth) {
		String email = auth.getName();
		EmailRequestDto dto = imapInboxService.readSingleEmailByEmail(uid,email);
		return ResponseEntity.ok(dto);
	}
	
	@PutMapping("/inbox/{userId}/{uid}/read")
	public ResponseEntity<Void> markAsRead(
			@PathVariable String email,
			@PathVariable long uid,
			@RequestParam boolean read){
		
		imapInboxService.updateReadStatus(email, uid, read);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/saveDrafts")
	public Drafts saveDraft(@RequestBody DraftRequest draftRequest,
			                Authentication authentication) {
		String email = authentication.getName();
		return draftsService.saveDrafts(email, draftRequest);
	}
	
	@GetMapping("/drafts")
	public List<Drafts> getAllDrafts(Authentication authentication){
		String email = authentication.getName();
		return draftsService.fetchADraftsforUser(email);
	}
	
	@DeleteMapping("/inbox/delete")
	public ResponseEntity<Void> deleteEmails(
			@RequestBody List<Long> uids,Authentication authentication){
		
		String email = authentication.getName();
		imapInboxService.deleteEmailsByUid(email,uids);
		return ResponseEntity.ok().build();
	}
}
