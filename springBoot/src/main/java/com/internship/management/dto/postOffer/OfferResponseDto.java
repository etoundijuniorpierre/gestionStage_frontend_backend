package com.internship.management.dto.postOffer;


import com.internship.management.enums.OfferStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OfferResponseDto {

    private Long id;
    private String title;
    private String description;
    private String domain;
    private String job;
    private String typeOfInternship;
    private Integer numberOfPlaces;
    private Long durationOfInternship;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean remote;
    private boolean paying;
    private OfferStatus status;
    private EnterpriseResponseDto enterprise;
    private ConventionResponseDto convention;
}
