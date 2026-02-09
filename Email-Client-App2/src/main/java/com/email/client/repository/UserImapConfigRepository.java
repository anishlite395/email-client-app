package com.email.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.email.client.entity.UserImapConfig;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserImapConfigRepository extends JpaRepository<UserImapConfig, Long>{

	List<UserImapConfig> findByEnabledTrue();
	
	Optional<UserImapConfig> findByUser_Email(String email);
}
