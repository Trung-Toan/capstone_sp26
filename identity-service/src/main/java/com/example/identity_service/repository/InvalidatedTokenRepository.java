package com.example.identity_service.repository;

import com.example.identity_service.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    Optional<InvalidatedToken> findByTokenId(String tokenId);
    
    boolean existsByTokenId(String tokenId);
    
    void deleteByExpiryDateBefore(LocalDateTime now);
}
