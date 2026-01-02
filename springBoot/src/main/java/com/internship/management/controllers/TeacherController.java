package com.internship.management.controllers;

import com.internship.management.dto.InternshipStatDto;
import com.internship.management.dto.StudentResponseDto;
import com.internship.management.dto.postOffer.EnterpriseResponseDto;
import com.internship.management.dto.postOffer.OfferValidationRequestDto;
import com.internship.management.dto.postOffer.OfferResponseDto;
import com.internship.management.dto.response.ApiResponse;
import com.internship.management.entities.*;
import com.internship.management.enums.ConventionState;
import com.internship.management.enums.OfferStatus;
import com.internship.management.interfaces.ChartInterface;
import com.internship.management.interfaces.DepartmentInternshipStat;
import com.internship.management.interfaces.NotificationInterface;
import com.internship.management.interfaces.PostOffer;
import com.internship.management.mappers.PostOfferMapper;
import com.internship.management.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "api/teacher")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Teacher Controller", description = "Endpoints pour les actions spécifiques aux enseignants")
public class TeacherController {

    private final PostOffer postOffer;
    private final PostOfferMapper postOfferMapper;
    private final NotificationInterface notificationInterface;
    private final ChartInterface chartInterface;

    @Operation(summary = "Récupérer les offres à valider pour le département")
    @GetMapping("/offerToReview")
    public ResponseEntity<ApiResponse<List<OfferResponseDto>>> getOffersToReviewByDepartment(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Teacher teacher = postOffer.getTeacherByEmail(email);

        Pageable pageable = PaginationUtil.createPageable(page, size, "createdAt");
        Page<Offer> offerPage = postOffer.getOfferByDepartmentAndPendingOfferStatusAndInPartnershipTruePaged(
                teacher.getDepartment(), OfferStatus.PENDING, pageable);

        List<OfferResponseDto> dtos = postOfferMapper.toDtoList(offerPage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(offerPage);

        return ResponseEntity.ok(ApiResponse.success("Offers to review retrieved successfully", dtos, pageInfo));
    }

    @Operation(summary = "Récupérer les offres approuvées par l'enseignant")
    @GetMapping("/offersApprovedByTeacher")
    public ResponseEntity<ApiResponse<List<OfferResponseDto>>> getOffersApprovedByTeacher(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Pageable pageable = PaginationUtil.createPageable(page, size, "createdAt");
        Page<Offer> offerPage = postOffer.getOffersByStatusApprovedAndTeacherEmailPaged(OfferStatus.APPROVED, email,
                pageable);

        List<OfferResponseDto> dtos = postOfferMapper.toDtoList(offerPage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(offerPage);

        return ResponseEntity.ok(ApiResponse.success("Approved offers retrieved successfully", dtos, pageInfo));
    }

    @Operation(summary = "Valider ou rejeter une offre et sa convention")
    @PutMapping("/offers/{id}/validate")
    public ResponseEntity<ApiResponse<String>> validateOfferAndConvention(
            @PathVariable Long id,
            @RequestBody OfferValidationRequestDto offerValidationRequest) {

        Offer offer = postOffer.getOfferById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Teacher teacher = postOffer.getTeacherByEmail(email);

        if (offer.getStatus() != OfferStatus.PENDING) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Offer already processed."));
        }

        offer.setStatus(offerValidationRequest.isOfferApproved() ? OfferStatus.APPROVED : OfferStatus.REJECTED);
        offer.setValidatedBy(teacher);

        Convention convention = offer.getConvention();
        if (convention != null) {
            if (convention.getConventionState() == ConventionState.PENDING) {
                convention.setConventionState(offerValidationRequest.isConventionApproved()
                        ? ConventionState.APPROVED
                        : ConventionState.REJECTED);
            }
        }

        postOffer.saveOffer(offer);

        Enterprise enterprise = offer.getEnterprise();
        String enterpriseMsg = (offer.getStatus() == OfferStatus.APPROVED)
                ? "Ton offre \"" + offer.getTitle() + "\" a été approuvée par l'enseignant " + teacher.getName()
                : "Ton offre \"" + offer.getTitle() + "\" a été rejetée par l'enseignant " + teacher.getName();

        notificationInterface.sendNotification(enterprise, enterpriseMsg);

        if (offer.getStatus() == OfferStatus.APPROVED && convention != null
                && convention.getConventionState() == ConventionState.APPROVED) {
            String studentMsg = "Nouvelle offre approuvée dans votre département par: " + teacher.getName();
            List<Student> studentsInDepartment = postOffer.getStudentsByDepartment(teacher.getDepartment());
            for (Student s : studentsInDepartment) {
                notificationInterface.sendNotification(s, studentMsg);
            }
        }

        String result = "Offer: " + offer.getStatus() + ", Convention: " +
                (convention != null ? convention.getConventionState() : "None");

        return ResponseEntity.ok(ApiResponse.success("Offer validation processed", result));
    }

    @Operation(summary = "Statistiques de stage par département")
    @GetMapping("/internshipsByDepartment")
    public ResponseEntity<ApiResponse<List<InternshipStatDto>>> getInternshipStats() {

        List<DepartmentInternshipStat> stats = chartInterface.getInternshipsByDepartment();
        List<InternshipStatDto> dtos = stats.stream()
                .map(stat -> new InternshipStatDto(stat.getDepartment(), stat.getCount()))
                .toList();

        return ResponseEntity.ok(ApiResponse.success("Stats retrieved successfully", dtos));
    }

    @Operation(summary = "Liste des étudiants du département")
    @GetMapping("/listOfStudentByDepartment")
    public ResponseEntity<ApiResponse<List<StudentResponseDto>>> getStudentByDepartment(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Teacher teacher = postOffer.getTeacherByEmail(email);

        Pageable pageable = PaginationUtil.createPageable(page, size, "lastName");
        Page<Student> studentPage = postOffer.getStudentsByDepartmentPaged(teacher.getDepartment(), pageable);

        List<StudentResponseDto> dtos = postOfferMapper.toDtoStudentList(studentPage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(studentPage);

        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", dtos, pageInfo));
    }

    @Operation(summary = "Liste des entreprises en partenariat")
    @GetMapping("/enterpriseInPartnership")
    public ResponseEntity<ApiResponse<List<EnterpriseResponseDto>>> getEnterpriseInPartnership(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PaginationUtil.createPageable(page, size, "name");
        Page<Enterprise> enterprisePage = postOffer.getEnterpriseByPartnershipTruePaged(pageable);

        List<EnterpriseResponseDto> dtos = postOfferMapper.toDtoEnterpriseList(enterprisePage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(enterprisePage);

        return ResponseEntity.ok(ApiResponse.success("Partner enterprises retrieved successfully", dtos, pageInfo));
    }
}
