package com.internship.management.controllers;

import com.internship.management.dto.ResetPasswordRequestDto;
import com.internship.management.dto.registration.TokenVerificationRequestDto;
import com.internship.management.dto.response.ApiResponse;
import com.internship.management.entities.Users;
import com.internship.management.interfaces.PostOffer;
import com.internship.management.services.registrationService.VerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/auth/reset-password")
@RequiredArgsConstructor
@Tag(name = "Reset Password Controller", description = "Endpoints pour la réinitialisation du mot de passe")
public class ResetPasswordController {

    private final PostOffer postOffer;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;

    @Operation(summary = "Envoyer un jeton de réinitialisation")
    @PostMapping("/send-token")
    public ResponseEntity<ApiResponse<String>> sendTokenWhenResetting(
            @RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        Users user = postOffer.getUserByEmail(resetPasswordRequestDto.getEmail());
        verificationTokenService.createAndSendToken(user);
        return ResponseEntity.ok(ApiResponse.success("Verification token sent to your email"));
    }

    @Operation(summary = "Vérifier l'email pour la réinitialisation")
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@Valid @RequestBody TokenVerificationRequestDto request) {
        try {
            verificationTokenService.verifyCode(request.getEmail(), request.getToken());
            return ResponseEntity.ok(ApiResponse.success("Token verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Réinitialiser le mot de passe")
    @PatchMapping
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        Users user = postOffer.getUserByEmail(resetPasswordRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getPassword()));
        postOffer.saveUser(user);
        log.info("Password reset for user: {}", user.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
    }
}
