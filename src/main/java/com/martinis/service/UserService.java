package com.martinis.service;

import java.util.List;
import java.util.Map;

/**
 * Service interface for user management and registration
 */
public interface UserService {

    /**
     * Creates a new user with the specified credentials
     * @param username Username for the new user
     * @param password Plain text password (will be BCrypt hashed)
     * @param firstName First name of the user
     * @param lastName Last name of the user
     * @throws IllegalArgumentException if username already exists or parameters are invalid
     */
    void createUser(String username, String password, String firstName, String lastName);

    /**
     * Checks if a username is already taken
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    boolean usernameExists(String username);

    /**
     * Gets all users pending approval (enabled = 0)
     * @return List of user data maps containing username, first_name, last_name, id
     */
    List<Map<String, Object>> getPendingUsers();

    /**
     * Approves a user by setting enabled = 1
     * @param username Username to approve
     */
    void approveUser(String username);

    /**
     * Rejects a user by deleting their account
     * @param username Username to reject
     */
    void rejectUser(String username);

}
