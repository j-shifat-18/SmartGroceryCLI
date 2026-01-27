package com.smartgrocery.storage;

import com.smartgrocery.auth.UserRole;
import com.smartgrocery.models.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {
    private static final String USERS_FILE = "data/users/users.txt";
    private static final String PRODUCTS_FILE = "data/inventory/products.txt";
    private static final String PURCHASES_FILE = "data/transactions/purchases.txt";
    private static final String COMPANIES_FILE = "data/inventory/companies.txt";
    private static final String CATEGORIES_FILE = "data/inventory/categories.txt";

    //? Companies
    public List<Company> loadCompanies() {
        List<Company> list = new ArrayList<>();
        File file = new File(COMPANIES_FILE);
        ensureFile(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    list.add(new Company(parts[0], parts[1]));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    public void saveCompanies(List<Company> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COMPANIES_FILE))) {
            for (Company c : list) {
                bw.write(c.getId() + "," + c.getName());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    //? Categories
    public List<Category> loadCategories() {
        List<Category> list = new ArrayList<>();
        File file = new File(CATEGORIES_FILE);
        ensureFile(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    list.add(new Category(parts[0], parts[1], UnitType.valueOf(parts[2])));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    public void saveCategories(List<Category> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CATEGORIES_FILE))) {
            for (Category c : list) {
                bw.write(c.getId() + "," + c.getName() + "," + c.getUnitType());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    //? Products
    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return products;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // New Format: id,name,categoryId,companyId,price,stock
                if (parts.length == 6) {
                    products.add(new Product(parts[0], parts[1], parts[2], parts[3], 
                                           Double.parseDouble(parts[4]), Integer.parseInt(parts[5])));
                } 
                // Legacy Format support could be added here if needed, but for now we assume fresh start or conversion
            }
        } catch (IOException e) {
            System.err.println("Error loading products: " + e.getMessage());
        }
        return products;
    }

    public void saveProducts(List<Product> products) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product p : products) {
                bw.write(p.getId() + "," + p.getName() + "," + p.getCategoryId() + "," + 
                         p.getCompanyId() + "," + p.getPrice() + "," + p.getStock());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving products: " + e.getMessage());
        }
    }

    //? Users
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        ensureFile(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], UserRole.valueOf(parts[2])));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return users;
    }

    public void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                bw.write(u.getUsername() + "," + u.getPassword() + "," + u.getRole());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Purchases
    public void loadPurchases(List<User> users, List<Product> products) {
        File file = new File(PURCHASES_FILE);
        if (!file.exists()) return;

        // Map by ID for accurate lookup now, or Name if legacy? 
        // New system should track by Product Object or ID. 
        // Current memory model uses Product object references.
        // We will try to match by ID first, then Name (for migration safety)
        
        Map<String, Product> productMap = new HashMap<>();
        for (Product p : products) {
            productMap.put(p.getId(), p); // Map ID -> Product
            productMap.put(p.getName(), p); // Map Name -> Product (Fall back)
        }

        Map<String, User> userMap = new HashMap<>();
        for (User u : users) userMap.put(u.getUsername(), u);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    String username = parts[0];
                    User user = userMap.get(username);
                    if (user == null) continue;

                    LocalDateTime timestamp = LocalDateTime.parse(parts[1]);
                    double total = Double.parseDouble(parts[2]);
                    
                    Map<Product, Integer> items = new HashMap<>();
                    String[] itemParts = parts[3].split("\\|");
                    for (String itemStr : itemParts) {
                        String[] pair = itemStr.split(":");
                        if (pair.length == 2) {
                            // pair[0] could be Name (legacy) or ID (new)
                            Product p = productMap.get(pair[0]);
                            int qty = Integer.parseInt(pair[1]);
                            if (p != null) items.put(p, qty);
                        }
                    }
                    user.addPurchase(new Purchase(items, timestamp, total));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading purchases: " + e.getMessage());
        }
    }

    public void savePurchase(Purchase purchase, User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PURCHASES_FILE, true))) {
            StringBuilder sb = new StringBuilder();
            sb.append(user.getUsername()).append(";")
              .append(purchase.getTimestamp().toString()).append(";")
              .append(purchase.getTotalAmount()).append(";");
            
            int i = 0;
            for (Map.Entry<Product, Integer> entry : purchase.getItems().entrySet()) {
                // Save ID instead of Name for better referencing
                sb.append(entry.getKey().getId()).append(":").append(entry.getValue());
                if (i < purchase.getItems().size() - 1) sb.append("|");
                i++;
            }
            bw.write(sb.toString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error saving purchase: " + e.getMessage());
        }
    }
    
    private void ensureFile(File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }
}
