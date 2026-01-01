package com.example.identity_service.service;

public interface OtpService {
    long OTP_EXPIRATION_MS = 300000;

    public void generateAndSendOtp(String email);

    public void verifyOtp(String email, String otp);
}
