package com.example.demo.repo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Doctor;
import com.example.demo.enums.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorId(Long doctorId);

    Page<Appointment> findAll(Pageable pageable);

    long countByAppointmentDate(LocalDate appointmentDate);

    List<Appointment> findByAppointmentDateAfter(LocalDate appointment);

    List<Appointment> findByAppointmentDate(LocalDate appointment);

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

    List<Appointment> findByDoctorIdAndAppointmentDateAfter(Long doctorId, LocalDate appointmentDate);

    long countByAppointmentDateAndStatus(LocalDate appointmentDate, AppointmentStatus status);

    List<Appointment> findByAppointmentDateOrderByAppointmentTime(LocalDate appointmentDate);

    List<Appointment> findByPatientNameContainingIgnoreCaseOrPhoneNumberContaining(String patientName, String phoneNumber);

    @Query("SELECT a FROM Appointment a WHERE " +
           "LOWER(a.patientName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "a.phoneNumber LIKE CONCAT('%', :keyword, '%')")
    Page<Appointment> searchPatientPaged(@Param("keyword") String keyword, Pageable pageable);

    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.status = :status")
    Page<Appointment> findByStatusPaged(@Param("status") AppointmentStatus status, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.status = :status AND " +
           "(LOWER(a.patientName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR a.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<Appointment> searchByStatusAndPatient(@Param("status") AppointmentStatus status,
                                                @Param("keyword") String keyword,
                                                Pageable pageable);
    @Query("""
    SELECT a FROM Appointment a
    LEFT JOIN a.doctor d
    WHERE LOWER(a.patientName) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(a.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(a.reason) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(d.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    Page<Appointment> searchAppointments(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    List<Appointment> findByDoctorAndAppointmentDateGreaterThanEqual(Doctor doctor, LocalDate date);

    long countByDoctor(Doctor doctor);

    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, LocalDate date);

    long countByDoctorAndAppointmentDate(Doctor doctor, LocalDate appointmentDate);

    List<Appointment> findTop5ByDoctorOrderByAppointmentDateDescAppointmentTimeDesc(Doctor doctor);

    List<Appointment> findByPatientNameContainingIgnoreCase(String keyword);

    Appointment findFirstByPhoneNumberAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscAppointmentTimeAsc(
            String phoneNumber, LocalDate appointmentDate);

    long countByPhoneNumber(String phumber);

    boolean existsByDoctorAndAppointmentDateAndAppointmentTime(Doctor doctor, LocalDate appointmentDate, LocalTime appointmentTime);

    List<Appointment> findByPatientIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscAppointmentTimeAsc(
            Long patientid, LocalDate Localdate);

    List<Appointment> findByPatientIdAndAppointmentDateLessThanOrderByAppointmentDateDescAppointmentTimeDesc(
            long patientId, LocalDate now);
}
