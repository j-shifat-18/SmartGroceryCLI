package com.smartgrocery.engine;

import com.smartgrocery.inventory.Inventory;
import com.smartgrocery.models.Category;
import com.smartgrocery.models.Product;
import com.smartgrocery.models.Purchase;
import com.smartgrocery.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Analytics {
    private Inventory inventory;

    public Analytics() {
        this.inventory = null;
    }

    public Analytics(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    // Most Sold Items
    public List<Map.Entry<Product, Integer>> getMostSoldItems(List<User> allUsers, int limit) {
        Map<Product, Integer> productCount = new HashMap<>();
        
        for (User u : allUsers) {
            for (Purchase p : u.getPurchaseHistory()) {
                for (Map.Entry<Product, Integer> entry : p.getItems().entrySet()) {
                    productCount.put(entry.getKey(), 
                        productCount.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
            }
        }
        
        return productCount.entrySet().stream()
            .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    // Total Sales Summary
    public SalesSummary getTotalSalesSummary(List<User> allUsers) {
        int totalOrders = 0;
        int totalProductsSold = 0;
        double totalRevenue = 0.0;
        
        for (User u : allUsers) {
            totalOrders += u.getPurchaseHistory().size();
            for (Purchase p : u.getPurchaseHistory()) {
                totalRevenue += p.getTotalAmount();
                for (Integer qty : p.getItems().values()) {
                    totalProductsSold += qty;
                }
            }
        }
        
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;
        return new SalesSummary(totalOrders, totalProductsSold, totalRevenue, averageOrderValue);
    }

    // Low Stock Products (Legacy)
    public List<Product> getLowStockProducts(int threshold) {
        if (inventory == null) return new ArrayList<>();

        return inventory.getAllProducts().stream()
            .filter(p -> p.getStock() > 0 && p.getStock() < threshold)
            .sorted(Comparator.comparingInt(Product::getStock))
            .collect(Collectors.toList());
    }

    /**
     * Smart Low Stock Detection based on Sales Velocity
     * Algorithm:
     * 1. Get last 7 days sales for each product
     * 2. Compute average daily sales
     * 3. Calculate days_of_stock = current_stock / avg_daily_sales
     * 4. If days_of_stock < threshold_days, mark as low stock
     */
    public List<LowStockAlert> getSmartLowStockProducts(List<User> allUsers, int thresholdDays) {
        if (inventory == null) return new ArrayList<>();

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<LowStockAlert> alerts = new ArrayList<>();
        Map<Product, Integer> productSales = new HashMap<>();

        for (User u : allUsers) {
            for (Purchase p : u.getPurchaseHistory()) {
                if (p.getTimestamp().isAfter(sevenDaysAgo)) {
                    for (Map.Entry<Product, Integer> entry : p.getItems().entrySet()) {
                        Product product = entry.getKey();
                        productSales.put(product, 
                            productSales.getOrDefault(product, 0) + entry.getValue());
                    }
                }
            }
        }

        for (Product product : inventory.getAllProducts()) {
            int currentStock = product.getStock();
            if (currentStock == 0) continue;

            int salesLast7Days = productSales.getOrDefault(product, 0);
            double avgDailySales = salesLast7Days / 7.0;

            // If product has sales history, calculate days of stock remaining
            if (avgDailySales > 0) {
                double daysOfStock = currentStock / avgDailySales;
                if (daysOfStock < thresholdDays) {
                    alerts.add(new LowStockAlert(
                        product, 
                        currentStock, 
                        salesLast7Days, 
                        avgDailySales, 
                        daysOfStock
                    ));
                }
            } else {
                if (currentStock < 10) {
                    alerts.add(new LowStockAlert(
                        product, 
                        currentStock, 
                        0, 
                        0.0, 
                        -1 // Indicates no sales data
                    ));
                }
            }
        }

        alerts.sort((a, b) -> {
            if (a.daysOfStock < 0) return 1;
            if (b.daysOfStock < 0) return -1;
            return Double.compare(a.daysOfStock, b.daysOfStock);
        });

        return alerts;
    }

    // Product Sales History - detailed sales data
    public ProductSalesHistory getProductSalesHistory(Product product, List<User> allUsers, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        Map<LocalDate, Integer> dailySales = new HashMap<>();
        int totalSales = 0;

        for (User u : allUsers) {
            for (Purchase p : u.getPurchaseHistory()) {
                if (p.getTimestamp().isAfter(startDate)) {
                    for (Map.Entry<Product, Integer> entry : p.getItems().entrySet()) {
                        if (entry.getKey().getId().equals(product.getId())) {
                            LocalDate date = p.getTimestamp().toLocalDate();
                            int qty = entry.getValue();
                            dailySales.put(date, dailySales.getOrDefault(date, 0) + qty);
                            totalSales += qty;
                        }
                    }
                }
            }
        }

        double avgDailySales = totalSales / (double) days;
        return new ProductSalesHistory(product, dailySales, totalSales, avgDailySales, days);
    }

    // Sales by Category
    public Map<String, Integer> getSalesByCategory(List<User> allUsers) {
        Map<String, Integer> categorySales = new HashMap<>();
        
        for (User u : allUsers) {
            for (Purchase p : u.getPurchaseHistory()) {
                for (Map.Entry<Product, Integer> entry : p.getItems().entrySet()) {
                    String categoryId = entry.getKey().getCategoryId();
                    categorySales.put(categoryId, 
                        categorySales.getOrDefault(categoryId, 0) + entry.getValue());
                }
            }
        }
        
        return categorySales;
    }

    // Top Revenue Products
    public List<Map.Entry<Product, Double>> getTopRevenueProducts(List<User> allUsers, int limit) {
        Map<Product, Double> productRevenue = new HashMap<>();
        
        for (User u : allUsers) {
            for (Purchase p : u.getPurchaseHistory()) {
                for (Map.Entry<Product, Integer> entry : p.getItems().entrySet()) {
                    Product product = entry.getKey();
                    double revenue = product.getPrice() * entry.getValue();
                    productRevenue.put(product, 
                        productRevenue.getOrDefault(product, 0.0) + revenue);
                }
            }
        }
        
        return productRevenue.entrySet().stream()
            .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    // Least Sold Items
    public List<Map.Entry<Product, Integer>> getLeastSoldItems(List<User> allUsers, int limit) {
        Map<Product, Integer> productCount = new HashMap<>();
        
        if (inventory != null) {
            for (Product p : inventory.getAllProducts()) {
                productCount.put(p, 0);
            }
        }
        for (User u : allUsers) {
            for (Purchase p : u.getPurchaseHistory()) {
                for (Map.Entry<Product, Integer> entry : p.getItems().entrySet()) {
                    productCount.put(entry.getKey(), 
                        productCount.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
            }
        }
        
        return productCount.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .limit(limit)
            .collect(Collectors.toList());
    }

    // Out of Stock Products
    public List<Product> getOutOfStockProducts() {
        if (inventory == null) return new ArrayList<>();
        
        return inventory.getAllProducts().stream()
            .filter(p -> p.getStock() == 0)
            .sorted(Comparator.comparing(Product::getName))
            .collect(Collectors.toList());
    }

    // Daily Sales Report
    public SalesReport getDailySalesReport(List<User> allUsers, LocalDate date) {
        int orders = 0;
        int productsSold = 0;
        double revenue = 0.0;
        
        for (User u : allUsers) {
            for (Purchase p : u.getPurchaseHistory()) {
                if (p.getTimestamp().toLocalDate().equals(date)) {
                    orders++;
                    revenue += p.getTotalAmount();
                    for (Integer qty : p.getItems().values()) {
                        productsSold += qty;
                    }
                }
            }
        }
        
        return new SalesReport(orders, productsSold, revenue, date.toString());
    }

    // Weekly Sales Report
    public SalesReport getWeeklySalesReport(List<User> allUsers) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        int orders = 0;
        int productsSold = 0;
        double revenue = 0.0;
        
        for (User u : allUsers) {
            for (Purchase p : u.getPurchaseHistory()) {
                if (p.getTimestamp().isAfter(weekAgo)) {
                    orders++;
                    revenue += p.getTotalAmount();
                    for (Integer qty : p.getItems().values()) {
                        productsSold += qty;
                    }
                }
            }
        }
        
        return new SalesReport(orders, productsSold, revenue, "Last 7 Days");
    }

    // Legacy methods
    public Map<Product, Integer> getMostBoughtItems(List<User> allUsers) {
        Map<Product, Integer> productCount = new HashMap<>();
        
        for (User u : allUsers) {
            for (Purchase p : u.getPurchaseHistory()) {
                for (Map.Entry<Product, Integer> entry : p.getItems().entrySet()) {
                    productCount.put(entry.getKey(), 
                        productCount.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
            }
        }
        return productCount;
    }

    public Map<String, Double> getCategoryWiseRevenue(List<User> allUsers) {
        Map<String, Double> categoryRevenue = new HashMap<>();
        
        for (User u : allUsers) {
            for (Purchase p : u.getPurchaseHistory()) {
                for (Map.Entry<Product, Integer> entry : p.getItems().entrySet()) {
                    Product prod = entry.getKey();
                    double revenue = prod.getPrice() * entry.getValue();
                    categoryRevenue.put(prod.getCategoryId(), 
                        categoryRevenue.getOrDefault(prod.getCategoryId(), 0.0) + revenue);
                }
            }
        }
        return categoryRevenue;
    }

    public double getUserTotalSpending(User user) {
        double total = 0;
        for (Purchase p : user.getPurchaseHistory()) {
            total += p.getTotalAmount();
        }
        return total;
    }

    public static class SalesSummary {
        public final int totalOrders;
        public final int totalProductsSold;
        public final double totalRevenue;
        public final double averageOrderValue;

        public SalesSummary(int totalOrders, int totalProductsSold, 
                          double totalRevenue, double averageOrderValue) {
            this.totalOrders = totalOrders;
            this.totalProductsSold = totalProductsSold;
            this.totalRevenue = totalRevenue;
            this.averageOrderValue = averageOrderValue;
        }
    }

    public static class SalesReport {
        public final int orders;
        public final int productsSold;
        public final double revenue;
        public final String period;

        public SalesReport(int orders, int productsSold, double revenue, String period) {
            this.orders = orders;
            this.productsSold = productsSold;
            this.revenue = revenue;
            this.period = period;
        }
    }
    
    public static class LowStockAlert {
        public final Product product;
        public final int currentStock;
        public final int salesLast7Days;
        public final double avgDailySales;
        public final double daysOfStock;
        
        public LowStockAlert(Product product, int currentStock, int salesLast7Days, 
                           double avgDailySales, double daysOfStock) {
            this.product = product;
            this.currentStock = currentStock;
            this.salesLast7Days = salesLast7Days;
            this.avgDailySales = avgDailySales;
            this.daysOfStock = daysOfStock;
        }
        
        public String getUrgencyLevel() {
            if (daysOfStock < 0) return "NO DATA";
            if (daysOfStock < 1) return "CRITICAL";
            if (daysOfStock < 2) return "URGENT";
            if (daysOfStock < 3) return "WARNING";
            return "LOW";
        }
    }
    
    public static class ProductSalesHistory {
        public final Product product;
        public final Map<LocalDate, Integer> dailySales;
        public final int totalSales;
        public final double avgDailySales;
        public final int days;
        
        public ProductSalesHistory(Product product, Map<LocalDate, Integer> dailySales,
                                 int totalSales, double avgDailySales, int days) {
            this.product = product;
            this.dailySales = dailySales;
            this.totalSales = totalSales;
            this.avgDailySales = avgDailySales;
            this.days = days;
        }
    }
}