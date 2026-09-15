package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("cityhospitalproject2026@gmail.com");
        message.setTo(toEmail);
        message.setSubject("City Hospital - Password Reset OTP");
        message.setText("Your OTP is: " + otp + "\n\nThis OTP is valid for 5 minutes.");

        mailSender.send(message);
    }
}
