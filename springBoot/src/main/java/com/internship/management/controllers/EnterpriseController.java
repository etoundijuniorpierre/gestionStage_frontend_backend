package com.internship.management.controllers;

import com.internship.management.dto.application.ApplicationResponseDto;
import com.internship.management.dto.postOffer.EnterpriseResponseDto;
import com.internship.management.dto.postOffer.OfferRequestDto;
import com.internship.management.dto.postOffer.OfferResponseDto;
import com.internship.management.dto.profile.UpdateEnterpriseProfile;
import com.internship.management.dto.response.ApiResponse;
import com.internship.management.entities.*;
import com.internship.management.enums.ApplicationState;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "api/enterprise")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Enterprise Controller", description = "Endpoints pour les actions spécifiques aux entreprises")
public class EnterpriseController {

    private final PostOffer postOffer;
    private final PostOfferMapper postOfferMapper;
    private final NotificationInterface notificationInterface;

    @Operation(summary = "Créer une nouvelle offre de stage")
    @PostMapping("/createOffer")
    public ResponseEntity<ApiResponse<OfferResponseDto>> create(@Valid @RequestBody OfferRequestDto offerRequestDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = postOffer.getByEnterpriseEmail(email);

        Offer offer = postOfferMapper.toEntity(offerRequestDto);
        offer.setEnterprise(enterprise);
        offer.setStatus(com.internship.management.enums.OfferStatus.PENDING);

        postOffer.saveOffer(offer);
        log.info("Offer created successfully for enterprise {}", email);

        return ResponseEntity.ok(ApiResponse.success("Offer created successfully", postOfferMapper.toDto(offer)));
    }

    @Operation(summary = "Uploader la convention de stage signée pour une offre")
    @PostMapping("/{offerId}/convention")
    public ResponseEntity<ApiResponse<OfferResponseDto>> createPdfConvention(
            @PathVariable Long offerId,
            @RequestParam MultipartFile pdfConvention) throws IOException {

        Offer offer = postOffer.getOfferById(offerId);

        if (pdfConvention != null && !pdfConvention.isEmpty()) {
            Convention c = new Convention();
            c.setPdfConvention(pdfConvention.getBytes());
            c.setOffer(offer);
            c.setConventionState(com.internship.management.enums.ConventionState.PENDING);
            postOffer.saveConvention(c);
            offer.setConvention(c);
        }

        postOffer.saveOffer(offer);

        List<Teacher> teachers = postOffer.getTeachersByDepartment(offer.getDomain());
        for (Teacher teacher : teachers) {
            notificationInterface.sendNotification(teacher,
                    "Nouvelle convention à valider pour l'offre: " + offer.getTitle());
        }

        return ResponseEntity.ok(ApiResponse.success("Convention uploaded successfully", postOfferMapper.toDto(offer)));
    }

    @Operation(summary = "Récupérer toutes les candidatures pour l'entreprise")
    @GetMapping("/Applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponseDto>>> getApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = postOffer.getByEnterpriseEmail(email);

        Pageable pageable = PaginationUtil.createPageable(page, size, "applicationDate");
        Page<Application> applicationPage = postOffer.getAllApplicationsByEnterpriseIdPaged(enterprise.getId(),
                pageable);

        List<ApplicationResponseDto> dtos = postOfferMapper.toDtoApplicationList(applicationPage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(applicationPage);

        return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", dtos, pageInfo));
    }

    @Operation(summary = "Récupérer la liste des offres de l'entreprise")
    @GetMapping("/listOfOffers")
    public ResponseEntity<ApiResponse<List<OfferResponseDto>>> getOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = postOffer.getByEnterpriseEmail(email);

        Pageable pageable = PaginationUtil.createPageable(page, size, "createdAt");
        Page<Offer> offerPage = postOffer.getOfferByEnterpriseIdPaged(enterprise.getId(), pageable);

        List<OfferResponseDto> dtos = postOfferMapper.toDtoList(offerPage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(offerPage);

        return ResponseEntity.ok(ApiResponse.success("Offers retrieved successfully", dtos, pageInfo));
    }

    @Operation(summary = "Valider ou rejeter une candidature")
    @PutMapping("application/{id}/validate")
    public ResponseEntity<ApiResponse<String>> validateApplication(
            @PathVariable Long id,
            @RequestParam boolean approved) {

        Application application = postOffer.getApplicationById(id);
        Student student = application.getStudent();

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = postOffer.getByEnterpriseEmail(email);

        String msg;
        if (approved) {
            application.setState(ApplicationState.APPROVED);
            msg = "Votre candidature a été approuvée par l'entreprise " + enterprise.getName();
        } else {
            application.setState(ApplicationState.REJECTED);
            msg = "Votre candidature a été rejetée par l'entreprise " + enterprise.getName();
        }

        postOffer.saveApplication(application);
        notificationInterface.sendNotification(student, msg);

        return ResponseEntity.ok(ApiResponse.success(msg, msg));
    }

    @Operation(summary = "Récupérer les informations de l'entreprise actuelle")
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<EnterpriseResponseDto>> getCurrentEnterpriseInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = postOffer.getByEnterpriseEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Info retrieved", postOfferMapper.toDtoEnterprise(enterprise)));
    }

    @Operation(summary = "Mettre à jour le contact de l'entreprise")
    @PatchMapping("/updateContact")
    public ResponseEntity<ApiResponse<String>> updateContact(
            @Valid @RequestBody UpdateEnterpriseProfile updateEnterpriseProfile) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = postOffer.getByEnterpriseEmail(email);

        enterprise.setContact(updateEnterpriseProfile.getContact());
        postOffer.saveUser(enterprise);

        return ResponseEntity.ok(ApiResponse.success("Contact updated successfully", enterprise.getContact()));
    }

    @Operation(summary = "Mettre à jour la localisation de l'entreprise")
    @PatchMapping("/updateLocation")
    public ResponseEntity<ApiResponse<String>> updateLocation(
            @Valid @RequestBody UpdateEnterpriseProfile updateEnterpriseProfile) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Enterprise enterprise = postOffer.getByEnterpriseEmail(email);

        enterprise.setLocation(updateEnterpriseProfile.getLocation());
        postOffer.saveUser(enterprise);

        return ResponseEntity.ok(ApiResponse.success("Location updated successfully", enterprise.getLocation()));
    }

    @Operation(summary = "Mettre à jour le logo de l'entreprise")
    @PutMapping("/updateLogo/{enterpriseId}")
    public ResponseEntity<ApiResponse<String>> updateLogo(
            @PathVariable Long enterpriseId,
            @RequestParam("file") MultipartFile file) {

        try {
            postOffer.updateLogo(enterpriseId, file);
            return ResponseEntity.ok(ApiResponse.success("Logo updated successfully", "Logo updated"));
        } catch (Exception e) {
            log.error("Error updating logo for enterprise {}", enterpriseId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating logo: " + e.getMessage()));
        }
    }
}
