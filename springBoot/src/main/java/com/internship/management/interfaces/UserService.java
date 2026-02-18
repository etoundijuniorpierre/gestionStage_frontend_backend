package com.internship.management.interfaces;

import com.internship.management.entities.Users;

public interface UserService {
    Users getUserByEmail(String email);

    void deleteUser(Long id);

    void saveUser(Users user);
}
