package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entite.DoctorOtp;

public interface DoctorOtpRepository extends JpaRepository<DoctorOtp, Long>  {

	DoctorOtp findTopByDoctorEmailOrderByIdDesc(String email);
	DoctorOtp findTopByDoctorIdOrderByIdDesc(Long doctorId);

}
