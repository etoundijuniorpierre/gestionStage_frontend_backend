package com.internship.management.controllers;

import com.internship.management.dto.application.ApplicationResponseDto;
import com.internship.management.dto.postOffer.EnterpriseResponseDto;
import com.internship.management.dto.postOffer.OfferRequestDto;
import com.internship.management.dto.postOffer.OfferResponseDto;
import com.internship.management.dto.profile.UpdateEnterpriseProfile;
import com.internship.management.entities.*;
import com.internship.management.enums.ApplicationState;
import com.internship.management.interfaces.*;
import com.internship.management.mappers.DtoMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "api/enterprise")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class EnterpriseController {

    private final EnterpriseService enterpriseService;
    private final OfferService offerService;
    private final ApplicationService applicationService;
    private final ConventionService conventionService;
    private final TeacherService teacherService;
    private final UserService userService;
    private final DtoMapper dtoMapper;
    private final NotificationInterface notificationInterface;

    @PostMapping("/createOffer")
    public ResponseEntity<OfferResponseDto> create(@RequestBody OfferRequestDto offerRequestDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = enterpriseService.getByEnterpriseEmail(email);

        Offer offer = dtoMapper.toEntity(offerRequestDto);
        offer.setEnterprise(enterprise);

        offerService.saveOffer(offer);
        return ResponseEntity.ok(dtoMapper.toDto(offer));
    }

    @PostMapping("/{offerId}/convention")
    public ResponseEntity<OfferResponseDto> createPdfConvention(@PathVariable Long offerId,
            @RequestParam MultipartFile pdfConvention) throws IOException {

        Offer offer = offerService.getOfferById(offerId);

        if (pdfConvention != null && !pdfConvention.isEmpty()) {

            Convention c = new Convention();
            c.setPdfConvention(pdfConvention.getBytes());
            c.setOffer(offer);
            conventionService.saveConvention(c);
            offer.setConvention(c);
        }

        offerService.saveOffer(offer);

        List<Teacher> teachers = teacherService.getTeachersByDepartment(offer.getDomain());

        for (Teacher teacher : teachers) {
            notificationInterface.sendNotification(teacher, "Nouvel arrivage d'offres");
        }

        return ResponseEntity.ok(dtoMapper.toDto(offer));
    }

    @GetMapping("/Applications")
    public List<ApplicationResponseDto> getApplications() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = enterpriseService.getByEnterpriseEmail(email);
        List<Application> applications = applicationService.getAllApplicationsByEnterpriseId(enterprise.getId());

        return dtoMapper.toDtoApplicationList(applications);
    }

    @GetMapping("/listOfOffers")
    public List<OfferResponseDto> getOffers() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = enterpriseService.getByEnterpriseEmail(email);
        List<Offer> offersByEnterpriseId = offerService.getOfferByEnterpriseId(enterprise.getId());

        return dtoMapper.toDtoList(offersByEnterpriseId);
    }

    @PutMapping("application/{id}/validate")
    public ResponseEntity<String> validateApplication(@PathVariable Long id,
            @RequestParam boolean approved) {

        Application application = applicationService.getApplicationById(id);

        Student student = application.getStudent();

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = enterpriseService.getByEnterpriseEmail(email);

        String msg = "";

        if (approved) {

            application.setState(ApplicationState.APPROVED);
            msg = "Votre candidature a été approuvée et examiner par l'entreprise " +
                    enterprise.getName();
            notificationInterface.sendNotification(student, msg);

        } else {

            application.setState(ApplicationState.REJECTED);
            msg = "Votre candidature a été rejetée et examiner par l'entreprise " +
                    enterprise.getName();
            notificationInterface.sendNotification(student, msg);
        }

        return ResponseEntity.ok().body(msg);
    }

    @GetMapping("/info")
    public ResponseEntity<EnterpriseResponseDto> getCurrentEnterpriseInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = enterpriseService.getByEnterpriseEmail(email);
        return ResponseEntity.ok(dtoMapper.toDtoEnterprise(enterprise));
    }

    @PatchMapping("/updateContact")
    public ResponseEntity<String> updateContact(@RequestBody UpdateEnterpriseProfile updateEnterpriseProfile) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = enterpriseService.getByEnterpriseEmail(email);

        enterprise.setContact(updateEnterpriseProfile.getContact());
        userService.saveUser(enterprise);

        return ResponseEntity.ok().body(enterprise.getName() + " updated contact successfully");
    }

    @PatchMapping("/updateLocation")
    public ResponseEntity<String> updateLocation(@RequestBody UpdateEnterpriseProfile updateEnterpriseProfile) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = enterpriseService.getByEnterpriseEmail(email);

        enterprise.setLocation(updateEnterpriseProfile.getLocation());
        userService.saveUser(enterprise);

        return ResponseEntity.ok().body(enterprise.getName() + " updated location successfully");
    }

    @PutMapping("/updateLogo/{enterpriseId}")
    public ResponseEntity<String> updateLogo(
            @PathVariable Long enterpriseId,
            @RequestParam("file") MultipartFile file) {

        try {

            enterpriseService.updateLogo(enterpriseId, file);
            return ResponseEntity.ok("Logo updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
}