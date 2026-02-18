package com.internship.management.repositories;


import com.internship.management.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface UsersRepository extends JpaRepository<Users, Long> {
       Optional<Users> findByEmail(String email);

       List<Users> findAllByEmailVerifiedFalseAndCreatedAtBefore(LocalDateTime cutoff);

       boolean existsByEmail(String email);




}
