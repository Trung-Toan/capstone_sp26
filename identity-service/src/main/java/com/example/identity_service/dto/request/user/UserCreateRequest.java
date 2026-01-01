package com.example.identity_service.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
     String id;

    @Size(min = 6, message = "USERNAME_INVALID")
    private String userName;

    @Size(min = 8, message = "PASSWORD_INVALID")
     String password;
     String fullName;

    @Email
     String email;
     String phone;
     String address;
     LocalDate dob;
}
