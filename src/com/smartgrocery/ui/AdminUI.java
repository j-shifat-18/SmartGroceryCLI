package com.smartgrocery.ui;

import com.smartgrocery.auth.UserRole;
import com.smartgrocery.models.*;
import com.smartgrocery.utils.ReportGenerator;


import java.util.List;



public class AdminUI extends BaseUI {

    ReportGenerator reportGenerator ;

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
                case "3": viewReports(); break;
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


    private void viewReports() {
        while (true) {
            System.out.println("\n==== Reports Dashboard ====");
            System.out.println("1. Most Sold Items");
            System.out.println("2. Total Sales Summary");
            System.out.println("3. Low Stock Products");
            System.out.println("4. Sales by Category");
            System.out.println("5. Top Revenue Products");
            System.out.println("6. Least Sold Items");
            System.out.println("7. Out of Stock Products");
            System.out.println("8. Daily / Weekly Sales Report");
            System.out.println("0. Back to Admin Menu");
            System.out.print("Select option: ");
            
            String choice = context.getScanner().nextLine();
            
            switch (choice) {
                case "1": reportGenerator.showMostSoldItems(context); break;
                case "2": reportGenerator.showTotalSalesSummary(context); break;
                case "3": reportGenerator.showLowStockProducts(context); break;
                case "4": reportGenerator.showSalesByCategory(context); break;
                case "5": reportGenerator.showTopRevenueProducts(context); break;
                case "6": reportGenerator.showLeastSoldItems(context); break;
                case "7": reportGenerator.showOutOfStockProducts(context); break;
                case "8": reportGenerator.showSalesReport(context); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

}