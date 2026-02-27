package com.internship.management.entities;

import com.internship.management.enums.Role;
import com.internship.management.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String name;
    private String email;
    private String password;
    private boolean emailVerified;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.INACTIF;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<Notification> messages = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VerificationToken>  verificationTokens = new ArrayList<>();

}
