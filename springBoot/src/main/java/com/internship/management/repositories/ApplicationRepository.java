package com.internship.management.repositories;

import com.internship.management.entities.Application;
import com.internship.management.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application,Long> {

    List<Application> findAllByEnterpriseId(Long id);

    List<Application> findApprovedApplicationsByStudentEmail(String email);

    List<Application> findRejectedOrPendingApplicationsByStudentEmail(String email);

    Application findApprovedApplicationById(Long id);

    Application findApplicationByStudentAndStudent_OnInternshipTrue(Student student);

}
