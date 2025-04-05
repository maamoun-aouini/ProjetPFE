package com.example.OnlineSellingApplicationBackend.Services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private static final long OTP_VALID_DURATION = 5 * 60 * 1000; // 5 minutes

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp + "|" + (System.currentTimeMillis() + OTP_VALID_DURATION));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String storedOtpData = otpStorage.get(email);
        if (storedOtpData == null) return false;

        String[] parts = storedOtpData.split("\\|");
        if (parts.length != 2) return false;

        String storedOtp = parts[0];
        long expirationTime = Long.parseLong(parts[1]);

        // Clean up expired OTP
        if (System.currentTimeMillis() > expirationTime) {
            otpStorage.remove(email);
            return false;
        }

        return storedOtp.equals(otp);
    }
}