package com.internship.management;


import com.internship.management.entities.Users;
import com.internship.management.enums.Role;
import com.internship.management.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@admin.com";

        if (userRepository.existsByEmail(adminEmail)) return;

        Users admin = new Users();
        admin.setName("System");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("admin123")); // mot de passe encodé
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true); // pour éviter la vérif par email

        userRepository.save(admin);
        System.out.println("Admin created: " + adminEmail);
    }
}
