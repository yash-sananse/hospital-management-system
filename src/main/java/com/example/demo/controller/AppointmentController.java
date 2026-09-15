package com.example.demo.controller;




import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.demo.entite.Bill;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.repo.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import com.example.demo.entite.Appointment;
import com.example.demo.entite.Doctor;
import com.example.demo.enums.AppointmentStatus;
import com.example.demo.repo.AppointmentRepository;
import com.example.demo.repo.DoctorRepository;

import javax.naming.BinaryRefAddr;

@RequestMapping("/admin")
@Controller
public class AppointmentController {

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private BillRepository billRepository;
	
	
	
	
	@GetMapping("/doctor/{id}")
	public String getAppoinmentiewDoctorAppointments(
	        @PathVariable Long id,
	        Model model) {
	
	Doctor doctor=	 doctorRepository.findById(id).orElse(null);
	
	List<Appointment> allAppoinment= appointmentRepository.findByDoctorId(id);
	
	model.addAttribute("doctor", doctor);
	model.addAttribute("appointments",allAppoinment);
	
	model.addAttribute("active", "appointments");
		model.addAttribute("pageTitle", "Doctor Appointments");
		return "doctor-appointments";
		
	}
	
	
	@GetMapping("/appointments")
	public String getAppoinment(Model model, @RequestParam(defaultValue = "0") int page,
								@RequestParam(defaultValue = "") String keyword) {

		int safepage=Math.max(page,0);
		Page<Appointment> appointmentPage;

		if (keyword!=null && !keyword.equals("")) {
			appointmentPage=appointmentRepository.searchAppointments(keyword.trim(),
					PageRequest.of(safepage,
							10,
							Sort.by(Sort.Direction.DESC,"id")));
		}
		else {

			appointmentPage = appointmentRepository.findAll(
					PageRequest.of(safepage,10,
							Sort.by(Sort.Direction.DESC,"id"))
			);


		}



        model.addAttribute("appointments", appointmentPage.getContent());
		model.addAttribute("appointmentPage", appointmentPage);

		model.addAttribute("keyword", keyword);

		model.addAttribute("active", "appointments");
		model.addAttribute("pageTitle", "Appointments");


		return "appointments";

	}

	
	@GetMapping("/doctor/{id}/add-appointment")
	public String showAddAppointmentPage(  @PathVariable long id,
											Model model) {
		
	Doctor doctor=	doctorRepository.findById(id).orElse(null);
	
	Appointment appointment= new Appointment();
	
	appointment.setDoctor(doctor);
	
	
	model.addAttribute("doctor", doctor);
	model.addAttribute("appointment", appointment);
		
		model.addAttribute("active", "appointments");
		model.addAttribute("pageTitle", "Add Appointment");
		return "add-appointment";
		
	}
	
	@PostMapping("/save-appointment")
	public String saveAppoinment( @ModelAttribute Appointment appointment,
			 						Model model,
			 						 RedirectAttributes redirectAttributes) {
		
		Long doctorId= appointment.getDoctor().getId();
		
		Doctor doctor= doctorRepository.findById(doctorId).orElse(null);
		
		 if (doctor == null) {
		        redirectAttributes.addFlashAttribute("error", "Doctor not found");
		        return "redirect:/";
		    }
		
		appointment.setDoctor(doctor);
		 appointment.setStatus(AppointmentStatus.BOOKED);

		appointmentRepository.save(appointment);
		
	    redirectAttributes.addFlashAttribute("success", "Appointment Saved");

		    return "redirect:/admin/doctor/" + doctorId;

		
	}
	
	@GetMapping("/upcoming-appointments")
	public String upcomingAppointments(Model model) {
		
		LocalDate today= LocalDate.now();
		
		
	    List<Appointment> upcomingAppointments = appointmentRepository.findByAppointmentDateAfter(today);
	    model.addAttribute("appointments", upcomingAppointments);
        model.addAttribute("active", "appointments");
        model.addAttribute("pageTitle", "Upcoming Appointments");
		
		return "upcoming-appointments";
		
	}
	@GetMapping("/today-appointments")
	public String todaysAppointment(Model model) {
		
		LocalDate today=LocalDate.now();
		
	List<Appointment> appointments =	appointmentRepository.findByAppointmentDate(today);
	
	model.addAttribute("appointments", appointments);
        model.addAttribute("active", "appointments");
        model.addAttribute("pageTitle", "Today's Appointments");
		
		return "today-appointments";
		
	}
	
	
	@GetMapping("/doctor/{id}/today-appointments")
	public String showDoctorTodaysAppointment( @PathVariable Long id ,
												Model model) {
		LocalDate today=LocalDate.now();
		
		List<Appointment> appointments =appointmentRepository.findByDoctorIdAndAppointmentDate(id, today);
		
		
		Doctor doctor=doctorRepository.findById(id).orElse(null);
		
		model.addAttribute("appointments", appointments);
		model.addAttribute("doctor", doctor);
        model.addAttribute("active", "appointments");
        model.addAttribute("pageTitle", "Today's Doctor Appointments");
		
		return "today-appointments";
		
	}
	
	@GetMapping("/doctor/{id}/upcoming-appointments")
	public String showDoctorUpcomingAppointments(@PathVariable long id , 
													Model model) {
		LocalDate today=LocalDate.now();
		
			List<Appointment> appointments=appointmentRepository.findByDoctorIdAndAppointmentDateAfter(id, today);
			
			Doctor doctor=doctorRepository.findById(id).orElse(null);
			
			
			model.addAttribute("appointments", appointments);
			model.addAttribute("doctor", doctor);
            model.addAttribute("active", "appointments");
            model.addAttribute("pageTitle", "Upcoming Doctor Appointments");
		
				return "upcoming-appointments";
		
	}
	@GetMapping("/appointment/delete/{id}")
	public String deleteAppointment(@PathVariable Long id) {

		Optional<Bill> bill = billRepository.findByAppointmentId(id);

		if (bill.isPresent()) {

			if (PaymentStatus.PAID.equals(bill.get().getPaymentStatus())) {

				// First delete the bill
				billRepository.deleteById(bill.get().getId());

				// Then delete appointment
				appointmentRepository.deleteById(id);

			} else {

				// Bill is not paid → don't delete appointment
				return "redirect:/admin/appointments?error=BillNotPaid";
			}

		} else {

			// No bill exists → delete appointment directly
			appointmentRepository.deleteById(id);
		}

		return "redirect:/admin/appointments";
	}
}
