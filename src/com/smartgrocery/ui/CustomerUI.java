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
                case "4": System.out.println("Feature comming soon!");; break;
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