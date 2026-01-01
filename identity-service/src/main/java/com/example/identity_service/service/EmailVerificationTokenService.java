package com.example.identity_service.service;

import com.example.identity_service.entity.EmailVerificationToken;
import com.example.identity_service.entity.User;

public interface EmailVerificationTokenService {

    public String createToken(User user);

    public EmailVerificationToken validateToken(String token);

    public void markAsVerified(EmailVerificationToken token);

    public void deleteExpiredTokens();
}
