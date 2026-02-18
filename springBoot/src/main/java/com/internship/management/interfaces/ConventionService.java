package com.internship.management.interfaces;

import com.internship.management.entities.Convention;

public interface ConventionService {
    Convention getConventionByOfferId(Long offerId);

    void saveConvention(Convention convention);
}
