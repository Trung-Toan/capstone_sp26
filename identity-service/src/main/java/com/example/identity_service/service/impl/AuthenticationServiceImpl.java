package com.example.identity_service.service.impl;

import com.example.identity_service.dto.request.auth.AuthenticationRequest;
import com.example.identity_service.dto.request.IntrospectRequest;
import com.example.identity_service.dto.request.RefreshTokenRequest;
import com.example.identity_service.dto.request.UserCreateRequest;
import com.example.identity_service.dto.response.AuthenticationResponse;
import com.example.identity_service.dto.response.IntrospectResponse;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.repository.UserRepository;
import com.example.identity_service.security.JwtTokenProvider;
import com.example.identity_service.service.*;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final InvalidatedTokenService invalidatedTokenService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(UserCreateRequest request) {
        UserResponse userResponse = userService.createUser(request);
        otpService.generateAndSendOtp(request.getEmail());
        return userResponse;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Get user details
            User user = userRepository.findByUserName(request.getUsername())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            if (!user.isEmailVerified()) {
                throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
            }

            // Generate tokens
            List<String> roles = user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList());

            String accessToken = tokenProvider.generateTokenFromUsername(user.getUserName(), roles);
            String refreshToken = tokenProvider.generateRefreshToken(user.getUserName());

            return AuthenticationResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .type("Bearer")
                    .username(user.getUserName())
                    .email(user.getEmail())
                    .build();

        } catch (AuthenticationException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Generate new tokens
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        String newAccessToken = tokenProvider.generateTokenFromUsername(user.getUserName(), roles);
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getUserName());

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .type("Bearer")
                .username(user.getUserName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        String token = request.getToken();
        boolean isValid = tokenProvider.validateToken(token);

        if (!isValid) {
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }

        try {
            Claims claims = tokenProvider.getClaimsFromToken(token);
            String username = claims.getSubject();
            
            User user = userRepository.findByUserName(username).orElse(null);

            return IntrospectResponse.builder()
                    .valid(true)
                    .username(username)
                    .email(user != null ? user.getEmail() : null)
                    .build();
        } catch (Exception e) {
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }
    }

    @Override
    public void logout(String token) {
        try {
            // Extract JWT ID and expiration
            String jwtId = tokenProvider.getJwtIdFromToken(token);
            java.util.Date expiration = tokenProvider.getExpirationFromToken(token);
            
            // Convert to LocalDateTime
            java.time.LocalDateTime expiryDate = expiration.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
            
            // Add to blacklist
            invalidatedTokenService.invalidateToken(jwtId, expiryDate);
        } catch (Exception e) {
            // If token is invalid, just ignore
        }
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Generate password reset token
        String token = passwordResetTokenService.createToken(user);

        // Send reset email
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), token);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // Validate token
        com.example.identity_service.entity.PasswordResetToken resetToken = 
                passwordResetTokenService.validateToken(token);

        User user = resetToken.getUser();

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        passwordResetTokenService.markAsUsed(resetToken);

        // Send confirmation email
        emailService.sendPasswordChangedNotification(user.getEmail(), user.getFullName());
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword, String confirmPassword) {
        // Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Get user
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_OLD_PASSWORD);
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Send notification email
        emailService.sendPasswordChangedNotification(user.getEmail(), user.getFullName());
    }

    @Override
    public void verifyEmail(String token) {
        // Validate token
        com.example.identity_service.entity.EmailVerificationToken verificationToken = 
                emailVerificationTokenService.validateToken(token);

        User user = verificationToken.getUser();

        // Mark email as verified
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        // Mark token as verified
        emailVerificationTokenService.markAsVerified(verificationToken);
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Check if already verified
        if (user.isEmailVerified()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        // Generate new verification token
        String token = emailVerificationTokenService.createToken(user);

        // Send verification email
        emailService.sendEmailVerification(user.getEmail(), user.getFullName(), token);
    }
}
