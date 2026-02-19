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
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        String filename = application.getStudent().getName() + "_" + application.getStudent().getFirstName()
                + "_CV.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(application.getCv());
    }

    @GetMapping("/coverLetter/{id}/download")
    public ResponseEntity<byte[]> downloadCoverLetter(@PathVariable Long id) {

        Application application = applicationService.getApplicationById(id);
        String filename = application.getStudent().getName() + "_" + application.getStudent().getFirstName()
                + "_LettreMotivation.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
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

    @GetMapping("/bulkDownload")
    public ResponseEntity<byte[]> bulkDownload(@RequestParam List<Long> ids) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        for (Long id : ids) {
            Application app = applicationService.getApplicationById(id);
            if (app == null)
                continue;

            String studentName = app.getStudent().getName() + "_" + app.getStudent().getFirstName();

            if (app.getCv() != null) {
                ZipEntry cvEntry = new ZipEntry(studentName + "_CV_" + id + ".pdf");
                zos.putNextEntry(cvEntry);
                zos.write(app.getCv());
                zos.closeEntry();
            }

            if (app.getCoverLetter() != null) {
                ZipEntry clEntry = new ZipEntry(studentName + "_LettreMotivation_" + id + ".pdf");
                zos.putNextEntry(clEntry);
                zos.write(app.getCoverLetter());
                zos.closeEntry();
            }
        }
        zos.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"documents_etudiants.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(baos.toByteArray());
    }

    @GetMapping("/bulkDownloadByStudents")
    public ResponseEntity<byte[]> bulkDownloadByStudents(@RequestParam List<Long> studentIds) throws IOException {
        List<Application> applications = applicationService.getApplicationsByStudentIds(studentIds);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        for (Application app : applications) {
            String studentName = app.getStudent().getName() + "_" + app.getStudent().getFirstName();
            Long id = app.getId();

            if (app.getCv() != null) {
                ZipEntry cvEntry = new ZipEntry(studentName + "/CV_" + id + ".pdf");
                zos.putNextEntry(cvEntry);
                zos.write(app.getCv());
                zos.closeEntry();
            }

            if (app.getCoverLetter() != null) {
                ZipEntry clEntry = new ZipEntry(studentName + "/LettreMotivation_" + id + ".pdf");
                zos.putNextEntry(clEntry);
                zos.write(app.getCoverLetter());
                zos.closeEntry();
            }
        }
        zos.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"documents_liste_etudiants.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(baos.toByteArray());
    }
}