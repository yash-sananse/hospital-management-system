package com.example.demo.service;

import com.example.demo.entite.Prescription;

public interface PrescriptionService {

	Prescription getLatestPrescription(long id);
}
