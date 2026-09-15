package com.example.demo.entite;



import java.time.LocalDateTime;

import com.example.demo.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Patient
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Appointment for which this bill was generated
    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    // Charges
    @Column(nullable = false)
    private Double consultationFee = 0.0;

    @Column(nullable = false)
    private Double medicineCharge = 0.0;

    @Column(nullable = false)
    private Double labCharge = 0.0;

    @Column(nullable = false)
    private Double roomCharge = 0.0;

    @Column(nullable = false)
    private Double otherCharge = 0.0;

    @Column(nullable = false)
    private Double discount = 0.0;

    // Amounts
    @Column(nullable = false)
    private Double totalAmount = 0.0;

    @Column(nullable = false)
    private Double paidAmount = 0.0;

    @Column(nullable = false)
    private Double remainingAmount = 0.0;

    // Payment status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // Bill creation date
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    // =========================
    // Getters and Setters
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(Double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public Double getMedicineCharge() {
        return medicineCharge;
    }

    public void setMedicineCharge(Double medicineCharge) {
        this.medicineCharge = medicineCharge;
    }

    public Double getLabCharge() {
        return labCharge;
    }

    public void setLabCharge(Double labCharge) {
        this.labCharge = labCharge;
    }

    public Double getRoomCharge() {
        return roomCharge;
    }

    public void setRoomCharge(Double roomCharge) {
        this.roomCharge = roomCharge;
    }

    public Double getOtherCharge() {
        return otherCharge;
    }

    public void setOtherCharge(Double otherCharge) {
        this.otherCharge = otherCharge;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}