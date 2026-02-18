package com.internship.management.repositories;

import com.internship.management.entities.Student;
import com.internship.management.interfaces.DepartmentInternshipStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    @Query("SELECT s.department AS department, COUNT(s) AS count " +
            "FROM Student s WHERE s.onInternship = true " +
            "GROUP BY s.department")

    List<DepartmentInternshipStat> countInternsByDepartment();

    List<Student> findByDepartment(String department);

    Page<Student> findAll(Pageable pageable);
}
