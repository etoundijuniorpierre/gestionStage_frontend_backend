package com.internship.management.repositories;

import com.internship.management.entities.Offer;
import com.internship.management.entities.Teacher;
import com.internship.management.enums.ConventionState;
import com.internship.management.enums.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface OfferRepository extends JpaRepository<Offer,Long> {

     List<Offer> findByDomainAndStatusAndEnterprise_InPartnershipTrue(String domain, OfferStatus status);


     @Query("SELECT o FROM Offer o JOIN o.convention c " +
             "WHERE o.status = :offerStatus AND c.conventionState = :conventionState AND o.domain = :domain")
     List<Offer> findOffersByStatusAndConventionStateAndDomain(
             @Param("offerStatus") OfferStatus offerStatus,
             @Param("conventionState") ConventionState conventionState,
             @Param("domain") String domain
     );

     List<Offer> findOfferByEnterpriseId(Long enterpriseId);

     List<Offer> findByPaying(boolean paying);
     List<Offer> findByRemote(boolean Remote);

     List<Offer> findByPayingAndRemote(boolean paying, boolean remote);

     List<Offer> findOffersByStatusAndValidatedBy_Email(OfferStatus status, String email);
}
