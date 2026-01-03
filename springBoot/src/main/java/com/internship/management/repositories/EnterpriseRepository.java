package com.internship.management.repositories;

import com.internship.management.entities.Enterprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

    Optional<Enterprise> findByEmail(String email);

    List<Enterprise> findByInPartnershipFalse();

    Page<Enterprise> findByInPartnershipFalse(Pageable pageable);

    List<Enterprise> findByInPartnershipTrue();

    Page<Enterprise> findByInPartnershipTrue(Pageable pageable);

}
