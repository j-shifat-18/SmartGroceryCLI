package com.smartgrocery.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.smartgrocery.engine.Analytics;
import com.smartgrocery.models.Category;
import com.smartgrocery.models.Company;
import com.smartgrocery.models.Product;
import com.smartgrocery.models.User;
import com.smartgrocery.ui.BaseUI;
import com.smartgrocery.ui.UIContext;

public class ReportGenerator {
    
     
    public void showMostSoldItems(UIContext context) {
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
    public void showTotalSalesSummary(UIContext context) {
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
    public void showLowStockProducts(UIContext context) {
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
        System.out.println("  🟠 URGENT   - Less than 2 days of stock");
        System.out.println("  🟡 WARNING  - Less than 3 days of stock");
        System.out.println("  ⚪ NO DATA  - No recent sales (low stock anyway)");
        
        // Show detailed analysis option
        System.out.print("\nView detailed sales history for a product? (y/n): ");
        String choice = context.getScanner().nextLine();
        
        if (choice.equalsIgnoreCase("y")) {
            showProductSalesHistory(alerts , context);
        }
    }
    
    public void showProductSalesHistory(List<Analytics.LowStockAlert> alerts , UIContext context) {
        System.out.println("\n--- Select Product for Detailed Analysis ---");
        for (int i = 0; i < alerts.size(); i++) {
            System.out.printf("%d. %s (%.1f days left)\n", 
                i + 1, 
                alerts.get(i).product.getName(),
                alerts.get(i).daysOfStock);
        }
        
        System.out.print("\nEnter product number (0 to cancel): ");
        String input = context.getScanner().nextLine();
        
        try {
            int choice = Integer.parseInt(input);
            if (choice > 0 && choice <= alerts.size()) {
                Product product = alerts.get(choice - 1).product;
                showDetailedProductAnalysis(product , context);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    public void showDetailedProductAnalysis(Product product , UIContext context) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         DETAILED SALES ANALYSIS                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        Analytics.ProductSalesHistory history = context.getAnalytics()
            .getProductSalesHistory(product, context.getAuthManager().getAllUsers(), 7);
        
        System.out.println("\nProduct: " + product.getName());
        System.out.println("Current Stock: " + product.getStock());
        System.out.println("Price: $" + String.format("%.2f", product.getPrice()));
        System.out.println("\n--- Last 7 Days Sales ---");
        
        if (history.dailySales.isEmpty()) {
            System.out.println("No sales recorded in the last 7 days.");
        } else {
            System.out.println("Date           Quantity Sold");
            System.out.println("─────────────────────────────");
            
            // Sort by date
            List<LocalDate> dates = new ArrayList<>(history.dailySales.keySet());
            dates.sort(Comparator.reverseOrder());
            
            for (LocalDate date : dates) {
                int qty = history.dailySales.get(date);
                System.out.printf("%-15s %d\n", date.toString(), qty);
            }
        }
        
        System.out.println("\n--- Summary ---");
        System.out.println("Total Sales (7 days): " + history.totalSales);
        System.out.println("Average Daily Sales: " + String.format("%.2f", history.avgDailySales));
        
        if (history.avgDailySales > 0) {
            double daysOfStock = product.getStock() / history.avgDailySales;
            System.out.println("Days of Stock Remaining: " + String.format("%.1f", daysOfStock));
            
            int recommendedRestock = (int) Math.ceil(history.avgDailySales * 14); // 2 weeks supply
            System.out.println("\n💡 Recommendation:");
            System.out.println("   Restock with at least " + recommendedRestock + " units");
            System.out.println("   (2 weeks supply based on current sales velocity)");
        } else {
            System.out.println("\n💡 Recommendation:");
            System.out.println("   Monitor product - no recent sales activity");
        }
        
        System.out.println("\nPress Enter to continue...");
        context.getScanner().nextLine();
    }
    
    public String getUrgencySymbol(String urgency) {
        switch (urgency) {
            case "CRITICAL": return "🔴";
            case "URGENT": return "🟠";
            case "WARNING": return "🟡";
            case "NO DATA": return "⚪";
            default: return "⚪";
        }
    }
    
    public String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * 4. Show Sales by Category
     */
    public void showSalesByCategory(UIContext context) {
        System.out.println("\n===== Sales by Category =====");
        List<User> allUsers = context.getAuthManager().getAllUsers();
        Map<String, Integer> categorySales = context.getAnalytics().getSalesByCategory(allUsers);
        
        if (categorySales.isEmpty()) {
            System.out.println("No sales data available yet.");
            return;
        }
        
        System.out.println("\nCategory              Quantity Sold");
        System.out.println("-------------------------------------");
        
        // Sort by quantity sold (descending)
        categorySales.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(entry -> {
                Category cat = context.getInventory().getCategory(entry.getKey());
                String catName = (cat != null) ? cat.getName() : entry.getKey();
                System.out.printf("%-20s %d units\n", catName, entry.getValue());
            });
    }

    /**
     * 5. Show Top Revenue Products
     */
    public void showTopRevenueProducts(UIContext context) {
        System.out.println("\n===== Top Revenue Products =====");
        List<User> allUsers = context.getAuthManager().getAllUsers();
        List<Map.Entry<Product, Double>> topRevenue = 
            context.getAnalytics().getTopRevenueProducts(allUsers, 10);
        
        if (topRevenue.isEmpty()) {
            System.out.println("No sales data available yet.");
            return;
        }
        
        System.out.println("\nRank  Product Name              Revenue Generated");
        System.out.println("---------------------------------------------------");
        int rank = 1;
        for (Map.Entry<Product, Double> entry : topRevenue) {
            System.out.printf("%-5d %-25s $%.2f\n", 
                rank++, entry.getKey().getName(), entry.getValue());
        }
    }

    /**
     * 6. Show Least Sold Items
     */
    public void showLeastSoldItems(UIContext context) {
        System.out.println("\n===== Least Sold Items =====");
        List<User> allUsers = context.getAuthManager().getAllUsers();
        List<Map.Entry<Product, Integer>> leastSold = 
            context.getAnalytics().getLeastSoldItems(allUsers, 10);
        
        if (leastSold.isEmpty()) {
            System.out.println("No product data available.");
            return;
        }
        
        System.out.println("\nProduct Name              Category          Quantity Sold");
        System.out.println("------------------------------------------------------------");
        for (Map.Entry<Product, Integer> entry : leastSold) {
            Category cat = context.getInventory().getCategory(entry.getKey().getCategoryId());
            String catName = (cat != null) ? cat.getName() : "Unknown";
            System.out.printf("%-25s %-15s %d units\n", 
                entry.getKey().getName(), catName, entry.getValue());
        }
        
        System.out.println("\n💡 Tip: Consider discounting or removing slow-moving items.");
    }

    /**
     * 7. Show Out of Stock Products
     */
    public void showOutOfStockProducts(UIContext context) {
        System.out.println("\n===== Out of Stock Products =====");
        List<Product> outOfStock = context.getAnalytics().getOutOfStockProducts();
        
        if (outOfStock.isEmpty()) {
            System.out.println("✓ All products are in stock!");
            return;
        }
        
        System.out.println("\nProduct Name              Category          Company");
        System.out.println("-------------------------------------------------------");
        for (Product p : outOfStock) {
            Category cat = context.getInventory().getCategory(p.getCategoryId());
            Company comp = context.getInventory().getCompany(p.getCompanyId());
            String catName = (cat != null) ? cat.getName() : "Unknown";
            String compName = (comp != null) ? comp.getName() : "Unknown";
            System.out.printf("%-25s %-15s %s\n", p.getName(), catName, compName);
        }
        
        System.out.println("\n⚠️  " + outOfStock.size() + " product(s) out of stock!");
    }

    /**
     * 8. Show Daily/Weekly Sales Report
     */
    public void showSalesReport(UIContext context) {
        System.out.println("\n===== Sales Report =====");
        System.out.println("1. Today's Sales");
        System.out.println("2. Weekly Sales (Last 7 Days)");
        System.out.println("3. Custom Date");
        System.out.print("Select option: ");
        
        String choice = context.getScanner().nextLine();
        List<User> allUsers = context.getAuthManager().getAllUsers();
        Analytics.SalesReport report = null;
        
        switch (choice) {
            case "1":
                report = context.getAnalytics().getDailySalesReport(allUsers, 
                    java.time.LocalDate.now());
                break;
            case "2":
                report = context.getAnalytics().getWeeklySalesReport(allUsers);
                break;
            case "3":
                System.out.print("Enter date (YYYY-MM-DD): ");
                String dateStr = context.getScanner().nextLine();
                try {
                    java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
                    report = context.getAnalytics().getDailySalesReport(allUsers, date);
                } catch (Exception e) {
                    System.out.println("Invalid date format. Use YYYY-MM-DD.");
                    return;
                }
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        if (report != null) {
            System.out.println("\n----- Sales Report (" + report.period + ") -----");
            System.out.println("Orders:         " + report.orders);
            System.out.println("Products Sold:  " + report.productsSold + " units");
            System.out.printf("Revenue:        $%.2f\n", report.revenue);
            
            if (report.orders == 0) {
                System.out.println("\nNo sales recorded for this period.");
            }
        }
    }
}