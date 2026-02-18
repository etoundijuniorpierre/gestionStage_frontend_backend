package com.internship.management.repositories;


import com.internship.management.entities.Notification;
import com.internship.management.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

     List<Notification> findByRecipientAndSeenFalse(Users recipient);

    Optional<Notification>  findByIdAndRecipientId(Long id, Long userId);
}
