package com.internship.management.services;

import com.internship.management.entities.*;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.interfaces.NotificationInterface;
import com.internship.management.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service de gestion des notifications en temps réel.
 * 
 * @author Backend Team
 * @version 2.0
 * @since 2026-01-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationInterface {

     private final NotificationRepository notificationRepository;
     private final SimpMessagingTemplate messagingTemplate;

     @Transactional
     public void sendNotification(Users user, String message) {
          try {
               // Sauvegarder la notification en base de données
               Notification notif = new Notification();
               notif.setRecipient(user);
               notif.setMessage(message);
               notificationRepository.save(notif);

               // Envoyer via WebSocket
               sendWebSocketNotification(user, message);

          } catch (Exception e) {
               log.error("Failed to send notification to user {}: {}", user.getId(), e.getMessage(), e);
               // Ne pas propager l'exception pour ne pas bloquer le flux principal
          }
     }

     /**
      * Envoie une notification via WebSocket selon le type d'utilisateur.
      */
     private void sendWebSocketNotification(Users user, String message) {
          String destination = determineDestination(user);
          messagingTemplate.convertAndSend(destination, Map.of("content", message));
          log.info("WebSocket notification sent to {}", destination);
     }

     /**
      * Détermine la destination WebSocket selon le type d'utilisateur.
      */
     private String determineDestination(Users user) {
          if (user instanceof Enterprise) {
               return "/topic/enterprise/" + user.getId();
          } else if (user instanceof Teacher) {
               return "/topic/department/" + ((Teacher) user).getDepartment();
          } else if (user instanceof Student) {
               return "/topic/student/" + ((Student) user).getDepartment();
          }
          throw new IllegalArgumentException("Unknown user type: " + user.getClass().getName());
     }

     public List<Notification> getAllUnSeenNotificationsByUser(Users user) {
          return notificationRepository.findByRecipientAndSeenFalse(user);
     }

     @Transactional
     public void markAsSeen(Long id, Users user) {
          log.debug("Marking notification {} as seen for user {}", id, user.getId());

          Notification notif = notificationRepository.findByIdAndRecipientId(id, user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));

          notif.setSeen(true);
          notificationRepository.save(notif);
     }

}
