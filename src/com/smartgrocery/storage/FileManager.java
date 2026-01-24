package com.smartgrocery.storage;

import com.smartgrocery.auth.UserRole;
import com.smartgrocery.models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileManager {
    private static final String USERS_FILE = "data/users/users.txt";


    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    UserRole role = UserRole.valueOf(parts[2]);
                    users.add(new User(username, password, role));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                bw.write(u.getUsername() + "," + u.getPassword() + "," + u.getRole());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

}
