package com.smartgrocery.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.smartgrocery.engine.Analytics;
import com.smartgrocery.models.Company;
import com.smartgrocery.models.Product;
import com.smartgrocery.models.User;
import com.smartgrocery.ui.UIContext;

public class ReportGenerator {

    protected final UIContext context;
     
    private void showMostSoldItems() {
        System.out.println("\n===== Most Sold Items =====");
        List<User> allUsers = context.getAuthManager().getAllUsers();
        List<Map.Entry<Product, Integer>> mostSold = context.getAnalytics().getMostSoldItems(allUsers, 10);
        
        if (mostSold.isEmpty()) {
            System.out.println("No sales data available yet.");
            return;
        }
        
        System.out.println("\nRank  Product Name              Quantity Sold");
        System.out.println("------------------------------------------------");
        int rank = 1;
        for (Map.Entry<Product, Integer> entry : mostSold) {
            System.out.printf("%-5d %-25s %d units\n", 
                rank++, entry.getKey().getName(), entry.getValue());
        }
    }

    /**
     * 2. Show Total Sales Summary
     */
    private void showTotalSalesSummary() {
        System.out.println("\n===== Total Sales Summary =====");
        List<User> allUsers = context.getAuthManager().getAllUsers();
        Analytics.SalesSummary summary = context.getAnalytics().getTotalSalesSummary(allUsers);
        
        System.out.println("\nTotal Orders:          " + summary.totalOrders);
        System.out.println("Total Products Sold:   " + summary.totalProductsSold + " units");
        System.out.printf("Total Revenue:         $%.2f\n", summary.totalRevenue);
        System.out.printf("Average Order Value:   $%.2f\n", summary.averageOrderValue);
        
        if (summary.totalOrders == 0) {
            System.out.println("\nNo sales data available yet.");
        }
    }

    /**
     * 3. Show Low Stock Products - Smart Detection based on Sales Velocity
     */
    private void showLowStockProducts() {
        System.out.println("\n===== Smart Low Stock Detection =====");
        System.out.println("This system analyzes sales velocity to predict stock shortages.");
        System.out.println("Algorithm: Calculates days of stock remaining based on last 7 days sales.");
        System.out.println();
        
        System.out.print("Enter threshold days (default 3 days): ");
        String input = context.getScanner().nextLine();
        int thresholdDays = 3;
        
        try {
            if (!input.isEmpty()) {
                thresholdDays = Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default threshold of 3 days.");
        }
        
        System.out.println("\n===== Low Stock Products (< " + thresholdDays + " days of stock) =====");
        
        List<Analytics.LowStockAlert> alerts = context.getAnalytics()
            .getSmartLowStockProducts(context.getAuthManager().getAllUsers(), thresholdDays);
        
        if (alerts.isEmpty()) {
            System.out.println("✓ No low stock alerts! All products have sufficient inventory.");
            return;
        }
        
        System.out.println("\nProduct Name              Stock   7-Day Sales  Avg/Day  Days Left  Status");
        System.out.println("─────────────────────────────────────────────────────────────────────────────");
        
        for (Analytics.LowStockAlert alert : alerts) {
            String urgency = alert.getUrgencyLevel();
            String urgencySymbol = getUrgencySymbol(urgency);
            
            if (alert.daysOfStock < 0) {
                // No sales data
                System.out.printf("%-25s %-7d %-12d %-8s %-10s %s %s\n", 
                    truncate(alert.product.getName(), 25),
                    alert.currentStock,
                    alert.salesLast7Days,
                    "N/A",
                    "N/A",
                    urgencySymbol,
                    urgency);
            } else {
                System.out.printf("%-25s %-7d %-12d %-8.1f %-10.1f %s %s\n", 
                    truncate(alert.product.getName(), 25),
                    alert.currentStock,
                    alert.salesLast7Days,
                    alert.avgDailySales,
                    alert.daysOfStock,
                    urgencySymbol,
                    urgency);
            }
        }
        
        System.out.println("─────────────────────────────────────────────────────────────────────────────");
        System.out.println("\n⚠️  " + alerts.size() + " product(s) need attention!");
        System.out.println("\nUrgency Levels:");
        System.out.println("  🔴 CRITICAL - Less than 1 day of stock");
        System.out.println("  🟠 URGENT   - Less than 2 day