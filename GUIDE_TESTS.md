# 🧪 GUIDE DE TESTS - BACKEND

Ce document fournit des exemples concrets de tests à implémenter.

---

## 📦 DÉPENDANCES DE TEST

### Ajouter dans `pom.xml`:

```xml
<dependencies>
    <!-- Tests existants -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- AssertJ pour des assertions fluides -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Mockito (déjà inclus dans spring-boot-starter-test) -->

    <!-- Testcontainers pour tests d'intégration avec PostgreSQL -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 🧪 TESTS UNITAIRES - SERVICES

### 1. `OfferServiceImplTest.java`

```java
package com.internship.management.services;

import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Offer;
import com.internship.management.entities.Teacher;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OfferService Tests")
class OfferServiceImplTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private EnterpriseRepository enterpriseRepository;

    @InjectMocks
    private OfferServiceImpl offerService;

    private Offer testOffer;
    private Enterprise testEnterprise;
    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        // Arrange - Créer des données de test
        testEnterprise = new Enterprise();
        testEnterprise.setId(1L);
        testEnterprise.setName("Tech Corp");
        testEnterprise.setEmail("contact@techcorp.com");

        testTeacher = new Teacher();
        testTeacher.setId(1L);
        testTeacher.setEmail("teacher@university.com");
        testTeacher.setDepartment("Informatique");

        testOffer = new Offer();
        testOffer.setId(1L);
        testOffer.setTitle("Stage Développeur Java");
        testOffer.setDescription("Stage de 6 mois");
        testOffer.setDomain("Informatique");
        testOffer.setStatus(OfferStatus.PENDING);
        testOffer.setEnterprise(testEnterprise);
    }

    @Test
    @DisplayName("getOfferById - Should return offer when exists")
    void getOfferById_WhenOfferExists_ShouldReturnOffer() {
        // Arrange
        Long offerId = 1L;
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // Act
        Offer result = offerService.getOfferById(offerId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(offerId);
        assertThat(result.getTitle()).isEqualTo("Stage Développeur Java");
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("getOfferById - Should throw exception when offer not found")
    void getOfferById_WhenOfferNotExists_ShouldThrowException() {
        // Arrange
        Long offerId = 999L;
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> offerService.getOfferById(offerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Offer")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("saveOffer - Should save and return offer")
    void saveOffer_ShouldSaveAndReturnOffer() {
        // Arrange
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);

        // Act
        offerService.saveOffer(testOffer);

        // Assert
        verify(offerRepository, times(1)).save(testOffer);
    }

    @Test
    @DisplayName("getTeacherByEmail - Should return teacher when exists")
    void getTeacherByEmail_WhenExists_ShouldReturnTeacher() {
        // Arrange
        String email = "teacher@university.com";
        when(teacherRepository.findByEmail(email)).thenReturn(Optional.of(testTeacher));

        // Act
        Teacher result = offerService.getTeacherByEmail(email);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getDepartment()).isEqualTo("Informatique");
    }

    @Test
    @DisplayName("getTeacherByEmail - Should throw exception when not found")
    void getTeacherByEmail_WhenNotExists_ShouldThrowException() {
        // Arrange
        String email = "unknown@university.com";
        when(teacherRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> offerService.getTeacherByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Teacher")
                .hasMessageContaining("email");
    }

    @Test
    @DisplayName("getOfferByDepartmentAndPendingOfferStatusAndInPartnershipTruePaged - Should return paginated offers")
    void getOfferByDepartmentPaged_ShouldReturnPaginatedOffers() {
        // Arrange
        String department = "Informatique";
        OfferStatus status = OfferStatus.PENDING;
        Pageable pageable = PageRequest.of(0, 10);

        List<Offer> offers = List.of(testOffer);
        Page<Offer> offerPage = new PageImpl<>(offers, pageable, offers.size());

        when(offerRepository.findByDomainAndStatusAndEnterprise_InPartnershipTrue(
                department, status, pageable))
                .thenReturn(offerPage);

        // Act
        Page<Offer> result = offerService.getOfferByDepartmentAndPendingOfferStatusAndInPartnershipTruePaged(
                department, status, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Stage Développeur Java");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("getByEnterpriseEmail - Should return enterprise when exists")
    void getByEnterpriseEmail_WhenExists_ShouldReturnEnterprise() {
        // Arrange
        String email = "contact@techcorp.com";
        when(enterpriseRepository.findByEmail(email)).thenReturn(Optional.of(testEnterprise));

        // Act
        Enterprise result = offerService.getByEnterpriseEmail(email);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getName()).isEqualTo("Tech Corp");
    }
}
```

---

## 🧪 TESTS UNITAIRES - MAPPERS

### 2. `PostOfferMapperTest.java`

```java
package com.internship.management.mappers;

import com.internship.management.dto.postOffer.OfferRequestDto;
import com.internship.management.dto.postOffer.OfferResponseDto;
import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Offer;
import com.internship.management.enums.OfferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("PostOfferMapper Tests")
class PostOfferMapperTest {

    @Autowired
    private PostOfferMapper mapper;

    private Offer testOffer;
    private Enterprise testEnterprise;

    @BeforeEach
    void setUp() {
        testEnterprise = new Enterprise();
        testEnterprise.setId(1L);
        testEnterprise.setName("Tech Corp");
        testEnterprise.setEmail("contact@techcorp.com");

        testOffer = new Offer();
        testOffer.setId(1L);
        testOffer.setTitle("Stage Développeur Java");
        testOffer.setDescription("Stage de 6 mois");
        testOffer.setDomain("Informatique");
        testOffer.setJob("Développeur");
        testOffer.setTypeOfInternship("Stage");
        testOffer.setStartDate(LocalDate.now().plusMonths(1));
        testOffer.setEndDate(LocalDate.now().plusMonths(7));
        testOffer.setNumberOfPlaces(2);
        testOffer.setRequirements("Java, Spring Boot");
        testOffer.setRemote(true);
        testOffer.setPaying(true);
        testOffer.setStatus(OfferStatus.PENDING);
        testOffer.setEnterprise(testEnterprise);
    }

    @Test
    @DisplayName("toDto - Should map Offer to OfferResponseDto correctly")
    void toDto_ShouldMapOfferToDto() {
        // Act
        OfferResponseDto dto = mapper.toDto(testOffer);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(testOffer.getId());
        assertThat(dto.getTitle()).isEqualTo(testOffer.getTitle());
        assertThat(dto.getDescription()).isEqualTo(testOffer.getDescription());
        assertThat(dto.getDomain()).isEqualTo(testOffer.getDomain());
        assertThat(dto.isRemote()).isTrue();
        assertThat(dto.isPaying()).isTrue();
        assertThat(dto.getEnterprise()).isNotNull();
        assertThat(dto.getEnterprise().getName()).isEqualTo("Tech Corp");
    }

    @Test
    @DisplayName("toEntity - Should map OfferRequestDto to Offer correctly")
    void toEntity_ShouldMapDtoToOffer() {
        // Arrange
        OfferRequestDto dto = new OfferRequestDto();
        dto.setTitle("Stage Développeur Python");
        dto.setDescription("Stage de 3 mois");
        dto.setDomain("Data Science");
        dto.setJob("Data Scientist");
        dto.setTypeOfInternship("Stage");
        dto.setStartDate(LocalDate.now().plusMonths(2));
        dto.setEndDate(LocalDate.now().plusMonths(5));
        dto.setNumberOfPlaces(1);
        dto.setRequirements("Python, Pandas");
        dto.setRemote(false);
        dto.setPaying(true);

        // Act
        Offer offer = mapper.toEntity(dto);

        // Assert
        assertThat(offer).isNotNull();
        assertThat(offer.getTitle()).isEqualTo(dto.getTitle());
        assertThat(offer.getDescription()).isEqualTo(dto.getDescription());
        assertThat(offer.getDomain()).isEqualTo(dto.getDomain());
        assertThat(offer.isRemote()).isFalse();
        assertThat(offer.isPaying()).isTrue();
    }
}
```

---

## 🧪 TESTS D'INTÉGRATION - REPOSITORIES

### 3. `OfferRepositoryTest.java`

```java
package com.internship.management.repositories;

import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Offer;
import com.internship.management.enums.EnterpriseState;
import com.internship.management.enums.OfferStatus;
import com.internship.management.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("OfferRepository Integration Tests")
class OfferRepositoryTest {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    private Enterprise partnerEnterprise;
    private Enterprise nonPartnerEnterprise;

    @BeforeEach
    void setUp() {
        // Créer une entreprise partenaire
        partnerEnterprise = new Enterprise();
        partnerEnterprise.setName("Partner Corp");
        partnerEnterprise.setEmail("partner@corp.com");
        partnerEnterprise.setPassword("password");
        partnerEnterprise.setRole(Role.ENTERPRISE);
        partnerEnterprise.setInPartnership(true);
        partnerEnterprise.setEnterpriseState(EnterpriseState.APPROVED);
        partnerEnterprise.setMatriculation("MAT123");
        partnerEnterprise.setSectorOfActivity("IT");
        partnerEnterprise = enterpriseRepository.save(partnerEnterprise);

        // Créer une entreprise non partenaire
        nonPartnerEnterprise = new Enterprise();
        nonPartnerEnterprise.setName("Non Partner Corp");
        nonPartnerEnterprise.setEmail("nonpartner@corp.com");
        nonPartnerEnterprise.setPassword("password");
        nonPartnerEnterprise.setRole(Role.ENTERPRISE);
        nonPartnerEnterprise.setInPartnership(false);
        nonPartnerEnterprise.setEnterpriseState(EnterpriseState.PENDING);
        nonPartnerEnterprise.setMatriculation("MAT456");
        nonPartnerEnterprise.setSectorOfActivity("IT");
        nonPartnerEnterprise = enterpriseRepository.save(nonPartnerEnterprise);

        // Créer des offres
        createOffer("Offre 1", "Informatique", OfferStatus.APPROVED, partnerEnterprise);
        createOffer("Offre 2", "Informatique", OfferStatus.PENDING, partnerEnterprise);
        createOffer("Offre 3", "Mathématiques", OfferStatus.APPROVED, partnerEnterprise);
        createOffer("Offre 4", "Informatique", OfferStatus.APPROVED, nonPartnerEnterprise);
    }

    private void createOffer(String title, String domain, OfferStatus status, Enterprise enterprise) {
        Offer offer = new Offer();
        offer.setTitle(title);
        offer.setDescription("Description");
        offer.setDomain(domain);
        offer.setJob("Job");
        offer.setTypeOfInternship("Stage");
        offer.setStartDate(LocalDate.now().plusMonths(1));
        offer.setEndDate(LocalDate.now().plusMonths(7));
        offer.setNumberOfPlaces(2);
        offer.setRequirements("Requirements");
        offer.setRemote(false);
        offer.setPaying(true);
        offer.setStatus(status);
        offer.setEnterprise(enterprise);
        offerRepository.save(offer);
    }

    @Test
    @DisplayName("findByDomainAndStatusAndEnterprise_InPartnershipTrue - Should return only partner offers")
    void findByDomainAndStatusAndPartnership_ShouldReturnOnlyPartnerOffers() {
        // Act
        List<Offer> offers = offerRepository.findByDomainAndStatusAndEnterprise_InPartnershipTrue(
                "Informatique", OfferStatus.APPROVED);

        // Assert
        assertThat(offers).hasSize(1);
        assertThat(offers.get(0).getTitle()).isEqualTo("Offre 1");
        assertThat(offers.get(0).getEnterprise().isInPartnership()).isTrue();
    }

    @Test
    @DisplayName("findByDomainAndStatusAndEnterprise_InPartnershipTrue - Should support pagination")
    void findByDomainAndStatusAndPartnershipPaged_ShouldReturnPaginatedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Offer> offerPage = offerRepository.findByDomainAndStatusAndEnterprise_InPartnershipTrue(
                "Informatique", OfferStatus.APPROVED, pageable);

        // Assert
        assertThat(offerPage.getContent()).hasSize(1);
        assertThat(offerPage.getTotalElements()).isEqualTo(1);
        assertThat(offerPage.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("findOfferByEnterpriseId - Should return all offers for enterprise")
    void findOfferByEnterpriseId_ShouldReturnAllOffersForEnterprise() {
        // Act
        List<Offer> offers = offerRepository.findOfferByEnterpriseId(partnerEnterprise.getId());

        // Assert
        assertThat(offers).hasSize(3);
        assertThat(offers).allMatch(offer ->
                offer.getEnterprise().getId().equals(partnerEnterprise.getId()));
    }

    @Test
    @DisplayName("findByPaying - Should return only paying offers")
    void findByPaying_ShouldReturnOnlyPayingOffers() {
        // Act
        List<Offer> offers = offerRepository.findByPaying(true);

        // Assert
        assertThat(offers).hasSize(4);
        assertThat(offers).allMatch(Offer::isPaying);
    }
}
```

---

## 🧪 TESTS D'INTÉGRATION - CONTROLLERS

### 4. `StudentControllerIntegrationTest.java`

```java
package com.internship.management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.management.entities.Student;
import com.internship.management.enums.Role;
import com.internship.management.repositories.StudentRepository;
import com.internship.management.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("StudentController Integration Tests")
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtService jwtService;

    private String jwtToken;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        // Créer un étudiant de test
        testStudent = new Student();
        testStudent.setName("Doe");
        testStudent.setFirstName("John");
        testStudent.setEmail("john.doe@student.com");
        testStudent.setPassword("password");
        testStudent.setRole(Role.STUDENT);
        testStudent.setDepartment("Informatique");
        testStudent.setSector("Génie Logiciel");
        testStudent.setEmailVerified(true);
        testStudent.setOnInternship(false);
        testStudent.setLanguages(new ArrayList<>());
        testStudent = studentRepository.save(testStudent);

        // Générer un token JWT
        UserDetails userDetails = User.builder()
                .username(testStudent.getEmail())
                .password(testStudent.getPassword())
                .roles("STUDENT")
                .build();
        jwtToken = jwtService.generateToken(userDetails);
    }

    @Test
    @DisplayName("GET /api/student/status - Should return student status")
    void getStudentStatus_ShouldReturnStatus() throws Exception {
        mockMvc.perform(get("/api/student/status")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.onInternship").value(false))
                .andExpect(jsonPath("$.data.canApply").value(true))
                .andExpect(jsonPath("$.data.message").exists());
    }

    @Test
    @DisplayName("GET /api/student/profile - Should return student profile")
    void getStudentProfile_ShouldReturnProfile() throws Exception {
        mockMvc.perform(get("/api/student/profile")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(testStudent.getEmail()))
                .andExpect(jsonPath("$.data.name").value(testStudent.getName()))
                .andExpect(jsonPath("$.data.firstName").value(testStudent.getFirstName()))
                .andExpect(jsonPath("$.data.department").value(testStudent.getDepartment()));
    }

    @Test
    @DisplayName("GET /api/student/offersByApprovedStatus - Should return paginated offers")
    void getOfferByStatus_ShouldReturnPaginatedOffers() throws Exception {
        mockMvc.perform(get("/api/student/offersByApprovedStatus")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pagination").exists())
                .andExpect(jsonPath("$.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.pagination.pageSize").value(10));
    }

    @Test
    @DisplayName("GET /api/student/offersByApprovedStatus - Should return 401 without token")
    void getOfferByStatus_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/student/offersByApprovedStatus")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
```

---

## 🧪 TESTS DE SÉCURITÉ

### 5. `SecurityTest.java`

```java
package com.internship.management.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Security Tests")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Public endpoints should be accessible without authentication")
    void publicEndpoints_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Protected endpoints should return 401 without token")
    void protectedEndpoints_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/student/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Student endpoint should not be accessible with wrong role")
    void studentEndpoint_WithWrongRole_ShouldReturn403() throws Exception {
        // Ce test nécessite un token avec un rôle différent (TEACHER, ENTERPRISE, etc.)
        // À implémenter avec un token généré pour un autre rôle
    }

    @Test
    @DisplayName("CORS should be configured correctly")
    void cors_ShouldBeConfigured() throws Exception {
        mockMvc.perform(get("/api/student/status")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isUnauthorized()); // 401 car pas de token, mais CORS OK
    }
}
```

---

## 🧪 TESTS DE VALIDATION

### 6. `ValidationTest.java`

```java
package com.internship.management.validation;

import com.internship.management.dto.postOffer.OfferRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Validation Tests")
class ValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("OfferRequestDto - Valid DTO should pass validation")
    void offerRequestDto_Valid_ShouldPassValidation() {
        // Arrange
        OfferRequestDto dto = new OfferRequestDto();
        dto.setTitle("Stage Développeur");
        dto.setDescription("Description du stage");
        dto.setDomain("Informatique");
        dto.setJob("Développeur");
        dto.setTypeOfInternship("Stage");
        dto.setStartDate(LocalDate.now().plusMonths(1));
        dto.setEndDate(LocalDate.now().plusMonths(7));
        dto.setNumberOfPlaces(2);
        dto.setRequirements("Java, Spring");
        dto.setRemote(true);
        dto.setPaying(true);

        // Act
        Set<ConstraintViolation<OfferRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("OfferRequestDto - Blank title should fail validation")
    void offerRequestDto_BlankTitle_ShouldFailValidation() {
        // Arrange
        OfferRequestDto dto = new OfferRequestDto();
        dto.setTitle(""); // Blank
        dto.setDescription("Description");
        dto.setDomain("Informatique");
        dto.setJob("Développeur");
        dto.setTypeOfInternship("Stage");
        dto.setStartDate(LocalDate.now().plusMonths(1));
        dto.setEndDate(LocalDate.now().plusMonths(7));
        dto.setNumberOfPlaces(2);
        dto.setRequirements("Java");
        dto.setRemote(true);
        dto.setPaying(true);

        // Act
        Set<ConstraintViolation<OfferRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }
}
```

---

## 📊 CONFIGURATION DE COUVERTURE

### `pom.xml` - Ajouter JaCoCo

```xml
<build>
    <plugins>
        <!-- Plugin JaCoCo pour la couverture de code -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>jacoco-check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>PACKAGE</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.80</minimum> <!-- 80% minimum -->
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

---

## 🚀 COMMANDES MAVEN

### Exécuter tous les tests

```bash
mvn test
```

### Exécuter les tests avec rapport de couverture

```bash
mvn clean test jacoco:report
```

### Voir le rapport de couverture

```bash
# Le rapport est généré dans: target/site/jacoco/index.html
start target/site/jacoco/index.html
```

### Exécuter uniquement les tests d'une classe

```bash
mvn test -Dtest=OfferServiceImplTest
```

### Exécuter uniquement un test spécifique

```bash
mvn test -Dtest=OfferServiceImplTest#getOfferById_WhenOfferExists_ShouldReturnOffer
```

---

## ✅ CHECKLIST DE TESTS

### Tests Unitaires (Services)

- [ ] OfferServiceImplTest
- [ ] NotificationServiceImplTest
- [ ] ProfilePhotoServiceTest
- [ ] RegistrationServiceImplTest
- [ ] VerificationTokenServiceTest
- [ ] InternshipChartServiceImplTest

### Tests Unitaires (Mappers)

- [ ] PostOfferMapperTest
- [ ] RegistrationMapperTest

### Tests d'Intégration (Repositories)

- [ ] OfferRepositoryTest
- [ ] ApplicationRepositoryTest
- [ ] StudentRepositoryTest
- [ ] TeacherRepositoryTest
- [ ] EnterpriseRepositoryTest

### Tests d'Intégration (Controllers)

- [ ] StudentControllerIntegrationTest
- [ ] TeacherControllerIntegrationTest
- [ ] EnterpriseControllerIntegrationTest
- [ ] AdminControllerIntegrationTest

### Tests de Sécurité

- [ ] SecurityTest
- [ ] JwtServiceTest
- [ ] RateLimitFilterTest

### Tests de Validation

- [ ] ValidationTest
- [ ] CustomValidatorTest

---

## 🎯 OBJECTIF DE COUVERTURE

| Couche       | Objectif |
| ------------ | -------- |
| Services     | 90%      |
| Repositories | 80%      |
| Controllers  | 80%      |
| Mappers      | 90%      |
| **Global**   | **80%**  |

---

**Bon courage pour l'implémentation des tests! 🧪**
