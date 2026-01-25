package com.smartgrocery.ui;

import com.smartgrocery.auth.AuthenticationManager;
import com.smartgrocery.auth.UserRole;
import com.smartgrocery.models.Product;
import com.smartgrocery.inventory.Inventory;
import com.smartgrocery.models.User;
import com.smartgrocery.storage.FileManager;


import java.util.List;
import java.util.Scanner;

public class CLI {
    private Scanner scanner;
    private AuthenticationManager authManager;
    private Inventory inventory;
    private User currentUser;

    public CLI() {
        FileManager fileManager = new FileManager();
        this.scanner = new Scanner(System.in);
        this.authManager = new AuthenticationManager(fileManager);
        this.inventory = new Inventory(fileManager);
        
    }

    public void start() {
        System.out.println("Welcome to Smart Grocery CLI!");
        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": login(); break;
                case "2": register(); break;
                case "3": 
                    System.out.println("Goodbye!");
                    return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        currentUser = authManager.login(username, password);
        if (currentUser != null) {
            System.out.println("Login Successful! Welcome, " + currentUser.getUsername());
            if (currentUser.getRole() == UserRole.ADMIN) {
                adminMenu();
            } else {
                customerMenu();
            }
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        // Default role is CUSTOMER
        if (authManager.register(username, password, UserRole.CUSTOMER)) {
            System.out.println("Registration Successful! Please login.");
        } else {
            System.out.println("Username already exists.");
        }
    }

    private void adminMenu() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Manage Inventory");
            System.out.println("2. Manage Users");
            System.out.println("3. View Reports");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": manageInventory(); break;
                case "2": manageUsers(); break;
                case "3": System.out.println("view report"); break;
                case "4": currentUser = null; return;
                default: System.out.println("Invalid choice.");
            }
        }
    }


     private void manageInventory() {
        while (true) {
            System.out.println("\n--- Inventory Management ---");
            System.out.println("1. View All Products");
            System.out.println("2. Add Product");
            System.out.println("3. Update Stock & Price");
            System.out.println("4. Remove Product");
            System.out.println("5. Back to Admin Menu");
            
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": 
                    printProductTable(inventory.getAllProducts());
                    break;
                case "2":
                    System.out.print("Name (or press Enter to cancel): ");
                    String name = scanner.nextLine();
                    if (name.trim().isEmpty()) {
                        System.out.println("Operation cancelled.");
                        break;
                    }
                    System.out.print("Category: ");
                    String cat = scanner.nextLine();
                    System.out.print("Price: ");
                    double price = Double.parseDouble(scanner.nextLine());
                    System.out.print("Stock: ");
                    int stock = Integer.parseInt(scanner.nextLine());
                    inventory.addProduct(new Product(name, cat, price, stock));
                    System.out.println("Product Added.");
                    break;
                case "3":
                    System.out.print("Product Name: ");
                    String pName = scanner.nextLine();
                    Product p = inventory.searchProduct(pName);
                    if (p != null) {
                        System.out.println("Current Stock: " + p.getStock() + ", Price: " + p.getPrice());
                        
                        System.out.print("Quantity to Add (negative to reduce, 0 to skip): ");
                        int qty = Integer.parseInt(scanner.nextLine());
                        inventory.updateProductStock(pName, qty);
                        
                        System.out.print("New Price (-1 to keep current): ");
                        double newPrice = Double.parseDouble(scanner.nextLine());
                        if (newPrice >= 0) {
                            inventory.updateProductPrice(pName, newPrice);
                        }
                        System.out.println("Product updated.");
                    } else {
                        System.out.println("Product not found.");
                    }
                    break;
                case "4":
                    System.out.print("Enter product name to remove: ");
                    String delName = scanner.nextLine();
                    if (inventory.deleteProduct(delName)) {
                        System.out.println("Product removed successfully.");
                    } else {
                        System.out.println("Product not found.");
                    }
                    break;
                case "5": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }



    private void manageUsers() {
        System.out.println("\n--- Manage Users ---");
        List<User> users = authManager.getAllUsers();
        System.out.printf("%-15s %-10s\n", "Username", "Role");
        System.out.println("-------------------------");
        for (User u : users) {
            System.out.printf("%-15s %-10s\n", u.getUsername(), u.getRole());
        }

        System.out.println("\nUpdate User Role:");
        System.out.print("Enter username (or press Enter to cancel): ");
        String targetUser = scanner.nextLine();
        if (targetUser.isEmpty()) return;

        System.out.println("Select New Role: 1. ADMIN 2. CUSTOMER");
        String roleChoice = scanner.nextLine();
        UserRole newRole = null;
        if (roleChoice.equals("1")) newRole = UserRole.ADMIN;
        else if (roleChoice.equals("2")) newRole = UserRole.CUSTOMER;
        else {
            System.out.println("Invalid role choice.");
            return;
        }

        if (authManager.updateUserRole(targetUser, newRole)) {
            System.out.println("User role updated successfully.");
        } else {
            System.out.println("User not found.");
        }
    }


    private void customerMenu() {
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Browse Products");
            System.out.println("2. View Cart");
            System.out.println("3. View Recommendations");
            System.out.println("4. View History");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": browseProducts(); break;
                case "2": System.out.println("show cart");; break;
                case "3": System.out.println("show recommendations"); break;
                case "4": System.out.println("show history"); break ;
                case "5": currentUser = null; return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

     private void browseProducts() {
        List<Product> products = inventory.getAllProducts();
        System.out.println("\n--- Product List ---");
        printProductTable(products);

        System.out.print("Enter product name to add to cart (or press Enter to cancel): ");
        String pName = scanner.nextLine();
        if (pName.trim().isEmpty()) return;
        
        Product selected = inventory.searchProduct(pName);
        if (selected != null) {
            System.out.print("Enter quantity: ");
            try {
                int qty = Integer.parseInt(scanner.nextLine());
                cart.addItem(selected, qty);
                System.out.println("Added to cart.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity.");
            }
        } else {
             System.out.println("Product not found.");
        }
    }
    


    private void printProductTable(List<Product> products) {
        System.out.printf("%-20s %-15s %-10s %-10s\n", "Name", "Category", "Price", "Stock");
        System.out.println("-----------------------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-20s %-15s $%-9.2f %-10d\n", p.getName(), p.getCategory(), p.getPrice(), p.getStock());
        }
    }
  
  
    public static void main(String[] args) {
        new CLI().start();
    }
}
