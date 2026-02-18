package com.internship.management.repositories;


import com.internship.management.entities.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, Long> {

    Optional<ProfilePhoto> findByUserEmail(String email);
    boolean existsByUserId(Long userId);
    Optional<ProfilePhoto> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
