package com.internship.management.dto.application;

import com.internship.management.enums.OfferStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationOfferDto {

    private String title;
    private String description;
    private String domain;
    private OfferStatus status;
}
