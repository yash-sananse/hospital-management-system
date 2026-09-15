package com.example.demo.repo;

import com.example.demo.entite.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {


    // Find all bills of a particular patient
    List<Bill> findByPatientId(Long patientId);

    // Find bill for a particular appointment
    Optional<Bill> findByAppointmentId(Long appointmentId);

    // Find all bills of a particular doctor
    List<Bill> findByDoctorId(Long doctorId);




}
