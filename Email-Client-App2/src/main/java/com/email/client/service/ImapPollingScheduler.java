package com.email.client.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.email.client.entity.UserImapConfig;
import com.email.client.repository.UserImapConfigRepository;

@Service
public class ImapPollingScheduler {

	private final UserImapConfigRepository imapRepository;
	
	private final ImapInboxService inboxService;

	@Autowired
	public ImapPollingScheduler(UserImapConfigRepository imapRepository, ImapInboxService inboxService) {
		this.imapRepository = imapRepository;
		this.inboxService = inboxService;
	}
	
	@Scheduled(fixedDelay = 10000)
	public void pollInboxes() {
		
		List<UserImapConfig> configs = imapRepository.findByEnabledTrue();
		
		for(UserImapConfig config: configs) {
			try {
				inboxService.readInboxByEmail(config.getUser().getEmail());
			}catch(RuntimeException e) {
				System.err.println("Polling failed for the user "+config.getUser().getId()+": "+e.getMessage());
			}
		}
		
		
	}
	
	
	
	
}
