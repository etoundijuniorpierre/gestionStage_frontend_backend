package com.internship.management.controllers;


import com.internship.management.entities.*;
import com.internship.management.mappers.RegistrationMapper;
import com.internship.management.dto.UserResponseDto;
import com.internship.management.dto.registration.EnterpriseRegistrationRequestDto;
import com.internship.management.dto.registration.StudentRegistrationRequestDto;
import com.internship.management.dto.registration.TeacherRegistrationRequestDto;
import com.internship.management.dto.registration.TokenVerificationRequestDto;
import com.internship.management.interfaces.InternshipService;
import com.internship.management.services.registrationService.VerificationTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping(path ="registration")
@RequiredArgsConstructor
public class registrationController {

    private final InternshipService internshipService;
    private final RegistrationMapper registrationMapper;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/registerEnterprise")
    public ResponseEntity<String> create(@Valid @ModelAttribute EnterpriseRegistrationRequestDto enterpriseRequestDto) throws IOException {

        // Vérifier si l'utilisateur existe déjà
        Users existingUser = internshipService.getUserByEmail(enterpriseRequestDto.getEmail());
        if (existingUser != null) {
            if (existingUser.getStatus() == com.internship.management.enums.UserStatus.INACTIF) {
                // Renvoyer un token et message approprié
                verificationTokenService.createAndSendToken(existingUser);
                return ResponseEntity.badRequest().body(
                    "An account with this email already exists but is inactive. " +
                    "A new verification code has been sent to your email. " +
                    "Please check your emails to activate your account. " +
                    "If this is not your account, please create a new one with a different email address."
                );
            } else {
                return ResponseEntity.badRequest().body("User with email " + enterpriseRequestDto.getEmail() + " already exists");
            }
        }

        Enterprise toEnterpriseEntity = registrationMapper.toEntity(enterpriseRequestDto,passwordEncoder);

        if (enterpriseRequestDto.getLogo() != null && !enterpriseRequestDto.getLogo().isEmpty()) {

            Logo logo = new Logo();
            logo.setLogo(enterpriseRequestDto.getLogo().getBytes());
            logo.setContentType(enterpriseRequestDto.getLogo().getContentType());
            logo.setEnterprise(toEnterpriseEntity);
            toEnterpriseEntity.setLogo(logo);
        }

       internshipService.registerEnterprise(toEnterpriseEntity);

        return ResponseEntity.ok().body(enterpriseRequestDto.getName() + " Company" + " is registered successfully");
    }

    @PostMapping("/registerStudent")
    public ResponseEntity<String> create(@Valid @RequestBody StudentRegistrationRequestDto studentRequestDto) {

        // Vérifier si l'utilisateur existe déjà
        Users existingUser = internshipService.getUserByEmail(studentRequestDto.getEmail());
        if (existingUser != null) {
            if (existingUser.getStatus() == com.internship.management.enums.UserStatus.INACTIF) {
                // Renvoyer un token et message approprié
                verificationTokenService.createAndSendToken(existingUser);
                return ResponseEntity.badRequest().body(
                    "An account with this email already exists but is inactive. " +
                    "A new verification code has been sent to your email. " +
                    "Please check your emails to activate your account. " +
                    "If this is not your account, please create a new one with a different email address."
                );
            } else {
                return ResponseEntity.badRequest().body("User with email " + studentRequestDto.getEmail() + " already exists");
            }
        }

        Student toStudentEntity = registrationMapper.toEntity(studentRequestDto, passwordEncoder);
        internshipService.registerStudent(toStudentEntity);

        return ResponseEntity.ok().body(studentRequestDto.getName() + " student" + " is registered successfully");
    }

    @PostMapping("/registerTeacher")
    public ResponseEntity<String> create(@Valid @RequestBody TeacherRegistrationRequestDto teacherRequestDto) {

        // Vérifier si l'utilisateur existe déjà
        Users existingUser = internshipService.getUserByEmail(teacherRequestDto.getEmail());
        if (existingUser != null) {
            if (existingUser.getStatus() == com.internship.management.enums.UserStatus.INACTIF) {
                // Renvoyer un token et message approprié
                verificationTokenService.createAndSendToken(existingUser);
                return ResponseEntity.badRequest().body(
                    "An account with this email already exists but is inactive. " +
                    "A new verification code has been sent to your email. " +
                    "Please check your emails to activate your account. " +
                    "If this is not your account, please create a new one with a different email address."
                );
            } else {
                return ResponseEntity.badRequest().body("User with email " + teacherRequestDto.getEmail() + " already exists");
            }
        }

        Teacher toTeacherEntity = registrationMapper.toEntity(teacherRequestDto, passwordEncoder);
        internshipService.registerTeacher(toTeacherEntity);

        return ResponseEntity.ok().body(teacherRequestDto.getName() + " teacher" + " is registered successfully");
    }


    @PostMapping("/resendToken")
    public ResponseEntity<String> resendToken(@RequestParam String email) {
        Users user = internshipService.getUserByEmail(email);

        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest().body("User is already verified");
        }

        verificationTokenService.resendToken(user);
        return ResponseEntity.ok("A new token has been sent to your email");
    }


    @PostMapping("/verifyEmail")
    public ResponseEntity<UserResponseDto> verifyEmail(@Valid @RequestBody TokenVerificationRequestDto request) {

        Users userVerified = verificationTokenService.verifyCode(request.getEmail(), request.getToken());

        UserResponseDto dto = registrationMapper.toDtoWithStatus(userVerified);

        return ResponseEntity.ok(dto);
    }


}
