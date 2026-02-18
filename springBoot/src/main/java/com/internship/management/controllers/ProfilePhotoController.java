package com.internship.management.controllers;

import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Logo;
import com.internship.management.interfaces.EnterpriseService;
import com.internship.management.services.ProfilePhotoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "profilePhoto")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
public class ProfilePhotoController {

    private final ProfilePhotoService profilePhotoService;
    private final EnterpriseService enterpriseService;

    @PostMapping(value = "/upload-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestParam("photo") MultipartFile photo) {

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            profilePhotoService.uploadOrUpdateLogo(photo, email);
            return ResponseEntity.ok().body("Photo uploaded successfully");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getEnterpriseLogo")
    public ResponseEntity<byte[]> getEnterpriseLogo() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Enterprise enterprise = enterpriseService.getByEnterpriseEmail(email);

        Logo logo = enterpriseService.getLogoByEnterprise(enterprise);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(logo.getContentType()));

        return new ResponseEntity<>(logo.getLogo(), headers, HttpStatus.OK);
    }
}