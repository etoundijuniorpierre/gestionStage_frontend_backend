package com.internship.management.interfaces;

import com.internship.management.entities.Offer;
import com.internship.management.enums.ConventionState;
import com.internship.management.enums.OfferStatus;

import java.util.List;

public interface OfferService {
    Offer getOfferById(Long id);

    void saveOffer(Offer offer);

    List<Offer> getOffersByStatusAndConventionApproved(OfferStatus offerStatus, ConventionState conventionState,
            String domain);

    List<Offer> getOfferByEnterpriseId(Long enterpriseId);

    List<Offer> getOfferByDepartmentAndPendingOfferStatusAndInPartnershipTrue(String department,
            OfferStatus offerStatus);

    List<Offer> getOffersByStatusApprovedAndTeacherEmail(OfferStatus offerStatus, String email);
}
