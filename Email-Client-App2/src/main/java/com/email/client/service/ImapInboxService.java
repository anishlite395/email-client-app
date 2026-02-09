package com.email.client.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.email.client.entity.EmailRequestDto;
import com.email.client.entity.UserImapConfig;
import com.email.client.entity.UserSmtpConfig;
import com.email.client.jwt.util.CryptoUtil;
import com.email.client.repository.UserImapConfigRepository;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.UIDFolder;
import jakarta.mail.Flags.Flag;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class ImapInboxService {
	
	private UserImapConfigRepository configRepo;
	
	private CryptoUtil cryptoUtil;
	
	@Autowired
	public ImapInboxService(UserImapConfigRepository configRepo,CryptoUtil cryptoUtil) {
		this.configRepo = configRepo;
		this.cryptoUtil = cryptoUtil;
	}


	public List<EmailRequestDto> readInboxByEmail(String email) {
		// TODO Auto-generated method stub
		UserImapConfig config = configRepo.findByUser_Email(email)
								.orElseThrow(() -> new RuntimeException("IMAP config not found"));
		
		if(!config.isEnabled()) return List.of();
		
		List<EmailRequestDto> emails = new ArrayList<>();
		
		Store store = null;
		Folder folder = null;
		
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imap");
			props.put("mail.imap.host", config.getImapHost());
			props.put("mail.imap.port", config.getImapPort());
			props.put("mail.imap.connectiontimeout", "5000");
			props.put("mail.imap.timeout", "5000");
			
			if(config.isUseSsl()) {
				props.put("mail.imap.ssl.enabled", "true");
			}
			
			Session session = Session.getInstance(props);
			store = session.getStore("imap");
			
			store.connect(config.getImapHost(), config.getUsername(), cryptoUtil.decrypt(config.getEncryptedPassword()));
			
			folder = store.getFolder(config.getFolder());
			folder.open(Folder.READ_WRITE);
			
			UIDFolder uidFolder = (UIDFolder) folder;
			
			
			Message[] messages = folder.getMessages();
			
			
			for(Message message: messages) {
				long uid = uidFolder.getUID(message);
				boolean isRead = message.isSet(Flags.Flag.SEEN);
				String body = extractText(message);
				
				String fromUsername = null;
				Address[] fromAddresses = message.getFrom();
				
				if(fromAddresses != null && fromAddresses.length > 0) {
					InternetAddress ia = (InternetAddress) fromAddresses[0];
					String fullEmail = ia.getAddress();
					if(fullEmail != null && fullEmail.contains("@")) {
						fromUsername = fullEmail.substring(0, fullEmail.indexOf("@"));
					}
				}
				
				EmailRequestDto new_email = new EmailRequestDto(
						uid,
						fromUsername,
						message.getSubject(),
						message.getSentDate(),
						isRead,body);
				
				emails.add(new_email);
				
				
				
			}
			
			return emails;
			
		}catch(Exception e) {
			throw new RuntimeException("IMAP read failed");
		}
		finally {
			try {
				if(folder != null && folder.isOpen()) folder.close(false);
				if(store != null && store.isConnected())store.close();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
			}
			
		}
	}
	
	public EmailRequestDto readSingleEmailByEmail(Long uid,String email) {
		
		UserImapConfig config = configRepo.findByUser_Email(email)
				.orElseThrow(() -> new RuntimeException("IMAP config not found"));


		Store store = null;
		Folder folder = null;

		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imap");
			props.put("mail.imap.host", config.getImapHost());
			props.put("mail.imap.port", config.getImapPort());
			props.put("mail.imap.connectiontimeout", "5000");
			props.put("mail.imap.timeout", "5000");

			if(config.isUseSsl()) {
				props.put("mail.imap.ssl.enabled", "true");
			}

			Session session = Session.getInstance(props);
			store = session.getStore("imap");

			store.connect(config.getImapHost(), config.getUsername(), cryptoUtil.decrypt(config.getEncryptedPassword()));

			folder = store.getFolder(config.getFolder());
			folder.open(Folder.READ_WRITE);

			UIDFolder uidFolder = (UIDFolder) folder;


			Message message = uidFolder.getMessageByUID(uid);

			if(message == null) {
				throw new RuntimeException("Failed To Fetch the Email");
			}
			
			String body = extractText(message);
			
				boolean isRead = message.isSet(Flag.SEEN);
				EmailRequestDto new_email = new EmailRequestDto(
						uid,
						message.getFrom()[0].toString(),
						message.getSubject(),
						message.getSentDate(),
						isRead,
						body);
			

			return new_email;

		}catch(Exception e) {
			throw new RuntimeException("IMAP read failed");
		}
		finally {
			try {
				if(folder != null && folder.isOpen()) folder.close(false);
				if(store != null && store.isConnected())store.close();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
			}

		}
	}


	public void updateReadStatus(String email, Long uid, boolean read) {
		// TODO Auto-generated method stub
		UserImapConfig config = configRepo.findByUser_Email(email)
				                .orElseThrow(() -> new RuntimeException("IMAP Config not found"));
	
		Store store = null;
		Folder folder = null;
		
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imap");
			props.put("mail.imap.host", config.getImapHost());
			props.put("mail.imap.port", config.getImapPort());
			props.put("mail.imap.connectiontimeout", "5000");
			props.put("mail.imap.timeout", "5000");
			
			if(config.isUseSsl()) {
				props.put("mail.imap.ssl.enabled", "true");
			}
			
			Session session = Session.getInstance(props);
			store = session.getStore("imap");
			
			store.connect(config.getImapHost(), config.getUsername(), cryptoUtil.decrypt(config.getEncryptedPassword()));
		
			folder = store.getFolder(config.getFolder());
			folder.open(Folder.READ_WRITE);
			
			UIDFolder uidFolder = (UIDFolder) folder;
			Message message = uidFolder.getMessageByUID(uid);
			
			if(message == null) {
				throw new RuntimeException("Email not found for UID: "+ uid);
			}
			
			message.setFlag(Flags.Flag.SEEN, read);
		
		}catch(Exception e) {
			throw new RuntimeException("Failed to update read status", e);
		} finally {
			try {
				if(folder != null && folder.isOpen()) folder.close();
				if(store != null && store.isConnected()) store.close();
			} catch(MessagingException e) {}
		}
	}
	
	public String extractText(Part part) throws IOException, MessagingException {
		
		if(part.isMimeType("text/plain")) {
			return part.getContent().toString();
		}
		
		if(part.isMimeType("text/html")) {
			return part.getContent().toString();
		}
		
		if(part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			for(int i=0;i < multipart.getCount();i++) {
				String text = extractText(multipart.getBodyPart(i));
				if(text != null) return text;
			}
		}
		
		return null;
	}

	public void saveToSentFolder(MimeMessage message,
			UserImapConfig cfg,
			String decryptedPassword) throws MessagingException {

		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imap");
			props.put("mail.imap.host", cfg.getImapHost());
			props.put("mail.imap.port", cfg.getImapPort());
			props.put("mail.imap.ssl.enable", "false");
			
			Session session = Session.getInstance(props);
			Store store = session.getStore("imap");
			
			store.connect(cfg.getImapHost(), cfg.getUsername(), decryptedPassword);
			Folder sent = store.getFolder("Sent");
			
			if(!sent.exists()) {
				sent.create(Folder.HOLDS_MESSAGES);
			}
			
			sent.open(Folder.READ_WRITE);
			sent.appendMessages(new Message[] {message});
			sent.close(false);
			store.close();
			System.out.println("Email Saved Successfully in SENT");
		
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	
	public void deleteEmailsByUid(String email,List<Long> uids) {
		
		Store store = null;
		Folder inbox = null;
		
		try {
			UserImapConfig config = configRepo.findByUser_Email(email).orElseThrow(
					() -> new RuntimeException("IMAP Config not found"));
			
			String password = cryptoUtil.decrypt(config.getEncryptedPassword());
			
			Properties props = new Properties();
			props.put("mail.store.protocol","imap");
			
			Session session = Session.getInstance(props);
			store = session.getStore("imap");
			
			store.connect(
					config.getImapHost(),
					config.getImapPort(),
					email,password);
			
			inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);
			
			UIDFolder uidFolder = (UIDFolder)inbox;
			
			for(Long uid: uids) {
				Message msg = uidFolder.getMessageByUID(uid);
				if(msg != null) {
					msg.setFlag(Flags.Flag.DELETED, true);
				}
			}
			
			inbox.expunge();
		}catch(Exception e) {
			throw new RuntimeException("Failed to Delete emails",e);
		}finally {
			try {
				if(inbox != null) inbox.close(true);
				if(store != null) store.close();
			}catch(Exception ignored) {}
		}
		
		
	}
	
}
