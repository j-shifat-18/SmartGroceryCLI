package com.smartgrocery.engine;

import com.smartgrocery.inventory.Inventory;
import com.smartgrocery.models.Category;
import com.smartgrocery.models.Product;
import com.smartgrocery.models.Purchase;
import com.smartgrocery.models.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class RecommendationEngine {
    private Inventory inventory;
    private Map<String, Integer> globalPurchaseCount; // Track purchase counts globally

    public RecommendationEngine(Inventory inventory) {
        this.inventory = inventory;
        this.globalPurchaseCount = new HashMap<>();
    }

    /**
     * Track a purchase for recommendation analytics
     */
    public void trackPurchase(Purchase purchase) {
        for (Map.Entry<Product, Integer> entry : purchase.getItems().entrySet()) {
            String productId = entry.getKey().getId();
            globalPurchaseCount.put(productId, 
                globalPurchaseCount.getOrDefault(productId, 0) + entry.getValue());
        }
    }

    /**
     * 1. Best Sellers - Most purchased products
     */
    public List<Product> recommendBestSellers(int limit) {
        return globalPurchaseCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> findProductById(entry.getKey()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 2. By Category - Products from a specific category
     */
    public List<Product> recommendByCategory(String categoryId) {
        return inventory.getAllProducts().stream()
            .filter(p -> p.getCategoryId().equals(categoryId))
            .filter(p -> p.getStock() > 0)
            .sorted(Comparator.comparingDouble(Product::getPrice))
            .collect(Collectors.toList());
    }

    /**
     * 3. Value for Money - Best price per unit ratio
     */
    public List<Product> recommendValueForMoney(int limit) {
        return inventory.getAllProducts().stream()
            .filter(p -> p.getStock() > 0)
            .sorted(Comparator.comparingDouble(Product::getPrice))
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 5. Based on Previous Purchases - Personalized recommendations
     */
    public List<Product> recommendByHistory(User user) {
        if (user.getPurchaseHistory().isEmpty()) {
            return new ArrayList<>();
        }

        // Find most frequent categories from history
        Map<String, Integer> categoryCount = new HashMap<>();
        Set<String> purchasedProductIds = new HashSet<>();
        
        for (Purchase p : user.getPurchaseHistory()) {
            for (Product item : p.getItems().keySet()) {
                categoryCount.put(item.getCategoryId(), 
                    categoryCount.getOrDefault(item.getCategoryId(), 0) + 1);
                purchasedProductIds.add(item.getId());
            }
        }

        // Get top 2 categories
        List<String> topCategories = categoryCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(2)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        // Recommend products from top categories that user hasn't bought
        return inventory.getAllProducts().stream()
            .filter(p -> topCategories.contains(p.getCategoryId()))
            .filter(p -> !purchasedProductIds.contains(p.getId()))
            .filter(p -> p.getStock() > 0)
            .limit(5)
            .collect(Collectors.toList());
    }

    /**
     * 6. Recently Popular - Products purchased in last 7 days
     */
    public List<Product> recommendRecentlyPopular(List<User> allUsers, int limit) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        Map<String, Integer> recentPurchases = new HashMap<>();

        for (User user : allUsers) {
            for (Purchase purchase : user.getPurchaseHistory()) {
                if (purchase.getTimestamp().isAfter(weekAgo)) {
                    for (Product product : purchase.getItems().keySet()) {
                        recentPurchases.put(product.getId(), 
                            recentPurchases.getOrDefault(product.getId(), 0) + 1);
                    }
                }
            }
        }

        return recentPurchases.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> findProductById(entry.getKey()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 7. Low Stock / Hurry Deals - Products with limited stock
     */
    public List<Product> recommendLowStock(int stockThreshold) {
        return inventory.getAllProducts().stream()
            .filter(p -> p.getStock() > 0 && p.getStock() <= stockThreshold)
            .sorted(Comparator.comparingInt(Product::getStock))
            .collect(Collectors.toList());
    }

    /**
     * 8. Budget Friendly - Cheapest products under a price limit
     */
    public List<Product> recommendBudgetFriendly(double priceLimit) {
        return inventory.getAllProducts().stream()
            .filter(p -> p.getPrice() <= priceLimit)
            .filter(p -> p.getStock() > 0)
            .sorted(Comparator.comparingDouble(Product::getPrice))
            .collect(Collectors.toList());
    }

    /**
     * Helper method to find product by ID
     */
    private Product findProductById(String productId) {
        return inventory.getAllProducts().stream()
            .filter(p -> p.getId().equals(productId))
            .findFirst()
            .orElse(null);
    }
}
