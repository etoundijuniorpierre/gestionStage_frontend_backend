package com.internship.management.repositories;

import com.internship.management.entities.Users;
import com.internship.management.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByUser(Users user);

    void deleteByUser(Users user);
}
