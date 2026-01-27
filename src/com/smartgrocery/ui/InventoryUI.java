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
            System.out.println("2. Add Product");
            System.out.println("3. Manage Categories");
            System.out.println("4. Manage Companies");
            System.out.println("5. Remove Product");
            System.out.println("6. Search Product");
            System.out.println("7. Back to Admin Menu");
            
            String choice = context.getScanner().nextLine();
            switch (choice) {
                case "1": viewProductsHierarchical(); break;
                case "2": addProductFlow(); break;
                case "3": manageCategories(); break;
                case "4": manageCompanies(); break;
                case "5": removeProduct(); break;
                case "6": searchProduct(); break;
                case "7": return;
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