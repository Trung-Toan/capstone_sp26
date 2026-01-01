package com.example.identity_service.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
     String password;
     String fullName;
     String email;
     String phone;
     String address;
    LocalDate dob;
}
