package com.email.client.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.angus.mail.imap.IMAPFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.email.client.dto.AttachmentDto;
import com.email.client.dto.SentMailDto;
import com.email.client.entity.UserImapConfig;
import com.email.client.entity.UserSmtpConfig;
import com.email.client.jwt.util.CryptoUtil;
import com.email.client.repository.UserImapConfigRepository;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.search.FromStringTerm;
import jakarta.mail.search.SearchTerm;

@Service
public class SentService {
	
	@Autowired
	private UserImapConfigRepository configRepo;
	
	@Autowired
	private CryptoUtil crypto;
	
	private Store connectImap(UserImapConfig cfg,String password) throws Exception {
			
			Properties props = new Properties();
			props.put("mail.store.protocol","imap");
			props.put("mail.imap.host",cfg.getImapHost());
			props.put("mail.imap.port",cfg.getImapPort());
			props.put("mail.imap.ssl.enable","false");
			
			Session session = Session.getInstance(props);
			Store store = session.getStore("imap");
			
			store.connect(
					cfg.getImapHost(),
					cfg.getUsername(),
					password);
		
		return store;
	}
	
	private IMAPFolder openSentFolder(Store store) throws MessagingException {
		IMAPFolder sent = (IMAPFolder) store.getFolder("Sent");
		
		if(!sent.exists()) {
			sent.create(Folder.HOLDS_MESSAGES);
		}
		
		sent.open(Folder.READ_ONLY);
		
		return sent;
	}
	
	public List<SentMailDto> getAllSentEmails(String email̥){
		List<SentMailDto> allSentEmails = new ArrayList<>();
		try {
			UserImapConfig cfg = configRepo.findByUser_Email(email̥).get();
			String password = crypto.decrypt(cfg.getEncryptedPassword());
			
			Store store = connectImap(cfg, password);
			IMAPFolder sentFolder = openSentFolder(store);
			
			SearchTerm fromMe = new FromStringTerm(cfg.getUsername());
			
			Message[] messages = sentFolder.search(fromMe);
			
			for(Message message:messages) {
				allSentEmails.add(convertToDto(message,sentFolder));
			}
			
			sentFolder.close(false);
			store.close();
		}catch(Exception e) {
			throw new RuntimeException("Failed to fetch sent emails",e);
		}
		return allSentEmails;
	}

	private SentMailDto convertToDto(Message message,IMAPFolder sentFolder) throws MessagingException, IOException {
		// TODO Auto-generated method stub
		SentMailDto dto = new SentMailDto();
		List<AttachmentDto> attachments = new ArrayList<>();
		
		dto.setUid(sentFolder.getUID(message));
		dto.setFrom(message.getFrom()[0].toString());
		dto.setTo(InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)));
		dto.setSubject(message.getSubject());
		dto.setSentDate(message.getSentDate());
		dto.setBody(extractTextAndAttachments(message,attachments));
		dto.setAttachments(attachments);
		return dto;
	}

	private String extractTextAndAttachments(Part part,
	        List<AttachmentDto> attachments)
	        throws MessagingException, IOException {

	    // TEXT BODY
	    if (part.isMimeType("text/plain") && part.getFileName() == null) {
	        return part.getContent().toString();
	    }

	    if (part.isMimeType("text/html") && part.getFileName() == null) {
	        return part.getContent().toString();
	    }

	    // MULTIPART
	    if (part.isMimeType("multipart/*")) {
	        Multipart multipart = (Multipart) part.getContent();
	        StringBuilder body = new StringBuilder();

	        for (int i = 0; i < multipart.getCount(); i++) {
	            Part bodyPart = multipart.getBodyPart(i);

	            // ATTACHMENT
	            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
	                    || Part.INLINE.equalsIgnoreCase(bodyPart.getDisposition())
	                    || bodyPart.getFileName() != null) {

	                AttachmentDto attachment = new AttachmentDto();
	                attachment.setFileName(bodyPart.getFileName());
	                attachment.setMimeType(bodyPart.getContentType());
	                attachment.setContent(bodyPart.getInputStream().readAllBytes());
	                attachments.add(attachment);
	            } else {
	                String text = extractTextAndAttachments(bodyPart, attachments);
	                if (text != null) {
	                    body.append(text).append("\n");
	                }
	            }
	        }
	        return body.toString();
	    }

	    return null;
	}

	public SentMailDto getSingleMailByUid(String email, Long uid) {
		// TODO Auto-generated method stub
		SentMailDto dto = new SentMailDto();
		try {
			UserImapConfig cfg = configRepo.findByUser_Email(email).get();
			String password = crypto.decrypt(cfg.getEncryptedPassword());
			
			Store store = connectImap(cfg, password);
			IMAPFolder sentFolder = openSentFolder(store);
			
			SearchTerm fromMe = new FromStringTerm(cfg.getUsername());
			
			Message message = sentFolder.getMessageByUID(uid);
			
			if(message == null) throw new RuntimeException("Email not found");

		    dto = convertToDto(message, sentFolder);
			
			sentFolder.close(false);
			store.close();
		}catch(Exception e) {
			throw new RuntimeException("Failed to fetch sent emails",e);
		}
		return dto;
	}

	public void deleteSentEmailsByUid(String email,List<Long> uids) {
		Store  store = null;
		IMAPFolder sentFolder = null;
		
		try {
			UserImapConfig config = configRepo.findByUser_Email(email).orElseThrow(
					() -> new RuntimeException("IMAP Config not found"));
			
			String password = crypto.decrypt(config.getEncryptedPassword());
			
			store = connectImap(config, password);
			
			sentFolder = (IMAPFolder) store.getFolder("Sent");
			
			if(!sentFolder.exists()) {
				throw new RuntimeException("Sent Folder Not Found");
			}
			
			sentFolder.open(Folder.READ_WRITE);
			
			for(Long uid: uids) {
				Message msg = sentFolder.getMessageByUID(uid);
				if(msg != null) {
					msg.setFlag(Flags.Flag.DELETED, true);
				}
			}
			sentFolder.expunge();
			
		}catch(Exception e) {
			throw new RuntimeException("Failed to delete sent emails", e);
		}finally {
			try {
				if(sentFolder != null) sentFolder.close(true);
				if(store != null) store.close();
			}catch(Exception ignored) {};
		}
	}
}
