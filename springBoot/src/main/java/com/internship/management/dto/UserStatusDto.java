package com.internship.management.dto;

import com.internship.management.enums.UserStatus;

public record UserStatusDto(
    String email,
    UserStatus status,
    String message
) {}
