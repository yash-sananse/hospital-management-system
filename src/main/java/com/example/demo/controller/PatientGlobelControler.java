package com.example.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entite.User;
import com.example.demo.repo.PatientRepo;

@ControllerAdvice
@RequestMapping("/patient")
public class PatientGlobelControler {
	 @Autowired
	    private PatientRepo patientRepository;

	    @ModelAttribute
	    public void commonData(Model model, Principal principal) {

	        if (principal != null) {

	            String email = principal.getName();

	            User patient = patientRepository.findByEmail(email);

	            model.addAttribute("loggedInUser", patient);
	        }
	    }

}
