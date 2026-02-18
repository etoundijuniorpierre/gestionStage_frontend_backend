package com.internship.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentResponseDto {

    private Long id;
    private String name;
    private String firstName;
    private String email;
    private boolean onInternship;
    private String department;
    private String sector;
    private List<String> languages;
    private String githubLink;
    private String linkedinLink;
}
