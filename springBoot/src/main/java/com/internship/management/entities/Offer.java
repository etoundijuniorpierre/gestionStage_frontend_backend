package com.internship.management.entities;

import com.internship.management.enums.OfferStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String title;
    private String description;
    private String domain;
    private String job;
    private String typeOfInternship;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfPlaces;
    private String requirements;
    private boolean remote;
    private boolean paying;

    @Enumerated(EnumType.STRING)
    private OfferStatus status = OfferStatus.PENDING;;

    @ManyToOne
    @JoinColumn(name = "enterprise_id", nullable = false)
    private Enterprise enterprise;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher validatedBy; //

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

    @OneToOne(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Convention convention;
}
