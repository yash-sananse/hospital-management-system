package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Doctor;
import com.example.demo.enums.AppointmentStatus;
import com.example.demo.enums.UserRole;
import com.example.demo.repo.AppointmentRepository;

@Service
public class AppointmentServiceImpl implements AppointmentService {
	
	  @Autowired
	    private AppointmentRepository appointmentRepository;


	@Override
	public List<Appointment> getTodayAppointments(LocalDate date) {
	
		return appointmentRepository.findByAppointmentDate(date);
	}


	@Override
	public void checkInPatient(long id) {
	Appointment appointment=	appointmentRepository.findById(id).orElse(null);
	
	if(appointment !=null) {
		
		appointment.setStatus(AppointmentStatus.CHECKED_IN);
		appointmentRepository.save(appointment);
	}
		
	}


	@Override
	public void cancelAppointment(long id) {
		
		
	Appointment appointment=	appointmentRepository.findById(id).orElse(null);
	
	if(appointment !=null) {
		appointment.setStatus(AppointmentStatus.CANCELLED);
		appointmentRepository.save(appointment);
	}
		
	}


	@Override
	public List<Appointment> getTodayQueue() {
		
		LocalDate today=LocalDate.now();
		
		List<Appointment> appointments= appointmentRepository.findByAppointmentDate(today);
		appointments.stream()
							.filter(appt -> appt.getStatus()!=AppointmentStatus.CANCELLED)
							.toList();
		return appointments;
	}


	@Override
	public List<Appointment> getAllAppointment() {
		
		List<Appointment> list=appointmentRepository.findAll();
		return list;
	}


	@Override
	public List<Appointment> searchAppointments(String keyword) {
		List<Appointment> list=appointmentRepository.findByPatientNameContainingIgnoreCase(keyword);
		return list;
	}


	@Override
	public List<Appointment> todaysAppointment(LocalDate today) {
		
		
		List<Appointment> list=appointmentRepository.findByAppointmentDate(today);
		
		return list;
	}


	@Override
	public List<Appointment> getTodayAppointmentsByDoctor(Doctor doctor, LocalDate date) {
		
		return appointmentRepository.findByDoctorAndAppointmentDate(doctor, date);
	}


	public Appointment findwithAppoinmentid(long id) {
		
		Appointment appointment =appointmentRepository.findById(id).orElse(null);
		if(appointment == null) {
			throw new RuntimeException("Appointment not found with id: " + id);
		}
		return appointment;
	}


	public void saveAppointment(Appointment appointment) {
		appointmentRepository.save(appointment);
		
	}


	@Override
	public Appointment getNextAppointment(String num, LocalDate date) {
		return appointmentRepository.findFirstByPhoneNumberAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscAppointmentTimeAsc(num, date);
	}


	@Override
	public long countAppointments(String phumber) {
		
		long exist =appointmentRepository.countByPhoneNumber(phumber);
		
		return exist;
	}


	@Override
	public List<Appointment> getUpcomingAppointments(Long patientId) {
		
		
		return appointmentRepository
	            .findByPatientIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscAppointmentTimeAsc(
	                    patientId,
	                    LocalDate.now());
	}


	@Override
	public List<Appointment> getAppointmentHistory(long  patientId) {
		
		
		return appointmentRepository.findByPatientIdAndAppointmentDateLessThanOrderByAppointmentDateDescAppointmentTimeDesc(
                patientId,
                LocalDate.now());
	}

	@Override
	public Appointment getAppointmentById(Long appointmentId) {
		return appointmentRepository.findById(appointmentId).orElse(null);
	}


}


	


	

