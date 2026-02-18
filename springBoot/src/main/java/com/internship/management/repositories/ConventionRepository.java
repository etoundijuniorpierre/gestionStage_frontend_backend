package com.internship.management.repositories;

import com.internship.management.entities.Convention;
import com.internship.management.entities.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConventionRepository extends JpaRepository<Convention,Long> {

    Optional<Convention> findByOffer_Id(Long offerId);

}
