package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.entite.User;
import com.example.demo.enums.UserRole;

import java.util.List;

public interface Userrepo extends JpaRepository<User, Integer>{

	User findByEmail(String email);
	
	Page<User> findByNameContainingOrEmailContaining(
	        String name,
	        
	        String email, Pageable pageable);
	
	 

	 User findByName(String name);

	
	List<User> findByRole(UserRole role);
	List<User> findTop5ByOrderByCreatedAtDesc();

			
	
}
