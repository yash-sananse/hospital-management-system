package com.example.demo.service;

public interface DoctorService {

	public void sendotp(String email);
	
	boolean verifyOtp(String email, String otp);
	public void updatePassword(String email, String newPassword);
}
