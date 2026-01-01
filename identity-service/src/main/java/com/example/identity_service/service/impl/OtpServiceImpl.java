package com.example.identity_service.service.impl;

import com.example.identity_service.entity.EmailVerificationToken;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.repository.EmailVerificationTokenRepository;
import com.example.identity_service.repository.UserRepository;
import com.example.identity_service.service.EmailService;
import com.example.identity_service.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void generateAndSendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        // Generate 6 digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Remove existing token
        tokenRepository.deleteByUser(user);
        
        // Save new OTP
        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(otp)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();
        
        tokenRepository.save(token);
        
        // Send email
        emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otp);
    }

    @Override
    @Transactional
    public void verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        EmailVerificationToken token = tokenRepository.findByUserAndVerifiedFalseAndExpiryDateAfter(user, LocalDateTime.now())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN));
        
        if (!token.getToken().equals(otp)) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }
        
        // Mark as verified
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Consume token
        token.setVerified(true);
        tokenRepository.save(token);
    }
}
