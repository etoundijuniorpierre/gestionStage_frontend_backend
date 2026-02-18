package com.internship.management.interfaces;

import com.internship.management.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
    Student getStudentByEmail(String email);

    List<Student> getStudentsByDepartment(String department);

    List<Student> getAllStudents();

    Page<Student> getAllStudentsByPagination(Pageable pageable);

    void saveStudent(Student student);
}
