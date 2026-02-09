package com.email.client.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.email.client.entity.UserSmtpConfig;

public interface UserSmtpConfigRepository extends JpaRepository<UserSmtpConfig, Long> {

	@Query(
		    value = "SELECT u.id, u.user_id, u.smtp_host, u.smtp_port, u.smtp_username, u.tls_enabled, u.auth_enabled, u.encrypted_password " +
		            "FROM user_smtp_config u " +
		            "JOIN user_info ui ON u.user_id = ui.id " +
		            "WHERE ui.email = :username",
		    nativeQuery = true
		)
		Optional<UserSmtpConfig> findByUsernameNative(@Param("username") String username);
}
