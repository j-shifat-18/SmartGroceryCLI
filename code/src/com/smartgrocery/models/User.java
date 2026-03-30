package com.smartgrocery.models;

import com.smartgrocery.auth.UserRole;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private UserRole role;
    private List<Purchase> purchaseHistory;

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.purchaseHistory = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
    public List<Purchase> getPurchaseHistory() { return purchaseHistory; }

    public void addPurchase(Purchase purchase) {
        this.purchaseHistory.add(purchase);
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
