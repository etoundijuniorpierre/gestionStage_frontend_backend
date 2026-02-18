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

        Student toStudentEntity = registrationMapper.toEntity(studentRequestDto, passwordEncoder);
        internshipService.registerStudent(toStudentEntity);

        return ResponseEntity.ok().body(studentRequestDto.getName() + " student" + " is registered successfully");
    }

    @PostMapping("/registerTeacher")
    public ResponseEntity<String> create(@Valid @RequestBody TeacherRegistrationRequestDto teacherRequestDto) {

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

        UserResponseDto dto;
        if (userVerified instanceof Student student) {
            dto = registrationMapper.toDto(student);
        } else if (userVerified instanceof Enterprise enterprise) {
            dto = registrationMapper.toDto(enterprise);
        } else if (userVerified instanceof Teacher teacher) {
            dto = registrationMapper.toDto(teacher);
        } else {
            throw new RuntimeException("Unknown user type");
        }

        return ResponseEntity.ok(dto);
    }


}
