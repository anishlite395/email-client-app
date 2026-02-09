package com.email.client.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.email.client.entity.UserImapConfig;
import com.email.client.entity.UserInfo;
import com.email.client.entity.UserSmtpConfig;
import com.email.client.jwt.util.CryptoUtil;
import com.email.client.repository.UserImapConfigRepository;
import com.email.client.repository.UserRepository;
import com.email.client.repository.UserSmtpConfigRepository;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

@Service
public class UserInfoService implements UserDetailsService {
	
	private UserRepository userRepository;
	
	private PasswordEncoder passwordEncoder;
	
	@Value("${hmail.admin.username}")
	private String hmailAdminUsername;
	
	@Value("${hmail.admin.password}")
	private String hmailAdminPassword;
	
	private CryptoUtil cryptoUtil;
	
	private UserSmtpConfigRepository smtpConfigRepository;
	
	private UserImapConfigRepository imapConfigRepository;
	
	
	@Autowired
	public UserInfoService(UserRepository userRepository, PasswordEncoder passwordEncoder, CryptoUtil cryptoUtil,
			UserSmtpConfigRepository smtpConfigRepository,UserImapConfigRepository imapConfigRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.cryptoUtil = cryptoUtil;
		this.smtpConfigRepository = smtpConfigRepository;
		this.imapConfigRepository = imapConfigRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Optional<UserInfo> userInfo = userRepository.findByEmail(username);
		if(userInfo.isEmpty()) {
			throw new UsernameNotFoundException("Cannot find user: "+username);
		}
		UserInfo user = userInfo.get();
		return new UserInfoDetails(user);
	}
	
	public String addNewUser(UserInfo userInfo) {
		userInfo.setUsername(userInfo.getUsername());
		String rawPassword = userInfo.getPassword();
		userInfo.setPassword(passwordEncoder.encode(rawPassword));
		userInfo.setRoles("ROLE_USER");
		userRepository.save(userInfo);
		saveUserInHmail(userInfo,rawPassword);
		saveUserSmtpConfig(userInfo,rawPassword);
		saveUserImapConfig(userInfo, rawPassword);
		return "User Added Successfully";
	}



	public void saveUserSmtpConfig(UserInfo userInfo, String rawPassword) {
		// TODO Auto-generated method stub
		UserSmtpConfig smtp = new UserSmtpConfig();
		smtp.setUser(userInfo);
		smtp.setSmtpHost("localhost");
		smtp.setSmtpPort(25);
		smtp.setSmtpUsername(userInfo.getEmail());
		smtp.setEncryptedPassword(cryptoUtil.encrypt(rawPassword));
		smtp.setAuthEnabled(true);
		smtp.setTlsEnabled(false);
		smtpConfigRepository.save(smtp);
	}
	
	public void saveUserImapConfig(UserInfo userInfo,String rawPassword) {
		UserImapConfig imap = new UserImapConfig();
		imap.setUser(userInfo);
		imap.setImapHost("localhost");
		imap.setImapPort(143);
		imap.setUsername(userInfo.getEmail());
		imap.setEncryptedPassword(cryptoUtil.encrypt(rawPassword));
		imap.setEnabled(true);
		imap.setUseSsl(false);
		imapConfigRepository.save(imap);
	}



	public void saveUserInHmail(UserInfo userInfo,String rawPassword) {
		// TODO Auto-generated method stub
		String email = userInfo.getEmail();
		String domainName = email.substring(email.indexOf("@")+1);
		
		ActiveXComponent hMailAppServer = null;
		
		try {
			hMailAppServer = new ActiveXComponent("hMailServer.Application");
			Dispatch appDispatch = hMailAppServer.getObject();
			
			Dispatch.call(appDispatch,"Authenticate",new Variant(hmailAdminUsername),new Variant(hmailAdminPassword));
		
			Dispatch domains = Dispatch.get(appDispatch,"Domains").toDispatch();
			
			Dispatch domain = Dispatch.call(domains,"ItemByName",domainName).toDispatch();
			
			Dispatch accounts = Dispatch.get(domain,"Accounts").toDispatch();
			
			Dispatch account = Dispatch.call(accounts,"Add").toDispatch();
			
			Dispatch.put(account,"Address",email);
			Dispatch.put(account,"Password",rawPassword);
			Dispatch.put(account,"Active",true);
			
			Dispatch.call(account, "Save");
			
			
		}catch(Exception e) {
			throw new RuntimeException("Error creating hMailServer Account");
			
		}finally {
			if(hMailAppServer != null) {
				hMailAppServer.safeRelease();
			}
		}
		
		
	}

}
