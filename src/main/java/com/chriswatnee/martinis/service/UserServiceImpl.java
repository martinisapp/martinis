package com.chriswatnee.martinis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of UserService for managing user registration and authentication
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createUser(String username, String password, String firstName, String lastName) {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }

        // Trim inputs
        username = username.trim();
        firstName = firstName.trim();
        lastName = lastName.trim();

        // Check username length (max 20 chars per schema)
        if (username.length() > 20) {
            throw new IllegalArgumentException("Username must be 20 characters or less");
        }

        // Check if username already exists
        if (usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(password);

        try {
            // Insert the user
            jdbcTemplate.update(
                "INSERT INTO `user` (username, `password`, enabled, first_name, last_name) VALUES (?, ?, ?, ?, ?)",
                username, hashedPassword, 1, firstName, lastName
            );

            // Grant ROLE_USER authority
            jdbcTemplate.update(
                "INSERT INTO authority (username, authority) VALUES (?, ?)",
                username, "ROLE_USER"
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean usernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        Long count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM `user` WHERE username = ?",
            Long.class,
            username.trim()
        );

        return count != null && count > 0;
    }

}
