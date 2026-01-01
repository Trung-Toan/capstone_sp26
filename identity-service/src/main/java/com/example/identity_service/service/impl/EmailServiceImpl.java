package com.example.identity_service.service.impl;

import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String userName, String token) {
        try {
            String resetUrl = frontendUrl + "/reset-password?token=" + token;
            
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("resetUrl", resetUrl);
            context.setVariable("expiryTime", "1 hour");
            
            String htmlContent = templateEngine.process("password-reset-email", context);
            
            sendHtmlEmail(to, "Reset Your Password", htmlContent);
            
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    @Override
    @Async
    public void sendEmailVerification(String to, String userName, String token) {
        try {
            String verifyUrl = frontendUrl + "/verify-email?token=" + token;
            
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("verifyUrl", verifyUrl);
            context.setVariable("expiryTime", "24 hours");
            
            String htmlContent = templateEngine.process("email-verification", context);
            
            sendHtmlEmail(to, "Verify Your Email Address", htmlContent);
            
            log.info("Email verification sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email verification to: {}", to, e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    @Override
    @Async
    public void sendPasswordChangedNotification(String to, String userName) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("supportEmail", fromEmail);
            
            String htmlContent = templateEngine.process("password-changed-notification", context);
            
            sendHtmlEmail(to, "Password Changed Successfully", htmlContent);
            
            log.info("Password changed notification sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password changed notification to: {}", to, e);
            // Don't throw exception for notification emails
        }
    }

    @Override
    @Async
    public void sendOtpEmail(String to, String userName, String otpCode) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("otpCode", otpCode);
            context.setVariable("expiryTime", "5 minutes");
            
            String htmlContent = templateEngine.process("otp-email", context);
            
            sendHtmlEmail(to, "Your Authentication Code", htmlContent);
            
            log.info("OTP email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", to, e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
}
