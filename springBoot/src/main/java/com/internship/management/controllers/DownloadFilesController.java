package com.internship.management.controllers;

import com.internship.management.entities.Application;
import com.internship.management.entities.Convention;
import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Logo;
import com.internship.management.interfaces.PostOffer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/files")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Download Controller", description = "Endpoints pour le téléchargement de documents et logos")
public class DownloadFilesController {

    private final PostOffer postOffer;

    @Operation(summary = "Télécharger une convention de stage")
    @GetMapping("/convention/{id}")
    public ResponseEntity<byte[]> downloadConvention(@PathVariable Long id) {
        Convention convention = postOffer.getConventionByOfferId(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=convention.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(convention.getPdfConvention());
    }

    @Operation(summary = "Télécharger le CV d'un étudiant")
    @GetMapping("/cv/{id}")
    public ResponseEntity<byte[]> downloadCV(@PathVariable Long id) {
        Application application = postOffer.getApplicationById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_CV.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(application.getCv());
    }

    @Operation(summary = "Télécharger la lettre de motivation d'un étudiant")
    @GetMapping("/cover-letter/{id}")
    public ResponseEntity<byte[]> downloadCoverLetter(@PathVariable Long id) {
        Application application = postOffer.getApplicationById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_coverLetter.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(application.getCoverLetter());
    }

    @Operation(summary = "Récupérer le logo de l'entreprise")
    @GetMapping("/enterprise-logo")
    public ResponseEntity<byte[]> getEnterpriseLogo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = postOffer.getByEnterpriseEmail(email);
        Logo logo = postOffer.getLogoByEnterprise(enterprise);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(logo.getContentType()));
        return new ResponseEntity<>(logo.getLogo(), headers, HttpStatus.OK);
    }
}
