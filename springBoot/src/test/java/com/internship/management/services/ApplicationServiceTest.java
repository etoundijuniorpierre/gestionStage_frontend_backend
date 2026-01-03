package com.internship.management.services;

import com.internship.management.dto.application.ApplicationRequestDto;
import com.internship.management.dto.application.ApplicationResponseDto;
import com.internship.management.entities.*;
import com.internship.management.enums.ApplicationState;
import com.internship.management.exception.BusinessException;
import com.internship.management.interfaces.NotificationInterface;
import com.internship.management.interfaces.PostOffer;
import com.internship.management.mappers.PostOfferMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour ApplicationService.
 * Couverture: 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationService Tests")
class ApplicationServiceTest {

    @Mock
    private PostOffer postOffer;

    @Mock
    private PostOfferMapper mapper;

    @Mock
    private NotificationInterface notificationInterface;

    @InjectMocks
    private ApplicationService applicationService;

    private Student student;
    private Offer offer;
    private Enterprise enterprise;
    private Application application;
    private ApplicationRequestDto requestDto;
    private ApplicationResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // Setup Student
        student = new Student();
        student.setId(1L);
        student.setEmail("student@test.com");
        student.setFirstName("John");
        student.setName("Doe");
        student.setOnInternship(false);
        student.setApplications(new ArrayList<>());

        // Setup Enterprise
        enterprise = new Enterprise();
        enterprise.setId(1L);
        enterprise.setEmail("enterprise@test.com");

        // Setup Offer
        offer = new Offer();
        offer.setId(1L);
        offer.setTitle("Java Developer Internship");
        offer.setEnterprise(enterprise);

        // Setup Application
        application = new Application();
        application.setId(1L);
        application.setStudent(student);
        application.setOffer(offer);
        application.setEnterprise(enterprise);
        application.setState(ApplicationState.PENDING);

        // Setup DTOs
        requestDto = new ApplicationRequestDto();
        responseDto = new ApplicationResponseDto();
        responseDto.setId(1L);
    }

    @Test
    @DisplayName("Should create application successfully")
    void testCreateApplication_Success() {
        // Given
        when(postOffer.getStudentByEmail(student.getEmail())).thenReturn(student);
        when(postOffer.getOfferById(offer.getId())).thenReturn(offer);
        when(mapper.toEntity(requestDto)).thenReturn(application);
        when(mapper.toDto(application)).thenReturn(responseDto);

        // When
        ApplicationResponseDto result = applicationService.createApplication(
                student.getEmail(), offer.getId(), requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(postOffer).getStudentByEmail(student.getEmail());
        verify(postOffer).getOfferById(offer.getId());
        verify(postOffer).saveApplication(any(Application.class));
        verify(notificationInterface).sendNotification(eq(enterprise), anyString());
    }

    @Test
    @DisplayName("Should throw exception when student is already on internship")
    void testCreateApplication_StudentAlreadyOnInternship() {
        // Given
        student.setOnInternship(true);
        when(postOffer.getStudentByEmail(student.getEmail())).thenReturn(student);

        // When & Then
        assertThatThrownBy(() -> applicationService.createApplication(
                student.getEmail(), offer.getId(), requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already on internship");

        verify(postOffer, never()).saveApplication(any());
        verify(notificationInterface, never()).sendNotification(any(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when student already has pending application")
    void testCreateApplication_AlreadyApplied() {
        // Given
        Application existingApp = new Application();
        existingApp.setOffer(offer);
        existingApp.setState(ApplicationState.PENDING);
        student.getApplications().add(existingApp);

        when(postOffer.getStudentByEmail(student.getEmail())).thenReturn(student);

        // When & Then
        assertThatThrownBy(() -> applicationService.createApplication(
                student.getEmail(), offer.getId(), requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already have a pending");

        verify(postOffer, never()).saveApplication(any());
    }

    @Test
    @DisplayName("Should throw exception when student already has approved application")
    void testCreateApplication_AlreadyApproved() {
        // Given
        Application existingApp = new Application();
        existingApp.setOffer(offer);
        existingApp.setState(ApplicationState.APPROVED);
        student.getApplications().add(existingApp);

        when(postOffer.getStudentByEmail(student.getEmail())).thenReturn(student);

        // When & Then
        assertThatThrownBy(() -> applicationService.createApplication(
                student.getEmail(), offer.getId(), requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already have a pending or approved");

        verify(postOffer, never()).saveApplication(any());
    }

    @Test
    @DisplayName("Should allow application when previous application was rejected")
    void testCreateApplication_PreviousRejected() {
        // Given
        Application rejectedApp = new Application();
        rejectedApp.setOffer(offer);
        rejectedApp.setState(ApplicationState.REJECTED);
        student.getApplications().add(rejectedApp);

        when(postOffer.getStudentByEmail(student.getEmail())).thenReturn(student);
        when(postOffer.getOfferById(offer.getId())).thenReturn(offer);
        when(mapper.toEntity(requestDto)).thenReturn(application);
        when(mapper.toDto(application)).thenReturn(responseDto);

        // When
        ApplicationResponseDto result = applicationService.createApplication(
                student.getEmail(), offer.getId(), requestDto);

        // Then
        assertThat(result).isNotNull();
        verify(postOffer).saveApplication(any(Application.class));
    }

    @Test
    @DisplayName("Should update student status when accepting application")
    void testUpdateStudentStatus_Accept() {
        // Given
        application.setStudent(student);
        when(postOffer.getStudentByEmail(student.getEmail())).thenReturn(student);
        when(postOffer.getApplicationById(application.getId())).thenReturn(application);
        when(mapper.toDto(application)).thenReturn(responseDto);

        // When
        ApplicationResponseDto result = applicationService.updateStudentStatus(
                student.getEmail(), application.getId(), true);

        // Then
        assertThat(result).isNotNull();
        assertThat(student.isOnInternship()).isTrue();
        assertThat(application.getState()).isEqualTo(ApplicationState.APPROVED);

        verify(postOffer).saveUser(student);
        verify(postOffer).saveApplication(application);
    }

    @Test
    @DisplayName("Should not update student status when rejecting application")
    void testUpdateStudentStatus_Reject() {
        // Given
        application.setStudent(student);
        when(postOffer.getStudentByEmail(student.getEmail())).thenReturn(student);
        when(postOffer.getApplicationById(application.getId())).thenReturn(application);
        when(mapper.toDto(application)).thenReturn(responseDto);

        // When
        ApplicationResponseDto result = applicationService.updateStudentStatus(
                student.getEmail(), application.getId(), false);

        // Then
        assertThat(result).isNotNull();
        assertThat(student.isOnInternship()).isFalse();
        assertThat(application.getState()).isEqualTo(ApplicationState.PENDING);

        verify(postOffer, never()).saveUser(any());
        verify(postOffer, never()).saveApplication(any());
    }

    @Test
    @DisplayName("Should throw exception when student tries to manage another student's application")
    void testUpdateStudentStatus_WrongOwner() {
        // Given
        Student anotherStudent = new Student();
        anotherStudent.setId(2L);
        anotherStudent.setEmail("another@test.com");
        application.setStudent(anotherStudent);

        when(postOffer.getStudentByEmail(student.getEmail())).thenReturn(student);
        when(postOffer.getApplicationById(application.getId())).thenReturn(application);

        // When & Then
        assertThatThrownBy(() -> applicationService.updateStudentStatus(
                student.getEmail(), application.getId(), true))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("only manage your own applications");

        verify(postOffer, never()).saveUser(any());
        verify(postOffer, never()).saveApplication(any());
    }

    @Test
    @DisplayName("Should get all student applications")
    void testGetStudentApplications_All() {
        // Given
        Application app1 = new Application();
        app1.setState(ApplicationState.PENDING);
        Application app2 = new Application();
        app2.setState(ApplicationState.APPROVED);
        student.getApplications().add(app1);
        student.getApplications().add(app2);

        when(postOffer.getStudentByEmail(student.getEmail())).thenReturn(student);
        when(mapper.toDto(any(Application.class))).thenReturn(responseDto);

        // When
        List<ApplicationResponseDto> result = applicationService.getStudentApplications(
                student.getEmail(), null);

        // Then
        assertThat(result).hasSize(2);
        verify(mapper, times(2)).toDto(any(Application.class));
    }

    @Test
    @DisplayName("Should get student applications filtered by state")
    void testGetStudentApplications_FilteredByState() {
        // Given
        Application app1 = new Application();
        app1.setState(ApplicationState.PENDING);
        Application app2 = new Application();
        app2.setState(ApplicationState.APPROVED);
        Application app3 = new Application();
        app3.setState(ApplicationState.PENDING);
        student.getApplications().add(app1);
        student.getApplications().add(app2);
        student.getApplications().add(app3);

        when(postOffer.getStudentByEmail(student.getEmail())).thenReturn(student);
        when(mapper.toDto(any(Application.class))).thenReturn(responseDto);

        // When
        List<ApplicationResponseDto> result = applicationService.getStudentApplications(
                student.getEmail(), ApplicationState.PENDING);

        // Then
        assertThat(result).hasSize(2);
        verify(mapper, times(2)).toDto(any(Application.class));
    }

    @Test
    @DisplayName("Should return empty list when no applications match filter")
    void testGetStudentApplications_EmptyResult() {
        // Given
        Application app1 = new Application();
        app1.setState(ApplicationState.PENDING);
        student.getApplications().add(app1);

        when(postOffer.getStudentByEmail(student.getEmail())).thenReturn(student);

        // When
        List<ApplicationResponseDto> result = applicationService.getStudentApplications(
                student.getEmail(), ApplicationState.APPROVED);

        // Then
        assertThat(result).isEmpty();
        verify(mapper, never()).toDto(any(Application.class));
    }
}
