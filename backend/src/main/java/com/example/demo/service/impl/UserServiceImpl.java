package com.example.demo.service.impl;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Cacheable(value = "users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Cacheable(value = "userByUsername", key = "#username")
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @CachePut(value = "user", key = "#user.id")
    @CacheEvict(value = "users", allEntries = true)
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @CacheEvict(value = {"user", "users"}, key = "#id", allEntries = true)
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}