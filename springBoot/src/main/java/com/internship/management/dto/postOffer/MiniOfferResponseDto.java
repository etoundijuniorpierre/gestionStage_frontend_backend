package com.internship.management.dto.postOffer;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MiniOfferResponseDto {
    private Long id;
    private String title;
    private String description;
    private String domain;
    private String job;
    private Integer numberOfPlaces;
}
