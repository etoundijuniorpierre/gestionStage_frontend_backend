package com.internship.management.controllers;

import com.internship.management.dto.StudentResponseDto;
import com.internship.management.dto.application.ApplicationRequestDto;
import com.internship.management.dto.application.ApplicationResponseDto;
import com.internship.management.dto.postOffer.OfferResponseDto;
import com.internship.management.dto.profile.*;
import com.internship.management.dto.response.ApiResponse;
import com.internship.management.entities.*;
import com.internship.management.enums.ApplicationState;
import com.internship.management.enums.ConventionState;
import com.internship.management.enums.OfferStatus;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.interfaces.NotificationInterface;
import com.internship.management.interfaces.PostOffer;
import com.internship.management.mappers.PostOfferMapper;
import com.internship.management.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = "api/student")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Student Controller", description = "Endpoints pour les actions spécifiques aux étudiants")
public class StudentController {

    private final PostOffer postOffer;
    private final PostOfferMapper postOfferMapper;
    private final NotificationInterface notificationInterface;

    @Operation(summary = "Récupérer les offres approuvées pour le département de l'étudiant")
    @GetMapping("/offersByApprovedStatus")
    public ResponseEntity<ApiResponse<List<OfferResponseDto>>> getOfferByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = postOffer.getStudentByEmail(email);

        if (student.isOnInternship()) {
            return ResponseEntity.ok(ApiResponse.success("Student is on internship", List.of()));
        }

        Pageable pageable = PaginationUtil.createPageable(page, size, "createdAt");
        Page<Offer> offerPage = postOffer.getOffersByStatusAndConventionApprovedPaged(
                OfferStatus.APPROVED, ConventionState.APPROVED, student.getDepartment(), pageable);

        List<OfferResponseDto> dtos = postOfferMapper.toDtoList(offerPage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(offerPage);

        return ResponseEntity.ok(ApiResponse.success("Offers retrieved successfully", dtos, pageInfo));
    }

    @Operation(summary = "Récupérer les candidatures en attente ou rejetées de l'étudiant")
    @GetMapping("pendingApplicationsOfStudent")
    public ResponseEntity<ApiResponse<List<ApplicationResponseDto>>> getPendingApplicationsOfStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = postOffer.getStudentByEmail(email);

        if (student.isOnInternship()) {
            return ResponseEntity.ok(ApiResponse.success("Student is on internship", List.of()));
        }

        Pageable pageable = PaginationUtil.createPageable(page, size, "applicationDate");
        Page<Application> applicationPage = postOffer.getApplicationsRejectedOrPendingByStudentEmailPaged(email,
                pageable);

        List<ApplicationResponseDto> dtos = postOfferMapper.toDtoApplicationList(applicationPage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(applicationPage);

        return ResponseEntity.ok(ApiResponse.success("Pending applications retrieved successfully", dtos, pageInfo));
    }

    @Operation(summary = "Récupérer les candidatures approuvées de l'étudiant")
    @GetMapping("/applicationsApprovedOfStudent")
    public ResponseEntity<ApiResponse<List<ApplicationResponseDto>>> getApplicationsApprovedOfStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = postOffer.getStudentByEmail(email);

        Pageable pageable = PaginationUtil.createPageable(page, size, "applicationDate");
        Page<Application> applicationPage = postOffer.getApplicationsApprovedByStudentEmailPaged(email, pageable);

        List<Application> applications = applicationPage.getContent();
        Application chosenApplicationByStudent = postOffer.getApplicationByStudentOnInternshipTrue(student);

        if (student.isOnInternship()) {
            // Logic remains: if student is on internship, show only the current one
            // potentially,
            // but the original code returned empty list if on internship.
            // Let's keep consistency with original logic but improved.
            return ResponseEntity.ok(ApiResponse.success("Student is on internship", List.of()));
        }

        List<ApplicationResponseDto> dtos = postOfferMapper.toDtoApplicationList(applications);
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(applicationPage);

        return ResponseEntity.ok(ApiResponse.success("Approved applications retrieved successfully", dtos, pageInfo));
    }

    @Operation(summary = "Supprimer une candidature rejetée")
    @DeleteMapping("/{application_id}")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable("application_id") Long application_id) {
        log.info("Deleting application with id: {}", application_id);
        postOffer.deleteApplicationRejected(application_id);
        return ResponseEntity.ok(ApiResponse.success("Application deleted successfully", null));
    }

    @Operation(summary = "Créer une nouvelle candidature")
    @PostMapping("{offer_id}/createApplication")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> create(
            @Valid @ModelAttribute ApplicationRequestDto applicationRequestDto,
            @PathVariable Long offer_id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = postOffer.getStudentByEmail(email);

        if (student.isOnInternship()) {
            throw new IllegalStateException("You are already on internship and cannot apply anymore.");
        }

        Offer offer = postOffer.getOfferById(offer_id);

        // Check if already applied
        boolean alreadyApplied = student.getApplications().stream()
                .anyMatch(app -> app.getOffer().getId().equals(offer_id) &&
                        (app.getState() == ApplicationState.PENDING || app.getState() == ApplicationState.APPROVED));

        if (alreadyApplied) {
            throw new IllegalArgumentException("You already have a pending or approved application for this offer.");
        }

        Application application = postOfferMapper.toEntity(applicationRequestDto);
        application.setStudent(student);
        application.setEnterprise(offer.getEnterprise());
        application.setOffer(offer);
        application.setState(ApplicationState.PENDING);

        postOffer.saveApplication(application);

        log.info("Application created successfully for student {} on offer {}", email, offer_id);

        String enterpriseMsg = "Nouvelle candidature reçue pour l'offre: " + offer.getTitle();
        notificationInterface.sendNotification(offer.getEnterprise(), enterpriseMsg);

        return ResponseEntity
                .ok(ApiResponse.success("Application created successfully", postOfferMapper.toDto(application)));
    }

    @Operation(summary = "Accepter une offre de stage par l'étudiant")
    @PutMapping("{application_id}/updateStudentStatus")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> updateStudentStatus(
            @PathVariable Long application_id,
            @RequestParam boolean applicationAccepted) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = postOffer.getStudentByEmail(email);

        Application application = postOffer.getApplicationById(application_id);

        if (!application.getStudent().getId().equals(student.getId())) {
            throw new SecurityException("You can only manage your own applications.");
        }

        if (applicationAccepted) {
            student.setOnInternship(true);
            postOffer.saveUser(student);

            application.setState(ApplicationState.APPROVED);
            postOffer.saveApplication(application);

            log.info("Student {} accepted internship from application {}", email, application_id);
        }

        return ResponseEntity.ok(ApiResponse.success("Student status updated", postOfferMapper.toDto(application)));
    }

    @Operation(summary = "Mettre à jour les langues maîtrisées")
    @PatchMapping("updateLanguages")
    public ResponseEntity<ApiResponse<List<String>>> updateLanguages(
            @Valid @RequestBody LanguageRequestDto languageRequestDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = postOffer.getStudentByEmail(email);

        List<String> languages = student.getLanguages();
        if (!languages.contains(languageRequestDto.getLanguage())) {
            languages.add(languageRequestDto.getLanguage());
            student.setLanguages(languages);
            postOffer.saveUser(student);
        }

        return ResponseEntity.ok(ApiResponse.success("Languages updated successfully", student.getLanguages()));
    }

    @Operation(summary = "Mettre à jour le lien GitHub")
    @PatchMapping("/updateGithubLink")
    public ResponseEntity<ApiResponse<String>> updateGithubLink(@Valid @RequestBody GithubRequestDto githubRequestDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = postOffer.getStudentByEmail(email);

        student.setGithubLink(githubRequestDto.getGithub());
        postOffer.saveUser(student);

        return ResponseEntity.ok(ApiResponse.success("GitHub link updated successfully", student.getGithubLink()));
    }

    @Operation(summary = "Mettre à jour le lien LinkedIn")
    @PatchMapping("/updateLinkedinLink")
    public ResponseEntity<ApiResponse<String>> updateLinkedinLink(
            @Valid @RequestBody LinkedinRequestDto linkedinRequestDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = postOffer.getStudentByEmail(email);

        student.setLinkedinLink(linkedinRequestDto.getLinkedin());
        postOffer.saveUser(student);

        return ResponseEntity.ok(ApiResponse.success("LinkedIn link updated successfully", student.getLinkedinLink()));
    }

    @Operation(summary = "Vérifier le statut actuel de l'étudiant")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentStatus() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = postOffer.getStudentByEmail(email);

        Map<String, Object> status = Map.of(
                "onInternship", student.isOnInternship(),
                "message", student.isOnInternship() ? "Vous êtes en stage" : "Vous pouvez candidater aux offres",
                "canApply", !student.isOnInternship());

        return ResponseEntity.ok(ApiResponse.success("Status retrieved", status));
    }

    @Operation(summary = "Récupérer le profil complet de l'étudiant")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StudentResponseDto>> getStudentProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = postOffer.getStudentByEmail(email);

        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", postOfferMapper.toDtoStudent(student)));
    }
}
