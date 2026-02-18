package com.internship.management.dto.application;

import com.internship.management.enums.ApplicationState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationResponseDto {

    private Long id;
    private ApplicationState state;
    private HasFilesDto hasFiles;
    private StudentApplicationDto student;
    private ApplicationOfferDto offer;
    private ApplicationEnterpriseDto enterprise;
}
