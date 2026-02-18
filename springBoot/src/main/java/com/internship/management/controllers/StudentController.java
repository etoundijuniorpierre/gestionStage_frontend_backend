package com.internship.management.controllers;

import com.internship.management.dto.StudentResponseDto;
import com.internship.management.dto.application.ApplicationRequestDto;
import com.internship.management.dto.application.ApplicationResponseDto;
import com.internship.management.dto.postOffer.OfferResponseDto;
import com.internship.management.dto.profile.*;
import com.internship.management.entities.*;
import com.internship.management.enums.ApplicationState;
import com.internship.management.enums.ConventionState;
import com.internship.management.enums.OfferStatus;
import com.internship.management.interfaces.ApplicationService;
import com.internship.management.interfaces.NotificationInterface;
import com.internship.management.interfaces.OfferService;
import com.internship.management.interfaces.StudentService;
import com.internship.management.interfaces.UserService;
import com.internship.management.mappers.DtoMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(path = "api/student")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class StudentController {

    private final StudentService studentService;
    private final OfferService offerService;
    private final ApplicationService applicationService;
    private final UserService userService;
    private final DtoMapper dtoMapper;
    private final NotificationInterface notificationInterface;

    @GetMapping("/offersByApprovedStatus")
    public List<OfferResponseDto> getOfferByStatus() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.getStudentByEmail(email);

        List<Offer> offers = offerService.getOffersByStatusAndConventionApproved(OfferStatus.APPROVED,
                ConventionState.APPROVED, student.getDepartment());

        return student.isOnInternship() ? List.of() : dtoMapper.toDtoList(offers);
    }

    @GetMapping("pendingApplicationsOfStudent")
    public List<ApplicationResponseDto> getPendingApplicationsOfStudent() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.getStudentByEmail(email);

        List<Application> applications = applicationService.getApplicationsRejectedOrPendingByStudentEmail(email);

        return student.isOnInternship() ? List.of() : dtoMapper.toDtoApplicationList(applications);
    }

    @GetMapping("/applicationsApprovedOfStudent")
    public List<ApplicationResponseDto> getApplicationsApprovedOfStudent() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.getStudentByEmail(email);

        List<Application> applications = applicationService.getApplicationsApprovedByStudentEmail(email);

        Application chosenApplicationByStudent = applicationService.getApplicationByStudentOnInternshipTrue(student);

        if (student.isOnInternship()) {

            for (Application application : applications) {

                if (!Objects.equals(application.getId(), chosenApplicationByStudent.getId())) {
                    application.setState(ApplicationState.CANCELLED);
                }
            }

            return List.of();
        }

        return dtoMapper.toDtoApplicationList(applications);
    }

    @DeleteMapping("/{application_id}")
    public void deleteApplication(@PathVariable("application_id") Long application_id) {
        applicationService.deleteApplicationRejected(application_id);
    }

    @PostMapping("{offer_id}/createApplication")
    public ResponseEntity<?> create(@ModelAttribute ApplicationRequestDto applicationRequestDto,
            @PathVariable Long offer_id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.getStudentByEmail(email);

        if (student.isOnInternship()) {
            return ResponseEntity.badRequest().body("you are on internship and cannot apply anymore.");
        }

        List<Application> allApplications = student.getApplications();
        boolean alreadyApplied = allApplications.stream()
                .anyMatch(app -> app.getOffer().getId().equals(offer_id) &&
                        (app.getState() == ApplicationState.PENDING || app.getState() == ApplicationState.APPROVED));

        if (alreadyApplied) {
            return ResponseEntity.badRequest().body("you already applied for this offer");
        }

        Application application = dtoMapper.toEntity(applicationRequestDto);
        application.setStudent(student);

        Offer offer = offerService.getOfferById(offer_id);
        Enterprise enterprise = offer.getEnterprise();
        application.setEnterprise(enterprise);
        application.setOffer(offer);

        applicationService.saveApplication(application);

        String enterpriseMsg = " Nouvelle candidature reçu pour l'offre: " + offer.getTitle();
        notificationInterface.sendNotification(enterprise, enterpriseMsg);

        return ResponseEntity.ok(dtoMapper.toDto(application));
    }

    @PutMapping("{application_id}/updateStudentStatus")
    public ResponseEntity<ApplicationResponseDto> updateStudentStatus(@PathVariable Long application_id,
            @RequestParam boolean applicationAccepted) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.getStudentByEmail(email);

        Application application = applicationService.getApplicationApprovedById(application_id);

        if (applicationAccepted) {

            student.setOnInternship(true);
            userService.saveUser(student);

            application.setStudent(student);
            applicationService.saveApplication(application);

        }

        return ResponseEntity.ok(dtoMapper.toDto(application));
    }

    @PatchMapping("updateLanguages")
    public ResponseEntity<String> updateLanguages(@RequestBody LanguageRequestDto languageRequestDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.getStudentByEmail(email);

        List<String> languages = student.getLanguages();
        languages.add(languageRequestDto.getLanguage());
        student.setLanguages(languages);

        userService.saveUser(student);

        return ResponseEntity.ok().body("language updated successfully");
    }

    @PatchMapping("/updateGithubLink")
    public ResponseEntity<String> updateGithubLink(@RequestBody GithubRequestDto githubRequestDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.getStudentByEmail(email);

        student.setGithubLink(githubRequestDto.getGithub());
        userService.saveUser(student);

        return ResponseEntity.ok().body("github link updated successfully");
    }

    @PatchMapping("/updateLinkedinLink")
    public ResponseEntity<String> updateLinkedinLink(@RequestBody LinkedinRequestDto linkedinRequestDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.getStudentByEmail(email);

        student.setLinkedinLink(linkedinRequestDto.getLinkedin());
        userService.saveUser(student);

        return ResponseEntity.ok().body("linkedin link updated successfully");
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStudentStatus() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.getStudentByEmail(email);

        if (student.isOnInternship()) {
            return ResponseEntity.ok().body(Map.of(
                    "onInternship", true,
                    "message", "Vous êtes en stage",
                    "canApply", false));
        }

        return ResponseEntity.ok().body(Map.of(
                "onInternship", false,
                "message", "Vous pouvez candidater aux offres",
                "canApply", true));
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentResponseDto> getStudentProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.getStudentByEmail(email);

        StudentResponseDto profileDto = dtoMapper.toDtoStudent(student);
        return ResponseEntity.ok(profileDto);
    }
}
