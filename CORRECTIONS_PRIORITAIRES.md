# 🔧 CORRECTIONS PRIORITAIRES - BACKEND

Ce document liste les corrections **concrètes et immédiates** à appliquer au backend.

---

## 🔴 PRIORITÉ 1: EXCEPTIONS PERSONNALISÉES

### 1. Créer le package `exception`

#### `ResourceNotFoundException.java`

```java
package com.internship.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
```

#### `BusinessException.java`

```java
package com.internship.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_RULE_VIOLATION";
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
```

#### `InvalidFileException.java`

```java
package com.internship.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### `DuplicateResourceException.java`

```java
package com.internship.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
```

---

## 🔴 PRIORITÉ 2: AMÉLIORER GlobalExceptionHandler

### Remplacer `GlobalExceptionHandler.java`

```java
package com.internship.management.security;

import com.internship.management.dto.response.ApiResponse;
import com.internship.management.exception.BusinessException;
import com.internship.management.exception.DuplicateResourceException;
import com.internship.management.exception.InvalidFileException;
import com.internship.management.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.warn("Business rule violation: {} - {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(DuplicateResourceException ex) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidFile(InvalidFileException ex) {
        log.warn("Invalid file: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Bad credentials attempt");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid email or password"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UsernameNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access denied"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiResponse<Void>> handleMultipartException(MultipartException ex) {
        log.warn("Failed to upload file: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("File upload failed: " + ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
    }
}
```

---

## 🔴 PRIORITÉ 3: CORRIGER LES SERVICES

### 1. `OfferServiceImpl.java` - Remplacer toutes les exceptions

**Rechercher et remplacer:**

```java
// AVANT
.orElseThrow(() -> new RuntimeException("Offer Not Found"))

// APRÈS
.orElseThrow(() -> new ResourceNotFoundException("Offer", "id", id))
```

```java
// AVANT
.orElseThrow(() -> new RuntimeException("Teacher not found"))

// APRÈS
.orElseThrow(() -> new ResourceNotFoundException("Teacher", "email", email))
```

```java
// AVANT
.orElseThrow(() -> new RuntimeException("Enterprise Not Found"))

// APRÈS
.orElseThrow(() -> new ResourceNotFoundException("Enterprise", "email", email))
```

```java
// AVANT
.orElseThrow(() -> new RuntimeException("Student Not Found"))

// APRÈS
.orElseThrow(() -> new ResourceNotFoundException("Student", "email", email))
```

```java
// AVANT
.orElseThrow(() -> new RuntimeException("Application Not Found"))

// APRÈS
.orElseThrow(() -> new ResourceNotFoundException("Application", "id", id))
```

```java
// AVANT
.orElseThrow(() -> new RuntimeException("User not found"))

// APRÈS
.orElseThrow(() -> new ResourceNotFoundException("User", "email", email))
```

```java
// AVANT
.orElseThrow(() -> new RuntimeException("Logo not found"))

// APRÈS
.orElseThrow(() -> new ResourceNotFoundException("Logo", "enterprise", enterprise.getId()))
```

```java
// AVANT
.orElseThrow(() -> new RuntimeException("Convention not found"))

// APRÈS
.orElseThrow(() -> new ResourceNotFoundException("Convention", "offerId", offerId))
```

**Ajouter les imports:**

```java
import com.internship.management.exception.ResourceNotFoundException;
```

**Ajouter @Transactional:**

```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true) // Par défaut pour toutes les méthodes
public class OfferServiceImpl implements PostOffer {

    @Transactional // Pour les méthodes d'écriture
    public void saveOffer(Offer offer) {
        log.info("Saving offer: {}", offer.getTitle());
        offerRepository.save(offer);
    }

    // readOnly hérité de la classe
    public Offer getOfferById(Long id) {
        log.debug("Fetching offer with id: {}", id);
        return offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer", "id", id));
    }
}
```

### 2. `NotificationServiceImpl.java`

**Améliorer la gestion d'erreur:**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationInterface {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void sendNotification(Users user, String message) {
        try {
            // Sauvegarder la notification
            Notification notif = new Notification();
            notif.setRecipient(user);
            notif.setMessage(message);
            notificationRepository.save(notif);

            // Envoyer via WebSocket
            sendWebSocketNotification(user, message);

        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}", user.getId(), e.getMessage(), e);
            // Ne pas propager l'exception pour ne pas bloquer le flux principal
        }
    }

    private void sendWebSocketNotification(Users user, String message) {
        String destination = determineDestination(user);
        messagingTemplate.convertAndSend(destination, Map.of("content", message));
        log.info("WebSocket notification sent to {}", destination);
    }

    private String determineDestination(Users user) {
        if (user instanceof Enterprise) {
            return "/topic/enterprise/" + user.getId();
        } else if (user instanceof Teacher) {
            return "/topic/department/" + ((Teacher) user).getDepartment();
        } else if (user instanceof Student) {
            return "/topic/student/" + ((Student) user).getDepartment();
        }
        throw new IllegalArgumentException("Unknown user type: " + user.getClass().getName());
    }

    @Transactional(readOnly = true)
    public List<Notification> getAllUnSeenNotificationsByUser(Users user) {
        return notificationRepository.findByRecipientAndSeenFalse(user);
    }

    @Transactional
    public void markAsSeen(Long id, Users user) {
        Notification notif = notificationRepository.findByIdAndRecipientId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));

        notif.setSeen(true);
        notificationRepository.save(notif);
    }
}
```

### 3. `ProfilePhotoService.java`

**Améliorer la validation:**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePhotoService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final ProfilePhotoRepository profilePhotoRepository;
    private final UsersRepository userRepository;

    @Transactional
    public void uploadOrUpdateLogo(MultipartFile file, String email) throws IOException {
        log.info("Uploading/updating profile photo for user: {}", email);

        // Validation
        validateFile(file);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Optional<ProfilePhoto> existingPhoto = profilePhotoRepository.findByUserEmail(email);
        ProfilePhoto profilePhoto;

        if (existingPhoto.isPresent()) {
            profilePhoto = existingPhoto.get();
            profilePhoto.setOriginalFileName(file.getOriginalFilename());
            profilePhoto.setFileType(file.getContentType());
            profilePhoto.setFileData(file.getBytes());
            profilePhoto.setUploadDate(LocalDateTime.now());
            log.debug("Updating existing profile photo for user: {}", email);
        } else {
            profilePhoto = new ProfilePhoto();
            profilePhoto.setOriginalFileName(file.getOriginalFilename());
            profilePhoto.setFileType(file.getContentType());
            profilePhoto.setFileData(file.getBytes());
            profilePhoto.setUser(user);
            log.debug("Creating new profile photo for user: {}", email);
        }

        profilePhotoRepository.save(profilePhoto);
        log.info("Profile photo saved successfully for user: {}", email);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                    String.format("File size (%d bytes) exceeds maximum allowed size (%d bytes)",
                            file.getSize(), MAX_FILE_SIZE));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileException(
                    "Invalid file type. Allowed types: " + String.join(", ", ALLOWED_TYPES));
        }
    }

    @Transactional(readOnly = true)
    public Optional<ProfilePhoto> getLogoByUserId(Long userId) {
        return profilePhotoRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteLogoByUserId(Long userId) {
        log.info("Deleting profile photo for user: {}", userId);
        profilePhotoRepository.deleteByUserId(userId);
    }
}
```

### 4. `RegistrationServiceImpl.java`

**Ajouter @Transactional et améliorer les exceptions:**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements InternshipService {

    private final EnterpriseRepository enterpriseRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final VerificationTokenService verificationTokenService;
    private final UsersRepository userRepository;

    @Transactional
    public void registerEnterprise(Enterprise enterprise) {
        log.info("Registering new enterprise: {}", enterprise.getEmail());

        if (userRepository.existsByEmail(enterprise.getEmail())) {
            throw new DuplicateResourceException("User", "email", enterprise.getEmail());
        }

        Enterprise newEnterprise = enterpriseRepository.save(enterprise);
        verificationTokenService.createAndSendToken(newEnterprise);

        log.info("Enterprise registered successfully: {}", enterprise.getEmail());
    }

    @Transactional
    public void registerStudent(Student student) {
        log.info("Registering new student: {}", student.getEmail());

        if (userRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateResourceException("User", "email", student.getEmail());
        }

        Student newStudent = studentRepository.save(student);
        verificationTokenService.createAndSendToken(newStudent);

        log.info("Student registered successfully: {}", student.getEmail());
    }

    @Transactional
    public void registerTeacher(Teacher teacher) {
        log.info("Registering new teacher: {}", teacher.getEmail());

        if (userRepository.existsByEmail(teacher.getEmail())) {
            throw new DuplicateResourceException("User", "email", teacher.getEmail());
        }

        Teacher newTeacher = teacherRepository.save(teacher);
        verificationTokenService.createAndSendToken(newTeacher);

        log.info("Teacher registered successfully: {}", teacher.getEmail());
    }

    @Transactional(readOnly = true)
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
}
```

### 5. `VerificationTokenService.java`

**Améliorer la sécurité:**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;
    private final UsersRepository userRepository;
    private final RegistrationMapper registrationMapper;
    private final JavaMailSender mailSender;

    private static final int CODE_LENGTH = 6;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateCode() {
        int code = SECURE_RANDOM.nextInt(900000) + 100000; // 6 chiffres
        return String.valueOf(code);
    }

    @Transactional
    public void createAndSendToken(Users user) {
        log.info("Creating verification token for user: {}", user.getEmail());

        String code = generateCode();
        VerificationToken token = registrationMapper.verificationTokenUpdate(code, user);

        tokenRepository.save(token);
        sendEmail(user.getEmail(), code);

        log.info("Verification token created and sent to: {}", user.getEmail());
    }

    @Transactional
    public void resendToken(Users user) {
        log.info("Resending verification token for user: {}", user.getEmail());

        tokenRepository.deleteByUser(user);
        createAndSendToken(user);
    }

    @Transactional
    public Users verifyCode(String email, String inputCode) {
        log.info("Verifying code for user: {}", email);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        VerificationToken token = tokenRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException("No verification code found for this user"));

        if (token.isUsed()) {
            throw new BusinessException("This verification code has already been used");
        }

        if (token.getExpirationDate().isBefore(LocalDateTime.now())) {
            log.warn("Verification code expired for user: {}", email);

            String newCode = generateCode();
            VerificationToken newToken = registrationMapper.updateToken(token, newCode);
            tokenRepository.save(newToken);

            sendEmail(email, newCode);
            throw new BusinessException("The verification code has expired. A new code has been sent to your email");
        }

        if (!token.getCode().equals(inputCode)) {
            throw new BusinessException("Incorrect verification code");
        }

        token.setUsed(true);
        tokenRepository.save(token);

        user.setEmailVerified(true);
        Users verifiedUser = userRepository.save(user);

        log.info("User verified successfully: {}", email);
        return verifiedUser;
    }

    private void sendEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your account verification code");
            message.setText("Hello,\n\n" +
                    "Here is your verification code: " + code + "\n\n" +
                    "This code will expire in 10 minutes.\n\n" +
                    "Best regards,\n" +
                    "Internship Platform Team");

            mailSender.send(message);
            log.debug("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new BusinessException("Failed to send verification email");
        }
    }
}
```

---

## 🔴 PRIORITÉ 4: CORRIGER LES ENTITÉS

### 1. `Offer.java` - Corriger le double point-virgule

**Ligne 34:**

```java
// AVANT
private OfferStatus status = OfferStatus.PENDING;;

// APRÈS
private OfferStatus status = OfferStatus.PENDING;
```

### 2. `Student.java` - Corriger languages

```java
@Entity
@Getter
@Setter
public class Student extends Users {

    private String firstName;
    private String sector;
    private String department;

    @ElementCollection
    @CollectionTable(name = "student_languages", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "language")
    private List<String> languages = new ArrayList<>();

    private String githubLink;
    private String linkedinLink;
    private boolean onInternship;

    @Lob
    private byte[] photo;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();
}
```

### 3. `Application.java` et `Convention.java` - Supprimer le code commenté

**Application.java - Supprimer lignes 31-32:**

```java
// SUPPRIMER:
//    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Convention convention;
//
```

**Convention.java - Supprimer lignes 28-30:**

```java
// SUPPRIMER:
//    @OneToOne
//    @JoinColumn(name = "application_id", unique = true)
//    private Application application;
```

### 4. `Users.java` - Ajouter index et contraintes

```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email")
})
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<Notification> messages = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VerificationToken> verificationTokens = new ArrayList<>();
}
```

---

## 🔴 PRIORITÉ 5: NETTOYER LES REPOSITORIES

### 1. `TeacherRepository.java` - Supprimer import inutilisé

**Supprimer ligne 3:**

```java
// SUPPRIMER:
import com.internship.management.entities.Student;
```

### 2. `EnterpriseRepository.java` - Supprimer import inutilisé

**Supprimer ligne 4:**

```java
// SUPPRIMER:
import com.internship.management.entities.Offer;
```

### 3. `OfferRepository.java` - Corriger le paramètre

**Ligne 43:**

```java
// AVANT
List<Offer> findByRemote(boolean Remote);

// APRÈS
List<Offer> findByRemote(boolean remote);
```

---

## 🟡 PRIORITÉ 6: AJOUTER @Transactional AUX SERVICES

### Template à appliquer:

```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true) // Par défaut pour toutes les méthodes
public class YourServiceImpl {

    // Méthodes de lecture: héritent de readOnly = true
    public Entity getById(Long id) {
        // ...
    }

    // Méthodes d'écriture: override avec @Transactional
    @Transactional
    public Entity save(Entity entity) {
        // ...
    }

    @Transactional
    public void delete(Long id) {
        // ...
    }
}
```

---

## 🟡 PRIORITÉ 7: CRÉER ApplicationService

### Nouveau fichier: `ApplicationService.java`

```java
package com.internship.management.services;

import com.internship.management.dto.application.ApplicationRequestDto;
import com.internship.management.dto.application.ApplicationResponseDto;
import com.internship.management.entities.Application;
import com.internship.management.entities.Offer;
import com.internship.management.entities.Student;
import com.internship.management.enums.ApplicationState;
import com.internship.management.exception.BusinessException;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.interfaces.NotificationInterface;
import com.internship.management.interfaces.PostOffer;
import com.internship.management.mappers.PostOfferMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApplicationService {

    private final PostOffer postOffer;
    private final PostOfferMapper mapper;
    private final NotificationInterface notificationInterface;

    @Transactional
    public ApplicationResponseDto createApplication(
            String studentEmail,
            Long offerId,
            ApplicationRequestDto dto) {

        log.info("Creating application for student {} on offer {}", studentEmail, offerId);

        Student student = postOffer.getStudentByEmail(studentEmail);
        validateStudentCanApply(student, offerId);

        Offer offer = postOffer.getOfferById(offerId);
        Application application = buildApplication(student, offer, dto);

        postOffer.saveApplication(application);
        notifyEnterprise(offer, student);

        log.info("Application created successfully: id={}", application.getId());
        return mapper.toDto(application);
    }

    private void validateStudentCanApply(Student student, Long offerId) {
        if (student.isOnInternship()) {
            throw new BusinessException("You are already on internship and cannot apply anymore");
        }

        boolean alreadyApplied = student.getApplications().stream()
                .anyMatch(app -> app.getOffer().getId().equals(offerId) &&
                        (app.getState() == ApplicationState.PENDING ||
                         app.getState() == ApplicationState.APPROVED));

        if (alreadyApplied) {
            throw new BusinessException("You already have a pending or approved application for this offer");
        }
    }

    private Application buildApplication(Student student, Offer offer, ApplicationRequestDto dto) {
        Application application = mapper.toEntity(dto);
        application.setStudent(student);
        application.setEnterprise(offer.getEnterprise());
        application.setOffer(offer);
        application.setState(ApplicationState.PENDING);
        return application;
    }

    private void notifyEnterprise(Offer offer, Student student) {
        String message = String.format("Nouvelle candidature reçue de %s %s pour l'offre: %s",
                student.getFirstName(), student.getName(), offer.getTitle());
        notificationInterface.sendNotification(offer.getEnterprise(), message);
    }

    @Transactional
    public ApplicationResponseDto updateStudentStatus(
            String studentEmail,
            Long applicationId,
            boolean accepted) {

        log.info("Updating student status for application {}: accepted={}", applicationId, accepted);

        Student student = postOffer.getStudentByEmail(studentEmail);
        Application application = postOffer.getApplicationById(applicationId);

        validateOwnership(student, application);

        if (accepted) {
            acceptApplication(student, application);
        }

        return mapper.toDto(application);
    }

    private void validateOwnership(Student student, Application application) {
        if (!application.getStudent().getId().equals(student.getId())) {
            throw new BusinessException("You can only manage your own applications");
        }
    }

    private void acceptApplication(Student student, Application application) {
        student.setOnInternship(true);
        postOffer.saveUser(student);

        application.setState(ApplicationState.APPROVED);
        postOffer.saveApplication(application);

        log.info("Student {} accepted internship from application {}",
                 student.getEmail(), application.getId());
    }
}
```

### Modifier `StudentController.java`

**Remplacer la méthode `create`:**

```java
@Operation(summary = "Créer une nouvelle candidature")
@PostMapping("{offer_id}/createApplication")
public ResponseEntity<ApiResponse<ApplicationResponseDto>> create(
        @Valid @ModelAttribute ApplicationRequestDto applicationRequestDto,
        @PathVariable Long offer_id) {

    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    ApplicationResponseDto response = applicationService.createApplication(email, offer_id, applicationRequestDto);

    return ResponseEntity.ok(ApiResponse.success("Application created successfully", response));
}
```

**Remplacer la méthode `updateStudentStatus`:**

```java
@Operation(summary = "Accepter une offre de stage par l'étudiant")
@PutMapping("{application_id}/updateStudentStatus")
public ResponseEntity<ApiResponse<ApplicationResponseDto>> updateStudentStatus(
        @PathVariable Long application_id,
        @RequestParam boolean applicationAccepted) {

    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    ApplicationResponseDto response = applicationService.updateStudentStatus(email, application_id, applicationAccepted);

    return ResponseEntity.ok(ApiResponse.success("Student status updated", response));
}
```

**Ajouter l'injection:**

```java
@RestController
@RequestMapping(path = "api/student")
@RequiredArgsConstructor
public class StudentController {

    private final PostOffer postOffer;
    private final PostOfferMapper postOfferMapper;
    private final NotificationInterface notificationInterface;
    private final ApplicationService applicationService; // ✅ AJOUTER

    // ...
}
```

---

## ✅ CHECKLIST DE VALIDATION

Après avoir appliqué toutes les corrections:

- [ ] Toutes les exceptions RuntimeException ont été remplacées
- [ ] GlobalExceptionHandler gère toutes les exceptions personnalisées
- [ ] @Transactional ajouté sur tous les services
- [ ] @Slf4j et logs ajoutés partout
- [ ] Code commenté supprimé
- [ ] Imports inutilisés supprimés
- [ ] Double point-virgule corrigé
- [ ] ApplicationService créé et utilisé
- [ ] Validation des fichiers améliorée
- [ ] Le code compile sans erreur
- [ ] Les tests passent (une fois créés)

---

## 📝 ORDRE D'APPLICATION

1. **Créer les exceptions** (30 min)
2. **Améliorer GlobalExceptionHandler** (15 min)
3. **Corriger les entités** (15 min)
4. **Nettoyer les repositories** (5 min)
5. **Corriger les services** (2h)
6. **Créer ApplicationService** (1h)
7. **Tester que tout compile** (15 min)

**Total estimé: 4h30**

---

**Bon courage pour les corrections! 💪**
