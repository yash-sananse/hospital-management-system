package com.example.demo.repo;

import java.time.LocalDate;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Doctor;
import com.example.demo.entite.User;

public interface DoctorRepository extends JpaRepository<Doctor, Long>{

	List<Doctor> findByActiveTrue();

	Page<Doctor> findAll(Pageable pageable);
	
	Doctor findByUser(User user);

	Doctor findByEmail(String email);
	
	List<Doctor> findBySpecialtyAndActiveTrue(String specialty);

	@Query("SELECT DISTINCT d.specialty FROM Doctor d WHERE d.active = true ORDER BY d.specialty")
	List<String> findAllDepartments();
	
	//Doctor Doc 
	
	
	
   
}
