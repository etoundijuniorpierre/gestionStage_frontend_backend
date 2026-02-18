package com.internship.management.interfaces;

import com.internship.management.entities.*;


public interface InternshipService {

    void registerEnterprise(Enterprise enterprise);

    void registerStudent(Student student);

    void registerTeacher(Teacher teacher);

    Users getUserByEmail(String email);
}
