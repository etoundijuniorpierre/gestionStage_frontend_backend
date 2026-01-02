package com.internship.management.controllers;

import com.internship.management.dto.UserResponseDto;
import com.internship.management.dto.registration.EnterpriseRegistrationRequestDto;
import com.internship.management.dto.registration.StudentRegistrationRequestDto;
import com.internship.management.dto.registration.TeacherRegistrationRequestDto;
import com.internship.management.dto.registration.TokenVerificationRequestDto;
import com.internship.management.dto.response.ApiResponse;
import com.internship.management.entities.*;
import com.internship.management.interfaces.InternshipService;
import com.internship.management.mappers.RegistrationMapper;
import com.internship.management.services.registrationService.VerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(path = "/api/registration")
@RequiredArgsConstructor
@Tag(name = "Registration Controller", description = "Endpoints pour l'inscription et la vérification des comptes")
public class RegistrationController {

    private final InternshipService internshipService;
    private final RegistrationMapper registrationMapper;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Inscrire une entreprise")
    @PostMapping("/registerEnterprise")
    public ResponseEntity<ApiResponse<String>> registerEnterprise(
            @Valid @ModelAttribute EnterpriseRegistrationRequestDto enterpriseRequestDto) throws IOException {

        Enterprise toEnterpriseEntity = registrationMapper.toEntity(enterpriseRequestDto, passwordEncoder);

        if (enterpriseRequestDto.getLogo() != null && !enterpriseRequestDto.getLogo().isEmpty()) {
            Logo logo = new Logo();
            logo.setLogo(enterpriseRequestDto.getLogo().getBytes());
            logo.setContentType(enterpriseRequestDto.getLogo().getContentType());
            logo.setEnterprise(toEnterpriseEntity);
            toEnterpriseEntity.setLogo(logo);
        }

        internshipService.registerEnterprise(toEnterpriseEntity);
        log.info("Enterprise registered: {}", enterpriseRequestDto.getEmail());

        return ResponseEntity.ok(ApiResponse.success(enterpriseRequestDto.getName() + " registered successfully"));
    }

    @Operation(summary = "Inscrire un étudiant")
    @PostMapping("/registerStudent")
    public ResponseEntity<ApiResponse<String>> registerStudent(
            @Valid @RequestBody StudentRegistrationRequestDto studentRequestDto) {

        Student toStudentEntity = registrationMapper.toEntity(studentRequestDto, passwordEncoder);
        internshipService.registerStudent(toStudentEntity);
        log.info("Student registered: {}", studentRequestDto.getEmail());

        return ResponseEntity.ok(ApiResponse.success(studentRequestDto.getName() + " registered successfully"));
    }

    @Operation(summary = "Inscrire un enseignant")
    @PostMapping("/registerTeacher")
    public ResponseEntity<ApiResponse<String>> registerTeacher(
            @Valid @RequestBody TeacherRegistrationRequestDto teacherRequestDto) {

        Teacher toTeacherEntity = registrationMapper.toEntity(teacherRequestDto, passwordEncoder);
        internshipService.registerTeacher(toTeacherEntity);
        log.info("Teacher registered: {}", teacherRequestDto.getEmail());

        return ResponseEntity.ok(ApiResponse.success(teacherRequestDto.getName() + " registered successfully"));
    }

    @Operation(summary = "Renvoyer le jeton de vérification")
    @PostMapping("/resendToken")
    public ResponseEntity<ApiResponse<String>> resendToken(@RequestParam String email) {
        Users user = internshipService.getUserByEmail(email);

        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User is already verified"));
        }

        verificationTokenService.resendToken(user);
        return ResponseEntity.ok(ApiResponse.success("A new token has been sent to your email"));
    }

    @Operation(summary = "Vérifier l'email")
    @PostMapping("/verifyEmail")
    public ResponseEntity<ApiResponse<UserResponseDto>> verifyEmail(
            @Valid @RequestBody TokenVerificationRequestDto request) {

        Users userVerified = verificationTokenService.verifyCode(request.getEmail(), request.getToken());

        UserResponseDto dto;
        if (userVerified instanceof Student student) {
            dto = registrationMapper.toDto(student);
        } else if (userVerified instanceof Enterprise enterprise) {
            dto = registrationMapper.toDto(enterprise);
        } else if (userVerified instanceof Teacher teacher) {
            dto = registrationMapper.toDto(teacher);
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Unknown user type"));
        }

        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", dto));
    }
}
