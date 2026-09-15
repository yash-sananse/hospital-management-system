package com.example.demo.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.demo.entite.Bill;
import com.example.demo.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Doctor;
import com.example.demo.entite.User;
import com.example.demo.enums.AppointmentStatus;
import com.example.demo.repo.AppointmentRepository;
import com.example.demo.repo.DoctorRepository;
import com.example.demo.repo.Userrepo;
import com.example.demo.service.AppointmentServiceImpl;
import com.example.demo.service.UserServiceImp;

@Controller
@RequestMapping("/user")

public class UserController {
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private Userrepo userRepository;
	
	@Autowired
	private UserServiceImp userService;
	
	@Autowired
	private AppointmentServiceImpl appointmentService;

	@Autowired
	private BillService billService;
	
	 @GetMapping("/book-appointment")
	    public String bookAppointmentPage(Model model, Principal principal) {
		 
		 model.addAttribute("departments",
	                doctorRepository.findAllDepartments());
		 model.addAttribute("activePage","bookAppointment");
	        return "AllPatient/book-appointment";
	    }
	 
	 

	 @GetMapping("/doctors")
	 @ResponseBody
	 public List<Doctor> getDoctorsByDepartment(
	         @RequestParam String department){

	     return doctorRepository
	             .findBySpecialtyAndActiveTrue(department);

	 }
	 
	 @PostMapping("/book-appointment")
	 public String saveAppointment(
	         @RequestParam Long doctorId,
	         @RequestParam LocalDate appointmentDate,
	         @RequestParam LocalTime appointmentTime,
	         @RequestParam String reason,
	         Principal principal,
	         RedirectAttributes redirectAttributes) {

	     // Logged in User
	     User user = userRepository.findByEmail(principal.getName());

	     if (user == null) {
	         redirectAttributes.addFlashAttribute("error",
	                 "User not found.");
	         return "redirect:/user/book-appointment";
	     }

	     // Doctor
	     Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

	     if (doctor == null) {
	         redirectAttributes.addFlashAttribute("error",
	                 "Doctor not found.");
	         return "redirect:/user/book-appointment";
	     }

	     // Doctor Active Check
	     if (!doctor.isActive()) {
	         redirectAttributes.addFlashAttribute("error",
	                 "This doctor is not available.");
	         return "redirect:/user/book-appointment";
	     }

	     // Duplicate Slot Check
	     boolean exists = appointmentRepository
	             .existsByDoctorAndAppointmentDateAndAppointmentTime(
	                     doctor,
	                     appointmentDate,
	                     appointmentTime);

	     if (exists) {

	         redirectAttributes.addFlashAttribute("error",
	                 "This appointment slot is already booked change the  time slot ...");

	         return "redirect:/user/book-appointment";
	     }

	     // Create Appointment
	     Appointment appointment = new Appointment();

	     appointment.setDoctor(doctor);

	     appointment.setPatient(user);

	     appointment.setPatientName(user.getName());

	     appointment.setPhoneNumber(user.getPhoneNumber());

	     appointment.setAppointmentDate(appointmentDate);

	     appointment.setAppointmentTime(appointmentTime);

	     appointment.setReason(reason);

	     appointment.setStatus(AppointmentStatus.BOOKED);

	     appointmentRepository.save(appointment);

	     redirectAttributes.addFlashAttribute("success",
	             "Appointment booked successfully.");

	     return "redirect:/user/book-appointment";
	 }
	 
	 @GetMapping("/appointments")
	 public String appointments(Model model, Principal principal) {

	     User user = userService.findByEmail(principal.getName());

	     model.addAttribute("upcomingAppointments",
	             appointmentService.getUpcomingAppointments(user.getId()));

	     model.addAttribute("appointmentHistory",
	             appointmentService.getAppointmentHistory(user.getId()));

		 model.addAttribute("activePage","myAppointment");

	     return "AllPatient/Appointment";
	 }


	@GetMapping("/bills")
	public String bills(Model model, Principal principal) {


		// Get currently logged-in patient
		User user = userService.findByEmail(principal.getName());

		// Get all bills of this patient from database
		List<Bill> bills = billService.getBillsByPatientId(user.getId());

		// Send data to user-bills.html
		model.addAttribute("patient", user);
		model.addAttribute("bills", bills);
		model.addAttribute("activePage", "Bills");



		return "AllPatient/user-bills";
	}

	@GetMapping("/bill/{appointmentId}")
	public String bill(
			@PathVariable Long appointmentId,
			Model model,
			Principal principal) {

		// Get logged-in patient
		User user = userService.findByEmail(principal.getName());

		// Get bill using appointment ID

		Bill bill = billService.getBillByAppointmentId(appointmentId);

		// Send data to HTML
		model.addAttribute("patient", user);
	model.addAttribute("bill", bill);

		model.addAttribute("activePage", "Bills");

		return "AllPatient/bill-show";
	}
	@GetMapping("/appointment/{id}")
	public String appointmentDetails(@PathVariable Long id, Model model) {

		Appointment appointment = appointmentService.getAppointmentById(id);

		model.addAttribute("appointment", appointment);
		model.addAttribute("activePage", "myAppointment");

		return "AllPatient/appointment-details";
	}

	@GetMapping("/change-password")
	public String changePassword(Model model, Principal principal) {
		model.addAttribute("activePage", "Change Password");
		model.addAttribute("user", userService.findByEmail(principal.getName()));
		return "/AllPatient/password-change";
	}

	@GetMapping("/send-change-password-otp")
	public String sendChangePasswordOtp(Model model, Principal principal) {

		User user = userService.findByEmail(principal.getName());

		userService.sendChangePasswordOtp(user.getEmail());

		model.addAttribute("activePage", "Change Password");
		model.addAttribute("user", user);
		model.addAttribute("otpSent", true);
		model.addAttribute("success", "OTP has been sent to your registered email.");

		return "/AllPatient/password-change";
	}

	@PostMapping("/change-password")
	public String changePassword(
			@RequestParam String otp,
			@RequestParam String newPassword,
			@RequestParam String confirmPassword,
			Model model,
			Principal principal) {

		String email = principal.getName();

		User user = userService.findByEmail(email);

		// Check whether passwords match
		if (!newPassword.equals(confirmPassword)) {

			model.addAttribute("error",
					"New password and confirm password do not match.");

			model.addAttribute("otpSent", true);
			model.addAttribute("activePage", "Change Password");
			model.addAttribute("user", user);

			return "/AllPatient/password-change";
		}


		// Verify OTP
		boolean validOtp =
				userService.verifyChangePasswordOtp(email, otp);


		if (!validOtp) {

			model.addAttribute("error",
					"Invalid OTP. Please enter the correct OTP.");

			model.addAttribute("otpSent", true);
			model.addAttribute("activePage", "Change Password");
			model.addAttribute("user", user);

			return "/AllPatient/password-change";
		}


		// Change password
		userService.changePassword(email, newPassword);


		model.addAttribute(
				"success",
				"Password changed successfully."
		);

		model.addAttribute("activePage", "Change Password");
		model.addAttribute("user", user);

		return "/AllPatient/password-change";
	}
	@GetMapping("/profile")
	public String profile(Model model) {

		model.addAttribute("activePage", "mypro");

		return "AllPatient/profile";
	}

}
