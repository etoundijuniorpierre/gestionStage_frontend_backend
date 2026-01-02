package com.internship.management.services;

import com.internship.management.entities.*;
import com.internship.management.enums.ConventionState;
import com.internship.management.enums.OfferStatus;
import com.internship.management.interfaces.PostOffer;
import com.internship.management.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
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
        return offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer Not Found"));
    }

    public void saveOffer(Offer offer) {
        offerRepository.save(offer);
    }

    public Teacher getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

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
        return enterpriseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Enterprise Not Found"));
    }

    public Enterprise getByEnterpriseId(Long id) {
        return enterpriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enterprise Not Found"));
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
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student Not Found"));
    }

    public List<Student> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department);
    }

    @Override
    public Page<Student> getStudentsByDepartmentPaged(String department, Pageable pageable) {
        return studentRepository.findByDepartment(department, pageable);
    }

    public void saveApplication(Application application) {
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
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application Not Found"));
    }

    public void deleteUser(Long id) {
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
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void saveUser(Users user) {
        userRepository.save(user);
    }

    public Logo getLogoByEnterprise(Enterprise enterprise) {
        return logoRepository.findByEnterprise(enterprise)
                .orElseThrow(() -> new RuntimeException("Logo not found"));
    }

    public void updateLogo(Long enterpriseId, MultipartFile file) throws IOException {

        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new RuntimeException("Enterprise not found"));

        Logo logo = logoRepository.findByEnterprise(enterprise)
                .orElse(new Logo());

        logo.setEnterprise(enterprise);
        logo.setLogo(file.getBytes());
        logo.setContentType(file.getContentType());

        logoRepository.save(logo);
    }

    public Convention getConventionByOfferId(Long offerId) {
        return conventionRepository.findByOffer_Id(offerId)
                .orElseThrow(() -> new RuntimeException("Convention not found"));
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

    public void saveConvention(Convention convention) {
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

    public void deleteApplicationRejected(Long id) {
        applicationRepository.deleteById(id);
    }
}
