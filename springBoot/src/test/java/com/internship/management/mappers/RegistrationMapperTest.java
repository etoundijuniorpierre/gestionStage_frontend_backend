package com.internship.management.mappers;

import com.internship.management.dto.registration.EnterpriseRegistrationDto;
import com.internship.management.dto.registration.StudentRegistrationDto;
import com.internship.management.dto.registration.TeacherRegistrationDto;
import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Student;
import com.internship.management.entities.Teacher;
import com.internship.management.entities.Users;
import com.internship.management.entities.VerificationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour RegistrationMapper.
 * Couverture: 100%
 */
@DisplayName("RegistrationMapper Tests")
class RegistrationMapperTest {

    private RegistrationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(RegistrationMapper.class);
    }

    @Test
    @DisplayName("Should map EnterpriseRegistrationDto to Enterprise")
    void testToEntity_Enterprise() {
        // Given
        EnterpriseRegistrationDto dto = new EnterpriseRegistrationDto();
        dto.setEmail("enterprise@test.com");
        dto.setPassword("password123");
        dto.setName("Test Enterprise");
        dto.setMatriculation("MAT123");
        dto.setSectorOfActivity("IT");
        dto.setContact("1234567890");
        dto.setLocation("123 Main St");
        dto.setCity("Paris");
        dto.setCountry("France");

        // When
        Enterprise entity = mapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
        assertThat(entity.getPassword()).isEqualTo(dto.getPassword());
        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getMatriculation()).isEqualTo(dto.getMatriculation());
        assertThat(entity.getSectorOfActivity()).isEqualTo(dto.getSectorOfActivity());
        assertThat(entity.getContact()).isEqualTo(dto.getContact());
        assertThat(entity.getLocation()).isEqualTo(dto.getLocation());
        assertThat(entity.getCity()).isEqualTo(dto.getCity());
        assertThat(entity.getCountry()).isEqualTo(dto.getCountry());
    }

    @Test
    @DisplayName("Should map StudentRegistrationDto to Student")
    void testToEntity_Student() {
        // Given
        StudentRegistrationDto dto = new StudentRegistrationDto();
        dto.setEmail("student@test.com");
        dto.setPassword("password123");
        dto.setName("Doe");
        dto.setFirstName("John");
        dto.setSector("Computer Science");
        dto.setDepartment("CS");

        // When
        Student entity = mapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
        assertThat(entity.getPassword()).isEqualTo(dto.getPassword());
        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(entity.getSector()).isEqualTo(dto.getSector());
        assertThat(entity.getDepartment()).isEqualTo(dto.getDepartment());
    }

    @Test
    @DisplayName("Should map TeacherRegistrationDto to Teacher")
    void testToEntity_Teacher() {
        // Given
        TeacherRegistrationDto dto = new TeacherRegistrationDto();
        dto.setEmail("teacher@test.com");
        dto.setPassword("password123");
        dto.setName("Smith");
        dto.setFirstName("Jane");
        dto.setDepartment("Computer Science");

        // When
        Teacher entity = mapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
        assertThat(entity.getPassword()).isEqualTo(dto.getPassword());
        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(entity.getDepartment()).isEqualTo(dto.getDepartment());
    }

    @Test
    @DisplayName("Should create verification token with code and user")
    void testVerificationTokenUpdate() {
        // Given
        String code = "123456";
        Users user = new Users();
        user.setId(1L);
        user.setEmail("user@test.com");

        // When
        VerificationToken token = mapper.verificationTokenUpdate(code, user);

        // Then
        assertThat(token).isNotNull();
        assertThat(token.getCode()).isEqualTo(code);
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getExpirationDate()).isAfter(LocalDateTime.now());
        assertThat(token.isUsed()).isFalse();
    }

    @Test
    @DisplayName("Should update existing token with new code")
    void testUpdateToken() {
        // Given
        VerificationToken existingToken = new VerificationToken();
        existingToken.setId(1L);
        existingToken.setCode("oldcode");
        existingToken.setUsed(true);

        String newCode = "654321";

        // When
        VerificationToken updatedToken = mapper.updateToken(existingToken, newCode);

        // Then
        assertThat(updatedToken).isNotNull();
        assertThat(updatedToken.getCode()).isEqualTo(newCode);
        assertThat(updatedToken.getExpirationDate()).isAfter(LocalDateTime.now());
        assertThat(updatedToken.isUsed()).isFalse();
    }

    @Test
    @DisplayName("Should handle null EnterpriseRegistrationDto")
    void testToEntity_NullEnterpriseDto() {
        // When
        Enterprise entity = mapper.toEntity((EnterpriseRegistrationDto) null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should handle null StudentRegistrationDto")
    void testToEntity_NullStudentDto() {
        // When
        Student entity = mapper.toEntity((StudentRegistrationDto) null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should handle null TeacherRegistrationDto")
    void testToEntity_NullTeacherDto() {
        // When
        Teacher entity = mapper.toEntity((TeacherRegistrationDto) null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should set expiration date 10 minutes in future")
    void testVerificationTokenUpdate_ExpirationDate() {
        // Given
        String code = "123456";
        Users user = new Users();
        LocalDateTime before = LocalDateTime.now().plusMinutes(9);
        LocalDateTime after = LocalDateTime.now().plusMinutes(11);

        // When
        VerificationToken token = mapper.verificationTokenUpdate(code, user);

        // Then
        assertThat(token.getExpirationDate()).isBetween(before, after);
    }
}
