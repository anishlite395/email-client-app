package com.email.client.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.email.client.entity.UserInfo;

public class UserInfoDetails implements UserDetails {
	
	private String username;
	
	private String password;
	
	private List<GrantedAuthority> authorities;
	
	

	public UserInfoDetails(UserInfo user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.authorities = List.of(user.getRoles().split(","))
								.stream()
								.map(SimpleGrantedAuthority::new)
								.collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return username;
	}

}
