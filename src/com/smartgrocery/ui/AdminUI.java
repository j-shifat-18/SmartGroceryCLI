package com.smartgrocery.ui;

import com.smartgrocery.auth.UserRole;
import com.smartgrocery.models.*;

import java.util.List;


public class AdminUI extends BaseUI {

    public AdminUI(UIContext context) {
        super(context);
    }

   
    public void showMenu() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Manage Inventory");
            System.out.println("2. Manage Users");
            System.out.println("3. View Reports");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");
            
            String choice = context.getScanner().nextLine();
            switch (choice) {
                case "1": manageInventory(); break;
                case "2": manageUsers(); break;
                case "3": System.out.println("Feature comming soon!");; break;
                case "4": 
                    context.getAuthUI().logout();
                    return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    
    private void manageInventory() {
        InventoryUI inventoryUI = new InventoryUI(context);
        inventoryUI.showMenu();
    }

    
    private void manageUsers() {
        System.out.println("\n--- Manage Users ---");
        List<User> users = context.getAuthManager().getAllUsers();
        System.out.printf("%-15s %-10s\n", "Username", "Role");
        System.out.println("-------------------------");
        for (User u : users) {
            System.out.printf("%-15s %-10s\n", u.getUsername(), u.getRole());
        }

        System.out.println("\nUpdate User Role:");
        System.out.print("Enter username (or press Enter to cancel): ");
        String targetUser = context.getScanner().nextLine();
        if (targetUser.isEmpty()) return;

        System.out.println("Select New Role: 1. ADMIN 2. CUSTOMER");
        String roleChoice = context.getScanner().nextLine();
        UserRole newRole = null;
        if (roleChoice.equals("1")) newRole = UserRole.ADMIN;
        else if (roleChoice.equals("2")) newRole = UserRole.CUSTOMER;
        else {
            System.out.println("Invalid role choice.");
            return;
        }

        if (context.getAuthManager().updateUserRole(targetUser, newRole)) {
            System.out.println("User role updated successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

}