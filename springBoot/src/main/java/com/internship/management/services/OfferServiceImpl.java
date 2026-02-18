package com.internship.management.services;

import com.internship.management.entities.Offer;
import com.internship.management.enums.ConventionState;
import com.internship.management.enums.OfferStatus;
import com.internship.management.interfaces.OfferService;
import com.internship.management.repositories.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;

    @Override
    public Offer getOfferById(Long id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer Not Found ID: " + id));
    }

    @Override
    public void saveOffer(Offer offer) {
        offerRepository.save(offer);
    }

    @Override
    public List<Offer> getOffersByStatusAndConventionApproved(OfferStatus offerStatus, ConventionState conventionState,
            String domain) {
        return offerRepository.findOffersByStatusAndConventionStateAndDomain(offerStatus, conventionState, domain);
    }

    @Override
    public List<Offer> getOfferByEnterpriseId(Long enterpriseId) {
        return offerRepository.findOfferByEnterpriseId(enterpriseId);
    }

    @Override
    public List<Offer> getOfferByDepartmentAndPendingOfferStatusAndInPartnershipTrue(String department,
            OfferStatus offerStatus) {
        return offerRepository.findByDomainAndStatusAndEnterprise_InPartnershipTrue(department, offerStatus);
    }

    @Override
    public List<Offer> getOffersByStatusApprovedAndTeacherEmail(OfferStatus offerStatus, String email) {
        return offerRepository.findOffersByStatusAndValidatedBy_Email(offerStatus, email);
    }
}
