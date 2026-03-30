package com.smartgrocery.auth;

import com.smartgrocery.models.User;
import com.smartgrocery.storage.FileManager;
import com.smartgrocery.utils.PasswordUtils;
import com.smartgrocery.utils.ActivityLogger;

import java.util.List;

public class AuthenticationManager {
    private List<User> users;
    private FileManager fileManager;

    public AuthenticationManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.users = fileManager.loadUsers();
        
        // Migrate existing plain text passwords to hashed passwords
        migratePasswordsToHashed();
    }

    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                boolean passwordMatches;
                
                // Check if password is already hashed or plain text
                if (PasswordUtils.isHashed(user.getPassword())) {
                    passwordMatches = PasswordUtils.verifyPassword(password, user.getPassword());
                } else {
                    // Legacy support for plain text passwords
                    passwordMatches = user.getPassword().equals(password);
                    
                    // If login successful with plain text, hash it for future use
                    if (passwordMatches) {
                        user.setPassword(PasswordUtils.hashPassword(password));
                        fileManager.saveUsers(users);
                    }
                }
                
                if (passwordMatches) {
                    ActivityLogger.logLogin(username, user.getRole().toString());
                    return user;
                }
            }
        }
        
        // Log failed login attempt
        ActivityLogger.logFailedLogin(username);
        return null;
    }

    public boolean register(String username, String password, UserRole role) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false; // Username already exists
            }
        }
        
        // Password validation
        if (!isValidPassword(password)) {
            return false; // Invalid password format
        }
        
        // Hash the password before storing
        String hashedPassword = PasswordUtils.hashPassword(password);
        User newUser = new User(username, hashedPassword, role);
        users.add(newUser);
        fileManager.saveUsers(users);
        
        // Log registration
        ActivityLogger.logRegistration(username);
        return true;
    }
    
    public boolean isValidPassword(String password) {
        // Password must be at least 8 characters long and contain at least one uppercase letter
        String passwordRegex = "^(?=.*[A-Z]).{8,}$";
        return password.matches(passwordRegex);
    }
    
    public String getPasswordRequirements() {
        return "Password must be at least 8 characters long and contain at least one uppercase letter.";
    }
    
    public List<User> getAllUsers() {
        return users;
    }

    public boolean updateUserRole(String username, UserRole newRole) {
        for (User user : users) {
             if (user.getUsername().equals(username)) {
                 user.setRole(newRole);
                 fileManager.saveUsers(users);
                 ActivityLogger.logSystemEvent("ROLE_UPDATE", 
                     "Role updated for user " + username + " to: " + newRole);
                 return true;
             }
        }
        return false;
    }

    /**
     * Migrate existing plain text passwords to hashed passwords
     * This ensures backward compatibility while upgrading security
     */
    private void migratePasswordsToHashed() {
        boolean needsSave = false;
        
        for (User user : users) {
            if (!PasswordUtils.isHashed(user.getPassword())) {
                // Hash the plain text password
                String hashedPassword = PasswordUtils.hashPassword(user.getPassword());
                user.setPassword(hashedPassword);
                needsSave = true;
                
                ActivityLogger.logSystemEvent("PASSWORD_MIGRATION", 
                    "Migrated password to hashed format for user: " + user.getUsername());
            }
        }
        
        if (needsSave) {
            fileManager.saveUsers(users);
            ActivityLogger.logSystemEvent("MIGRATION_COMPLETE", 
                "All passwords migrated to hashed format");
        }
    }
}
