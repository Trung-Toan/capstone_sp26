package com.example.identity_service.service;

import java.time.LocalDateTime;

public interface InvalidatedTokenService {
    public void invalidateToken(String tokenId, LocalDateTime expiryDate);

    public boolean isTokenInvalidated(String tokenId);

    public void cleanupExpiredTokens();
}
