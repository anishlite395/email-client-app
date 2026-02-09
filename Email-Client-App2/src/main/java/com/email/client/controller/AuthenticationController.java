package com.email.client.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.email.client.entity.AuthRequest;
import com.email.client.entity.UserInfo;
import com.email.client.service.JwtService;
import com.email.client.service.UserInfoService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	
	private UserInfoService userInfoService;
	
	private JwtService jwtService;
	
	private AuthenticationManager authenticationManager;
	
	
	@Autowired
	public AuthenticationController(UserInfoService userInfoService, JwtService jwtService,
			AuthenticationManager authenticationManager) {
		this.userInfoService = userInfoService;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
	}

	@GetMapping("/welcome")
	public String welcomePage() {
		return "Welcome to Email Management System";
	}
	
	@PostMapping("/addNewUser")
	public String addNewUser(@RequestBody UserInfo userInfo) {
		return userInfoService.addNewUser(userInfo);
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> authenticationToken(@RequestBody AuthRequest authRequest) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
		if(authentication.isAuthenticated()) {
			String token = jwtService.generateToken(authRequest.getEmail());
			return ResponseEntity.ok(Map.of("token",token));
		}else {
			throw new UsernameNotFoundException("Invalid User Request");
		}
	}
}
