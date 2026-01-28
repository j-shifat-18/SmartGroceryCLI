package com.smartgrocery.ui;

import com.smartgrocery.auth.AuthenticationManager;
import com.smartgrocery.inventory.Inventory;
import com.smartgrocery.models.User;
import com.smartgrocery.shopping.Cart;
import com.smartgrocery.shopping.Checkout;


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
    private User currentUser;
    private AuthUI authUI;

    public UIContext(Scanner scanner, AuthenticationManager authManager, Inventory inventory, 
                    Cart cart, Checkout checkout) {
        this.scanner = scanner;
        this.authManager = authManager;
        this.inventory = inventory;
        this.cart = cart;
        this.checkout = checkout;
        this.authUI = new AuthUI(this);
      
    }

    // Getters
    public Scanner getScanner() { return scanner; }
    public AuthenticationManager getAuthManager() { return authManager; }
    public Inventory getInventory() { return inventory; }
    public Cart getCart() { return cart; }
    public Checkout getCheckout() { return checkout; }
    public User getCurrentUser() { return currentUser; }
    public AuthUI getAuthUI() { return authUI; }
    
    // Setters
    public void setCurrentUser(User user) { this.currentUser = user; }
}