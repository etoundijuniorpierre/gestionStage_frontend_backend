package com.internship.management.controllers;

import com.internship.management.dto.application.NotificationDto;
import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Notification;
import com.internship.management.entities.Users;
import com.internship.management.interfaces.NotificationInterface;
import com.internship.management.interfaces.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/getNotifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class NotificationController {

    private final NotificationInterface notificationInterface;
    private final UserService userService;

    @GetMapping("/getUnseenNotifications")
    public ResponseEntity<List<NotificationDto>> getUnseenNotifications() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.getUserByEmail(email);

        List<Notification> unseen = notificationInterface.getAllUnSeenNotificationsByUser(user);

        return ResponseEntity.ok(
                unseen.stream()
                        .map(n -> new NotificationDto(n.getId(), n.getMessage(), n.getCreatedAt()))
                        .toList());
    }

    @PutMapping("/userNotifications/{id}/seen")
    public ResponseEntity<String> markAsSeen(@PathVariable Long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.getUserByEmail(email);

        notificationInterface.markAsSeen(id, user);

        return ResponseEntity.ok("Notification marked as seen");
    }

}
