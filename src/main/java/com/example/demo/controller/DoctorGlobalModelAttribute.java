package com.example.demo.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Doctor;
import com.example.demo.enums.AppointmentStatus;
import com.example.demo.repo.DoctorRepository;
import com.example.demo.service.AppointmentServiceImpl;

@Controller
@ControllerAdvice
public class DoctorGlobalModelAttribute {

	@Autowired
	private DoctorRepository doctorRepository;
	@Autowired
	private AppointmentServiceImpl appointmentServiceImpl;

	@ModelAttribute
	public void addDoctorInfo(Model model, Principal principal) {

	    if (principal != null) {

	        String email = principal.getName();

	        Doctor doctor = doctorRepository.findByEmail(email);

	        if (doctor != null) {
	            String initials = doctor.getFullName()
	                                    .substring(0, 1)
	                                    .toUpperCase();

	            model.addAttribute("doctorName", doctor.getFullName());
	            model.addAttribute("initials", initials);
	            model.addAttribute("doctor", doctor);
	        }
	    }
	}
	@ModelAttribute
	public void addDashboardStats(Model model) {

		Doctor doctor=(Doctor) model.getAttribute("doctor");
	    LocalDate today = LocalDate.now();

	    List<Appointment>patient= appointmentServiceImpl.getTodayAppointmentsByDoctor(doctor,today);
	    
	    long consultedCount=patient.stream().filter(a -> a.getStatus()==AppointmentStatus.CONSULTED).count();
	    
	    
	    model.addAttribute("todaysVisits",patient.size());

	    model.addAttribute("consultedCount",consultedCount);
	}
	
	
	
}
