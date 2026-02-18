package com.internship.management.interfaces;

import com.internship.management.entities.Application;
import com.internship.management.entities.Student;

import java.util.List;

public interface ApplicationService {
    void saveApplication(Application application);

    List<Application> getAllApplicationsByEnterpriseId(Long id);

    Application getApplicationById(Long id);

    List<Application> getApplicationsRejectedOrPendingByStudentEmail(String email);

    List<Application> getApplicationsApprovedByStudentEmail(String email);

    Application getApplicationApprovedById(Long id);

    Application getApplicationByStudentOnInternshipTrue(Student student);

    void deleteApplicationRejected(Long id);
}
