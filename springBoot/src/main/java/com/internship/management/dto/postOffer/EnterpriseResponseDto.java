package com.internship.management.dto.postOffer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnterpriseResponseDto {

    private Long id;
    private String name;
    private String email;
    private String sectorOfActivity;
    private boolean inPartnership;
    private String matriculation;
    private String country;
    private String city;
    private String contact;
    private String location;
    private HasLogoDto hasLogo;
    private List<MiniOfferResponseDto> offers;
}
