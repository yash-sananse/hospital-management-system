package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.entite.User;

@Service
public interface UserService  {

	User findByEmail(String name);
	void sendChangePasswordOtp(String email);

	boolean verifyChangePasswordOtp(String email, String otp);

	void changePassword(String email, String newPassword);
}
