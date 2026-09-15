package com.example.demo.entite;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Prescription {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @OneToOne
	    private Appointment appointment;

		@ManyToOne
	    private Doctor doctor;

	    private String diagnosis;

	    @Column(length = 3000)
	    private String clinicalNotes;

	    @Column(length = 3000)
	    private String advice;

	    private LocalDate followUpDate;

	    private LocalDateTime createdAt;

	    @OneToMany(mappedBy = "prescription",
	               cascade = CascadeType.ALL)
	    private List<PrescriptionMedicine> medicines;
	    
	    
	    
	    public List<PrescriptionMedicine> getMedicines() {
			return medicines;
		}

		public void setMedicines(List<PrescriptionMedicine> medicines) {
			this.medicines = medicines;
		}
	    
	    
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Appointment getAppointment() {
			return appointment;
		}

		public void setAppointment(Appointment appointment) {
			this.appointment = appointment;
		}

		public Doctor getDoctor() {
			return doctor;
		}

		public void setDoctor(Doctor doctor) {
			this.doctor = doctor;
		}

		public String getDiagnosis() {
			return diagnosis;
		}

		public void setDiagnosis(String diagnosis) {
			this.diagnosis = diagnosis;
		}

		public String getClinicalNotes() {
			return clinicalNotes;
		}

		public void setClinicalNotes(String clinicalNotes) {
			this.clinicalNotes = clinicalNotes;
		}

		public String getAdvice() {
			return advice;
		}

		public void setAdvice(String advice) {
			this.advice = advice;
		}

		public LocalDate getFollowUpDate() {
			return followUpDate;
		}

		public void setFollowUpDate(LocalDate followUpDate) {
			this.followUpDate = followUpDate;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
	    
	    
}
