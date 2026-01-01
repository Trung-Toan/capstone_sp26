package com.example.identity_service.controller;

import com.example.identity_service.dto.request.ApiResponse;
import com.example.identity_service.dto.request.user.UserUpdateRequest;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        ApiResponse<List<UserResponse>> response = new ApiResponse<>();
        response.setResult(service.getUsers());
        return response;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> getUserById(@PathVariable String userId) {
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(service.getUserById(userId));
        return response;
    }

    @PutMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(service.updateUser(userId, userUpdateRequest));
        return response;
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteUser(@PathVariable String userId) {
        service.deleteUser(userId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("User deleted successfully");
        return response;
    }
}
