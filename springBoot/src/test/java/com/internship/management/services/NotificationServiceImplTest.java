package com.internship.management.services;

import com.internship.management.entities.*;
import com.internship.management.interfaces.NotificationInterface;
import com.internship.management.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour NotificationServiceImpl.
 * Couverture: 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationServiceImpl Tests")
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Enterprise enterprise;
    private Teacher teacher;
    private Student student;
    private Notification notification;

    @BeforeEach
    void setUp() {
        enterprise = new Enterprise();
        enterprise.setId(1L);
        enterprise.setEmail("enterprise@test.com");

        teacher = new Teacher();
        teacher.setId(2L);
        teacher.setEmail("teacher@test.com");
        teacher.setDepartment("Computer Science");

        student = new Student();
        student.setId(3L);
        student.setEmail("student@test.com");
        student.setDepartment("Computer Science");

        notification = new Notification();
        notification.setId(1L);
        notification.setMessage("Test notification");
        notification.setSeen(false);
        notification.setRecipient(enterprise);
    }

    @Test
    @DisplayName("Should send notification to enterprise successfully")
    void testSendNotification_Enterprise() {
        // Given
        String message = "New application received";

        // When
        notificationService.sendNotification(enterprise, message);

        // Then
        verify(notificationRepository).save(any(Notification.class));
        verify(messagingTemplate).convertAndSend(
                eq("/topic/enterprise/" + enterprise.getId()),
                anyMap());
    }

    @Test
    @DisplayName("Should send notification to teacher successfully")
    void testSendNotification_Teacher() {
        // Given
        String message = "New offer to review";

        // When
        notificationService.sendNotification(teacher, message);

        // Then
        verify(notificationRepository).save(any(Notification.class));
        verify(messagingTemplate).convertAndSend(
                eq("/topic/department/" + teacher.getDepartment()),
                anyMap());
    }

    @Test
    @DisplayName("Should send notification to student successfully")
    void testSendNotification_Student() {
        // Given
        String message = "Application status updated";

        // When
        notificationService.sendNotification(student, message);

        // Then
        verify(notificationRepository).save(any(Notification.class));
        verify(messagingTemplate).convertAndSend(
                eq("/topic/student/" + student.getDepartment()),
                anyMap());
    }

    @Test
    @DisplayName("Should handle WebSocket error gracefully")
    void testSendNotification_WebSocketError() {
        // Given
        String message = "Test message";
        doThrow(new RuntimeException("WebSocket error"))
                .when(messagingTemplate).convertAndSend(anyString(), anyMap());

        // When
        notificationService.sendNotification(enterprise, message);

        // Then
        verify(notificationRepository).save(any(Notification.class));
        // Should not throw exception
    }

    @Test
    @DisplayName("Should get all unseen notifications for user")
    void testGetAllUnSeenNotificationsByUser() {
        // Given
        Notification notif1 = new Notification();
        notif1.setId(1L);
        notif1.setSeen(false);

        Notification notif2 = new Notification();
        notif2.setId(2L);
        notif2.setSeen(false);

        List<Notification> notifications = Arrays.asList(notif1, notif2);
        when(notificationRepository.findByRecipientAndSeenFalse(enterprise))
                .thenReturn(notifications);

        // When
        List<Notification> result = notificationService.getAllUnSeenNotificationsByUser(enterprise);

        // Then
        assertThat(result).hasSize(2);
        verify(notificationRepository).findByRecipientAndSeenFalse(enterprise);
    }

    @Test
    @DisplayName("Should mark notification as seen successfully")
    void testMarkAsSeen_Success() {
        // Given
        when(notificationRepository.findByIdAndRecipientId(1L, enterprise.getId()))
                .thenReturn(Optional.of(notification));

        // When
        notificationService.markAsSeen(1L, enterprise);

        // Then
        assertThat(notification.isSeen()).isTrue();
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("Should throw exception when notification not found")
    void testMarkAsSeen_NotFound() {
        // Given
        when(notificationRepository.findByIdAndRecipientId(999L, enterprise.getId()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificationService.markAsSeen(999L, enterprise))
                .isInstanceOf(com.internship.management.exception.ResourceNotFoundException.class);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when notification belongs to another user")
    void testMarkAsSeen_WrongUser() {
        // Given
        Users anotherUser = new Users();
        anotherUser.setId(999L);

        when(notificationRepository.findByIdAndRecipientId(1L, anotherUser.getId()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificationService.markAsSeen(1L, anotherUser))
                .isInstanceOf(com.internship.management.exception.ResourceNotFoundException.class);
    }
}
