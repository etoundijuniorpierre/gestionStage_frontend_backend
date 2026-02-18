package com.internship.management.dto.registration;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentRegistrationRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "FirstName is required")
    private String firstName;


    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;


    @NotBlank(message = "Sector is required")
    private String sector;

    private List<String> languages;

    private String department;

    private String githubLink;

    private String linkedinLink;
}
