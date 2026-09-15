package com.example.demo.entite;



import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "doctor_otp")
public class DoctorOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
   

    private String otp;

    private LocalDateTime expiryTime;

    private boolean verified;

    public DoctorOtp() {
    }

    public DoctorOtp(Long id, Long doctorId, String otp, LocalDateTime expiryTime, boolean verified) {
        this.id = id;
      //  this.doctorId = doctorId;
        this.otp = otp;
        this.expiryTime = expiryTime;
        this.verified = verified;
    }

    public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public Long getDoctorId() {
//        return doctorId;
//    }
//
//    public void setDoctorId(Long doctorId) {
//        this.doctorId = doctorId;
//    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

}