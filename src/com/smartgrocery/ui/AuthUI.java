package com.smartgrocery.ui;

import com.smartgrocery.auth.UserRole;
import com.smartgrocery.models.User;
import com.smartgrocery.utils.ActivityLogger;

/**
 * Handles authentication-related UI operations
 */
public class AuthUI extends BaseUI {

    public AuthUI(UIContext context) {
        super(context);
    }

    /**
     * Handle user login
     */
    public User login() {
        System.out.print("Username: ");
        String username = context.getScanner().nextLine();
        System.out.print("Password: ");
        String password = context.getScanner().nextLine();
        
        User user = context.getAuthManager().login(username, password);
        if (user != null) {
            System.out.println("Login Successful! Welcome, " + user.getUsername());
            context.setCurrentUser(user);
        } else {
            System.out.println("Invalid credentials.");
        }
        return user;
    }

    /**
     * Handle user registration
     */
    public boolean register() {
        System.out.print("Enter username: ");
        String username = context.getScanner().nextLine();
        System.out.println("Password Requirements: " + context.getAuthManager().getPasswordRequirements());
        System.out.print("Enter password: ");
        String password = context.getScanner().nextLine();
        
        if (!context.getAuthManager().isValidPassword(password)) {
            System.out.println("Registration Failed: " + context.getAuthManager().getPasswordRequirements());
            return false;
        }
        
        if (context.getAuthManager().register(username, password, UserRole.CUSTOMER)) {
            System.out.println("Registration Successful! Please login.");
            return true;
        } else {
            System.out.println("Username already exists.");
            return false;
        }
    }


    public void logout() {
        if (context.getCurrentUser() != null) {
            String username = context.getCurrentUser().getUsername();
            ActivityLogger.logLogout(username);
            System.out.println("Goodbye, " + username + "!");
            context.setCurrentUser(null);
            context.getCart().clearCart();
        }
    }
}