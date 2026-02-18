package com.internship.management.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String originalFileName;

    private String fileType;

    @Lob
    private byte[] fileData;

    private LocalDateTime uploadDate = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
