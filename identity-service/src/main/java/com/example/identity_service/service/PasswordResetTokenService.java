package com.example.identity_service.service;

import com.example.identity_service.entity.PasswordResetToken;
import com.example.identity_service.entity.User;

public interface PasswordResetTokenService {

    public String createToken(User user);

    public PasswordResetToken validateToken(String token);

    public void markAsUsed(PasswordResetToken token);

    public void deleteExpiredTokens();
}
