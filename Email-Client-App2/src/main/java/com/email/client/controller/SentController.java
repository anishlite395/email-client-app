package com.email.client.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.email.client.dto.SentMailDto;
import com.email.client.service.SentService;

@RestController
public class SentController {
	
	@Autowired
	private SentService sentService;

	@GetMapping("/email/sent")
	public List<SentMailDto> getAllMails(Authentication auth){
		String email = auth.getName();
		return sentService.getAllSentEmails(email);
	}
	
	@GetMapping("/email/sent/{uid}")
	public SentMailDto getSingleMailByUid(@PathVariable Long uid, Authentication auth) {
		String email = auth.getName();
		return sentService.getSingleMailByUid(email,uid);
	}
	
	@DeleteMapping("/email/sent/delete")
	public void deleteSentEmails(@RequestBody List<Long> uids,Authentication auth) {
		String email = auth.getName();
		sentService.deleteSentEmailsByUid(email, uids);
	}
}
