package com.internship.management.dto.application;

import java.time.LocalDateTime;

public record NotificationDto(Long id, String message, LocalDateTime createdAt) {}
