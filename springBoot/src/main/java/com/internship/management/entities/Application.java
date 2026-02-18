package com.internship.management.entities;

import com.internship.management.enums.ApplicationState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicationState state = ApplicationState.PENDING;

    @Lob
    private byte[] cv;

    @Lob
    private byte[] coverLetter;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

//    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Convention convention;
//
    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    private Enterprise enterprise;
}
