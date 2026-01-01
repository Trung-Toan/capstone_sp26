package com.example.identity_service.service;

import com.example.identity_service.dto.request.user.UserCreateRequest;
import com.example.identity_service.dto.request.user.UserUpdateRequest;
import com.example.identity_service.dto.response.UserResponse;
import java.util.List;

public interface UserService {

    public UserResponse createUser(UserCreateRequest userCreateRequest);

    public List<UserResponse> getUsers();

    public UserResponse getUserById(String id);

    public UserResponse updateUser(String userId, UserUpdateRequest userUpdateRequest);

    public void deleteUser(String userId);
}
