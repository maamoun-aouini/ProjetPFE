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
        message.setSubject("Email Verification");
        message.setText("Your verification code is: " + verificationCode);
        mailSender.send(message);
    }

}
