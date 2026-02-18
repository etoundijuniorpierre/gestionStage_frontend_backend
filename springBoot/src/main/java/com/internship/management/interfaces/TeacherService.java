package com.internship.management.interfaces;

import com.internship.management.entities.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeacherService {
    Teacher getTeacherByEmail(String email);

    List<Teacher> getAllTeachers();

    Page<Teacher> getAllTeachersByPagination(Pageable pageable);

    List<Teacher> getTeachersByDepartment(String department);

    void saveTeacher(Teacher teacher);
}
