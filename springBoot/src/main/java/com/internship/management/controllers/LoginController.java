package com.internship.management.controllers;

import com.internship.management.dto.LoginRequest;
import com.internship.management.dto.response.ApiResponse;
import com.internship.management.entities.Users;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.repositories.UsersRepository;
import com.internship.management.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth/login")
@RequiredArgsConstructor
@Tag(name = "Login Controller", description = "Endpoints pour l'authentification")
public class LoginController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UsersRepository userRepository;

    @Operation(summary = "Authentifier un utilisateur et retourner un token JWT")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        log.info("Login attempt for email: {}", loginRequest.getEmail());

        Users user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginRequest.getEmail()));

        if (!user.isEmailVerified()) {
            log.warn("Login attempt with unverified email: {}", loginRequest.getEmail());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email not verified. Please verify your email first."));
        }

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String token = jwtService.generateToken(user);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("role", user.getRole().name());
        responseData.put("email", user.getEmail());
        responseData.put("name", user.getName());

        log.info("Login successful for user: {} with role: {}", user.getEmail(), user.getRole());

        return ResponseEntity.ok(ApiResponse.success("Login successful", responseData));
    }
}
