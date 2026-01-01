package com.example.identity_service.repository;

import com.example.identity_service.entity.EmailVerificationToken;
import com.example.identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByToken(String token);
    
    Optional<EmailVerificationToken> findByUserAndVerifiedFalseAndExpiryDateAfter(User user, LocalDateTime now);
    
    void deleteByExpiryDateBefore(LocalDateTime now);
    
    void deleteByUser(User user);
}
