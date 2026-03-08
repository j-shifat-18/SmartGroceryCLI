package com.smartgrocery.ui;

import com.smartgrocery.models.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class CustomerUI extends BaseUI {

    public CustomerUI(UIContext context) {
        super(context);
    }

    
    public void showMenu() {
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Browse Products");
            System.out.println("2. Search Products");
            System.out.println("3. View Cart");
            System.out.println("4. View Recommendations");
            System.out.println("5. View History");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");
            
            String choice = context.getScanner().nextLine();
            switch (choice) {
                case "1": browseProducts(); break;
                case "2": searchProducts(); break;
                case "3": viewCart(); break;
                case "4": viewRecommendations(); break;
                case "5": viewHistory(); break;
                case "6": 
                    context.getAuthUI().logout();
                    context.getCart().clearCart();
                    return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    
    private void browseProducts() {
        List<Category> cats = context.getInventory().getCategories();
        if (cats.isEmpty()) {
            System.out.println("No products available (No categories).");
            return;
        }
        
        System.out.println("\n--- Select Category ---");
        int index = 1;
        for (Category c : cats) {
            System.out.println(index++ + ". " + c.getName());
        }
        System.out.println("0. Back");
        
        int catChoice = getIntInput("Enter choice: ");
        if (catChoice == 0 || catChoice > cats.size()) return;
        
        Category selectedCat = cats.get(catChoice - 1);
        List<Product> productsInCat = context.getInventory().getProductsByCategory(selectedCat.getId());
        
        if (productsInCat.isEmpty()) {
            System.out.println("No products in " + selectedCat.getName());
            return;
        }
        
        // Show Unique Product Names
        Set<String> productNames = productsInCat.stream()
            .map(p -> p.getName().toUpperCase())
            .collect(Collectors.toSet());

        List<String> uniqueNames = productNames.stream()
            .sorted()
            .collect(Collectors.toList());

        System.out.println("\n--- Products in " + selectedCat.getName() + " ---");
        index = 1;
        for (String name : uniqueNames) {
            System.out.println(index++ + ". " + name);
        }
        
        int prodChoice = getIntInput("Enter choice: ");
        if (prodChoice < 1 || prodChoice > uniqueNames.size()) return;
        
        String selectedName = uniqueNames.get(prodChoice - 1);
        selectProductVariant(selectedName, selectedCat);
    }

    /**
     * Select product variant and add to cart
     */
    private void selectProductVariant(String productName, Category category) {
        List<Product> variants = context.getInventory().getProductVariants(productName);
        
        System.out.println("\n--- Select " + productName + " Variant ---");
        System.out.printf("%-5s %-15s %-10s %-10s %-10s\n", "#", "Company", "Price", "Stock", "Unit");
        
        int index = 1;
        for (Product p : variants) {
            Company comp = context.getInventory().getCompany(p.getCompanyId());
            String compName = (comp != null) ? comp.getName() : "Unknown";
            System.out.printf("%-5d %-15s $%-9.2f %-10d %s\n", 
                index++, compName, p.getPrice(), p.getStock(), category.getUnitType());
        }
        
        int varChoice = getIntInput("Enter choice: ");
        if (varChoice < 1 || varChoice > variants.size()) return;
        
        Product selectedVariant = variants.get(varChoice - 1);
        int qty = getIntInput("Enter quantity (" + category.getUnitType() + "): ");
        
        if (qty > 0) {
            context.getCart().addItem(selectedVariant, qty);
            System.out.println("Added to cart.");
        }
    }

    /**
     * Search products by name
     */
    private void searchProducts() {
        String searchTerm = getStringInput("Enter product name to search: ");
        if (searchTerm.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }
        
        List<Product> searchResults = context.getInventory().getAllProducts().stream()
            .filter(p -> p.getName().toLowerCase().contains(searchTerm.toLowerCase()))
            .collect(Collectors.toList());
        
        if (searchResults.isEmpty()) {
            System.out.println("No products found matching: " + searchTerm);
            return;
        }
        
        System.out.println("\n--- Search Results for: " + searchTerm + " ---");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-10s\n", "#", "Name", "Category", "Company", "Price", "Stock");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (int i = 0; i < searchResults.size(); i++) {
            Product p = searchResults.get(i);
            Category cat = context.getInventory().getCategory(p.getCategoryId());
            Company comp = context.getInventory().getCompany(p.getCompanyId());
            String catName = (cat != null) ? cat.getName() : "Unknown";
            String compName = (comp != null) ? comp.getName() : "Unknown";
            
            System.out.printf("%-5d %-20s %-15s %-15s $%-9.2f %-10d\n", 
                (i + 1), p.getName(), catName, compName, p.getPrice(), p.getStock());
        }
        
        int choice = getIntInput("\nEnter product number to add to cart (0 to cancel): ");
        if (choice > 0 && choice <= searchResults.size()) {
            Product selectedProduct = searchResults.get(choice - 1);
            Category cat = context.getInventory().getCategory(selectedProduct.getCategoryId());
            String unitType = (cat != null) ? cat.getUnitType().toString() : "PCS";
            
            int qty = getIntInput("Enter quantity (" + unitType + "): ");
            if (qty > 0) {
                context.getCart().addItem(selectedProduct, qty);
                System.out.println("Added " + qty + " " + unitType + " of " + selectedProduct.getName() + " to cart.");
            } else {
                System.out.println("Invalid quantity.");
            }
        }
    }

    //? View and manage cart
    private void viewCart() {
        System.out.println("\n--- Your Cart ---");
        if (context.getCart().isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        
        System.out.printf("%-20s %-15s %-10s %-10s %-10s\n", "Product", "Company", "Price", "Qty", "Subtotal");
        System.out.println("----------------------------------------------------------------");
        
        context.getCart().getItems().forEach((p, qty) -> {
            Company c = context.getInventory().getCompany(p.getCompanyId());
            System.out.printf("%-20s %-15s $%-9.2f %-10d $%-9.2f\n", 
                p.getName(), 
                (c != null ? c.getName() : "?"), 
                p.getPrice(), qty, p.getPrice() * qty);
        });
            
        System.out.println("----------------------------------------------------------------");
        System.out.printf("Total: $%.2f\n", context.getCart().calculateTotal());
        
        System.out.println("1. Checkout");
        System.out.println("2. Clear Cart");
        System.out.println("3. Back");
        
        String choice = context.getScanner().nextLine();
        if (choice.equals("1")) {
            checkout();
        } else if (choice.equals("2")) {
            context.getCart().clearCart();
            System.out.println("Cart cleared.");
        }
    }

    /**
     * Process checkout and print receipt
     */
    private void checkout() {
        Purchase purchase = context.getCheckout().processCheckout(context.getCart(), context.getCurrentUser());
        if (purchase != null) {
            printReceipt(purchase);
        } else {
            System.out.println("Checkout Failed (Stock issues or empty cart).");
        }
    }



     /**
     * View product recommendations with multiple categories
     */
    private void viewRecommendations() {
        while (true) {
            System.out.println("\n==== Recommendation Menu ====");
            System.out.println("1. Best Sellers");
            System.out.println("2. By Product Category");
            System.out.println("3. Value for Money");
            System.out.println("4. Based on Previous Purchases");
            System.out.println("5. Recently Popular");
            System.out.println("6. Low Stock / Hurry Deals");
            System.out.println("7. Budget Friendly Items");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select an option: ");
            
            String choice = context.getScanner().nextLine();
            
            switch (choice) {
                case "1": showBestSellers(); break;
                case "2": showByCategory(); break;
                case "3": showValueForMoney(); break;
                case "4": showBasedOnPurchases(); break;
                case "5": showRecentlyPopular(); break;
                case "6": showLowStock(); break;
                case "7": showBudgetFriendly(); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }


    /**
     * 1. Show Best Sellers
     */
    private void showBestSellers() {
        System.out.println("\n--- Best Sellers ---");
        List<Product> recommendations = context.getRecEngine().recommendBestSellers(10);
        
        if (recommendations.isEmpty()) {
            System.out.println("No purchase data available yet.");
            return;
        }
        
        System.out.println("Top selling products:");
        printProductTableWithSelection(recommendations);
    }

    /**
     * 2. Show recommendations by category
     */
    private void showByCategory() {
        System.out.println("\n--- Select Category ---");
        List<Category> categories = context.getInventory().getCategories();
        
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            return;
        }
        
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getName());
        }
        System.out.println("0. Back");
        
        int choice = getIntInput("Select category: ");
        if (choice < 1 || choice > categories.size()) return;
        
        Category selectedCategory = categories.get(choice - 1);
        List<Product> recommendations = context.getRecEngine().recommendByCategory(selectedCategory.getId());
        
        System.out.println("\n--- " + selectedCategory.getName() + " Recommendations ---");
        if (recommendations.isEmpty()) {
            System.out.println("No products available in this category.");
        } else {
            printProductTableWithSelection(recommendations);
        }
    }

    /**
     * 3. Show Value for Money products
     */
    private void showValueForMoney() {
        System.out.println("\n--- Best Value Products ---");
        List<Product> recommendations = context.getRecEngine().recommendValueForMoney(10);
        
        if (recommendations.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        
        System.out.println("Products with best prices:");
        printProductTableWithSelection(recommendations);
    }

    /**
     * 5. Show recommendations based on purchase history
     */
    private void showBasedOnPurchases() {
        System.out.println("\n--- Based on Your Purchases ---");
        
        if (context.getCurrentUser().getPurchaseHistory().isEmpty()) {
            System.out.println("No purchase history available. Make a purchase first!");
            return;
        }
        
        List<Product> recommendations = context.getRecEngine()
            .recommendByHistory(context.getCurrentUser());
        
        if (recommendations.isEmpty()) {
            System.out.println("No new recommendations available.");
            return;
        }
        
        System.out.println("You may also like:");
        printProductTableWithSelection(recommendations);
    }

    /**
     * 6. Show Recently Popular products
     */
    private void showRecentlyPopular() {
        System.out.println("\n--- Popular This Week ---");
        List<Product> recommendations = context.getRecEngine()
            .recommendRecentlyPopular(context.getAuthManager().getAllUsers(), 10);
        
        if (recommendations.isEmpty()) {
            System.out.println("No recent purchase data available.");
            return;
        }
        
        System.out.println("Trending products:");
        printProductTableWithSelection(recommendations);
    }

    /**
     * 7. Show Low Stock / Hurry Deals
     */
    private void showLowStock() {
        System.out.println("\n--- Limited Stock Deals ---");
        List<Product> recommendations = context.getRecEngine().recommendLowStock(10);
        
        if (recommendations.isEmpty()) {
            System.out.println("No limited stock items at the moment.");
            return;
        }
        
        System.out.println("Hurry! Limited quantities available:");
        printProductTableWithSelection(recommendations);
    }

    /**
     * 8. Show Budget Friendly items
     */
    private void showBudgetFriendly() {
        System.out.print("\nEnter your budget limit (e.g., 100): $");
        String input = context.getScanner().nextLine();
        
        double budget;
        try {
            budget = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            budget = 100.0; // Default
        }
        
        System.out.println("\n--- Budget Friendly Items (Under $" + budget + ") ---");
        List<Product> recommendations = context.getRecEngine().recommendBudgetFriendly(budget);
        
        if (recommendations.isEmpty()) {
            System.out.println("No products found within your budget.");
            return;
        }
        
        printProductTableWithSelection(recommendations);
    }


    /**
     * Print product table with option to add to cart
     */
    private void printProductTableWithSelection(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products to display.");
            return;
        }
        
        System.out.printf("\n%-5s %-20s %-15s %-15s %-10s %-10s\n", 
            "#", "Name", "Category", "Company", "Price", "Stock");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            Category cat = context.getInventory().getCategory(p.getCategoryId());
            Company comp = context.getInventory().getCompany(p.getCompanyId());
            String catName = (cat != null) ? cat.getName() : "Unknown";
            String compName = (comp != null) ? comp.getName() : "Unknown";
            
            System.out.printf("%-5d %-20s %-15s %-15s $%-9.2f %-10d\n", 
                (i + 1), p.getName(), catName, compName, p.getPrice(), p.getStock());
        }
        
        System.out.println("\nEnter product number to add to cart (0 to go back): ");
        int choice = getIntInput("Choice: ");
        
        if (choice > 0 && choice <= products.size()) {
            Product selectedProduct = products.get(choice - 1);
            Category cat = context.getInventory().getCategory(selectedProduct.getCategoryId());
            String unitType = (cat != null) ? cat.getUnitType().toString() : "PCS";
            
            int qty = getIntInput("Enter quantity (" + unitType + "): ");
            if (qty > 0) {
                context.getCart().addItem(selectedProduct, qty);
                System.out.println("✓ Added " + qty + " " + unitType + " of " + 
                    selectedProduct.getName() + " to cart.");
            } else {
                System.out.println("Invalid quantity.");
            }
        }
    }

    /**
     * Print formatted receipt
     */
    private void printReceipt(Purchase p) {
        System.out.println("\n=========================================");
        System.out.println("           OFFICIAL RECEIPT              ");
        System.out.println("=========================================");
        System.out.println("Date: " + p.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("-----------------------------------------");
        System.out.printf("%-20s %-5s %-10s %10s\n", "Item", "Qty", "Unit Price", "Subtotal");
        
        for (Map.Entry<Product, Integer> entry : p.getItems().entrySet()) {
             double unitPrice = entry.getKey().getPrice();
             int quantity = entry.getValue();
             double subtotal = unitPrice * quantity;
             System.out.printf("%-20s %-5d $%-9.2f $%-8.2f\n", 
                 entry.getKey().getName(), quantity, unitPrice, subtotal);
        }
        
        System.out.println("-----------------------------------------");
        System.out.printf("TOTAL PAID: %28.2f\n", p.getTotalAmount());
        System.out.println("=========================================\n");
    }

    /**
     * View purchase history
     */
    private void viewHistory() {
        System.out.println("\n--- Purchase History ---");
        List<Purchase> history = context.getCurrentUser().getPurchaseHistory();
        if (history.isEmpty()) {
            System.out.println("No purchase history found.");
            return;
        }

        System.out.printf("%-20s %-30s %-10s\n", "Date", "Items", "Total");
        System.out.println("-----------------------------------------------------------------");
        for (Purchase p : history) {
            StringBuilder itemsStr = new StringBuilder();
            p.getItems().forEach((prod, qty) -> itemsStr.append(prod.getName()).append("x").append(qty).append(" "));
            
            System.out.printf("%-20s %-30s $%-9.2f\n", 
                p.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                itemsStr.length() > 30 ? itemsStr.substring(0, 27) + "..." : itemsStr.toString(),
                p.getTotalAmount());
        }
    }

   
}