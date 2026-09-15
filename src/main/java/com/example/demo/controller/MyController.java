package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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

import jakarta.servlet.http.HttpSession;

@RequestMapping("")
@Controller
public class MyController {

    @Autowired
    private Userrepo userrepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }
    
    
    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication,
    							HttpSession session) {

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String email = userDetails.getUsername();

        User user = userrepo.findByEmail(email);
        
        session.setAttribute("loggedUser", user);
        
        System.out.println(user.getRole());
        if (user.getRole() == UserRole.ADMIN) {

            return "redirect:/admin";
        }
       

        else if (user.getRole() == UserRole.RECEPTIONIST) {

            return "redirect:/reception";
        }

        else if (user.getRole() == UserRole.DOCTOR) {

            return "redirect:/doctor/dashboard";
        }

        else if (user.getRole() == UserRole.NURSE) {

            return "redirect:/nurse";
        }

        else {

            return "redirect:/patient/dashboard";
        }
        
        
    }

//    @PostMapping("/login")
//    public String login(@RequestParam String email,
//                        @RequestParam String password,
//                        HttpSession session,
//                        Model model) {
//
//        User usr = userrepo.findByEmail(email);
//
//        if (usr != null &&
//            usr.getPassword() != null &&
//            encoder.matches(password, usr.getPassword())) {
//
//            session.setAttribute("loggedUser", usr);
//
//            if (usr.getRole()== UserRole.ADMIN) {
//
//                return "redirect:/admin";
//            }
//
//            else if (usr.getRole()== UserRole.DOCTOR) {
//
//                return "redirect:/doctor";
//            }
//            else if (usr.getRole()==UserRole.RECEPTIONIST) {
//
//                return "redirect:/reception";
//            }
//
//            else if (usr.getRole()==UserRole.NURSE) {
//
//                return "redirect:/nurse";
//            }
//            
//            else if (usr.getRole()==UserRole.PATIENT) {
//
//                return "redirect:/patient";
//            }
//            else {
//
//                return "redirect:/";
//            }
//        }
//
//        model.addAttribute("error",
//                "Unauthorized email or password");
//
//        return "login";
//    }
}