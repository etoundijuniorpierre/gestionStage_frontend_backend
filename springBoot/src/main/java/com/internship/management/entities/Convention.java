package com.internship.management.entities;

import com.internship.management.enums.ConventionState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;




@Entity
@Getter
@Setter
public class Convention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Lob
    private byte[] pdfConvention;


    @Enumerated(EnumType.STRING)
    private ConventionState conventionState = ConventionState.PENDING;

//    @OneToOne
//    @JoinColumn(name = "application_id", unique = true)
//    private Application application;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher reviewer;


    @OneToOne
    @JoinColumn(name = "offer_id", unique = true)
    private Offer offer;
}
