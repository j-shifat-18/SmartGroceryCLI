package com.smartgrocery.inventory;

import com.smartgrocery.models.Category;
import com.smartgrocery.models.Company;
import com.smartgrocery.models.Product;
import com.smartgrocery.storage.FileManager;

import java.util.List;
import java.util.stream.Collectors;

public class Inventory {
    private List<Product> products;
    private List<Category> categories;
    private List<Company> companies;
    private FileManager fileManager;

    public Inventory(FileManager fileManager) {
        this.fileManager = fileManager;
        this.products = fileManager.loadProducts();
        this.categories = fileManager.loadCategories();
        this.companies = fileManager.loadCompanies();
    }

    // Product Management 
    public void addProduct(Product product) {
        products.add(product);
        fileManager.saveProducts(products);
    }

    // public boolean updateProductPrice(String name, double newPrice) { 
    //     // Note: Name might not be unique anymore across different companies
    //     // Use exact ID or assume first match for legacy CLI calls?
    //     // Updating to find by Name but handle ambiguity?
    //     // For CLI "Admin", maybe search and list variants?
    //     // For now, simpler: Update first match or specific logic.
    //     Product p = searchProductByName(name);
    //     if (p != null) {
    //         p.setPrice(newPrice);
    //         fileManager.saveProducts(products);
    //         return true;
    //     }
    //     return false;
    // }
    
    public boolean updateProductPriceById(String id, double newPrice) {
        Product p = getProductById(id);
        if (p != null) {
            p.setPrice(newPrice);
            fileManager.saveProducts(products);
            return true;
        }
        return false;
    }

    // public boolean updateProductStock(String name, int quantity) {
    //     Product p = searchProductByName(name);
    //     if (p != null) {
    //         p.updateStock(quantity);
    //         fileManager.saveProducts(products);
    //         return true;
    //     }
    //     return false;
    // }
    
    public boolean updateProductStockById(String id, int quantity) {
        Product p = getProductById(id);
        if (p != null) {
            p.updateStock(quantity);
            fileManager.saveProducts(products);
            return true;
        }
        return false;
    }

    // public boolean deleteProduct(String name) {
    //     Product p = searchProductByName(name);
    //     if (p != null) {
    //         products.remove(p);
    //         fileManager.saveProducts(products);
    //         return true;
    //     }
    //     return false;
    // }
    
    public boolean deleteProductById(String id) {
         Product p = getProductById(id);
         if (p != null) {
             products.remove(p);
             fileManager.saveProducts(products);
             return true;
         }
         return false;
    }

    public Product searchProductByName(String name) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
    
    public Product getProductById(String id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Product> getAllProducts() {
        return products;
    }
    
    public List<Product> getProductsByCategory(String categoryId) {
        return products.stream()
                .filter(p -> p.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }
    
    public List<Product> getProductVariants(String name) {
        return products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    // Category Management 
    public List<Category> getCategories() { return categories; }
    
    public void addCategory(Category c) {
        categories.add(c);
        fileManager.saveCategories(categories);
    }
    
    public Category getCategory(String id) {
        return categories.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    public boolean deleteCategory(String id) {
        Category c = getCategory(id);
        if (c != null) {
            categories.remove(c);
            fileManager.saveCategories(categories);
            return true;
        }
        return false;
    }

    // Company Management
    public List<Company> getCompanies() { return companies; }
    
    public void addCompany(Company c) {
        companies.add(c);
        fileManager.saveCompanies(companies);
    }
    
    public Company getCompany(String id) {
        return companies.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }
}
