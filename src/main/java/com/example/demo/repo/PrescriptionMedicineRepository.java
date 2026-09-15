package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entite.PrescriptionMedicine;

public interface PrescriptionMedicineRepository extends JpaRepository<PrescriptionMedicine, Long> {

}
