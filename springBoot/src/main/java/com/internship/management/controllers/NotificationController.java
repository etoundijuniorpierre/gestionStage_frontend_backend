package com.internship.management.controllers;

import com.internship.management.dto.application.NotificationDto;
import com.internship.management.dto.response.ApiResponse;
import com.internship.management.entities.Notification;
import com.internship.management.entities.Users;
import com.internship.management.interfaces.NotificationInterface;
import com.internship.management.interfaces.PostOffer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Notification Controller", description = "Gestion des notifications utilisateur")
public class NotificationController {

    private final NotificationInterface notificationInterface;
    private final PostOffer postOffer;

    @Operation(summary = "Récupérer les notifications non lues")
    @GetMapping("/unseen")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnseenNotifications() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = postOffer.getUserByEmail(email);

        List<Notification> unseen = notificationInterface.getAllUnSeenNotificationsByUser(user);
        List<NotificationDto> dtos = unseen.stream()
                .map(n -> new NotificationDto(n.getId(), n.getMessage(), n.getCreatedAt()))
                .toList();

        return ResponseEntity.ok(ApiResponse.success("Unseen notifications retrieved", dtos));
    }

    @Operation(summary = "Marquer une notification comme lue")
    @PutMapping("/{id}/seen")
    public ResponseEntity<ApiResponse<String>> markAsSeen(@PathVariable Long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = postOffer.getUserByEmail(email);

        notificationInterface.markAsSeen(id, user);

        return ResponseEntity.ok(ApiResponse.success("Notification marked as seen"));
    }
}
