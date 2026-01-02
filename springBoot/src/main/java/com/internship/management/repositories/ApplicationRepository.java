package com.internship.management.repositories;

import com.internship.management.entities.Application;
import com.internship.management.entities.Student;
import com.internship.management.enums.ApplicationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findAllByEnterpriseId(Long id);

    Page<Application> findAllByEnterpriseId(Long id, Pageable pageable);

    @Query("SELECT a FROM Application a WHERE a.student.email = :email AND a.state = 'APPROVED'")
    List<Application> findApprovedApplicationsByStudentEmail(@Param("email") String email);

    @Query("SELECT a FROM Application a WHERE a.student.email = :email AND a.state = 'APPROVED'")
    Page<Application> findApprovedApplicationsByStudentEmailPaged(@Param("email") String email, Pageable pageable);

    @Query("SELECT a FROM Application a WHERE a.student.email = :email AND (a.state = 'PENDING' OR a.state = 'REJECTED')")
    List<Application> findRejectedOrPendingApplicationsByStudentEmail(@Param("email") String email);

    @Query("SELECT a FROM Application a WHERE a.student.email = :email AND (a.state = 'PENDING' OR a.state = 'REJECTED')")
    Page<Application> findRejectedOrPendingApplicationsByStudentEmailPaged(@Param("email") String email,
            Pageable pageable);

    @Query("SELECT a FROM Application a WHERE a.id = :id AND a.state = 'APPROVED'")
    Application findApprovedApplicationById(@Param("id") Long id);

    Application findApplicationByStudentAndStudent_OnInternshipTrue(Student student);
}
