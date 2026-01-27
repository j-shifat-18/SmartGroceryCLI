package com.smartgrocery.ui;

import com.smartgrocery.auth.AuthenticationManager;
import com.smartgrocery.auth.UserRole;
import com.smartgrocery.inventory.Inventory;
import com.smartgrocery.models.User;
import com.smartgrocery.shopping.Cart;
import com.smartgrocery.shopping.Checkout;
import com.smartgrocery.storage.FileManager;

import java.util.Scanner;

public class CLI {
    private final UIContext context;
    private final AuthUI authUI;
    private final AdminUI adminUI;
    private final CustomerUI customerUI;

    public CLI() {
        // Initialize core components
        FileManager fileManager = new FileManager();
        Scanner scanner = new Scanner(System.in);
        AuthenticationManager authManager = new AuthenticationManager(fileManager);
        Inventory inventory = new Inventory(fileManager);
        Cart cart = new Cart();
        Checkout checkout = new Checkout(inventory, fileManager);
        
        // Load purchase history
        fileManager.loadPurchases(authManager.getAllUsers(), inventory.getAllProducts());
        
        // Create shared context
        this.context = new UIContext(scanner, authManager, inventory, cart, checkout);
        
        // Initialize UI components
        this.authUI = new AuthUI(context);
        this.adminUI = new AdminUI(context);
        this.customerUI = new CustomerUI(context);
    }

    //? Start the application
    public void start() {
        System.out.println("Welcome to Smart Grocery CLI!");
        
        while (true) {
            showMainMenu();
            String choice = context.getScanner().nextLine();
            
            switch (choice) {
                case "1": handleLogin(); break;
                case "2": handleRegistration(); break;
                case "3": 
                    System.out.println("Goodbye!");
                    return;
                default: 
                    System.out.println("Invalid choice.");
            }
        }
    }

    
    private void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Enter choice: ");
    }

    
    private void handleLogin() {
        User user = authUI.login();
        if (user != null) {
            if (user.getRole() == UserRole.ADMIN) {
                adminUI.showMenu();
            } else {
                customerUI.showMenu();
            }
        }
    }

   
    private void handleRegistration() {
        authUI.register();
    }

    public static void main(String[] args) {
        new CLI().start();
    }
}
