package com.example.demo.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Doctor;
import com.example.demo.enums.AppointmentStatus;
import com.example.demo.repo.AppointmentRepository;
import com.example.demo.repo.DoctorRepository;
import com.example.demo.service.AppointmentServiceImpl;
import com.example.demo.service.DoctorServiceLmp;

@RequestMapping("/doctor")
@Controller
public class DoctorDocController {
	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private AppointmentServiceImpl appointmentServiceImpl;
	
	@Autowired
	private DoctorServiceLmp DoctorServiceLmp;
	

	@GetMapping("/dashboard")
	public String dashboard(Model model, Principal principal) {

		String email = principal.getName();

		Doctor doctor = doctorRepository.findByEmail(email);

		String initials = doctor.getFullName().substring(0, 1).toUpperCase();

		List<Appointment> todaySchedule = appointmentRepository.findByDoctorAndAppointmentDate(doctor, LocalDate.now());

		// Dashboard

		long totalPatients = appointmentRepository.countByDoctor(doctor);

		long appointmentcount = appointmentRepository.countByDoctorAndAppointmentDate(doctor, LocalDate.now());

//		Recent Patients

		List<Appointment> recent = appointmentRepository
				.findTop5ByDoctorOrderByAppointmentDateDescAppointmentTimeDesc(doctor);

		// Header Data
		model.addAttribute("pageTitle", "Doctor Dashboard");
	
		

		// Sidebar Active Menu
		model.addAttribute("active", "dashboard");

		// Dashboard Statistics
		model.addAttribute("todayAppointmentCount", appointmentcount);
		model.addAttribute("totalPatients", totalPatients);
		model.addAttribute("pendingReports", 7);

		// todays shedule
		model.addAttribute("todaySchedule", todaySchedule);

		return "AllDoctor/DoctorDoc";

	}

	@GetMapping("/appointments")
	public String doctorAppointment(Model model, @RequestParam(defaultValue = "0") int page) {

        Page<Appointment> appointmentPage = appointmentRepository.findAll(PageRequest.of(Math.max(page, 0), 10));
        model.addAttribute("appointments", appointmentPage.getContent());
        model.addAttribute("appointmentPage", appointmentPage);
        model.addAttribute("active", "appointments");
        return "AllDoctor/DoctorAppointments";
	}

	@GetMapping("/appointments/search")
	
	public String doctorAppointment(Model model,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "0") int page) {
		
        Page<Appointment> appointmentPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            appointmentPage = appointmentRepository.searchPatientPaged(
                    keyword, PageRequest.of(Math.max(page, 0), 10));
        } else {
            appointmentPage = appointmentRepository.findAll(PageRequest.of(Math.max(page, 0), 10));
        }

        model.addAttribute("appointments", appointmentPage.getContent());
        model.addAttribute("appointmentPage", appointmentPage);
        model.addAttribute("keyword", keyword);
	    model.addAttribute("active", "appointments");

		
		return "AllDoctor/DoctorAppointments";
		
	}
	
	@GetMapping("/schedule")
	public String todaysSchedule(Model model) {

	    LocalDate today = LocalDate.now();

	    List<Appointment> todayAppointments =
	            appointmentServiceImpl.getTodayAppointments(today);

	    long completedCount = todayAppointments.stream()
	            .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
	            .count();

	    long pending = todayAppointments.stream()
	            .filter(a -> a.getStatus() == AppointmentStatus.BOOKED)
	            .count();

	    model.addAttribute("todayAppointments", todayAppointments);
	    model.addAttribute("totalAppointments", todayAppointments.size());
	    model.addAttribute("completedCount", completedCount);
	    model.addAttribute("pendingCount", pending);
	    model.addAttribute("active", "schedule");

	    return "AllDoctor/todaysShedule";
	}
	
	@GetMapping("/patients")
	public String doctorPatients(Model model) {
		
		Doctor doctor=(Doctor) model.getAttribute("doctor");
		
		LocalDate today=LocalDate.now();
		List<Appointment> patient=appointmentServiceImpl.getTodayAppointmentsByDoctor(doctor, today);
		
		
		
		model.addAttribute("patients",patient);

		model.addAttribute("active", "patients");
		
		return "AllDoctor/DoctorPatients";
		
	}
	@GetMapping("/patients/search")
	public String patientSearch(Model model,
								@RequestParam(required = false) String keyword) {
		
		List<Appointment>list;
		LocalDate today=LocalDate.now();
		if(keyword!=null && !keyword.trim().isEmpty()) {
			list=appointmentServiceImpl.searchAppointments(keyword);
		}
		else {
			list=appointmentServiceImpl.getTodayAppointments(today);
		}
		
		model.addAttribute("active", "patients");
		  model.addAttribute("patients", list);

		
		return "AllDoctor/DoctorPatients";
		
	}
	@PostMapping("/consult/{id}")
	public String consultButton(@PathVariable long id ) {
		
		Appointment appointment= appointmentServiceImpl.findwithAppoinmentid(id);
		if(appointment.getStatus()== AppointmentStatus.CHECKED_IN) {
			appointment.setStatus(AppointmentStatus.CONSULTED);
			appointmentServiceImpl.saveAppointment(appointment);
			
		}
		
		return "redirect:/doctor/patients";
		
	}
	
	// password chnage by Email 
	
	@GetMapping("/change-password")
	public String changePassword(Model model) {
	    model.addAttribute("active", "password");
	    return "AllDoctor/change-password";
	}
	
	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam String email, Model model) {

		System.out.println("Email from form: " + email);
	   try {
		   
		   
		 DoctorServiceLmp.sendotp(email);
		 
		 
	} catch (Exception e) {

		e.printStackTrace();
		model.addAttribute("error", e.getMessage());
	}
		 
	    // 3. Save OTP
	    // 4. Send OTP to email
	    // 5. Redirect to OTP verification page

	   model.addAttribute("active", "password");
		return "AllDoctor/verify-otp";
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String email,
	                        @RequestParam String otp,
	                        Model model) {

		 System.out.println("Verify OTP Controller Called");
		    System.out.println("Email = " + email);
		    System.out.println("OTP = " + otp);
		    
		    
	    boolean verified = DoctorServiceLmp.verifyOtp(email, otp);
	    
	    System.out.println("Verified = " + verified);

	    if (!verified) {
	        model.addAttribute("email", email);
	        model.addAttribute("error", "Invalid or Expired OTP");
	        model.addAttribute("active", "password");
	        return "AllDoctor/verify-otp";
	    }

	    model.addAttribute("email", email);
		model.addAttribute("active", "password");

	    return "AllDoctor/new-password";
	}
	@PostMapping("/update-password")
	public String updatePassword(@RequestParam String email,
	                             @RequestParam String newPassword,
	                             @RequestParam String confirmPassword,
	                             Model model) {

	    if (!newPassword.equals(confirmPassword)) {
	        model.addAttribute("email", email);
	        model.addAttribute("error", "Passwords do not match.");
	        return "AllDoctor/new-password";
	    }

	    DoctorServiceLmp.updatePassword(email, newPassword);

	    return "redirect:/login";
	}
	
	@GetMapping("/profile")
	public String doctorProfile(Model model, Principal principal) {

	    String email = principal.getName();

	    Doctor doctor = doctorRepository.findByEmail(email);

	    model.addAttribute("doctor", doctor);
	    model.addAttribute("active", "profile");

	    return "AllDoctor/DoctorProfile";
	}
	@GetMapping("/appointment/view/{id}")
	public String viewAppointment(@PathVariable Long id, Model model) {

		Appointment appointment = appointmentServiceImpl.getAppointmentById(id);

		model.addAttribute("appointment", appointment);
		model.addAttribute("active", "appointments");

		return "AllDoctor/appointment-details";
	}
	@GetMapping("/appointment/complete/{id}")
	public String completeAppointment(@PathVariable Long id) {

		Appointment appointment = appointmentServiceImpl.getAppointmentById(id);

		appointment.setStatus(AppointmentStatus.COMPLETED);

		appointmentServiceImpl.saveAppointment(appointment);

		return "redirect:/doctor/appointments";
	}
	@GetMapping("/records")
	public String settings(Model model){
		model.addAttribute("active", "records");
		return "AllDoctor/coming-soon.html";
	}
}
