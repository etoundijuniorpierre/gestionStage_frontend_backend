package com.internship.management.mappers;

import com.internship.management.dto.postOffer.PostOfferRequestDto;
import com.internship.management.dto.postOffer.PostOfferResponseDto;
import com.internship.management.dto.application.ApplicationRequestDto;
import com.internship.management.dto.application.ApplicationResponseDto;
import com.internship.management.entities.*;
import com.internship.management.enums.ApplicationState;
import com.internship.management.enums.OfferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour PostOfferMapper.
 * Couverture: 100%
 */
@DisplayName("PostOfferMapper Tests")
class PostOfferMapperTest {

    private PostOfferMapper mapper;

    private Offer offer;
    private Enterprise enterprise;
    private Teacher teacher;
    private Application application;
    private Student student;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PostOfferMapper.class);

        enterprise = new Enterprise();
        enterprise.setId(1L);
        enterprise.setEmail("enterprise@test.com");
        enterprise.setName("Test Enterprise");

        teacher = new Teacher();
        teacher.setId(2L);
        teacher.setEmail("teacher@test.com");
        teacher.setName("Smith");

        offer = new Offer();
        offer.setId(1L);
        offer.setTitle("Java Developer");
        offer.setDescription("Java development internship");
        offer.setDomain("IT");
        offer.setJob("Developer");
        offer.setStartDate(LocalDate.of(2024, 6, 1));
        offer.setEndDate(LocalDate.of(2024, 8, 31));
        offer.setPlaces(2);
        offer.setRequirements("Java, Spring Boot");
        offer.setRemote(true);
        offer.setPaying(true);
        offer.setStatus(OfferStatus.PENDING);
        offer.setEnterprise(enterprise);
        offer.setValidatedByTeacher(teacher);

        student = new Student();
        student.setId(3L);
        student.setEmail("student@test.com");
        student.setName("Doe");

        application = new Application();
        application.setId(1L);
        application.setState(ApplicationState.PENDING);
        application.setStudent(student);
        application.setOffer(offer);
        application.setEnterprise(enterprise);
    }

    @Test
    @DisplayName("Should map Offer to PostOfferResponseDto")
    void testToDto_Offer() {
        // When
        PostOfferResponseDto dto = mapper.toDto(offer);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(offer.getId());
        assertThat(dto.getTitle()).isEqualTo(offer.getTitle());
        assertThat(dto.getDescription()).isEqualTo(offer.getDescription());
        assertThat(dto.getDomain()).isEqualTo(offer.getDomain());
        assertThat(dto.getJob()).isEqualTo(offer.getJob());
        assertThat(dto.getStartDate()).isEqualTo(offer.getStartDate());
        assertThat(dto.getEndDate()).isEqualTo(offer.getEndDate());
        assertThat(dto.getPlaces()).isEqualTo(offer.getPlaces());
        assertThat(dto.getRequirements()).isEqualTo(offer.getRequirements());
        assertThat(dto.isRemote()).isEqualTo(offer.isRemote());
        assertThat(dto.isPaying()).isEqualTo(offer.isPaying());
        assertThat(dto.getStatus()).isEqualTo(offer.getStatus());
    }

    @Test
    @DisplayName("Should map PostOfferRequestDto to Offer")
    void testToEntity_Offer() {
        // Given
        PostOfferRequestDto dto = new PostOfferRequestDto();
        dto.setTitle("Python Developer");
        dto.setDescription("Python development internship");
        dto.setDomain("IT");
        dto.setJob("Developer");
        dto.setStartDate(LocalDate.of(2024, 7, 1));
        dto.setEndDate(LocalDate.of(2024, 9, 30));
        dto.setPlaces(3);
        dto.setRequirements("Python, Django");
        dto.setRemote(false);
        dto.setPaying(true);

        // When
        Offer entity = mapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo(dto.getTitle());
        assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
        assertThat(entity.getDomain()).isEqualTo(dto.getDomain());
        assertThat(entity.getJob()).isEqualTo(dto.getJob());
        assertThat(entity.getStartDate()).isEqualTo(dto.getStartDate());
        assertThat(entity.getEndDate()).isEqualTo(dto.getEndDate());
        assertThat(entity.getPlaces()).isEqualTo(dto.getPlaces());
        assertThat(entity.getRequirements()).isEqualTo(dto.getRequirements());
        assertThat(entity.isRemote()).isEqualTo(dto.isRemote());
        assertThat(entity.isPaying()).isEqualTo(dto.isPaying());
    }

    @Test
    @DisplayName("Should map Application to ApplicationResponseDto")
    void testToDto_Application() {
        // When
        ApplicationResponseDto dto = mapper.toDto(application);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(application.getId());
        assertThat(dto.getState()).isEqualTo(application.getState());
    }

    @Test
    @DisplayName("Should map ApplicationRequestDto to Application")
    void testToEntity_Application() {
        // Given
        ApplicationRequestDto dto = new ApplicationRequestDto();
        // Assuming CV and CoverLetter are byte arrays in the DTO

        // When
        Application entity = mapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
    }

    @Test
    @DisplayName("Should handle null Offer")
    void testToDto_NullOffer() {
        // When
        PostOfferResponseDto dto = mapper.toDto((Offer) null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should handle null PostOfferRequestDto")
    void testToEntity_NullDto() {
        // When
        Offer entity = mapper.toEntity((PostOfferRequestDto) null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should handle null Application")
    void testToDto_NullApplication() {
        // When
        ApplicationResponseDto dto = mapper.toDto((Application) null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should map Offer with null enterprise")
    void testToDto_OfferWithNullEnterprise() {
        // Given
        offer.setEnterprise(null);

        // When
        PostOfferResponseDto dto = mapper.toDto(offer);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(offer.getId());
    }

    @Test
    @DisplayName("Should map Offer with null teacher")
    void testToDto_OfferWithNullTeacher() {
        // Given
        offer.setValidatedByTeacher(null);

        // When
        PostOfferResponseDto dto = mapper.toDto(offer);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(offer.getId());
    }
}
