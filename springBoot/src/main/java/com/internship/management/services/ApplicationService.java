package com.internship.management.services;

import com.internship.management.dto.application.ApplicationRequestDto;
import com.internship.management.dto.application.ApplicationResponseDto;
import com.internship.management.entities.Application;
import com.internship.management.entities.Offer;
import com.internship.management.entities.Student;
import com.internship.management.enums.ApplicationState;
import com.internship.management.exception.BusinessException;
import com.internship.management.interfaces.NotificationInterface;
import com.internship.management.interfaces.PostOffer;
import com.internship.management.mappers.PostOfferMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service métier pour la gestion des candidatures (applications).
 * Centralise toute la logique métier liée aux candidatures.
 * 
 * @author Backend Team
 * @version 1.0
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApplicationService {

    private final PostOffer postOffer;
    private final PostOfferMapper mapper;
    private final NotificationInterface notificationInterface;

    /**
     * Crée une nouvelle candidature pour un étudiant.
     *
     * @param studentEmail l'email de l'étudiant
     * @param offerId      l'ID de l'offre
     * @param dto          les données de la candidature
     * @return la candidature créée
     * @throws BusinessException si l'étudiant est déjà en stage ou a déjà candidaté
     */
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

    /**
     * Valide qu'un étudiant peut postuler à une offre.
     *
     * @param student l'étudiant
     * @param offerId l'ID de l'offre
     * @throws BusinessException si validation échoue
     */
    private void validateStudentCanApply(Student student, Long offerId) {
        if (student.isOnInternship()) {
            log.warn("Student {} is already on internship", student.getEmail());
            throw new BusinessException("You are already on internship and cannot apply anymore");
        }

        boolean alreadyApplied = student.getApplications().stream()
                .anyMatch(app -> app.getOffer().getId().equals(offerId) &&
                        (app.getState() == ApplicationState.PENDING ||
                                app.getState() == ApplicationState.APPROVED));

        if (alreadyApplied) {
            log.warn("Student {} already has a pending/approved application for offer {}",
                    student.getEmail(), offerId);
            throw new BusinessException("You already have a pending or approved application for this offer");
        }
    }

    /**
     * Construit une entité Application à partir du DTO.
     *
     * @param student l'étudiant
     * @param offer   l'offre
     * @param dto     les données de la candidature
     * @return l'application construite
     */
    private Application buildApplication(Student student, Offer offer, ApplicationRequestDto dto) {
        Application application = mapper.toEntity(dto);
        application.setStudent(student);
        application.setEnterprise(offer.getEnterprise());
        application.setOffer(offer);
        application.setState(ApplicationState.PENDING);
        return application;
    }

    /**
     * Notifie l'entreprise d'une nouvelle candidature.
     *
     * @param offer   l'offre concernée
     * @param student l'étudiant qui a postulé
     */
    private void notifyEnterprise(Offer offer, Student student) {
        String message = String.format("Nouvelle candidature reçue de %s %s pour l'offre: %s",
                student.getFirstName(), student.getName(), offer.getTitle());
        notificationInterface.sendNotification(offer.getEnterprise(), message);
        log.debug("Enterprise notified about new application from {}", student.getEmail());
    }

    /**
     * Met à jour le statut d'un étudiant suite à l'acceptation d'une offre.
     *
     * @param studentEmail  l'email de l'étudiant
     * @param applicationId l'ID de la candidature
     * @param accepted      true si l'étudiant accepte l'offre
     * @return la candidature mise à jour
     * @throws BusinessException si l'étudiant n'est pas propriétaire de la
     *                           candidature
     */
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

    /**
     * Valide que l'étudiant est propriétaire de la candidature.
     *
     * @param student     l'étudiant
     * @param application la candidature
     * @throws BusinessException si l'étudiant n'est pas propriétaire
     */
    private void validateOwnership(Student student, Application application) {
        if (!application.getStudent().getId().equals(student.getId())) {
            log.warn("Student {} tried to manage application {} owned by another student",
                    student.getEmail(), application.getId());
            throw new BusinessException("You can only manage your own applications");
        }
    }

    /**
     * Marque une candidature comme acceptée et met à jour le statut de l'étudiant.
     *
     * @param student     l'étudiant
     * @param application la candidature
     */
    private void acceptApplication(Student student, Application application) {
        student.setOnInternship(true);
        postOffer.saveUser(student);

        application.setState(ApplicationState.APPROVED);
        postOffer.saveApplication(application);

        log.info("Student {} accepted internship from application {}",
                student.getEmail(), application.getId());
    }

    /**
     * Récupère toutes les candidatures d'un étudiant.
     *
     * @param studentEmail l'email de l'étudiant
     * @param state        l'état des candidatures (optionnel)
     * @return la liste des candidatures
     */
    public java.util.List<ApplicationResponseDto> getStudentApplications(
            String studentEmail,
            ApplicationState state) {

        log.debug("Fetching applications for student {} with state {}", studentEmail, state);

        Student student = postOffer.getStudentByEmail(studentEmail);

        return student.getApplications().stream()
                .filter(app -> state == null || app.getState() == state)
                .map(mapper::toDto)
                .toList();
    }
}
