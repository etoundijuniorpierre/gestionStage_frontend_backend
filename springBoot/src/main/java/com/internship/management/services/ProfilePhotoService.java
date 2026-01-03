package com.internship.management.services;

import com.internship.management.entities.ProfilePhoto;
import com.internship.management.entities.Users;
import com.internship.management.exception.InvalidFileException;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.repositories.ProfilePhotoRepository;
import com.internship.management.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * Service de gestion des photos de profil des utilisateurs.
 * 
 * @author Backend Team
 * @version 2.0
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfilePhotoService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final ProfilePhotoRepository profilePhotoRepository;
    private final UsersRepository userRepository;

    /**
     * Upload ou met à jour la photo de profil d'un utilisateur.
     *
     * @param file  le fichier image
     * @param email l'email de l'utilisateur
     * @throws IOException               si erreur lors de la lecture du fichier
     * @throws ResourceNotFoundException si l'utilisateur n'existe pas
     * @throws InvalidFileException      si le fichier est invalide
     */
    @Transactional
    public void uploadOrUpdateLogo(MultipartFile file, String email) throws IOException {
        log.info("Uploading/updating profile photo for user: {}", email);

        // Validation
        validateFile(file);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Optional<ProfilePhoto> existingPhoto = profilePhotoRepository.findByUserEmail(email);
        ProfilePhoto profilePhoto;

        if (existingPhoto.isPresent()) {
            profilePhoto = existingPhoto.get();
            profilePhoto.setOriginalFileName(file.getOriginalFilename());
            profilePhoto.setFileType(file.getContentType());
            profilePhoto.setFileData(file.getBytes());
            profilePhoto.setUploadDate(LocalDateTime.now());
            log.debug("Updating existing profile photo for user: {}", email);
        } else {
            profilePhoto = new ProfilePhoto();
            profilePhoto.setOriginalFileName(file.getOriginalFilename());
            profilePhoto.setFileType(file.getContentType());
            profilePhoto.setFileData(file.getBytes());
            profilePhoto.setUser(user);
            log.debug("Creating new profile photo for user: {}", email);
        }

        profilePhotoRepository.save(profilePhoto);
        log.info("Profile photo saved successfully for user: {}", email);
    }

    /**
     * Valide le fichier uploadé.
     *
     * @param file le fichier à valider
     * @throws InvalidFileException si le fichier est invalide
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                    String.format("File size (%d bytes) exceeds maximum allowed size (%d bytes)",
                            file.getSize(), MAX_FILE_SIZE));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileException(
                    "Invalid file type. Allowed types: " + String.join(", ", ALLOWED_TYPES));
        }
    }

    /**
     * Récupère la photo de profil d'un utilisateur par son ID.
     *
     * @param userId l'ID de l'utilisateur
     * @return la photo de profil si elle existe
     */
    public Optional<ProfilePhoto> getLogoByUserId(Long userId) {
        log.debug("Fetching profile photo for user: {}", userId);
        return profilePhotoRepository.findByUserId(userId);
    }

    /**
     * Supprime la photo de profil d'un utilisateur.
     *
     * @param userId l'ID de l'utilisateur
     */
    @Transactional
    public void deleteLogoByUserId(Long userId) {
        log.warn("Deleting profile photo for user: {}", userId);
        profilePhotoRepository.deleteByUserId(userId);
    }
}