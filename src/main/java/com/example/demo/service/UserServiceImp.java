package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entite.User;
import com.example.demo.repo.Userrepo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImp implements UserService{

	@Autowired
	private Userrepo userrepo;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;



	Map<String, String> otpStorage = new HashMap<String, String>();
	
	@Override
	public User findByEmail(String name) {
		User userName= userrepo.findByEmail(name);
		return userName;
	}

	@Override
	public void sendChangePasswordOtp(String email) {

		String otp = String.valueOf(new Random().nextInt(9000));
		otpStorage.put(email, otp);
		emailService.sendOtp(email, otp);

	}

	@Override
	public boolean verifyChangePasswordOtp(String email, String otp) {

		String savedOtp = otpStorage.get(email);
		// Check OTP
		 if (savedOtp != null && savedOtp.equals(otp)) {
			 // Remove OTP after successful verification
			 otpStorage.remove(email);
			 return true;
		 }
		return false;
	}

	@Override
	public void changePassword(String email, String newPassword) {

		User user= userrepo.findByEmail(email);
		user. setPassword(passwordEncoder.encode(newPassword));
		userrepo.save(user);

	}

}
