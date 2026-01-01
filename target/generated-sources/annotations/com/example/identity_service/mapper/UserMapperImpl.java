package com.example.identity_service.mapper;

import com.example.identity_service.dto.request.UserCreateRequest;
import com.example.identity_service.dto.request.UserUpdateRequest;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-01T21:57:15+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.userName( request.getUserName() );
        user.password( request.getPassword() );
        user.fullName( request.getFullName() );
        user.email( request.getEmail() );
        user.phone( request.getPhone() );
        user.address( request.getAddress() );
        user.dob( request.getDob() );

        return user.build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.id( user.getId() );
        userResponse.userName( user.getUserName() );
        userResponse.fullName( user.getFullName() );
        userResponse.email( user.getEmail() );
        userResponse.phone( user.getPhone() );
        userResponse.address( user.getAddress() );
        userResponse.dob( user.getDob() );
        userResponse.provider( user.getProvider() );

        return userResponse.build();
    }

    @Override
    public void updateUser(User user, UserUpdateRequest userUpdateRequest) {
        if ( userUpdateRequest == null ) {
            return;
        }

        if ( userUpdateRequest.getPassword() != null ) {
            user.setPassword( userUpdateRequest.getPassword() );
        }
        if ( userUpdateRequest.getFullName() != null ) {
            user.setFullName( userUpdateRequest.getFullName() );
        }
        if ( userUpdateRequest.getEmail() != null ) {
            user.setEmail( userUpdateRequest.getEmail() );
        }
        if ( userUpdateRequest.getPhone() != null ) {
            user.setPhone( userUpdateRequest.getPhone() );
        }
        if ( userUpdateRequest.getAddress() != null ) {
            user.setAddress( userUpdateRequest.getAddress() );
        }
        if ( userUpdateRequest.getDob() != null ) {
            user.setDob( userUpdateRequest.getDob() );
        }
    }
}
