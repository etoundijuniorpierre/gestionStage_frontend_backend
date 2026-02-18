package com.internship.management.repositories;

import com.internship.management.entities.Student;
import com.internship.management.entities.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmail(String email);

    Page<Teacher> findAll(Pageable pageable);

    List<Teacher> findByDepartment(String department);
}
