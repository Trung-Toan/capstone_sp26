package com.example.identity_service.controller;

import com.example.identity_service.dto.request.*;
import com.example.identity_service.dto.request.auth.*;
import com.example.identity_service.dto.response.AuthenticationResponse;
import com.example.identity_service.dto.response.IntrospectResponse;
import com.example.identity_service.dto.response.MessageResponse;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.service.AuthenticationService;
import com.example.identity_service.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final OtpService otpService;


    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody @Valid UserCreateRequest request) {
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(authenticationService.register(request));
        response.setMessage("User registered successfully");
        return response;
    }


    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
        response.setResult(authenticationService.authenticate(request));
        response.setMessage("Login successful");
        return response;
    }


    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
        response.setResult(authenticationService.refreshToken(request));
        response.setMessage("Token refreshed successfully");
        return response;
    }


    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid IntrospectRequest request) {
        ApiResponse<IntrospectResponse> response = new ApiResponse<>();
        response.setResult(authenticationService.introspect(request));
        return response;
    }


    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestBody @Valid LogoutRequest request) {
        authenticationService.logout(request.getToken());
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Logout successful");
        response.setResult("Token has been invalidated");
        return response;
    }

    @PostMapping("/forgot-password")
    public ApiResponse<MessageResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request.getEmail());
        ApiResponse<MessageResponse> response = new ApiResponse<>();
        MessageResponse messageResponse = MessageResponse.builder()
                .message("Password reset email sent successfully")
                .success(true)
                .build();
        response.setResult(messageResponse);
        response.setMessage("Please check your email");
        return response;
    }

    @PostMapping("/reset-password")
    public ApiResponse<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request.getToken(), request.getNewPassword());
        ApiResponse<MessageResponse> response = new ApiResponse<>();
        MessageResponse messageResponse = MessageResponse.builder()
                .message("Password has been reset successfully")
                .success(true)
                .build();
        response.setResult(messageResponse);
        response.setMessage("Password reset successful");
        return response;
    }

    @PostMapping("/change-password")
    public ApiResponse<MessageResponse> changePassword(
            Authentication authentication,
            @RequestBody @Valid ChangePasswordRequest request) {
        String username = authentication.getName();
        authenticationService.changePassword(
                username,
                request.getOldPassword(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );
        ApiResponse<MessageResponse> response = new ApiResponse<>();
        MessageResponse messageResponse = MessageResponse.builder()
                .message("Password changed successfully")
                .success(true)
                .build();
        response.setResult(messageResponse);
        response.setMessage("Password updated");
        return response;
    }

    @PostMapping("/verify-email")
    public ApiResponse<MessageResponse> verifyEmail(@RequestBody @Valid VerifyEmailRequest request) {
        authenticationService.verifyEmail(request.getToken());
        ApiResponse<MessageResponse> response = new ApiResponse<>();
        MessageResponse messageResponse = MessageResponse.builder()
                .message("Email verified successfully")
                .success(true)
                .build();
        response.setResult(messageResponse);
        response.setMessage("Email verification successful");
        return response;
    }

    @PostMapping("/resend-verification")
    public ApiResponse<MessageResponse> resendVerification(@RequestBody @Valid ResendVerificationEmailRequest request) {
        authenticationService.resendVerificationEmail(request.getEmail());
        ApiResponse<MessageResponse> response = new ApiResponse<>();
        MessageResponse messageResponse = MessageResponse.builder()
                .message("Verification email sent successfully")
                .success(true)
                .build();
        response.setResult(messageResponse);
        response.setMessage("Please check your email");
        return response;
    }

    @PostMapping("/send-otp")
    public ApiResponse<MessageResponse> sendOtp(@RequestBody @Valid SendOtpRequest request) {
        otpService.generateAndSendOtp(request.getEmail());
        ApiResponse<MessageResponse> response = new ApiResponse<>();
        MessageResponse messageResponse = MessageResponse.builder()
                .message("OTP sent successfully")
                .success(true)
                .build();
        response.setResult(messageResponse);
        response.setMessage("Please check your email for OTP code");
        return response;
    }

    @PostMapping("/verify-otp")
    public ApiResponse<MessageResponse> verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        otpService.verifyOtp(request.getEmail(), request.getOtp());
        ApiResponse<MessageResponse> response = new ApiResponse<>();
        MessageResponse messageResponse = MessageResponse.builder()
                .message("Email verified successfully")
                .success(true)
                .build();
        response.setResult(messageResponse);
        response.setMessage("OTP verification successful");
        return response;
    }
}
