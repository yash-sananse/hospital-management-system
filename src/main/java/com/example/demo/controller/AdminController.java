package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.example.demo.entite.Doctor;
import com.example.demo.entite.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repo.DoctorRepository;
import com.example.demo.repo.Userrepo;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/admin")
@Controller
public class AdminController {

    @Autowired
    private Userrepo userrepo;

    @Autowired
    private BCryptPasswordEncoder encoder;
    
    @Autowired
    private DoctorRepository doctorRepository;

    private String getInitials(String fullName) {

        if (fullName == null || fullName.isBlank())
            return "A";

        String[] parts = fullName.trim().split("\\s+");

        StringBuilder initials = new StringBuilder();

        for (int i = 0; i < Math.min(2, parts.length); i++) {

            initials.append(parts[i]
                    .substring(0, 1)
                    .toUpperCase());
        }

        return initials.toString();
    }

    @GetMapping("")
    public String admin(Model model,
                        HttpSession session) {

        User loggedUser =
                (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {

            return "redirect:/login";
        }
        
        // add recent users
        List<User> recentUsers = userrepo.findTop5ByOrderByCreatedAtDesc();
        model.addAttribute("recentUsers", recentUsers);

        long totalUsers = userrepo.count();
        long totalAdmins = userrepo.findByRole(UserRole.ADMIN).size();
        long totalDoctors = doctorRepository.findByActiveTrue().size();
      
//        		userrepo.findByRole("Doctor").size();
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("totalDoctors", totalDoctors);
        
        

        String fullName = loggedUser.getName();

        model.addAttribute("name", fullName);

        model.addAttribute("initials",
                getInitials(fullName));

        model.addAttribute("active", "dashboard");
        model.addAttribute("pageTitle", "Dashboard");

        return "admin";
    }



    @GetMapping("/users")
    public String allUsers(Model model,
                           HttpSession session,
                           @RequestParam(defaultValue = "0") int page) {

        User loggedUser =
                (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        int safePage = Math.max(page, 0);

        Page<User> userPage = userrepo.findAll(
                PageRequest.of(
                        safePage,
                        10,
                        Sort.by(Sort.Direction.DESC, "id")
                )
        );

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("userPage", userPage);

        model.addAttribute("name",
                loggedUser.getName());

        model.addAttribute("initials",
                getInitials(loggedUser.getName()));

        model.addAttribute("active", "users");
        model.addAttribute("pageTitle", "Manage Users");

        return "Alladmin/users";
    }
    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable int id,
                           Model model,
                           HttpSession session) {

        User loggedUser =
                (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {

            return "redirect:/login";
        }

        User user = userrepo.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin/users";
        }

        if (user.getRole() == UserRole.DOCTOR) {

            model.addAttribute("doctor",doctorRepository.findByUser(user));

            model.addAttribute("user", user);

            model.addAttribute("name", loggedUser.getName());

            model.addAttribute("initials", getInitials(loggedUser.getName()));

            model.addAttribute("pageTitle", "Edit User");

            model.addAttribute("active", "users");

            return "Alladmin/editdoctor";
        }

        model.addAttribute("user", user);

        model.addAttribute("name", loggedUser.getName());

        model.addAttribute("initials", getInitials(loggedUser.getName()));

        model.addAttribute("active", "users");

        model.addAttribute("pageTitle", "Edit User");

        return "Alladmin/edituser";
    }

    @PostMapping("/updateuser")
    public String updateuser(@RequestParam int id,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam UserRole role,
                             @RequestParam String phoneNumber) {

        User usr = userrepo.findById(id).orElse(null);
        Doctor doctor=doctorRepository.findByEmail(usr.getEmail());
        
       

        if (usr != null) {

            usr.setEmail(email);

            usr.setName(name);

            usr.setPhoneNumber(phoneNumber);

            usr.setRole(role);

            userrepo.save(usr);
        }
        
        if(doctor !=null) {
        	
        	doctor.setEmail(email);
        	doctor.setFullName(name);
        	doctor.setPhoneNumber(phoneNumber);
        	doctorRepository.save(doctor);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/adduser")
    public String adduser(Model model,
                          HttpSession session) {

        User loggedUser =
                (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {

            return "redirect:/login";
        }

        model.addAttribute("name",
                loggedUser.getName());

        model.addAttribute("initials",
                getInitials(loggedUser.getName()));

        model.addAttribute("active", "users");
        model.addAttribute("pageTitle", "Add User");

        return "Alladmin/adduser";
    }

    @PostMapping("/saveuser")
    public String saveUser(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String phoneNumber,
                           @RequestParam UserRole role) {

        User user = new User();

        user.setName(name);

        user.setEmail(email);

        user.setPassword(
                encoder.encode(password));

        user.setPhoneNumber(phoneNumber);

        user.setRole(role);

        userrepo.save(user);

        return "redirect:/admin/users";
    }

    @GetMapping("/searchuser")
    public String searchUser(@RequestParam String keyword,
                             @RequestParam(defaultValue = "0") int page,
                             Model model,
                             HttpSession session) {

        User loggedUser =
                (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {

            return "redirect:/login";
        }

        int safePage = Math.max(page, 0);
        Page<User> userPage = userrepo.findByNameContainingOrEmailContaining(
                keyword, keyword, PageRequest.of(safePage, 10));

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("userPage", userPage);

        model.addAttribute("name",
                loggedUser.getName());

        model.addAttribute("initials",
                getInitials(loggedUser.getName()));

        model.addAttribute("active", "users");
        model.addAttribute("pageTitle", "Search Results");

        return "Alladmin/users";
    }

    @GetMapping("/reports")
    public String reports(Model model){
        model.addAttribute("active", "settings");
        model.addAttribute("pageTitle", "Reports");
        return "coming-soon.html";
    }

    @GetMapping("/settings")
    public String settings(Model model){
        model.addAttribute("active", "settings");
        model.addAttribute("pageTitle", "Settings");
        return "coming-soon.html";
    }
}