package com.internship.management.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Teacher extends Users {

    private String firstName;
    private String department;

    @OneToMany(mappedBy = "validatedBy")
    private List<Offer> validatedOffers = new ArrayList<>();

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL)
    private List<Convention> conventions = new ArrayList<>();
}
