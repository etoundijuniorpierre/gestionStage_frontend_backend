package com.internship.management.controllers;

import com.internship.management.entities.Application;
import com.internship.management.entities.Convention;
import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Logo;
import com.internship.management.interfaces.ApplicationService;
import com.internship.management.interfaces.ConventionService;
import com.internship.management.interfaces.EnterpriseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping(path = "downloadFiles")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class DownloadFilesController {

    private final ConventionService conventionService;
    private final ApplicationService applicationService;
    private final EnterpriseService enterpriseService;

    @GetMapping("/downloadConvention/{id}")
    public ResponseEntity<byte[]> downloadConvention(@PathVariable Long id) {

        Convention convention = conventionService.getConventionByOfferId(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=convention.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(convention.getPdfConvention());
    }

    @GetMapping("/cv/{id}/download")
    public ResponseEntity<byte[]> downloadCV(@PathVariable Long id) {

        Application application = applicationService.getApplicationById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_CV.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(application.getCv());
    }

    @GetMapping("/coverLetter/{id}/download")
    public ResponseEntity<byte[]> downloadCoverLetter(@PathVariable Long id) {

        Application application = applicationService.getApplicationById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_coverLetter.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(application.getCoverLetter());
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