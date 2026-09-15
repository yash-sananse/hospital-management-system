package com.example.demo.service;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Bill;
import com.example.demo.entite.Doctor;
import com.example.demo.entite.User;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.repo.BillRepository;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;


    // =========================================================
    // CREATE BILL
    // =========================================================

    @Transactional
    public Bill createBill(
            Appointment appointment,
            Double medicineCharge,
            Double labCharge,
            Double roomCharge,
            Double otherCharge,
            Double discount,
            Double paidAmount) {


        // =====================================================
        // VALIDATE APPOINTMENT
        // =====================================================

        if (appointment == null) {
            throw new IllegalArgumentException(
                    "Cannot generate bill: Appointment is null."
            );
        }


        // =====================================================
        // GET PATIENT AND DOCTOR
        // =====================================================

        User patient = appointment.getPatient();

        Doctor doctor = appointment.getDoctor();


        // =====================================================
        // VALIDATE PATIENT
        // =====================================================

        if (patient == null) {

            throw new IllegalArgumentException(
                    "Cannot generate bill: This appointment is not linked to a patient."
            );
        }


        // =====================================================
        // VALIDATE DOCTOR
        // =====================================================

        if (doctor == null) {

            throw new IllegalArgumentException(
                    "Cannot generate bill: This appointment is not linked to a doctor."
            );
        }


        // =====================================================
        // CHECK DUPLICATE BILL
        // =====================================================

        Bill existingBill =
                billRepository.findByAppointmentId(
                        appointment.getId()
                ).orElse(null);


        if (existingBill != null) {

            throw new IllegalStateException(
                    "A bill already exists for appointment ID: "
                            + appointment.getId()
            );
        }


        // =====================================================
        // CREATE BILL
        // =====================================================

        Bill bill = new Bill();


        // Appointment
        bill.setAppointment(appointment);


        // Patient
        bill.setPatient(patient);


        // Doctor
        bill.setDoctor(doctor);


        // =====================================================
        // CONSULTATION FEE
        // =====================================================

        Double consultationFee =
                doctor.getConsultationFee();


        if (consultationFee == null) {

            consultationFee = 0.0;

        }


        bill.setConsultationFee(
                consultationFee
        );


        // =====================================================
        // PREVENT NULL CHARGES
        // =====================================================

        medicineCharge =
                medicineCharge == null
                        ? 0.0
                        : medicineCharge;


        labCharge =
                labCharge == null
                        ? 0.0
                        : labCharge;


        roomCharge =
                roomCharge == null
                        ? 0.0
                        : roomCharge;


        otherCharge =
                otherCharge == null
                        ? 0.0
                        : otherCharge;


        discount =
                discount == null
                        ? 0.0
                        : discount;


        paidAmount =
                paidAmount == null
                        ? 0.0
                        : paidAmount;


        // =====================================================
        // PREVENT NEGATIVE VALUES
        // =====================================================

        if (medicineCharge < 0) {
            medicineCharge = 0.0;
        }


        if (labCharge < 0) {
            labCharge = 0.0;
        }


        if (roomCharge < 0) {
            roomCharge = 0.0;
        }


        if (otherCharge < 0) {
            otherCharge = 0.0;
        }


        if (discount < 0) {
            discount = 0.0;
        }


        if (paidAmount < 0) {
            paidAmount = 0.0;
        }


        // =====================================================
        // SET CHARGES
        // =====================================================

        bill.setMedicineCharge(
                medicineCharge
        );


        bill.setLabCharge(
                labCharge
        );


        bill.setRoomCharge(
                roomCharge
        );


        bill.setOtherCharge(
                otherCharge
        );


        bill.setDiscount(
                discount
        );


        // =====================================================
        // CALCULATE TOTAL
        // =====================================================

        Double totalAmount =
                consultationFee
                        + medicineCharge
                        + labCharge
                        + roomCharge
                        + otherCharge
                        - discount;


        // Don't allow negative total
        if (totalAmount < 0) {

            totalAmount = 0.0;

        }


        bill.setTotalAmount(
                totalAmount
        );


        // =====================================================
        // PAID AMOUNT
        // =====================================================

        // Patient cannot pay more than total
        if (paidAmount > totalAmount) {

            paidAmount = totalAmount;

        }


        bill.setPaidAmount(
                paidAmount
        );


        // =====================================================
        // REMAINING AMOUNT
        // =====================================================

        Double remainingAmount =
                totalAmount - paidAmount;


        bill.setRemainingAmount(
                remainingAmount
        );


        // =====================================================
        // PAYMENT STATUS
        // =====================================================

        if (paidAmount == 0.0) {

            bill.setPaymentStatus(
                    PaymentStatus.PENDING
            );

        } else if (paidAmount < totalAmount) {

            bill.setPaymentStatus(
                    PaymentStatus.PARTIALLY_PAID
            );

        } else {

            bill.setPaymentStatus(
                    PaymentStatus.PAID
            );
        }


        // =====================================================
        // SAVE BILL
        // =====================================================

        Bill savedBill =
                billRepository.save(bill);


        // =====================================================
        // DEBUG INFORMATION
        // =====================================================

        System.out.println(
                "========================================"
        );

        System.out.println(
                "BILL GENERATED SUCCESSFULLY"
        );

        System.out.println(
                "Bill ID       : "
                        + savedBill.getId()
        );

        System.out.println(
                "Appointment ID: "
                        + appointment.getId()
        );

        System.out.println(
                "Patient ID    : "
                        + patient.getId()
        );

        System.out.println(
                "Patient Name  : "
                        + patient.getName()
        );

        System.out.println(
                "Doctor        : "
                        + doctor.getFullName()
        );

        System.out.println(
                "Total Amount  : "
                        + savedBill.getTotalAmount()
        );

        System.out.println(
                "Paid Amount   : "
                        + savedBill.getPaidAmount()
        );

        System.out.println(
                "Remaining     : "
                        + savedBill.getRemainingAmount()
        );

        System.out.println(
                "Status        : "
                        + savedBill.getPaymentStatus()
        );

        System.out.println(
                "========================================"
        );


        return savedBill;
    }


    // =========================================================
    // GET BILL BY ID
    // =========================================================

    public Bill getBillById(Long id) {

        if (id == null) {

            throw new IllegalArgumentException(
                    "Bill ID cannot be null."
            );
        }


        return billRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Bill not found with ID: " + id
                        )
                );
    }


    // =========================================================
    // GET ALL BILLS
    // =========================================================

    public List<Bill> getAllBills() {

        return billRepository.findAll();
    }


    // =========================================================
    // GET PATIENT BILLS
    // =========================================================

    public List<Bill> getPatientBills(
            Long patientId) {

        return billRepository.findByPatientId(
                patientId
        );
    }


    // =========================================================
    // GET BILL BY APPOINTMENT
    // =========================================================

    public Bill getBillByAppointment(
            Long appointmentId) {

        if (appointmentId == null) {
            return null;
        }


        return billRepository
                .findByAppointmentId(appointmentId)
                .orElse(null);
    }


    // =========================================================
    // GET DOCTOR BILLS
    // =========================================================

    public List<Bill> getDoctorBills(
            Long doctorId) {

        return billRepository.findByDoctorId(
                doctorId
        );
    }

    public void markAsPaid(Long id) {

        Bill bill = billRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bill not found with ID: " + id));

        bill.setPaidAmount(bill.getTotalAmount());
        bill.setRemainingAmount(0.0);
        bill.setPaymentStatus(PaymentStatus.PAID);

        billRepository.save(bill);
    }
    public void updateBill(Long id, Bill updatedBill) {

        Bill bill = billRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bill not found with ID: " + id));


        bill.setConsultationFee(updatedBill.getConsultationFee());
        bill.setMedicineCharge(updatedBill.getMedicineCharge());
        bill.setLabCharge(updatedBill.getLabCharge());
        bill.setRoomCharge(updatedBill.getRoomCharge());
        bill.setOtherCharge(updatedBill.getOtherCharge());
        bill.setDiscount(updatedBill.getDiscount());
        bill.setPaidAmount(updatedBill.getPaidAmount());


        // Recalculate total

        double total =
                bill.getConsultationFee()
                        + bill.getMedicineCharge()
                        + bill.getLabCharge()
                        + bill.getRoomCharge()
                        + bill.getOtherCharge()
                        - bill.getDiscount();

        bill.setTotalAmount(total);


        // Make sure paid amount is valid

        if (bill.getPaidAmount() < 0) {

            bill.setPaidAmount(0.0);

        }

        if (bill.getPaidAmount() > total) {

            bill.setPaidAmount(total);

        }


        // Calculate remaining

        double remaining =
                total - bill.getPaidAmount();

        bill.setRemainingAmount(remaining);


        // Update payment status

        if (bill.getPaidAmount() == 0.0) {

            bill.setPaymentStatus(PaymentStatus.PENDING);

        } else if (bill.getPaidAmount() < total) {

            bill.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);

        } else {

            bill.setPaymentStatus(PaymentStatus.PAID);

        }


        billRepository.save(bill);
    }

    public @Nullable Object gettotalBill(Long appointment) {
        return billRepository.findByAppointmentId(appointment);
    }

    public List<Bill> getBillsByPatientId(long id) {
        return billRepository.findByPatientId(id);
    }

    public Bill getBillByAppointmentId(Long appointmentId) {
        return billRepository.findByAppointmentId(appointmentId).orElseThrow(() -> new  RuntimeException("Bill not found with ID: " + appointmentId));
    }
}