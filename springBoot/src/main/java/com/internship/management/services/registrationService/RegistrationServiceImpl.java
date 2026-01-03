package com.internship.management.services.registrationService;

import com.internship.management.entities.*;
import com.internship.management.exception.DuplicateResourceException;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.repositories.*;
import com.internship.management.interfaces.InternshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service d'enregistrement des utilisateurs (Enterprise, Student, Teacher).
 * 
 * @author Backend Team
 * @version 2.0
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RegistrationServiceImpl implements InternshipService {

    private final EnterpriseRepository enterpriseRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final VerificationTokenService verificationTokenService;
    private final UsersRepository userRepository;

    /**
     * Enregistre une nouvelle entreprise.
     *
     * @param enterprise l'entreprise à enregistrer
     * @throws DuplicateResourceException si l'email existe déjà
     */
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

    /**
     * Enregistre un nouvel étudiant.
     *
     * @param student l'étudiant à enregistrer
     * @throws DuplicateResourceException si l'email existe déjà
     */
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

    /**
     * Enregistre un nouvel enseignant.
     *
     * @param teacher l'enseignant à enregistrer
     * @throws DuplicateResourceException si l'email existe déjà
     */
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

    /**
     * Récupère un utilisateur par son email.
     *
     * @param email l'email de l'utilisateur
     * @return l'utilisateur trouvé
     * @throws ResourceNotFoundException si l'utilisateur n'existe pas
     */
    public Users getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

}
