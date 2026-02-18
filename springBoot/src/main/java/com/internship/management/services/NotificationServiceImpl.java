package com.internship.management.services;

import com.internship.management.entities.*;
import com.internship.management.interfaces.NotificationInterface;
import com.internship.management.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationInterface {

     private final NotificationRepository notificationRepository;
     private final SimpMessagingTemplate messagingTemplate;

     public void sendNotification(Users user, String message) {

          Notification notif = new Notification();
          notif.setRecipient(user);
          notif.setMessage(message);
          notificationRepository.save(notif);

          if(user instanceof Enterprise){

               messagingTemplate.convertAndSend(
                       "/topic/enterprise/" + user.getId(),
                       Map.of("content", message)
               );

               log.info("Sending notification to enterprise ID {} with message: {}", user.getId(), message);
          }

          if(user instanceof Teacher){
               String department =  ((Teacher) user).getDepartment();

               messagingTemplate.convertAndSend(
                       "/topic/department/" + department,
                       Map.of("content", message)
               );

               log.info("Sending notification to department {} with message: {}", department, message);
          }

          if(user instanceof Student){

               String department =  ((Student) user).getDepartment();

               messagingTemplate.convertAndSend(
                       "/topic/student/" + department,
                       Map.of("content", message)
               );

               log.info("Sending notification to Student {} department with message: {}", department, message);
          }

     }

     public List<Notification> getAllUnSeenNotificationsByUser(Users user) {
          return notificationRepository.findByRecipientAndSeenFalse(user);
     }

     public void markAsSeen(Long id, Users user) {

          Notification notif = notificationRepository.findByIdAndRecipientId(id, user.getId())
                  .orElseThrow(() -> new RuntimeException("Notification not found or not owned by this user"));

          notif.setSeen(true);
          notificationRepository.save(notif);
     }

}
