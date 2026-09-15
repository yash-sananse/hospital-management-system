package com.example.demo.controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Prescription;
import com.example.demo.entite.User;
import com.example.demo.repo.PatientRepo;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.PrescriptionServiceImp;

@Controller
@RequestMapping("/patient")
public class PatientController {
	
	@Autowired
	private PatientRepo patientRepo;
	
	@Autowired
	private AppointmentService appointmentService;
	@Autowired
	private PrescriptionServiceImp prescriptionServiceImp;
	
	@GetMapping("/dashboard")
	public String patientDashboard(Model model, Principal principal) {

	    User patient = patientRepo.findByEmail(principal.getName());

	    Appointment upcomingAppointment =
	            appointmentService.getNextAppointment(patient.getPhoneNumber(),LocalDate.now());

	    Prescription latestPrescription =
	            prescriptionServiceImp.getLatestPrescription(patient.getId());

	    long appointmentCount =
	            appointmentService.countAppointments(patient.getPhoneNumber());

	    long prescriptionCount =
	    		prescriptionServiceImp.countPrescriptions(patient.getPhoneNumber());

	    model.addAttribute("upcomingAppointment", upcomingAppointment);
	    model.addAttribute("latestPrescription", latestPrescription);
	    model.addAttribute("appointmentCount", appointmentCount);
	    model.addAttribute("prescriptionCount", prescriptionCount);
		model.addAttribute("activePage","dashboard");


		return "AllPatient/Patient-dashbord";
	}

}
