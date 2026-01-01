package com.example.identity_service.service.impl;

import com.example.identity_service.entity.InvalidatedToken;
import com.example.identity_service.repository.InvalidatedTokenRepository;
import com.example.identity_service.service.InvalidatedTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvalidatedTokenServiceImpl implements InvalidatedTokenService {

    private final InvalidatedTokenRepository tokenRepository;

    @Override
    @Transactional
    public void invalidateToken(String tokenId, LocalDateTime expiryDate) {
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .tokenId(tokenId)
                .expiryDate(expiryDate)
                .build();

        tokenRepository.save(invalidatedToken);
    }

    @Override
    public boolean isTokenInvalidated(String tokenId) {
        return tokenRepository.existsByTokenId(tokenId);
    }

    @Override
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
