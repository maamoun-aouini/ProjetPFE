package com.example.OnlineSellingApplicationBackend.Services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendPasswordResetOtp(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp +
                "\nThis OTP is valid for 5 minutes.");

        mailSender.send(message);
    }
    public void sendVerificationEmail(String email, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify Your Email Address");
        message.setText("Thank you for registering with our service!\n\n" +
                "Your verification code is: " + verificationCode + "\n\n" +
                "Please enter this code in the app to verify your email address.\n" +
                "This code is valid for 24 hours.");

        mailSender.send(message);
    }
}