package com.example.demo.entite;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.demo.enums.AppointmentStatus;

import jakarta.persistence.*;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    
  

	@Column
    private String patientName;
   

	@Column
    private String reason;
    @Column
    private String phoneNumber;
    
    @Column
    private LocalDate appointmentDate;

	@Transient
	private boolean billGenerated;
    
    
    @Column
    private LocalTime appointmentTime;


	@ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
	
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    

    public AppointmentStatus getStatus() {
		return status;
	}

	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

    public LocalDate getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(LocalDate appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public LocalTime getAppointmentTime() {
		return appointmentTime;
	}

	public void setAppointmentTime(LocalTime appointmentTime) {
		this.appointmentTime = appointmentTime;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	} 
	
	public User getPatient() {
		return patient;
	}

	public void setPatient(User patient) {
		this.patient = patient;
	}


	public boolean isBillGenerated() {
		return billGenerated;
	}

	public void setBillGenerated(boolean billGenerated) {
		this.billGenerated = billGenerated;
	}
}