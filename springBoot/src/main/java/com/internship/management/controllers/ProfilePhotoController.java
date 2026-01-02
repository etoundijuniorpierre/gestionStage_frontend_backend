package com.internship.management.controllers;

import com.internship.management.dto.response.ApiResponse;
import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Logo;
import com.internship.management.interfaces.PostOffer;
import com.internship.management.services.ProfilePhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(path = "/api/profile/photo")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN', 'ENTERPRISE')")
@Tag(name = "Profile Photo Controller", description = "Gestion des photos de profil et logos")
public class ProfilePhotoController {

    private final ProfilePhotoService profilePhotoService;
    private final PostOffer postOffer;

    @Operation(summary = "Uploader ou mettre à jour la photo de profil")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadProfilePhoto(
            @RequestParam("photo") MultipartFile photo) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            profilePhotoService.uploadOrUpdateLogo(photo, email);
            log.info("Photo uploaded successfully for user: {}", email);
            return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully"));
        } catch (Exception e) {
            log.error("Error uploading photo", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer le logo de l'entreprise (alias)")
    @GetMapping("/logo")
    public ResponseEntity<byte[]> getEnterpriseLogo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = postOffer.getByEnterpriseEmail(email);
        Logo logo = postOffer.getLogoByEnterprise(enterprise);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(logo.getContentType()));
        return new ResponseEntity<>(logo.getLogo(), headers, HttpStatus.OK);
    }
}
