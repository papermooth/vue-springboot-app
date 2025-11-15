package com.example.demo.service;

import com.example.demo.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUsername(String username);

    User saveUser(User user);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}