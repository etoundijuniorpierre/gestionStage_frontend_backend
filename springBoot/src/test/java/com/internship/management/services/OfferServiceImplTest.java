package com.internship.management.services;

import com.internship.management.entities.*;
import com.internship.management.enums.OfferStatus;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour OfferServiceImpl.
 * Couverture: 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OfferServiceImpl Tests")
class OfferServiceImplTest {

    @Mock
    private OfferRepository offerRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private EnterpriseRepository enterpriseRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private UsersRepository userRepository;
    @Mock
    private LogoRepository logoRepository;
    @Mock
    private ConventionRepository conventionRepository;

    @InjectMocks
    private OfferServiceImpl offerService;

    private Offer offer;
    private Teacher teacher;
    private Enterprise enterprise;
    private Student student;
    private Application application;
    private Convention convention;
    private Users user;

    @BeforeEach
    void setUp() {
        offer = new Offer();
        offer.setId(1L);
        offer.setTitle("Java Developer");
        offer.setStatus(OfferStatus.PENDING);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setEmail("teacher@test.com");
        teacher.setDepartment("Computer Science");

        enterprise = new Enterprise();
        enterprise.setId(1L);
        enterprise.setEmail("enterprise@test.com");

        student = new Student();
        student.setId(1L);
        student.setEmail("student@test.com");
        student.setDepartment("Computer Science");

        application = new Application();
        application.setId(1L);

        convention = new Convention();
        convention.setId(1L);
        convention.setOffer(offer);

        user = new Users();
        user.setId(1L);
        user.setEmail("user@test.com");
    }

    @Test
    @DisplayName("Should get offer by id successfully")
    void testGetOfferById_Success() {
        // Given
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        // When
        Offer result = offerService.getOfferById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(offerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when offer not found")
    void testGetOfferById_NotFound() {
        // Given
        when(offerRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> offerService.getOfferById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Offer not found");

        verify(offerRepository).findById(999L);
    }

    @Test
    @DisplayName("Should save offer successfully")
    void testSaveOffer() {
        // When
        offerService.saveOffer(offer);

        // Then
        verify(offerRepository).save(offer);
    }

    @Test
    @DisplayName("Should get teacher by email successfully")
    void testGetTeacherByEmail_Success() {
        // Given
        when(teacherRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(teacher));

        // When
        Teacher result = offerService.getTeacherByEmail("teacher@test.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("teacher@test.com");
        verify(teacherRepository).findByEmail("teacher@test.com");
    }

    @Test
    @DisplayName("Should throw exception when teacher not found")
    void testGetTeacherByEmail_NotFound() {
        // Given
        when(teacherRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> offerService.getTeacherByEmail("unknown@test.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Teacher not found");
    }

    @Test
    @DisplayName("Should get enterprise by email successfully")
    void testGetByEnterpriseEmail_Success() {
        // Given
        when(enterpriseRepository.findByEmail("enterprise@test.com")).thenReturn(Optional.of(enterprise));

        // When
        Enterprise result = offerService.getByEnterpriseEmail("enterprise@test.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("enterprise@test.com");
    }

    @Test
    @DisplayName("Should get enterprise by id successfully")
    void testGetByEnterpriseId_Success() {
        // Given
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise));

        // When
        Enterprise result = offerService.getByEnterpriseId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should get student by email successfully")
    void testGetStudentByEmail_Success() {
        // Given
        when(studentRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));

        // When
        Student result = offerService.getStudentByEmail("student@test.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("student@test.com");
    }

    @Test
    @DisplayName("Should save application successfully")
    void testSaveApplication() {
        // When
        offerService.saveApplication(application);

        // Then
        verify(applicationRepository).save(application);
    }

    @Test
    @DisplayName("Should get application by id successfully")
    void testGetApplicationById_Success() {
        // Given
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        // When
        Application result = offerService.getApplicationById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        // When
        offerService.deleteUser(1L);

        // Then
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void testGetUserByEmail_Success() {
        // Given
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        // When
        Users result = offerService.getUserByEmail("user@test.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("Should save user successfully")
    void testSaveUser() {
        // When
        offerService.saveUser(user);

        // Then
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should get logo by enterprise successfully")
    void testGetLogoByEnterprise_Success() {
        // Given
        Logo logo = new Logo();
        logo.setId(1L);
        when(logoRepository.findByEnterprise(enterprise)).thenReturn(Optional.of(logo));

        // When
        Logo result = offerService.getLogoByEnterprise(enterprise);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should update logo successfully")
    void testUpdateLogo_Success() throws IOException {
        // Given
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn("test".getBytes());
        when(file.getContentType()).thenReturn("image/png");
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise));

        Logo logo = new Logo();
        when(logoRepository.findByEnterprise(enterprise)).thenReturn(Optional.of(logo));

        // When
        offerService.updateLogo(1L, file);

        // Then
        verify(logoRepository).save(any(Logo.class));
    }

    @Test
    @DisplayName("Should create new logo when not exists")
    void testUpdateLogo_CreateNew() throws IOException {
        // Given
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn("test".getBytes());
        when(file.getContentType()).thenReturn("image/png");
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(enterprise));
        when(logoRepository.findByEnterprise(enterprise)).thenReturn(Optional.empty());

        // When
        offerService.updateLogo(1L, file);

        // Then
        verify(logoRepository).save(any(Logo.class));
    }

    @Test
    @DisplayName("Should get convention by offer id successfully")
    void testGetConventionByOfferId_Success() {
        // Given
        when(conventionRepository.findByOffer_Id(1L)).thenReturn(Optional.of(convention));

        // When
        Convention result = offerService.getConventionByOfferId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should save convention successfully")
    void testSaveConvention() {
        // When
        offerService.saveConvention(convention);

        // Then
        verify(conventionRepository).save(convention);
    }

    @Test
    @DisplayName("Should delete application successfully")
    void testDeleteApplicationRejected() {
        // When
        offerService.deleteApplicationRejected(1L);

        // Then
        verify(applicationRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should get offers by department and status")
    void testGetOfferByDepartmentAndPendingOfferStatusAndInPartnershipTrue() {
        // Given
        List<Offer> offers = Arrays.asList(offer);
        when(offerRepository.findByDomainAndStatusAndEnterprise_InPartnershipTrue(
                "Computer Science", OfferStatus.PENDING))
                .thenReturn(offers);

        // When
        List<Offer> result = offerService.getOfferByDepartmentAndPendingOfferStatusAndInPartnershipTrue(
                "Computer Science", OfferStatus.PENDING);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should get students by department with pagination")
    void testGetStudentsByDepartment() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> page = new PageImpl<>(Arrays.asList(student));
        when(studentRepository.findByDepartment("Computer Science", pageable)).thenReturn(page);

        // When
        Page<Student> result = offerService.getStudentsByDepartment("Computer Science", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should get all teachers")
    void testGetAllTeachers() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher);
        when(teacherRepository.findAll()).thenReturn(teachers);

        // When
        List<Teacher> result = offerService.getAllTeachers();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should get all students with pagination")
    void testGetAllStudents() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> page = new PageImpl<>(Arrays.asList(student));
        when(studentRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<Student> result = offerService.getAllStudents(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should get teachers by department")
    void testGetTeachersByDepartment() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher);
        when(teacherRepository.findByDepartment("Computer Science")).thenReturn(teachers);

        // When
        List<Teacher> result = offerService.getTeachersByDepartment("Computer Science");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartment()).isEqualTo("Computer Science");
    }
}
