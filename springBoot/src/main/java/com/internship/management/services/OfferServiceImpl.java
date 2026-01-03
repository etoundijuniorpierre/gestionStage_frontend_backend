package com.internship.management.services;

import com.internship.management.entities.*;
import com.internship.management.enums.ConventionState;
import com.internship.management.enums.OfferStatus;
import com.internship.management.exception.ResourceNotFoundException;
import com.internship.management.interfaces.PostOffer;
import com.internship.management.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Implémentation du service de gestion des offres de stage.
 * 
 * @author Backend Team
 * @version 2.0
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OfferServiceImpl implements PostOffer {

    private final OfferRepository offerRepository;
    private final TeacherRepository teacherRepository;
    private final ConventionRepository conventionRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final StudentRepository studentRepository;
    private final ApplicationRepository applicationRepository;
    private final UsersRepository userRepository;
    private final LogoRepository logoRepository;

    public Offer getOfferById(Long id) {
        log.debug("Fetching offer with id: {}", id);
        return offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer", "id", id));
    }

    @Transactional
    public void saveOffer(Offer offer) {
        log.info("Saving offer: {}", offer.getTitle());
        offerRepository.save(offer);
    }

    public Teacher getTeacherByEmail(String email) {
        log.debug("Fetching teacher with email: {}", email);
        return teacherRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "email", email));
    }

    public List<Offer> getOfferByDepartmentAndPendingOfferStatusAndInPartnershipTrue(String department,
            OfferStatus offerStatus) {
        return offerRepository.findByDomainAndStatusAndEnterprise_InPartnershipTrue(department, offerStatus);
    }

    @Override
    public Page<Offer> getOfferByDepartmentAndPendingOfferStatusAndInPartnershipTruePaged(String department,
            OfferStatus offerStatus, Pageable pageable) {
        return offerRepository.findByDomainAndStatusAndEnterprise_InPartnershipTrue(department, offerStatus, pageable);
    }

    public Enterprise getByEnterpriseEmail(String email) {
        log.debug("Fetching enterprise with email: {}", email);
        return enterpriseRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Enterprise", "email", email));
    }

    public Enterprise getByEnterpriseId(Long id) {
        log.debug("Fetching enterprise with id: {}", id);
        return enterpriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enterprise", "id", id));
    }

    public List<Enterprise> getEnterpriseByPartnershipFalse() {
        return enterpriseRepository.findByInPartnershipFalse();
    }

    @Override
    public Page<Enterprise> getEnterpriseByPartnershipFalsePaged(Pageable pageable) {
        return enterpriseRepository.findByInPartnershipFalse(pageable);
    }

    public List<Enterprise> getEnterpriseByPartnershipTrue() {
        return enterpriseRepository.findByInPartnershipTrue();
    }

    @Override
    public Page<Enterprise> getEnterpriseByPartnershipTruePaged(Pageable pageable) {
        return enterpriseRepository.findByInPartnershipTrue(pageable);
    }

    public List<Offer> getOffersByStatusAndConventionApproved(OfferStatus offerStatus, ConventionState conventionState,
            String domain) {
        return offerRepository.findOffersByStatusAndConventionStateAndDomain(offerStatus, conventionState, domain);
    }

    @Override
    public Page<Offer> getOffersByStatusAndConventionApprovedPaged(OfferStatus offerStatus,
            ConventionState conventionState, String domain, Pageable pageable) {
        return offerRepository.findOffersByStatusAndConventionStateAndDomainPaged(offerStatus, conventionState, domain,
                pageable);
    }

    public Student getStudentByEmail(String email) {
        log.debug("Fetching student with email: {}", email);
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "email", email));
    }

    public List<Student> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department);
    }

    @Override
    public Page<Student> getStudentsByDepartmentPaged(String department, Pageable pageable) {
        return studentRepository.findByDepartment(department, pageable);
    }

    @Transactional
    public void saveApplication(Application application) {
        log.info("Saving application for student: {}", application.getStudent().getEmail());
        applicationRepository.save(application);
    }

    public List<Application> getAllApplicationsByEnterpriseId(Long id) {
        return applicationRepository.findAllByEnterpriseId(id);
    }

    @Override
    public Page<Application> getAllApplicationsByEnterpriseIdPaged(Long id, Pageable pageable) {
        return applicationRepository.findAllByEnterpriseId(id, pageable);
    }

    public Application getApplicationById(Long id) {
        log.debug("Fetching application with id: {}", id);
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
    }

    @Transactional
    public void deleteUser(Long id) {
        log.warn("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }

    public List<Offer> getOfferByEnterpriseId(Long enterpriseId) {
        return offerRepository.findOfferByEnterpriseId(enterpriseId);
    }

    @Override
    public Page<Offer> getOfferByEnterpriseIdPaged(Long enterpriseId, Pageable pageable) {
        return offerRepository.findOfferByEnterpriseId(enterpriseId, pageable);
    }

    public Users getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Transactional
    public void saveUser(Users user) {
        log.info("Saving user: {}", user.getEmail());
        userRepository.save(user);
    }

    public Logo getLogoByEnterprise(Enterprise enterprise) {
        log.debug("Fetching logo for enterprise: {}", enterprise.getId());
        return logoRepository.findByEnterprise(enterprise)
                .orElseThrow(() -> new ResourceNotFoundException("Logo", "enterpriseId", enterprise.getId()));
    }

    @Transactional
    public void updateLogo(Long enterpriseId, MultipartFile file) throws IOException {
        log.info("Updating logo for enterprise: {}", enterpriseId);

        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enterprise", "id", enterpriseId));

        Logo logo = logoRepository.findByEnterprise(enterprise)
                .orElse(new Logo());

        logo.setEnterprise(enterprise);
        logo.setLogo(file.getBytes());
        logo.setContentType(file.getContentType());

        logoRepository.save(logo);
        log.debug("Logo updated successfully for enterprise: {}", enterpriseId);
    }

    public Convention getConventionByOfferId(Long offerId) {
        log.debug("Fetching convention for offer: {}", offerId);
        return conventionRepository.findByOffer_Id(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Convention", "offerId", offerId));
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Page<Teacher> getAllTeacherByPagination(Pageable pageable) {
        return teacherRepository.findAll(pageable);
    }

    public List<Student> getAllStudent() {
        return studentRepository.findAll();
    }

    public Page<Student> getAllStudentByPagination(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Transactional
    public void saveConvention(Convention convention) {
        log.info("Saving convention for offer: {}", convention.getOffer().getId());
        conventionRepository.save(convention);
    }

    public List<Offer> getOffersByStatusApprovedAndTeacherEmail(OfferStatus offerStatus, String email) {
        return offerRepository.findOffersByStatusAndValidatedBy_Email(offerStatus, email);
    }

    @Override
    public Page<Offer> getOffersByStatusApprovedAndTeacherEmailPaged(OfferStatus offerStatus, String email,
            Pageable pageable) {
        return offerRepository.findOffersByStatusAndValidatedBy_Email(offerStatus, email, pageable);
    }

    public List<Application> getApplicationsApprovedByStudentEmail(String email) {
        return applicationRepository.findApprovedApplicationsByStudentEmail(email);
    }

    @Override
    public Page<Application> getApplicationsApprovedByStudentEmailPaged(String email, Pageable pageable) {
        return applicationRepository.findApprovedApplicationsByStudentEmailPaged(email, pageable);
    }

    public List<Application> getApplicationsRejectedOrPendingByStudentEmail(String email) {
        return applicationRepository.findRejectedOrPendingApplicationsByStudentEmail(email);
    }

    @Override
    public Page<Application> getApplicationsRejectedOrPendingByStudentEmailPaged(String email, Pageable pageable) {
        return applicationRepository.findRejectedOrPendingApplicationsByStudentEmailPaged(email, pageable);
    }

    public Application getApplicationApprovedById(Long id) {
        return applicationRepository.findApprovedApplicationById(id);
    }

    public Application getApplicationByStudentOnInternshipTrue(Student student) {
        return applicationRepository.findApplicationByStudentAndStudent_OnInternshipTrue(student);
    }

    public List<Teacher> getTeachersByDepartment(String department) {
        return teacherRepository.findByDepartment(department);
    }

    @Transactional
    public void deleteApplicationRejected(Long id) {
        log.warn("Deleting rejected application: {}", id);
        applicationRepository.deleteById(id);
    }
}
