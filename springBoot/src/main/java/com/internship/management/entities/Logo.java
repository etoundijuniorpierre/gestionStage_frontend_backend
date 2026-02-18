package com.internship.management.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Logo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] logo;

    @OneToOne
    @JoinColumn(name = "enterprise_id")
    private Enterprise enterprise;

    private String contentType;
}

