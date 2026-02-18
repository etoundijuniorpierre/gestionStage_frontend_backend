package com.internship.management.dto.application;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationRequestDto {

    private MultipartFile cv;
    private MultipartFile coverLetter;
}
