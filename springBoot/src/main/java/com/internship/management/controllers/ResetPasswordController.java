package com.internship.management.controllers;

import com.internship.management.dto.ResetPasswordRequestDto;
import com.internship.management.dto.registration.TokenVerificationRequestDto;
import com.internship.management.entities.Users;
import com.internship.management.interfaces.UserService;
import com.internship.management.services.registrationService.VerificationTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/resetPassword")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;

    @PostMapping("/sendTokenWhenResetting")
    public ResponseEntity<String> sendTokenWhenResetting(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {

        Users user = userService.getUserByEmail(resetPasswordRequestDto.getEmail());
        verificationTokenService.createAndSendToken(user);

        return ResponseEntity.ok("token was sent to " + user.getName() + " for verification");
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody TokenVerificationRequestDto request) {

        try {
            Users userVerified = verificationTokenService.verifyCode(request.getEmail(), request.getToken());

        } catch (Exception e) {
            ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("token verified");
    }

    @PatchMapping
    public void resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {

        Users user = userService.getUserByEmail(resetPasswordRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getPassword()));

        userService.saveUser(user);
    }
}
