package com.internship.management.interfaces;

import com.internship.management.entities.*;
import com.internship.management.enums.ConventionState;
import com.internship.management.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostOffer {

        Offer getOfferById(Long id);

        void saveOffer(Offer offer);

        Teacher getTeacherByEmail(String email);

        Enterprise getByEnterpriseEmail(String email);

        List<Offer> getOffersByStatusAndConventionApproved(OfferStatus offerStatus, ConventionState conventionState,
                        String domain);

        Page<Offer> getOffersByStatusAndConventionApprovedPaged(OfferStatus offerStatus,
                        ConventionState conventionState,
                        String domain, Pageable pageable);

        Student getStudentByEmail(String email);

        void saveApplication(Application application);

        List<Application> getAllApplicationsByEnterpriseId(Long id);

        Page<Application> getAllApplicationsByEnterpriseIdPaged(Long id, Pageable pageable);

        Enterprise getByEnterpriseId(Long id);

        void deleteUser(Long id);

        List<Offer> getOfferByEnterpriseId(Long enterpriseId);

        Page<Offer> getOfferByEnterpriseIdPaged(Long enterpriseId, Pageable pageable);

        Users getUserByEmail(String email);

        void saveUser(Users user);

        Application getApplicationById(Long id);

        List<Student> getStudentsByDepartment(String department);

        Page<Student> getStudentsByDepartmentPaged(String department, Pageable pageable);

        Logo getLogoByEnterprise(Enterprise enterprise);

        Convention getConventionByOfferId(Long offerId);

        List<Enterprise> getEnterpriseByPartnershipFalse();

        Page<Enterprise> getEnterpriseByPartnershipFalsePaged(Pageable pageable);

        List<Offer> getOfferByDepartmentAndPendingOfferStatusAndInPartnershipTrue(String department,
                        OfferStatus offerStatus);

        Page<Offer> getOfferByDepartmentAndPendingOfferStatusAndInPartnershipTruePaged(String department,
                        OfferStatus offerStatus, Pageable pageable);

        List<Teacher> getAllTeachers();

        List<Student> getAllStudent();

        void saveConvention(Convention convention);

        Page<Teacher> getAllTeacherByPagination(Pageable pageable);

        Page<Student> getAllStudentByPagination(Pageable pageable);

        List<Enterprise> getEnterpriseByPartnershipTrue();

        Page<Enterprise> getEnterpriseByPartnershipTruePaged(Pageable pageable);

        List<Offer> getOffersByStatusApprovedAndTeacherEmail(OfferStatus offerStatus, String email);

        Page<Offer> getOffersByStatusApprovedAndTeacherEmailPaged(OfferStatus offerStatus, String email,
                        Pageable pageable);

        List<Application> getApplicationsApprovedByStudentEmail(String email);

        Page<Application> getApplicationsApprovedByStudentEmailPaged(String email, Pageable pageable);

        List<Application> getApplicationsRejectedOrPendingByStudentEmail(String email);

        Page<Application> getApplicationsRejectedOrPendingByStudentEmailPaged(String email, Pageable pageable);

        Application getApplicationApprovedById(Long id);

        Application getApplicationByStudentOnInternshipTrue(Student student);

        List<Teacher> getTeachersByDepartment(String department);

        void deleteApplicationRejected(Long id);

        void updateLogo(Long enterpriseId, MultipartFile file) throws IOException;
}
