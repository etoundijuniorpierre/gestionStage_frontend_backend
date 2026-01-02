package com.internship.management.controllers;

import com.internship.management.dto.profile.EmailRequestDto;
import com.internship.management.dto.profile.PasswordRequestDto;
import com.internship.management.dto.response.ApiResponse;
import com.internship.management.entities.Users;
import com.internship.management.interfaces.PostOffer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Profile Controller", description = "Endpoints pour la gestion du profil utilisateur")
public class ProfileController {

    private final PostOffer postOffer;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Mettre à jour le mot de passe")
    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<String>> updatePassword(@RequestBody PasswordRequestDto passwordRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = postOffer.getUserByEmail(email);
        user.setPassword(passwordEncoder.encode(passwordRequestDto.getPassword()));
        postOffer.saveUser(user);
        log.info("Password updated for user: {}", email);
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully"));
    }

    @Operation(summary = "Mettre à jour l'email")
    @PatchMapping("/email")
    public ResponseEntity<ApiResponse<String>> updateEmail(@RequestBody EmailRequestDto emailRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = postOffer.getUserByEmail(email);
        user.setEmail(emailRequestDto.getEmail());
        postOffer.saveUser(user);
        log.info("Email updated for user: {} -> {}", email, emailRequestDto.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Email updated successfully"));
    }

    @Operation(summary = "Récupérer l'email de l'utilisateur actuel")
    @GetMapping("/email")
    public ResponseEntity<ApiResponse<String>> getUserEmail() {
        return ResponseEntity.ok(ApiResponse.success("Email retrieved",
                SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @Operation(summary = "Récupérer l'utilisateur actuel")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Users>> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = postOffer.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("User retrieved", user));
    }

    @Operation(summary = "Supprimer le compte (Admin only for target user, or self?)")
    @DeleteMapping("/account/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUserById(@PathVariable Long userId) {
        postOffer.deleteUser(userId);
        log.info("User account deleted: ID {}", userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    @Operation(summary = "Vérifier le mot de passe actuel")
    @PostMapping("/verify-password")
    public ResponseEntity<ApiResponse<Boolean>> verifyPassword(@RequestBody PasswordRequestDto passwordRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = postOffer.getUserByEmail(email);
        boolean matches = passwordEncoder.matches(passwordRequestDto.getPassword(), user.getPassword());
        return ResponseEntity
                .ok(ApiResponse.success(matches ? "Password is correct" : "Password is incorrect", matches));
    }
}
