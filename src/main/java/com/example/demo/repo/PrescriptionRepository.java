package com.example.demo.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Prescription findByAppointment(Appointment appointment);

    List<Prescription> findByAppointmentPhoneNumberOrderByCreatedAtDesc(String phoneNumber);

    Page<Prescription> findByAppointmentPatientNameContainingIgnoreCaseOrAppointmentPhoneNumberContaining(
            String patientName, String phoneNumber, Pageable pageable);
}
