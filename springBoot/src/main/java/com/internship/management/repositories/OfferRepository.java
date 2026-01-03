package com.internship.management.repositories;

import com.internship.management.entities.Offer;
import com.internship.management.entities.Teacher;
import com.internship.management.enums.ConventionState;
import com.internship.management.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {

        List<Offer> findByDomainAndStatusAndEnterprise_InPartnershipTrue(String domain, OfferStatus status);

        Page<Offer> findByDomainAndStatusAndEnterprise_InPartnershipTrue(String domain, OfferStatus status,
                        Pageable pageable);

        @Query("SELECT o FROM Offer o JOIN o.convention c " +
                        "WHERE o.status = :offerStatus AND c.conventionState = :conventionState AND o.domain = :domain")
        List<Offer> findOffersByStatusAndConventionStateAndDomain(
                        @Param("offerStatus") OfferStatus offerStatus,
                        @Param("conventionState") ConventionState conventionState,
                        @Param("domain") String domain);

        @Query("SELECT o FROM Offer o JOIN o.convention c " +
                        "WHERE o.status = :offerStatus AND c.conventionState = :conventionState AND o.domain = :domain")
        Page<Offer> findOffersByStatusAndConventionStateAndDomainPaged(
                        @Param("offerStatus") OfferStatus offerStatus,
                        @Param("conventionState") ConventionState conventionState,
                        @Param("domain") String domain,
                        Pageable pageable);

        List<Offer> findOfferByEnterpriseId(Long enterpriseId);

        Page<Offer> findOfferByEnterpriseId(Long enterpriseId, Pageable pageable);

        List<Offer> findByPaying(boolean paying);

        List<Offer> findByRemote(boolean remote);

        List<Offer> findByPayingAndRemote(boolean paying, boolean remote);

        List<Offer> findOffersByStatusAndValidatedBy_Email(OfferStatus status, String email);

        Page<Offer> findOffersByStatusAndValidatedBy_Email(OfferStatus status, String email, Pageable pageable);
}
