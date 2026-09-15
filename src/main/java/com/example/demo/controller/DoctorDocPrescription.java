package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.example.demo.entite.Appointment;
import com.example.demo.entite.Prescription;
import com.example.demo.entite.PrescriptionMedicine;
import com.example.demo.enums.AppointmentStatus;
import com.example.demo.repo.PrescriptionMedicineRepository;
import com.example.demo.repo.PrescriptionRepository;
import com.example.demo.service.AppointmentServiceImpl;

@Controller
@RequestMapping("/doctor/prescription")

public class DoctorDocPrescription {
	
	@Autowired
	private AppointmentServiceImpl appointmentServiceImpl;
	
	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private PrescriptionMedicineRepository medicineRepository;
	
	 @GetMapping({"", "/"})
	public String prescription(Model model, @RequestParam(defaultValue = "0") int page) {

        Page<Prescription> prescriptionPage = prescriptionRepository.findAll(PageRequest.of(Math.max(page, 0), 10));
        model.addAttribute("active", "prescriptions");
        model.addAttribute("prescriptions", prescriptionPage.getContent());
        model.addAttribute("prescriptionPage", prescriptionPage);
        return "AllDoctor/prescriptions";
	}

	@GetMapping("/search")
	public String searchPrescriptions(@RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "0") int page,
                                    Model model) {

        Page<Prescription> prescriptionPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            String search = keyword.trim();
            prescriptionPage = prescriptionRepository
                    .findByAppointmentPatientNameContainingIgnoreCaseOrAppointmentPhoneNumberContaining(
                            search, search, PageRequest.of(Math.max(page, 0), 10));
        } else {
            prescriptionPage = prescriptionRepository.findAll(PageRequest.of(Math.max(page, 0), 10));
        }

        model.addAttribute("prescriptions", prescriptionPage.getContent());
        model.addAttribute("prescriptionPage", prescriptionPage);
        model.addAttribute("keyword", keyword);
		model.addAttribute("active", "prescriptions");
		return "AllDoctor/prescriptions";
	}

	@GetMapping("/{appointmentId}")
	public String prescriptionPage(@PathVariable Long appointmentId,
	                               Model model) {


	    Appointment appointment =
	            appointmentServiceImpl.findwithAppoinmentid(appointmentId);
	    model.addAttribute("appointment", appointment);
	    model.addAttribute("active", "prescriptions");
	  

	    return "AllDoctor/PrescriptionForm";
	}
	
	@PostMapping("/save")
	public String savePrescription(

	        @RequestParam Long appointmentId,

	        @RequestParam List<String> medicineNames,
	        @RequestParam List<String> dosages,
	        @RequestParam List<String> frequencies,
	        @RequestParam List<String> durations,
	        @RequestParam(required = false) LocalDate followUpDate,
	        @RequestParam(required = false) String diagnosis,
	        @RequestParam(required = false) String clinicalNotes,
	        @RequestParam(required = false) String advice
	) {

	    Appointment appointment =
	            appointmentServiceImpl.findwithAppoinmentid(appointmentId);
	    
		

	    Prescription prescription = new Prescription();

	    prescription.setAppointment(appointment);
	    prescription.setDoctor(appointment.getDoctor());
	    prescription.setCreatedAt(LocalDateTime.now());
	    prescription.setFollowUpDate(followUpDate);
	    prescription.setDiagnosis(diagnosis);
	    prescription.setClinicalNotes(clinicalNotes);
	    prescription.setAdvice(advice);

	    List<PrescriptionMedicine> medicines = new ArrayList<>();
	    
	   

	    for (int i = 0; i < medicineNames.size(); i++) {

	        PrescriptionMedicine medicine =
	                new PrescriptionMedicine();

	        medicine.setMedicineName(medicineNames.get(i));
	        medicine.setDosage(dosages.get(i));
	        medicine.setFrequency(frequencies.get(i));
	        medicine.setDuration(durations.get(i));
	        

	        // link child -> parent
	        medicine.setPrescription(prescription);

	        medicines.add(medicine);
	    }

	    // link parent -> children
	    prescription.setMedicines(medicines);

	    prescriptionRepository.save(prescription);
	    
	    if(appointment.getStatus() == AppointmentStatus.CHECKED_IN) {

	    
	        appointment.setStatus(AppointmentStatus.CONSULTED);

	        appointmentServiceImpl.saveAppointment(appointment);
	    }

	    return "redirect:/doctor/patients";
	}
	
	@GetMapping("/view/{id}")
	public String viewprescription(@PathVariable long id, Model model){
		
		Prescription latestPrescription= prescriptionRepository.findById(id)
				.orElseThrow(() -> new  RuntimeException("priscription not found"));
		
		 Appointment appointment =
				 latestPrescription.getAppointment();

		    List<Prescription> history =
		            prescriptionRepository
		            .findByAppointmentPhoneNumberOrderByCreatedAtDesc(
		                    appointment.getPhoneNumber());
		
		
		    
	 model.addAttribute("latestPrescription", latestPrescription);
		    model.addAttribute("appointment", latestPrescription.getAppointment());
		    model.addAttribute("medicines", latestPrescription.getMedicines());
		    model.addAttribute("history", history);

		    model.addAttribute("active", "prescriptions");
		return "/AllDoctor/prescription-details";
		
	}
	@GetMapping("/print/{id}")
	public String printPrescription(@PathVariable Long id,
	                                Model model) {

	    Prescription prescription =
	            prescriptionRepository.findById(id)
	            .orElseThrow(() ->
	                new RuntimeException("Prescription Not Found"));

	    model.addAttribute("prescription", prescription);
	    model.addAttribute("appointment",
	            prescription.getAppointment());

	    return "AllDoctor/prescription-print";
	}
	
}
