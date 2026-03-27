package com.smartgrocery.ui;

import com.smartgrocery.auth.AuthenticationManager;
import com.smartgrocery.engine.Analytics;
import com.smartgrocery.engine.RecommendationEngine;
import com.smartgrocery.inventory.Inventory;
import com.smartgrocery.models.User;
import com.smartgrocery.shopping.Cart;
import com.smartgrocery.shopping.Checkout;
import com.smartgrocery.storage.FileManager;

import java.util.Scanner;

/**
 * Shared context for all UI components
 */
public class UIContext {
    private final Scanner scanner;
    private final AuthenticationManager authManager;
    private final Inventory inventory;
    private final Cart cart;
    private final Checkout checkout;
    private final RecommendationEngine recEngine;
    private final Analytics analytics;
    private final FileManager fileManager;
    private User currentUser;
    private AuthUI authUI; 

    public UIContext(Scanner scanner, AuthenticationManager authManager, Inventory inventory, 
                    Cart cart, Checkout checkout, RecommendationEngine recEngine, Analytics analytics,
                    FileManager fileManager) {
        this.scanner = scanner;
        this.authManager = authManager;
        this.inventory = inventory;
        this.cart = cart;
        this.checkout = checkout;
        this.recEngine = recEngine;
        this.analytics = analytics;
        this.fileManager = fileManager;
        this.authUI = new AuthUI(this); // Initialize AuthUI
    }

    // Getters
    public Scanner getScanner() { return scanner; }
    public AuthenticationManager getAuthManager() { return authManager; }
    public Inventory getInventory() { return inventory; }
    public Cart getCart() { return cart; }
    public Checkout getCheckout() { return checkout; }
    public RecommendationEngine getRecEngine() { return recEngine; }
    public Analytics getAnalytics() { return analytics; }
    public FileManager getFileManager() { return fileManager; }
    public User getCurrentUser() { return currentUser; }
    public AuthUI getAuthUI() { return authUI; } // Add getter for AuthUI
    
  
    public void setCurrentUser(User user) { this.currentUser = user; }
}