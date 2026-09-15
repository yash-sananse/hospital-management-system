package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Doctor;



public interface AppointmentService {
	
	List<Appointment> getTodayAppointments(LocalDate date);
	
	public void checkInPatient(long id);
	
	public void cancelAppointment(long id );
	
	List<Appointment> getTodayQueue();
	
	List<Appointment> getAllAppointment();
	
	public List<Appointment> searchAppointments(String keyword);
	
	List<Appointment> todaysAppointment(LocalDate today);
	
	public List<Appointment> getTodayAppointmentsByDoctor(
	        Doctor doctor,
	        LocalDate date);
	
	public Appointment findwithAppoinmentid(long id);
	
	
	public void saveAppointment(Appointment appointment);

	Appointment getNextAppointment(String number,LocalDate date);

	long countAppointments(String number);
	
	
	List<Appointment>getUpcomingAppointments(Long patientId);
	
	public List<Appointment> getAppointmentHistory(long  patientId);

    Appointment getAppointmentById(Long appointmentId);
}

