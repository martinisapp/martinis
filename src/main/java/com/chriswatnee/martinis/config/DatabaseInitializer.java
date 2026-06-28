package com.martinis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes the database with an admin user if environment variables are set.
 * This provides a secure way to bootstrap the application on first deployment.
 *
 * Environment variables:
 * - ADMIN_USERNAME: Required - The username for the admin account
 * - ADMIN_PASSWORD: Required - The password for the admin account (will be BCrypt hashed)
 * - ADMIN_FIRSTNAME: Optional - First name for the admin user (default: "Admin")
 * - ADMIN_LASTNAME: Optional - Last name for the admin user (default: "User")
 */
@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String username = System.getenv("ADMIN_USERNAME");
        String password = System.getenv("ADMIN_PASSWORD");

        // Only create admin user if environment variables are set
        if (username != null && !username.trim().isEmpty() &&
            password != null && !password.trim().isEmpty()) {

            createAdminUserIfNotExists(username.trim(), password,
                                      System.getenv("ADMIN_FIRSTNAME"),
                                      System.getenv("ADMIN_LASTNAME"));
        }
    }

    private void createAdminUserIfNotExists(String username, String password,
                                           String firstName, String lastName) {
        try {
            // Check if user already exists
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM `user` WHERE username = ?",
                Long.class, username);

            if (count != null && count > 0) {
                logger.info("Admin user already exists. Skipping creation.");
                return;
            }

            // Set default values for optional fields
            String adminFirstName = (firstName != null && !firstName.trim().isEmpty())
                ? firstName.trim() : "Admin";
            String adminLastName = (lastName != null && !lastName.trim().isEmpty())
                ? lastName.trim() : "User";

            // Hash the password
            String hashedPassword = passwordEncoder.encode(password);

            // Insert the user
            jdbcTemplate.update(
                "INSERT INTO `user` (username, `password`, enabled, first_name, last_name) VALUES (?, ?, ?, ?, ?)",
                username, hashedPassword, 1, adminFirstName, adminLastName
            );

            // Grant admin authorities
            jdbcTemplate.update(
                "INSERT INTO authority (username, authority) VALUES (?, ?)",
                username, "ROLE_ADMIN"
            );

            jdbcTemplate.update(
                "INSERT INTO authority (username, authority) VALUES (?, ?)",
                username, "ROLE_USER"
            );

            logger.info("Admin user created successfully with ROLE_ADMIN and ROLE_USER authorities.");

        } catch (Exception e) {
            logger.error("Failed to create admin user", e);
        }
    }
}
