package com.internship.management.controllers;

import com.internship.management.dto.profile.EmailRequestDto;
import com.internship.management.dto.profile.PasswordRequestDto;
import com.internship.management.entities.Users;
import com.internship.management.interfaces.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "updateProfile")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class UpdateProfileUser {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PatchMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordRequestDto passwordRequestDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.getUserByEmail(email);

        user.setPassword(passwordEncoder.encode(passwordRequestDto.getPassword()));
        userService.saveUser(user);
        return ResponseEntity.ok().body("password updated successfully");
    }

    @PatchMapping("/updateEmail")
    public ResponseEntity<String> updateEmail(@RequestBody EmailRequestDto emailRequestDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.getUserByEmail(email);

        user.setEmail(emailRequestDto.getEmail());
        userService.saveUser(user);

        return ResponseEntity.ok().body(user.getName() + " email updated successfully");
    }

    @GetMapping("/getUserEmail")
    public ResponseEntity<String> getUserEmail() {
        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/getCurrentUser")
    public ResponseEntity<Users> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/deleteAccount/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/verifyPassword")
    public ResponseEntity<String> verifyPassword(@RequestBody PasswordRequestDto passwordRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.getUserByEmail(email);

        return passwordEncoder.matches(passwordRequestDto.getPassword(), user.getPassword())
                ? ResponseEntity.ok("Password is correct")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password is incorrect");
    }
}