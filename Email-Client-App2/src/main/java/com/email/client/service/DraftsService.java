package com.email.client.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.email.client.dto.DraftRequest;
import com.email.client.entity.Drafts;
import com.email.client.entity.UserInfo;
import com.email.client.repository.DraftsRepository;
import com.email.client.repository.UserRepository;import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@Service
public class DraftsService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DraftsRepository draftsRepository;

	public Drafts saveDrafts(String email,DraftRequest draftRequest) {
		
		UserInfo userInfo = userRepository.findByEmail(email).get();
		Drafts drafts = new Drafts();
		drafts.setUser(userInfo);
		drafts.setRecipient(draftRequest.getRecipient());
		drafts.setSubject(draftRequest.getSubject());
		drafts.setBody(draftRequest.getBody());
		drafts.setUpdatedAt(LocalDateTime.now());
		return draftsRepository.save(drafts);
	}
	
	public List<Drafts> fetchADraftsforUser(String email){
		return draftsRepository.findByUserEmail(email);
	}

}
