package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entite.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repo.Userrepo;

@RequestMapping("/register")
@Controller
public class RegisterController {
	@Autowired
	private Userrepo usrrep;
	
	@GetMapping("")
	public String reg() {
		return "register";
		
	}
	
	@PostMapping("")
	public String register(  
			@RequestParam String name ,
		    @RequestParam String email,
		    @RequestParam String password,
		    @RequestParam String phoneNumber,
		    Model model)
	{
		
		User existuser= usrrep.findByEmail(email);
		
		 BCryptPasswordEncoder encoder=new  BCryptPasswordEncoder();
		 
		 String encodepass=encoder.encode(password);
		  
		if(existuser !=null) {
			
			model.addAttribute("error", "Email is already registered ");
			return "register";
		}
		
		
		User usr =new User();
		
		usr.setEmail(email);
		usr.setName(name);
		usr.setPassword(encodepass);
		usr.setPhoneNumber(phoneNumber);
		usr.setRole(UserRole.PATIENT);
		 
		usrrep.save(usr);
		
		return "login";
	}
}

