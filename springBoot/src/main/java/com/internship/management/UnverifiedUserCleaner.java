package com.internship.management;

import com.internship.management.entities.Users;
import com.internship.management.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UnverifiedUserCleaner {

    private final UsersRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *") // All the days at midnight
    public void purgeUnverifiedUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        List<Users> toDelete = userRepository.findAllByEmailVerifiedFalseAndCreatedAtBefore(cutoff);

        if (!toDelete.isEmpty()) {
            userRepository.deleteAll(toDelete);
            System.out.println("cleaning : " + toDelete.size() + " deleted user(s)  unverified.");
        }
    }
}
