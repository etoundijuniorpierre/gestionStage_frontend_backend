package com.internship.management.interfaces;

import com.internship.management.entities.Notification;
import com.internship.management.entities.Users;

import java.util.List;

public interface NotificationInterface {

    void sendNotification(Users user, String message);

    List<Notification> getAllUnSeenNotificationsByUser(Users user);

    void markAsSeen(Long id, Users user);
}
