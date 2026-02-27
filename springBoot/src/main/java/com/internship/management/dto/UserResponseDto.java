package com.internship.management.dto;

import com.internship.management.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean emailVerified;
    private UserStatus status;
}
