package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entite.Doctor;
import com.example.demo.entite.DoctorOtp;
import com.example.demo.entite.User;
import com.example.demo.repo.DoctorOtpRepository;
import com.example.demo.repo.DoctorRepository;
import com.example.demo.repo.Userrepo;

@Service
public class DoctorServiceLmp implements DoctorService{
	
	@Autowired
	private DoctorRepository doctorRepository;
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private DoctorOtpRepository doctorOtpRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private Userrepo userrepo;
	
	
	private String generateOtp() {
        Random random = new Random();
        return String.valueOf(1000 + random.nextInt(9000));
    }

	@Override
	public void sendotp(String email) {
	   Doctor doctor = doctorRepository.findByEmail(email);

	    if (doctor == null) {
	        throw new RuntimeException("Doctor not found");
	    }
	    System.out.println("Email entered by user : " + email);
	    System.out.println("Email found in DB     : " + doctor.getEmail());

	    String otp = generateOtp();

	    DoctorOtp doctorOtp = new DoctorOtp();
	    doctorOtp.setDoctor(doctor);
	    doctorOtp.setOtp(otp);
	    doctorOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
	    doctorOtp.setVerified(false);

	    doctorOtpRepository.save(doctorOtp);

	    emailService.sendOtp(doctor.getEmail(), otp);


	}

	@Override
	public boolean verifyOtp(String email, String otp) {
	    Doctor doctor = doctorRepository.findByEmail(email);
	   
		
		 DoctorOtp doctorOtp =
		            doctorOtpRepository.findTopByDoctorIdOrderByIdDesc(doctor.getId());
		    System.out.println("DoctorOtp = " + doctorOtp);
		   

		    if (doctorOtp == null) {
		        return false;
		    }

		    if (doctorOtp.isVerified()) {
		        return false;
		    }

		    if (doctorOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
		        return false;
		    }

		    if (!doctorOtp.getOtp().equals(otp)) {
		        return false;
		    }

		    doctorOtp.setVerified(true);
		    doctorOtpRepository.save(doctorOtp);

		return true;
	}

	@Override
	public void updatePassword(String email, String newPassword) {
		Doctor doctor= doctorRepository.findByEmail(email);
		
		if(doctor == null) {
			  throw new RuntimeException("Doctor not found.");
		}
		
		User user= doctor.getUser();
		
	    String encodedPassword = passwordEncoder.encode(newPassword);

	    user.setPassword(encodedPassword);
	    
	    userrepo.save(user);
	}

	
	
	
}
