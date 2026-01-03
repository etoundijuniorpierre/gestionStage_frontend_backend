package com.internship.management.services.registrationService;

import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Student;
import com.internship.management.entities.Teacher;
import com.internship.management.entities.Users;
import com.internship.management.exception.DuplicateResourceException;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.repositories.EnterpriseRepository;
import com.internship.management.repositories.StudentRepository;
import com.internship.management.repositories.TeacherRepository;
import com.internship.management.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour RegistrationServiceImpl.
 * Couverture: 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RegistrationServiceImpl Tests")
class RegistrationServiceImplTest {

    @Mock
    private EnterpriseRepository enterpriseRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private VerificationTokenService verificationTokenService;

    @Mock
    private UsersRepository userRepository;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private Enterprise enterprise;
    private Student student;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        enterprise = new Enterprise();
        enterprise.setId(1L);
        enterprise.setEmail("enterprise@test.com");
        enterprise.setName("Test Enterprise");

        student = new Student();
        student.setId(2L);
        student.setEmail("student@test.com");
        student.setName("Doe");
        student.setFirstName("John");

        teacher = new Teacher();
        teacher.setId(3L);
        teacher.setEmail("teacher@test.com");
        teacher.setName("Smith");
        teacher.setFirstName("Jane");
    }

    @Test
    @DisplayName("Should register enterprise successfully")
    void testRegisterEnterprise_Success() {
        // Given
        when(userRepository.existsByEmail(enterprise.getEmail())).thenReturn(false);
        when(enterpriseRepository.save(enterprise)).thenReturn(enterprise);

        // When
        registrationService.registerEnterprise(enterprise);

        // Then
        verify(userRepository).existsByEmail(enterprise.getEmail());
        verify(enterpriseRepository).save(enterprise);
        verify(verificationTokenService).createAndSendToken(enterprise);
    }

    @Test
    @DisplayName("Should throw exception when enterprise email already exists")
    void testRegisterEnterprise_EmailExists() {
        // Given
        when(userRepository.existsByEmail(enterprise.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> registrationService.registerEnterprise(enterprise))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(enterpriseRepository, never()).save(any());
        verify(verificationTokenService, never()).createAndSendToken(any());
    }

    @Test
    @DisplayName("Should register student successfully")
    void testRegisterStudent_Success() {
        // Given
        when(userRepository.existsByEmail(student.getEmail())).thenReturn(false);
        when(studentRepository.save(student)).thenReturn(student);

        // When
        registrationService.registerStudent(student);

        // Then
        verify(userRepository).existsByEmail(student.getEmail());
        verify(studentRepository).save(student);
        verify(verificationTokenService).createAndSendToken(student);
    }

    @Test
    @DisplayName("Should throw exception when student email already exists")
    void testRegisterStudent_EmailExists() {
        // Given
        when(userRepository.existsByEmail(student.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> registrationService.registerStudent(student))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(studentRepository, never()).save(any());
        verify(verificationTokenService, never()).createAndSendToken(any());
    }

    @Test
    @DisplayName("Should register teacher successfully")
    void testRegisterTeacher_Success() {
        // Given
        when(userRepository.existsByEmail(teacher.getEmail())).thenReturn(false);
        when(teacherRepository.save(teacher)).thenReturn(teacher);

        // When
        registrationService.registerTeacher(teacher);

        // Then
        verify(userRepository).existsByEmail(teacher.getEmail());
        verify(teacherRepository).save(teacher);
        verify(verificationTokenService).createAndSendToken(teacher);
    }

    @Test
    @DisplayName("Should throw exception when teacher email already exists")
    void testRegisterTeacher_EmailExists() {
        // Given
        when(userRepository.existsByEmail(teacher.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> registrationService.registerTeacher(teacher))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(teacherRepository, never()).save(any());
        verify(verificationTokenService, never()).createAndSendToken(any());
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void testGetUserByEmail_Success() {
        // Given
        Users user = new Users();
        user.setEmail("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        // When
        Users result = registrationService.getUserByEmail("user@test.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("user@test.com");
        verify(userRepository).findByEmail("user@test.com");
    }

    @Test
    @DisplayName("Should throw exception when user not found by email")
    void testGetUserByEmail_NotFound() {
        // Given
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> registrationService.getUserByEmail("unknown@test.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("unknown@test.com");
    }
}
