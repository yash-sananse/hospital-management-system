package com.example.demo.configure;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.entite.User;
import com.example.demo.repo.PatientRepo;

@ControllerAdvice
public class GlobalController {

	@Autowired
	private PatientRepo patientRepository;

	@ModelAttribute
	public void commonData(Model model, Principal principal) {

		System.out.println("=== commonData called ===");

		if (principal != null) {

			System.out.println("Principal Name: " + principal.getName());

			User patient = patientRepository.findByEmail(principal.getName());

			System.out.println("Patient Object: " + patient);

			if (patient != null) {

				System.out.println("Patient Name: " + patient.getName());

				// Calculate initials
				String initials = "";

				if (patient.getName() != null &&
						!patient.getName().trim().isEmpty()) {

					String[] nameParts = patient.getName()
							.trim()
							.split("\\s+");

					if (nameParts.length == 1) {

						// Example: Yash → Y
						initials = nameParts[0]
								.substring(0, 1)
								.toUpperCase();

					} else {

						// Example: Yash Sananse → YS
						initials = (
								nameParts[0].substring(0, 1)
										+
										nameParts[nameParts.length - 1]
												.substring(0, 1)
						).toUpperCase();
					}
				}

				model.addAttribute("loggedInUser", patient);
				model.addAttribute("initials", initials);
			}

		} else {

			System.out.println("Principal is NULL");
		}
	}
}