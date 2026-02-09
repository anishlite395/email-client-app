package com.email.client.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.email.client.entity.Drafts;

@Repository
public interface DraftsRepository extends JpaRepository<Drafts, Long> {

	List<Drafts> findByUserEmail(String email);
	
}
