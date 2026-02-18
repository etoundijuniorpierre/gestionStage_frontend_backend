package com.internship.management.entities;


import com.internship.management.enums.EnterpriseState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Enterprise extends Users {

    private String matriculation;
    private String sectorOfActivity;
    private String contact;
    private String location;
    private String city;
    private String country;
    private boolean inPartnership;

    @Enumerated(EnumType.STRING)
    private EnterpriseState enterpriseState = EnterpriseState.PENDING;

    @OneToOne(mappedBy = "enterprise", cascade = CascadeType.ALL, orphanRemoval = true)
    private Logo logo;

    @OneToMany(mappedBy = "enterprise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Offer> offers = new ArrayList<>();

    @OneToMany(mappedBy ="enterprise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();
}
