package com.internship.management.services;

import com.internship.management.entities.Convention;
import com.internship.management.interfaces.ConventionService;
import com.internship.management.repositories.ConventionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConventionServiceImpl implements ConventionService {

    private final ConventionRepository conventionRepository;

    @Override
    public Convention getConventionByOfferId(Long offerId) {
        return conventionRepository.findByOffer_Id(offerId)
                .orElseThrow(() -> new RuntimeException("Convention not found for offer ID: " + offerId));
    }

    @Override
    public void saveConvention(Convention convention) {
        conventionRepository.save(convention);
    }
}
