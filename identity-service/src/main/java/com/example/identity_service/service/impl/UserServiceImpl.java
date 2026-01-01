package com.example.identity_service.service.impl;

import com.example.identity_service.dto.request.user.UserCreateRequest;
import com.example.identity_service.dto.request.user.UserUpdateRequest;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.entity.AuthProvider;
import com.example.identity_service.entity.Role;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.mapper.UserMapper;
import com.example.identity_service.repository.RoleRepository;
import com.example.identity_service.repository.UserRepository;
import com.example.identity_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        // Check if username already exists
        if(userRepository.existsByUserName(userCreateRequest.getUserName()))
            throw new AppException(ErrorCode.USER_EXISTED);

        if (userRepository.existsByEmail(userCreateRequest.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        // Map DTO to Entity
        User user = userMapper.toUser(userCreateRequest);

        // Encode password
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));

        // Set provider as LOCAL
        user.setProvider(AuthProvider.LOCAL);

        // Assign default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // Save user
        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(String userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(user, userUpdateRequest);
        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }
}
