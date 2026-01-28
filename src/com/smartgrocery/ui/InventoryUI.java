package com.smartgrocery.ui;

import com.smartgrocery.models.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class InventoryUI extends BaseUI {

    public InventoryUI(UIContext context) {
        super(context);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n--- Inventory Management ---");
            System.out.println("1. View All Products");
            System.out.println("2. Search Product");
            System.out.println("3. Add Product");
            System.out.println("4. Update Product");
            System.out.println("5. Manage Categories");
            System.out.println("6. Manage Companies");
            System.out.println("7. Remove Product");
            System.out.println("8. Back to Admin Menu");
            
            String choice = context.getScanner().nextLine();
            switch (choice) {
                case "1": viewProductsHierarchical(); break;
                case "2": searchProduct(); break;
                case "3": addProductFlow(); break;
                case "4": updateProductFlow(); break;
                case "5": manageCategories(); break;
                case "6": manageCompanies(); break;
                case "7": removeProduct(); break;
                case "8": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

  
    private void viewProductsHierarchical() {
        System.out.println("\n--- Products by Category ---");
        List<Category> categories = context.getInventory().getCategories();
        
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }
        
        for (Category category : categories) {
            System.out.println("\n📁 " + category.getName() + " (" + category.getUnitType() + ")");
            List<Product> productsInCategory = context.getInventory().getProductsByCategory(category.getId());
            
            if (productsInCategory.isEmpty()) {
                System.out.println("   └── No products in this category");
                continue;
            }
            
            // Group products by name (unique products)
            Map<String, List<Product>> productsByName = productsInCategory.stream()
                .collect(Collectors.groupingBy(p -> p.getName().toUpperCase()));
            
            for (Map.Entry<String, List<Product>> entry : productsByName.entrySet()) {
                String productName = entry.getKey();
                List<Product> variants = entry.getValue();
                
                System.out.println("   └── 📦 " + productName);
                
                // Show different company variants
                for (Product variant : variants) {
                    Company company = context.getInventory().getCompany(variant.getCompanyId());
                    String companyName = (company != null) ? company.getName() : "Unknown";
                    System.out.printf("       └── 🏢 %-15s | $%-8.2f | Stock: %-5d | ID: %s\n", 
                        companyName, variant.getPrice(), variant.getStock(), variant.getId());
                }
            }
        }
    }

   
    private void addProductFlow() {
        String name = getStringInput("Product Name: ");
        if (name.isEmpty()) return;
        
        Category selectedCat = selectCategory();
        if (selectedCat == null) return;
        
        String companyId = selectOrCreateCompany();
        if (companyId == null) return;
        
        double price = getDoubleInput("Price: ");
        if (price < 0) return;
        
        int stock = getIntInput("Stock: ");
        if (stock < 0) return;
        
        context.getInventory().addProduct(new Product(name, selectedCat.getId(), companyId, price, stock));
        System.out.println("Product Added.");
    }

  
    private Category selectCategory() {
        List<Category> cats = context.getInventory().getCategories();
        if (cats.isEmpty()) {
            System.out.println("No categories found. Create a category first.");
            return null;
        }
        
        System.out.println("Select Category:");
        for (int i = 0; i < cats.size(); i++) {
            System.out.println((i + 1) + ". " + cats.get(i).getName() + " (" + cats.get(i).getUnitType() + ")");
        }
        
        int catIdx = getIntInput("Enter choice: ") - 1;
        if (catIdx < 0 || catIdx >= cats.size()) {
            System.out.println("Invalid category.");
            return null;
        }
        
        return cats.get(catIdx);
    }


    private void updateProductFlow() {
        System.out.println("\n--- Update Product ---");
        
        // First, let user search for the product to update
        String searchTerm = getStringInput("Enter product name or ID to search: ");
        if (searchTerm.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }
        
        // Search for products by name or ID
        List<Product> searchResults = context.getInventory().getAllProducts().stream()
            .filter(p -> p.getName().toLowerCase().contains(searchTerm.toLowerCase()) || 
                        p.getId().equalsIgnoreCase(searchTerm))
            .collect(Collectors.toList());
        
        if (searchResults.isEmpty()) {
            System.out.println("No products found matching: " + searchTerm);
            return;
        }
        
        // Display search results
        System.out.println("\n--- Select Product to Update ---");
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
        
        // Select product to update
        int choice = getIntInput("\nEnter product number to update (0 to cancel): ");
        if (choice <= 0 || choice > searchResults.size()) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        Product selectedProduct = searchResults.get(choice - 1);
        
        // Show update options
        while (true) {
            System.out.println("\n--- Update Options for: " + selectedProduct.getName() + " ---");
            System.out.println("Current Price: $" + selectedProduct.getPrice());
            System.out.println("Current Stock: " + selectedProduct.getStock());
            System.out.println();
            System.out.println("1. Update Price");
            System.out.println("2. Update Stock");
            System.out.println("3. Update Both Price and Stock");
            System.out.println("4. Back to Inventory Menu");
            
            String updateChoice = context.getScanner().nextLine();
            switch (updateChoice) {
                case "1":
                    updateProductPrice(selectedProduct);
                    break;
                case "2":
                    updateProductStock(selectedProduct);
                    break;
                case "3":
                    updateProductPrice(selectedProduct);
                    updateProductStock(selectedProduct);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }


    private void updateProductPrice(Product product) {
        System.out.println("Current Price: $" + product.getPrice());
        double newPrice = getDoubleInput("Enter new price: $");
        
        if (newPrice < 0) {
            System.out.println("Invalid price.");
            return;
        }
        
        if (context.getInventory().updateProductPriceById(product.getId(), newPrice)) {
            System.out.println("Price updated successfully from $" + product.getPrice() + " to $" + newPrice);
        } else {
            System.out.println("Failed to update price.");
        }
    }

    private void updateProductStock(Product product) {
        System.out.println("Current Stock: " + product.getStock());
        System.out.println("1. Set new stock amount");
        System.out.println("2. Add to existing stock");
        System.out.println("3. Remove from existing stock");
        
        String stockChoice = context.getScanner().nextLine();
        int newStock = 0;
        
        switch (stockChoice) {
            case "1":
                newStock = getIntInput("Enter new stock amount: ");
                if (newStock < 0) {
                    System.out.println("Invalid stock amount.");
                    return;
                }
                // Set new stock (replace current stock)
                int currentStock = product.getStock();
                int stockDifference = newStock - currentStock;
                if (context.getInventory().updateProductStockById(product.getId(), stockDifference)) {
                    System.out.println("Stock updated successfully from " + currentStock + " to " + newStock);
                } else {
                    System.out.println("Failed to update stock.");
                }
                break;
                
            case "2":
                int addAmount = getIntInput("Enter amount to add: ");
                if (addAmount <= 0) {
                    System.out.println("Invalid amount.");
                    return;
                }
                if (context.getInventory().updateProductStockById(product.getId(), addAmount)) {
                    System.out.println("Added " + addAmount + " units. New stock: " + (product.getStock() + addAmount));
                } else {
                    System.out.println("Failed to update stock.");
                }
                break;
                
            case "3":
                int removeAmount = getIntInput("Enter amount to remove: ");
                if (removeAmount <= 0) {
                    System.out.println("Invalid amount.");
                    return;
                }
                if (product.getStock() < removeAmount) {
                    System.out.println("Cannot remove " + removeAmount + " units. Current stock: " + product.getStock());
                    return;
                }
                if (context.getInventory().updateProductStockById(product.getId(), -removeAmount)) {
                    System.out.println("Removed " + removeAmount + " units. New stock: " + (product.getStock() - removeAmount));
                } else {
                    System.out.println("Failed to update stock.");
                }
                break;
                
            default:
                System.out.println("Invalid choice.");
        }
    }


    private String selectOrCreateCompany() {
        List<Company> comps = context.getInventory().getCompanies();
        String companyId = null;
        
        if (!comps.isEmpty()) {
            System.out.println("Select Company (or 0 for New Company):");
            for (int i = 0; i < comps.size(); i++) {
                System.out.println((i + 1) + ". " + comps.get(i).getName());
            }
            
            int compIdx = getIntInput("Enter choice: ");
            if (compIdx > 0 && compIdx <= comps.size()) {
                companyId = comps.get(compIdx - 1).getId();
            }
        }
        
        if (companyId == null) {
            String compName = getStringInput("Enter New Company Name: ");
            if (compName.isEmpty()) {
                System.out.println("Invalid company name.");
                return null;
            }
            Company newComp = new Company(compName);
            context.getInventory().addCompany(newComp);
            companyId = newComp.getId();
        }
        
        return companyId;
    }

  
    private void removeProduct() {
        String delId = getStringInput("Enter Product ID to remove: ");
        if (context.getInventory().deleteProductById(delId)) {
            System.out.println("Product removed successfully.");
        } else {
            System.out.println("Product not found.");
        }
    }

    private void searchProduct() {
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
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-10s\n", "ID", "Name", "Category", "Company", "Price", "Stock");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (Product p : searchResults) {
            Category cat = context.getInventory().getCategory(p.getCategoryId());
            Company comp = context.getInventory().getCompany(p.getCompanyId());
            String catName = (cat != null) ? cat.getName() : "Unknown";
            String compName = (comp != null) ? comp.getName() : "Unknown";
            
            System.out.printf("%-5s %-20s %-15s %-15s $%-9.2f %-10d\n", 
                p.getId(), p.getName(), catName, compName, p.getPrice(), p.getStock());
        }
    }

    private void manageCategories() {
        while (true) {
            System.out.println("\n--- Manage Categories ---");
            System.out.println("1. View Categories");
            System.out.println("2. Add Category");
            System.out.println("3. Delete Category");
            System.out.println("4. Back");
            
            String choice = context.getScanner().nextLine();
            switch (choice) {
                case "1": viewCategories(); break;
                case "2": addCategory(); break;
                case "3": deleteCategory(); break;
                case "4": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void viewCategories() {
        System.out.println("Existing Categories:");
        context.getInventory().getCategories()
            .forEach(c -> System.out.println("- " + c.getName() + " (" + c.getUnitType() + ")"));
    }

    private void addCategory() {
        String name = getStringInput("Category Name: ");
        if (name.isEmpty()) return;
        
        System.out.println("Select Unit Type: 1. KG 2. LITER 3. PCS");
        String typeCh = context.getScanner().nextLine();
        UnitType ut = UnitType.PCS;
        if (typeCh.equals("1")) ut = UnitType.KG;
        else if (typeCh.equals("2")) ut = UnitType.LITER;
        
        context.getInventory().addCategory(new Category(name, ut));
        System.out.println("Category Added.");
    }

    private void deleteCategory() {
        List<Category> cats = context.getInventory().getCategories();
        if (cats.isEmpty()) {
            System.out.println("No categories to delete.");
            return;
        }
        
        System.out.println("Select Category to Delete:");
        for (int i = 0; i < cats.size(); i++) {
            System.out.println((i + 1) + ". " + cats.get(i).getName());
        }
        
        int idx = getIntInput("Enter choice: ") - 1;
        if (idx >= 0 && idx < cats.size()) {
            if (context.getInventory().deleteCategory(cats.get(idx).getId())) {
                System.out.println("Category Deleted.");
            } else {
                System.out.println("Failed to delete.");
            }
        } else {
            System.out.println("Invalid selection.");
        }
    }

    
    private void manageCompanies() {
        System.out.println("Existing Companies:");
        context.getInventory().getCompanies()
            .forEach(c -> System.out.println("- " + c.getName()));
    }
}