package com.email.client.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.email.client.entity.UserInfo;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
	
	Optional<UserInfo> findByEmail(String username);

}
