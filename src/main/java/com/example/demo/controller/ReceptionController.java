package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

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
import com.example.demo.entite.Prescription;
import com.example.demo.enums.AppointmentStatus;
import com.example.demo.repo.AppointmentRepository;
import com.example.demo.repo.DoctorRepository;
import com.example.demo.repo.PrescriptionRepository;
import com.example.demo.service.AppointmentService;

@RequestMapping("/reception")
@Controller
public class ReceptionController {

	@Autowired
	private AppointmentService appointmentService;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private DoctorRepository  doctorRepository;
	
	@Autowired
	private PrescriptionRepository prescriptionRepository;
	
	@GetMapping("")
	public String receptioncall(
	        @RequestParam(required = false) String keyword,
	        Model model) {

	    LocalDate today = LocalDate.now();

	    List<Appointment> appointments;

	    if (keyword != null && !keyword.trim().isEmpty()) {

	        appointments =
	                appointmentRepository
	                        .findByPatientNameContainingIgnoreCaseOrPhoneNumberContaining(
	                                keyword,
	                                keyword);

	    } else {

	        appointments =
	                appointmentService.getTodayAppointments(today);
	    }

	    model.addAttribute("appointments", appointments);
	    model.addAttribute("keyword", keyword);

	    model.addAttribute("todayCount",
	            appointmentRepository.countByAppointmentDate(today));

	    model.addAttribute("checkedInCount",
	            appointmentRepository.countByAppointmentDateAndStatus(
	                    today,
	                    AppointmentStatus.CHECKED_IN));

	    model.addAttribute("bookedCount",
	            appointmentRepository.countByAppointmentDateAndStatus(
	                    today,
	                    AppointmentStatus.BOOKED));

	    model.addAttribute("active", "dashboard");

	    return "reception";
	}



	@GetMapping("/appointments")
	public String appointments(
			@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "0") int page,
			Model model) {

		Page<Appointment> appointmentPage;

		int safePage=Math.max(page,0);

		if (keyword != null && !keyword.isBlank()) {

			appointmentPage = appointmentRepository.searchPatientPaged(
					keyword.trim(),
					PageRequest.of(
							safePage,
							10,
							Sort.by(
									Sort.Order.desc("appointmentDate"),
									Sort.Order.desc("appointmentTime")
							)
					)
			);
		}
		else {

				appointmentPage = appointmentRepository.findAll(
						PageRequest.of(
								safePage,
								10,
								Sort.by(
										Sort.Order.desc("appointmentDate"),
										Sort.Order.desc("appointmentTime")
								)
						)
				);
			}

		model.addAttribute("appointments", appointmentPage.getContent());
		model.addAttribute("appointmentPage", appointmentPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("active", "appointments");

		return "AllReception/reception-appointments";
	}

	@GetMapping("/today-queue")
	public String todaysQueue(Model model) {
		
		
	List<Appointment> appointments=	appointmentService.getTodayQueue();
		
		 model.addAttribute("active", "queue");
		 model.addAttribute("appointments", appointments);
		 
		return "/AllReception/today-queue";
	}
	
	
	@GetMapping("/appointment/checkin/{id}")
	public String checkedIn(@PathVariable long id ) {

	    appointmentService.checkInPatient(id);

	    return "redirect:/reception";

	}
	
	
	@GetMapping("/appointment/cancel/{id}")
	public String cancleAppointment(@PathVariable long id) {
		
		appointmentService.cancelAppointment(id);
		
		return "redirect:/reception/appointments";
		
	}
	
	
	@GetMapping("/appointment/delete/{id}")
	public String deleteAppointment(@PathVariable long id) {
		
		 Appointment appointment =
		            appointmentRepository.findById(id).orElse(null);

		    if (appointment != null) {

		        Prescription prescription =
		                prescriptionRepository.findByAppointment(appointment);

		        if (prescription != null) {
		            prescriptionRepository.delete(prescription);
		        }

		        appointmentRepository.deleteById(id);
		    }
		return "redirect:/reception/appointments";
		
	}
	
	@GetMapping("/Book-Appointment")
	
	public String bookAppointment(Model model) {
		
		Appointment appointment=new Appointment();
		appointment.setDoctor(new Doctor());
		
		model.addAttribute("active","book");
		model.addAttribute("appointment", appointment);
		model.addAttribute("doctors",doctorRepository.findAll());
		
		return "/AllReception/Book-Appointment";
		
	}
	@PostMapping("/save-appointment")
	public String savePatient( @ModelAttribute Appointment appointment,
			RedirectAttributes  redirectAttributes ) {
		
		Doctor doctor=
				doctorRepository.findById(
											appointment.getDoctor().getId())
											.orElse(null);
		
		appointment.setDoctor(doctor);
		appointment.setStatus(AppointmentStatus.BOOKED);
		appointmentRepository.save(appointment);
		redirectAttributes.addFlashAttribute( "successMessage",
	            "Appointment booked successfully!");

		return "redirect:/reception/Book-Appointment";
		
	}
	
	@GetMapping("/checkedin-patients")
	public String checkedinPatient(Model model,
	                              @RequestParam(required = false) String keyword,
	                              @RequestParam(defaultValue = "0") int page) {

        Page<Appointment> appointmentPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            appointmentPage = appointmentRepository.searchByStatusAndPatient(
                    AppointmentStatus.CHECKED_IN,
                    keyword.trim(),
                    PageRequest.of(Math.max(page, 0), 10));
        } else {
            appointmentPage = appointmentRepository.findByStatusPaged(
                    AppointmentStatus.CHECKED_IN,
                    PageRequest.of(Math.max(page, 0), 10));
        }

        model.addAttribute("appointments", appointmentPage.getContent());
        model.addAttribute("appointmentPage", appointmentPage);
        model.addAttribute("active", "checkedin");
        model.addAttribute("keyword", keyword);
        return "/AllReception/checkedin-patients";
	}

	@GetMapping("/appointment/admit/{id}")
	public String admitAppointment(@PathVariable Long id) {

		Appointment appointment = appointmentService.getAppointmentById(id);

		appointment.setStatus(AppointmentStatus.ADMITTED);

		appointmentService.saveAppointment(appointment);

		return "redirect:/reception/appointments";
	}
	@GetMapping("/appointment/discharge/{id}")
	public String dischargeAppointment(@PathVariable Long id) {

		Appointment appointment = appointmentService.getAppointmentById(id);

		appointment.setStatus(AppointmentStatus.DISCHARGED);

		appointmentService.saveAppointment(appointment);

		return "redirect:/reception/appointments";
	}
	@GetMapping("/appointment/complete/{id}")
	public String completeAppointment(@PathVariable Long id) {

		Appointment appointment = appointmentService.getAppointmentById(id);

		appointment.setStatus(AppointmentStatus.COMPLETED);

		appointmentService.saveAppointment(appointment);

		return "redirect:/reception/appointments";
	}
	
}
