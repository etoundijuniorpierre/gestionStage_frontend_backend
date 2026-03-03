package com.internship.management.controllers;

import com.internship.management.dto.LoginRequest;
import com.internship.management.dto.UserStatusDto;
import com.internship.management.entities.Users;
import com.internship.management.enums.UserStatus;
import com.internship.management.repositories.UsersRepository;
import com.internship.management.security.JwtService;
import com.internship.management.services.registrationService.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UsersRepository userRepository;
    private final VerificationTokenService verificationTokenService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {

        Users user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Si l'utilisateur existe mais est inactif, renvoyer un token et message
        // approprié
        if (user.getStatus() == UserStatus.INACTIF) {
            try {
                verificationTokenService.createAndSendToken(user);
                UserStatusDto response = new UserStatusDto(
                        user.getEmail(),
                        UserStatus.INACTIF,
                        "An account with this email already exists but is inactive. A new verification code has been sent to your email. Please check your emails to activate your account. If this is not your account, please create a new one with a different email address.");
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Account inactive",
                        "userStatus", response));
            } catch (Exception e) {
                // Logger l'erreur pour le diagnostic
                System.err.println("Error in createAndSendToken: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of(
                        "error", "Server error during token generation",
                        "details", e.getMessage()));
            }
        }

        if (!user.isEmailVerified()) {
            throw new RuntimeException("User is not verified");
        }

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String token = jwtService.generateToken(user);
        Map<String, Object> response = Map.of(
                "token", token,
                "role", user.getRole().name());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Backend is awake");
    }
}
