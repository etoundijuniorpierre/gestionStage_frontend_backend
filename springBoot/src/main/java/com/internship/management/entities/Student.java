package com.internship.management.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Student extends Users {

    private String firstName;
    private String sector;
    private String department;
    private List<String> languages;
    private String githubLink;
    private String linkedinLink;
    private boolean onInternship;

    @Lob
    private byte[] photo;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();
}
