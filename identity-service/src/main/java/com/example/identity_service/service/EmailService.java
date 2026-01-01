package com.example.identity_service.service;

public interface EmailService {

    void sendPasswordResetEmail(String to, String userName, String token);


    void sendEmailVerification(String to, String userName, String token);


    void sendPasswordChangedNotification(String to, String userName);


    void sendOtpEmail(String to, String userName, String otpCode);
}
