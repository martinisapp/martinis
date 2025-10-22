package com.martinis.service;

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

}
