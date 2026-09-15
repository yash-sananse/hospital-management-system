package com.example.demo.entite;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PrescriptionMedicine {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "prescription_id")  // foreign key column
	    private Prescription prescription;

	    private String medicineName;

	    private String dosage;

	    private String frequency;

	    private String duration;

	    private String instruction;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Prescription getPrescription() {
			return prescription;
		}

		public void setPrescription(Prescription prescription) {
			this.prescription = prescription;
		}

		public String getMedicineName() {
			return medicineName;
		}

		public void setMedicineName(String medicineName) {
			this.medicineName = medicineName;
		}

		public String getDosage() {
			return dosage;
		}

		public void setDosage(String dosage) {
			this.dosage = dosage;
		}

		public String getFrequency() {
			return frequency;
		}

		public void setFrequency(String frequency) {
			this.frequency = frequency;
		}

		public String getDuration() {
			return duration;
		}

		public void setDuration(String duration) {
			this.duration = duration;
		}

		public String getInstruction() {
			return instruction;
		}

		public void setInstruction(String instruction) {
			this.instruction = instruction;
		}
	    
	    
	    
}
