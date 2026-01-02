package com.internship.management.controllers;

import com.internship.management.dto.StudentResponseDto;
import com.internship.management.dto.TeacherResponseDto;
import com.internship.management.dto.postOffer.EnterpriseResponseDto;
import com.internship.management.dto.response.ApiResponse;
import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Student;
import com.internship.management.entities.Teacher;
import com.internship.management.enums.EnterpriseState;
import com.internship.management.interfaces.ChartInterface;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Admin Controller", description = "Endpoints pour les actions administratives")
public class AdminController {

    private final ChartInterface chartInterface;
    private final PostOffer postOffer;
    private final PostOfferMapper postOfferMapper;
    private final NotificationInterface notificationInterface;

    @Operation(summary = "Exporter les statistiques de stage en Excel")
    @GetMapping("/internships.xlsx")
    public ResponseEntity<byte[]> downloadInternshipsExcel() throws IOException {
        ByteArrayInputStream stream = chartInterface.exportInternshipsByDepartment();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=internshipBySector_" + LocalDate.now() + ".xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(stream.readAllBytes());
    }

    @Operation(summary = "Récupérer les entreprises en attente de validation")
    @GetMapping("/approvalPendingEnterprise")
    public ResponseEntity<ApiResponse<List<EnterpriseResponseDto>>> getPendingValidationEnterprise(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PaginationUtil.createPageable(page, size, "name");
        Page<Enterprise> enterprisePage = postOffer.getEnterpriseByPartnershipFalsePaged(pageable);

        List<EnterpriseResponseDto> dtos = postOfferMapper.toDtoEnterpriseList(enterprisePage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(enterprisePage);

        return ResponseEntity.ok(ApiResponse.success("Pending enterprises retrieved", dtos, pageInfo));
    }

    @Operation(summary = "Récupérer toutes les entreprises en partenariat")
    @GetMapping("/enterpriseInPartnership")
    public ResponseEntity<ApiResponse<List<EnterpriseResponseDto>>> getEnterpriseInPartnership(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PaginationUtil.createPageable(page, size, "name");
        Page<Enterprise> enterprisePage = postOffer.getEnterpriseByPartnershipTruePaged(pageable);

        List<EnterpriseResponseDto> dtos = postOfferMapper.toDtoEnterpriseList(enterprisePage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(enterprisePage);

        return ResponseEntity.ok(ApiResponse.success("Partner enterprises retrieved", dtos, pageInfo));
    }

    @Operation(summary = "Récupérer tous les enseignants avec pagination")
    @GetMapping("/allTeachers")
    public ResponseEntity<ApiResponse<List<TeacherResponseDto>>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PaginationUtil.createPageable(page, size, "lastName");
        Page<Teacher> teacherPage = postOffer.getAllTeacherByPagination(pageable);

        List<TeacherResponseDto> dtos = postOfferMapper.toDtoTeacherList(teacherPage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(teacherPage);

        return ResponseEntity.ok(ApiResponse.success("Teachers retrieved successfully", dtos, pageInfo));
    }

    @Operation(summary = "Récupérer tous les étudiants avec pagination")
    @GetMapping("/allStudent")
    public ResponseEntity<ApiResponse<List<StudentResponseDto>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PaginationUtil.createPageable(page, size, "lastName");
        Page<Student> studentPage = postOffer.getAllStudentByPagination(pageable);

        List<StudentResponseDto> dtos = postOfferMapper.toDtoStudentList(studentPage.getContent());
        ApiResponse.PageInfo pageInfo = PaginationUtil.createPageInfo(studentPage);

        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", dtos, pageInfo));
    }

    @Operation(summary = "Approuver ou rejeter une entreprise")
    @PutMapping("/Enterprise/{id}/approve")
    public ResponseEntity<ApiResponse<EnterpriseResponseDto>> approveEnterprise(
            @PathVariable Long id,
            @RequestParam boolean approved) {

        Enterprise enterprise = postOffer.getByEnterpriseId(id);
        String msg = approved ? "Ton entreprise a été approuvée" : "Ton entreprise a été rejetée";

        if (approved) {
            enterprise.setEnterpriseState(EnterpriseState.APPROVED);
            enterprise.setInPartnership(true);
        } else {
            enterprise.setEnterpriseState(EnterpriseState.REJECTED);
            enterprise.setInPartnership(false);
        }

        postOffer.saveUser(enterprise);
        notificationInterface.sendNotification(enterprise, msg);

        log.info("Enterprise {} processed with approved={}", enterprise.getEmail(), approved);

        return ResponseEntity.ok(ApiResponse.success(msg, postOfferMapper.toDtoEnterprise(enterprise)));
    }
}
