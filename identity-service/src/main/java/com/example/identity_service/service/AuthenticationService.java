package com.example.identity_service.service;

import com.example.identity_service.dto.request.auth.AuthenticationRequest;
import com.example.identity_service.dto.request.auth.IntrospectRequest;
import com.example.identity_service.dto.request.auth.RefreshTokenRequest;
import com.example.identity_service.dto.request.user.UserCreateRequest;
import com.example.identity_service.dto.response.AuthenticationResponse;
import com.example.identity_service.dto.response.IntrospectResponse;
import com.example.identity_service.dto.response.UserResponse;

public interface AuthenticationService {

    public UserResponse register(UserCreateRequest request);

    public AuthenticationResponse authenticate(AuthenticationRequest request);

    public AuthenticationResponse refreshToken(RefreshTokenRequest request);

    public IntrospectResponse introspect(IntrospectRequest request);


    public void logout(String token);


    public void forgotPassword(String email);


    public void resetPassword(String token, String newPassword);


    public void changePassword(String username, String oldPassword, String newPassword, String confirmPassword);

    public void verifyEmail(String token);

    public void resendVerificationEmail(String email);
}
