package com.internship.management.services;

import com.internship.management.entities.Application;
import com.internship.management.entities.Student;
import com.internship.management.interfaces.ApplicationService;
import com.internship.management.repositories.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Override
    public void saveApplication(Application application) {
        applicationRepository.save(application);
    }

    @Override
    public List<Application> getAllApplicationsByEnterpriseId(Long id) {
        return applicationRepository.findAllByEnterpriseId(id);
    }

    @Override
    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application Not Found ID: " + id));
    }

    @Override
    public List<Application> getApplicationsRejectedOrPendingByStudentEmail(String email) {
        return applicationRepository.findRejectedOrPendingApplicationsByStudentEmail(email);
    }

    @Override
    public List<Application> getApplicationsApprovedByStudentEmail(String email) {
        return applicationRepository.findApprovedApplicationsByStudentEmail(email);
    }

    @Override
    public Application getApplicationApprovedById(Long id) {
        return applicationRepository.findApprovedApplicationById(id);
    }

    @Override
    public Application getApplicationByStudentOnInternshipTrue(Student student) {
        return applicationRepository.findApplicationByStudentAndStudent_OnInternshipTrue(student);
    }

    @Override
    public List<Application> getApplicationsByStudentId(Long id) {
        return applicationRepository.findAllByStudentId(id);
    }

    @Override
    public List<Application> getApplicationsByStudentIds(List<Long> studentIds) {
        return applicationRepository.findAllByStudentIdIn(studentIds);
    }

    @Override
    public void deleteApplicationRejected(Long id) {
        applicationRepository.deleteById(id);
    }
}
