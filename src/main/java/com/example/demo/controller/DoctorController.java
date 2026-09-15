package com.example.demo.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Doctor;
import com.example.demo.entite.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repo.AppointmentRepository;
import com.example.demo.repo.DoctorRepository;
import com.example.demo.repo.Userrepo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RequestMapping("/admin")
@Controller
public class DoctorController {
	
	@Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private Userrepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private String getInitials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "DR";
        String[] parts = fullName.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            initials.append(parts[i].substring(0, 1).toUpperCase());
        }
        return initials.toString();
    }

    @GetMapping("/doctor")
    public String doctorPage(Model model, 
    						Principal principal,
                            @RequestParam(defaultValue = "0") int page)
    
    {
    	
        String username = principal != null ? principal.getName() : "Doctor";
        
        
        model.addAttribute("name", username);
        model.addAttribute("initials", getInitials(username));
        model.addAttribute("active", "doctors");
        model.addAttribute("pageTitle", "Doctor Dashboard");
        
//        long todayAppointmentsCount= appointmentRepository.count();

        // Safe defaults to avoid template errorsS
//        model.addAttribute("todayAppointmentsCount", todayAppointmentsCount);
        
        
        model.addAttribute("activePatientsCount", 0);
        model.addAttribute("upcomingDaysCount", 0);
        model.addAttribute("upcomingAppointments", new ArrayList<>());
        model.addAttribute("recentPatients", new ArrayList<>());
        model.addAttribute("doctor", null);
        
        
        Page<Doctor> doctorPage = doctorRepository.findAll(PageRequest.of(Math.max(page, 0), 10));

        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("doctorPage", doctorPage);
        model.addAttribute("totalDoctorCount", doctorRepository.count());
        
        
        LocalDate today =LocalDate.now();

        long todayAppointmentsCount =
       
        		appointmentRepository.countByAppointmentDate(today);
        
        model.addAttribute("todayAppointmentsCount",
                todayAppointmentsCount);
        
       long upcomingDaysCount= 	appointmentRepository.findByAppointmentDateAfter(today).size();
       
       model.addAttribute("upcomingDaysCount", upcomingDaysCount);

        return "doctor";
    }
    
    @GetMapping("/doctor/add-doctor")
    public String addDoc(Model model) {
    
    	model.addAttribute("doctor", new Doctor());
    	model.addAttribute("active", "doctors");
    	model.addAttribute("pageTitle", "Add Doctor");
    	
		return "add-doctor";
    	
    }
    
    @PostMapping("/save-doctor")
    public String saveDoctor(@ModelAttribute Doctor doctor) {

        User user = new User();

        user.setName(doctor.getFullName());

        user.setEmail(doctor.getEmail());
        user.setPhoneNumber(doctor.getPhoneNumber());

        user.setPassword(
                passwordEncoder.encode(
                        doctor.getPassword()));

        user.setRole(UserRole.DOCTOR);

        userRepository.save(user);

        doctor.setUser(user);

        doctorRepository.save(doctor);

        return "redirect:/admin/doctor";
    }
    @PostMapping("/updatedoctor")
    public String updateDoctor(@ModelAttribute Doctor doctor,
                               RedirectAttributes redirectAttributes) {

        Doctor existingDoctor = doctorRepository.findById(doctor.getId()).orElse(null);

        if (existingDoctor == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Doctor not found.");
            return "redirect:/admin/users";
        }

        existingDoctor.setFullName(doctor.getFullName());
        existingDoctor.setEmail(doctor.getEmail());
        existingDoctor.setUsername(doctor.getUsername());
        existingDoctor.setPhoneNumber(doctor.getPhoneNumber());
        existingDoctor.setSpecialty(doctor.getSpecialty());
        existingDoctor.setClinic(doctor.getClinic());
        existingDoctor.setConsultationFee(doctor.getConsultationFee());
        existingDoctor.setActive(doctor.isActive());

        User user = existingDoctor.getUser();
        if (user != null) {
            user.setName(doctor.getFullName());
            user.setEmail(doctor.getEmail());
            user.setPhoneNumber(doctor.getPhoneNumber());
            user.setActive(doctor.isActive());
            userRepository.save(user);
        }

        doctorRepository.save(existingDoctor);

        redirectAttributes.addFlashAttribute("successMessage", "Doctor updated successfully.");
        return "redirect:/admin/users";
    }

    @GetMapping("/doctor/deactivate/{id}")
    public String deactivateDoctor(@PathVariable int id,
                                   RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return "redirect:/admin/users";
        }

        Doctor doctor = doctorRepository.findByUser(user);

        if (doctor != null) {

            List<Appointment> appointments =
                    appointmentRepository.findByDoctorAndAppointmentDateGreaterThanEqual(doctor, LocalDate.now());

            if (!appointments.isEmpty()) {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Cannot deactivate doctor. Appointments are assigned to this doctor.");

                return "redirect:/admin/users";
            }

            doctor.setActive(false);
            doctorRepository.save(doctor);
        }

        user.setActive(false);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Doctor deactivated successfully.");

        return "redirect:/admin/users";
    }
    @GetMapping("/doctor/activate/{id}")
    public String activateDoctor(@PathVariable int id) {

        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            user.setActive(true);
            userRepository.save(user);
        }

        return "redirect:/admin/users";
    }
    
}
