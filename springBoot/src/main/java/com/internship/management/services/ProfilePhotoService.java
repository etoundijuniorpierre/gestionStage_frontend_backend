package com.internship.management.services;

import com.internship.management.entities.ProfilePhoto;
import com.internship.management.entities.Users;
import com.internship.management.repositories.ProfilePhotoRepository;
import com.internship.management.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfilePhotoService {

    private final ProfilePhotoRepository profilePhotoRepository;
    private final UsersRepository userRepository;


    public void uploadOrUpdateLogo(MultipartFile file, String email) throws IOException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found"));


        if (file.isEmpty()) {
            throw new RuntimeException("empty file");
        }

        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new RuntimeException("Images are authorised only");
        }

        Optional<ProfilePhoto> existingLogo = profilePhotoRepository.findByUserEmail(email);
        ProfilePhoto profilePhoto;

        if (existingLogo.isPresent()) {

            profilePhoto = existingLogo.get();
            profilePhoto.setOriginalFileName(file.getOriginalFilename());
            profilePhoto.setFileType(file.getContentType());
            profilePhoto.setFileData(file.getBytes());
            profilePhoto.setUploadDate(LocalDateTime.now());
        } else {
            profilePhoto = new ProfilePhoto();

            profilePhoto.setOriginalFileName(file.getOriginalFilename());
            profilePhoto.setFileType(file.getContentType());
            profilePhoto.setFileData(file.getBytes());
            profilePhoto.setUser(user);
        }

        profilePhotoRepository.save(profilePhoto);
    }

    public Optional<ProfilePhoto> getLogoByUserId(Long userId) {
        return profilePhotoRepository.findByUserId(userId);
    }

    public void deleteLogoByUserId(Long userId) {
        profilePhotoRepository.deleteByUserId(userId);
    }
}