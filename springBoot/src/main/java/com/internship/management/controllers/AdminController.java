package com.internship.management.controllers;

import com.internship.management.dto.StudentResponseDto;
import com.internship.management.dto.TeacherResponseDto;
import com.internship.management.dto.postOffer.EnterpriseResponseDto;
import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Student;
import com.internship.management.entities.Teacher;
import com.internship.management.enums.EnterpriseState;
import com.internship.management.interfaces.*;
import com.internship.management.mappers.DtoMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class AdminController {

    private final ChartInterface chartInterface;
    private final EnterpriseService enterpriseService;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final UserService userService;
    private final DtoMapper dtoMapper;
    private final NotificationInterface notificationInterface;

    @GetMapping("/internships.xlsx")
    public ResponseEntity<byte[]> downloadInternshipsExcel() throws IOException {

        ByteArrayInputStream stream = chartInterface.exportInternshipsByDepartment();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=internshipBySector" + LocalDate.now() + ".xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(stream.readAllBytes());
    }

    @GetMapping("/approvalPendingEnterprise")
    public List<EnterpriseResponseDto> getPendingValidationEnterprise() {

        List<Enterprise> listOfEnterprise = enterpriseService.getEnterpriseByPartnershipFalse();
        return dtoMapper.toDtoEnterpriseList(listOfEnterprise);
    }

    @GetMapping("/enterpriseInPartnership")
    public List<EnterpriseResponseDto> getEnterpriseInPartnership() {

        List<Enterprise> listOfEnterpriseInPartnership = enterpriseService.getEnterpriseByPartnershipTrue();
        return dtoMapper.toDtoEnterpriseList(listOfEnterpriseInPartnership);
    }

    @GetMapping("/allTeachers")
    public ResponseEntity<List<TeacherResponseDto>> getAllTeachers() {

        List<Teacher> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(dtoMapper.toDtoTeacherList(teachers));
    }

    @GetMapping("/allStudent")
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {

        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(dtoMapper.toDtoStudentList(students));
    }

    @GetMapping("/teacherPagination")
    public Page<TeacherResponseDto> getTeacherPagination(Pageable pageable) {

        Page<Teacher> saveTeacher = teacherService.getAllTeachersByPagination(pageable);
        List<TeacherResponseDto> teachers = dtoMapper.toDtoTeacherList(saveTeacher.getContent());

        return new PageImpl<>(teachers, pageable, teachers.size());
    }

    @GetMapping("/studentPagination")
    public Page<StudentResponseDto> getStudentPagination(Pageable pageable) {

        Page<Student> saveStudent = studentService.getAllStudentsByPagination(pageable);
        List<StudentResponseDto> students = dtoMapper.toDtoStudentList(saveStudent.getContent());

        return new PageImpl<>(students, pageable, students.size());
    }

    @PutMapping("/Enterprise/{id}/approve")
    public ResponseEntity<EnterpriseResponseDto> approveEnterprise(@PathVariable Long id,
            @RequestParam boolean approved) {

        Enterprise enterprise = enterpriseService.getByEnterpriseId(id);
        String enterpriseMsg = approved ? "Ton enterprise a été approuvée sur notre plateforme de gestion de stage"
                : "Ton enterprise a été rejetée sur notre plateforme de gestion de stage";

        if (approved) {

            enterprise.setEnterpriseState(EnterpriseState.APPROVED);
            enterprise.setInPartnership(true);
            userService.saveUser(enterprise);
            notificationInterface.sendNotification(enterprise, enterpriseMsg);

        } else {

            enterprise.setEnterpriseState(EnterpriseState.REJECTED);
            notificationInterface.sendNotification(enterprise, enterpriseMsg);
        }

        return ResponseEntity.ok(dtoMapper.toDtoEnterprise(enterprise));
    }
}