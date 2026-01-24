package com.smartgrocery.auth;

import com.smartgrocery.models.User;
import com.smartgrocery.storage.FileManager;

import java.util.List;

public class AuthenticationManager {
    private List<User> users;
    private FileManager fileManager;

    public AuthenticationManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.users = fileManager.loadUsers();
    }

    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public boolean register(String username, String password, UserRole role) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false; // Username already exists
            }
        }
        User newUser = new User(username, password, role);
        users.add(newUser);
        fileManager.saveUsers(users);
        return true;
    }
    
    public List<User> getAllUsers() {
        return users;
    }

    public boolean updateUserRole(String username, UserRole newRole) {
        for (User user : users) {
             if (user.getUsername().equals(username)) {
                 user.setRole(newRole);
                 fileManager.saveUsers(users);
                 return true;
             }
        }
        return false;
    }
}
