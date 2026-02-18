package com.internship.management.repositories;

import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Logo;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface LogoRepository extends JpaRepository<Logo, Long> {

    Optional<Logo> findByEnterprise(Enterprise enterprise);
}
