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

    public UIContext(Scanner scanner, AuthenticationManager authManager, Inventory inventory, 
                    Cart cart, Checkout checkout) {
        this.scanner = scanner;
        this.authManager = authManager;
        this.inventory = inventory;
        this.cart = cart;
        this.checkout = checkout;
      
    }

    // Getters
    public Scanner getScanner() { return scanner; }
    public AuthenticationManager getAuthManager() { return authManager; }
    public Inventory getInventory() { return inventory; }
    public Cart getCart() { return cart; }
    public Checkout getCheckout() { return checkout; }
    public User getCurrentUser() { return currentUser; }
    
    // Setters
    public void setCurrentUser(User user) { this.currentUser = user; }
}