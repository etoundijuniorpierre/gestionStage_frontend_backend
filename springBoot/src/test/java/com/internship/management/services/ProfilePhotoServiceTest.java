package com.internship.management.services;

import com.internship.management.entities.ProfilePhoto;
import com.internship.management.entities.Users;
import com.internship.management.exception.InvalidFileException;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.repositories.ProfilePhotoRepository;
import com.internship.management.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour ProfilePhotoService.
 * Couverture: 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProfilePhotoService Tests")
class ProfilePhotoServiceTest {

    @Mock
    private ProfilePhotoRepository profilePhotoRepository;

    @Mock
    private UsersRepository userRepository;

    @InjectMocks
    private ProfilePhotoService profilePhotoService;

    private Users user;
    private ProfilePhoto profilePhoto;
    private MultipartFile validFile;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1L);
        user.setEmail("user@test.com");

        profilePhoto = new ProfilePhoto();
        profilePhoto.setId(1L);
        profilePhoto.setUser(user);

        validFile = mock(MultipartFile.class);
        when(validFile.isEmpty()).thenReturn(false);
        when(validFile.getSize()).thenReturn(1024L); // 1KB
        when(validFile.getContentType()).thenReturn("image/jpeg");
        when(validFile.getOriginalFilename()).thenReturn("photo.jpg");
    }

    @Test
    @DisplayName("Should upload new photo successfully")
    void testUploadOrUpdateLogo_NewPhoto() throws IOException {
        // Given
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(profilePhotoRepository.findByUserEmail("user@test.com")).thenReturn(Optional.empty());
        when(validFile.getBytes()).thenReturn("test".getBytes());

        // When
        profilePhotoService.uploadOrUpdateLogo(validFile, "user@test.com");

        // Then
        verify(profilePhotoRepository).save(any(ProfilePhoto.class));
    }

    @Test
    @DisplayName("Should update existing photo successfully")
    void testUploadOrUpdateLogo_UpdateExisting() throws IOException {
        // Given
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(profilePhotoRepository.findByUserEmail("user@test.com")).thenReturn(Optional.of(profilePhoto));
        when(validFile.getBytes()).thenReturn("test".getBytes());

        // When
        profilePhotoService.uploadOrUpdateLogo(validFile, "user@test.com");

        // Then
        verify(profilePhotoRepository).save(profilePhoto);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testUploadOrUpdateLogo_UserNotFound() {
        // Given
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> profilePhotoService.uploadOrUpdateLogo(validFile, "unknown@test.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(profilePhotoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when file is null")
    void testUploadOrUpdateLogo_NullFile() {
        // When & Then
        assertThatThrownBy(() -> profilePhotoService.uploadOrUpdateLogo(null, "user@test.com"))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("File is empty");
    }

    @Test
    @DisplayName("Should throw exception when file is empty")
    void testUploadOrUpdateLogo_EmptyFile() {
        // Given
        when(validFile.isEmpty()).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> profilePhotoService.uploadOrUpdateLogo(validFile, "user@test.com"))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("File is empty");
    }

    @Test
    @DisplayName("Should throw exception when file size exceeds limit")
    void testUploadOrUpdateLogo_FileTooLarge() {
        // Given
        when(validFile.getSize()).thenReturn(6 * 1024 * 1024L); // 6MB

        // When & Then
        assertThatThrownBy(() -> profilePhotoService.uploadOrUpdateLogo(validFile, "user@test.com"))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("exceeds maximum allowed size");
    }

    @Test
    @DisplayName("Should throw exception for invalid content type")
    void testUploadOrUpdateLogo_InvalidContentType() {
        // Given
        when(validFile.getContentType()).thenReturn("application/pdf");

        // When & Then
        assertThatThrownBy(() -> profilePhotoService.uploadOrUpdateLogo(validFile, "user@test.com"))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("Invalid file type");
    }

    @Test
    @DisplayName("Should throw exception when content type is null")
    void testUploadOrUpdateLogo_NullContentType() {
        // Given
        when(validFile.getContentType()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> profilePhotoService.uploadOrUpdateLogo(validFile, "user@test.com"))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("Invalid file type");
    }

    @Test
    @DisplayName("Should accept all valid image types")
    void testUploadOrUpdateLogo_AllValidTypes() throws IOException {
        // Given
        String[] validTypes = { "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp" };
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(profilePhotoRepository.findByUserEmail("user@test.com")).thenReturn(Optional.empty());
        when(validFile.getBytes()).thenReturn("test".getBytes());

        // When & Then
        for (String type : validTypes) {
            when(validFile.getContentType()).thenReturn(type);
            assertThatCode(() -> profilePhotoService.uploadOrUpdateLogo(validFile, "user@test.com"))
                    .doesNotThrowAnyException();
        }

        verify(profilePhotoRepository, times(validTypes.length)).save(any(ProfilePhoto.class));
    }

    @Test
    @DisplayName("Should get logo by user id successfully")
    void testGetLogoByUserId_Found() {
        // Given
        when(profilePhotoRepository.findByUserId(1L)).thenReturn(Optional.of(profilePhoto));

        // When
        Optional<ProfilePhoto> result = profilePhotoService.getLogoByUserId(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return empty when logo not found")
    void testGetLogoByUserId_NotFound() {
        // Given
        when(profilePhotoRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // When
        Optional<ProfilePhoto> result = profilePhotoService.getLogoByUserId(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should delete logo by user id successfully")
    void testDeleteLogoByUserId() {
        // When
        profilePhotoService.deleteLogoByUserId(1L);

        // Then
        verify(profilePhotoRepository).deleteByUserId(1L);
    }
}
