package com.smartgrocery.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for logging user activities
 */
public class ActivityLogger {
    private static final String LOG_FILE = "data/logs/activity.log";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Log user login activity
     * @param username The username that logged in
     * @param role The user's role (ADMIN/CUSTOMER)
     */
    public static void logLogin(String username, String role) {
        logActivity(username, "LOGIN", "User logged in with role: " + role);
    }

    /**
     * Log user logout activity
     * @param username The username that logged out
     */
    public static void logLogout(String username) {
        logActivity(username, "LOGOUT", "User logged out");
    }

    /**
     * Log user registration activity
     * @param username The username that was registered
     */
    public static void logRegistration(String username) {
        logActivity(username, "REGISTER", "New user registered");
    }

    /**
     * Log failed login attempt
     * @param username The username that attempted to login
     */
    public static void logFailedLogin(String username) {
        logActivity(username, "LOGIN_FAILED", "Failed login attempt");
    }

    /**
     * Log general activity
     * @param username The username performing the activity
     * @param action The action type (LOGIN, LOGOUT, etc.)
     * @param details Additional details about the activity
     */
    private static void logActivity(String username, String action, String details) {
        try {
            // Ensure log directory exists
            java.io.File logFile = new java.io.File(LOG_FILE);
            logFile.getParentFile().mkdirs();
            
            // Write log entry
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
                String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
                String logEntry = String.format("[%s] %s | %s | %s%n", 
                    timestamp, action, username, details);
                writer.write(logEntry);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to activity log: " + e.getMessage());
        }
    }

    /**
     * Log system events (startup, shutdown, etc.)
     * @param event The system event
     * @param details Additional details
     */
    public static void logSystemEvent(String event, String details) {
        logActivity("SYSTEM", event, details);
    }
}