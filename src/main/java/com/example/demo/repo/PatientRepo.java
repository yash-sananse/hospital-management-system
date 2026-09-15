package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entite.User;

public interface PatientRepo extends JpaRepository<User, Long>{

	User findByEmail(String email);

}


