package com.example.identity_service.service.impl;

import com.example.identity_service.entity.EmailVerificationToken;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.repository.EmailVerificationTokenRepository;
import com.example.identity_service.service.EmailVerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {

    private final EmailVerificationTokenRepository tokenRepository;

    @Value("${app.token.email-verification-expiration}")
    private long expirationTime;

    @Override
    @Transactional
    public String createToken(User user) {
        // Delete any existing tokens for this user
        tokenRepository.deleteByUser(user);

        // Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(expirationTime / 1000);

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .verified(false)
                .build();

        tokenRepository.save(verificationToken);
        return token;
    }

    @Override
    public EmailVerificationToken validateToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN));

        if (verificationToken.isExpired()) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }

        if (verificationToken.isVerified()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        return verificationToken;
    }

    @Override
    @Transactional
    public void markAsVerified(EmailVerificationToken token) {
        token.setVerified(true);
        tokenRepository.save(token);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
