package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entite.Prescription;
import com.example.demo.repo.PrescriptionRepository;

@Service
public class PrescriptionServiceImp implements PrescriptionService {

	@Autowired
	private PrescriptionRepository prescriptionRepository;
	
	
	@Override
	public Prescription getLatestPrescription(long id) {
		
		return prescriptionRepository.findById(id).orElse(null) ;
	}


	public long countPrescriptions(String phuumber) {
		
		
		return prescriptionRepository.findByAppointmentPhoneNumberOrderByCreatedAtDesc(phuumber).size();
	}

}