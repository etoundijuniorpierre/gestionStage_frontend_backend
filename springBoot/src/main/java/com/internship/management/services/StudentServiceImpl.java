package com.internship.management.services;

import com.internship.management.entities.Student;
import com.internship.management.interfaces.StudentService;
import com.internship.management.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import com.internship.management.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student Not Found: " + email));
    }

    @Override
    public List<Student> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Page<Student> getAllStudentsByPagination(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Override
    public void saveStudent(Student student) {
        studentRepository.save(student);
    }
}
