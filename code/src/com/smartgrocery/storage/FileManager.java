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
    private static final String RECEIPTS_DIR = "data/receipts/";
    private static final String COMPANIES_FILE = "data/inventory/companies.txt";
    private static final String CATEGORIES_FILE = "data/inventory/categories.txt";

    // --- Companies ---
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

    // --- Categories ---
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

    // --- Products ---
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

    // --- Purchases ---
    public void loadPurchases(List<User> users, List<Product> products) {
        File file = new File(PURCHASES_FILE);
        if (!file.exists()) return;

        Map<String, Product> productMap = new HashMap<>();
        for (Product p : products) {
            productMap.put(p.getId(), p);
            productMap.put(p.getName(), p);
        }

        Map<String, User> userMap = new HashMap<>();
        for (User u : users) userMap.put(u.getUsername(), u);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    String receiptId = null;
                    String username;
                    LocalDateTime timestamp;
                    double total;
                    String itemsData;

                    // Check if format includes receiptId (new format has 5 parts)
                    if (parts.length >= 5) {
                        receiptId = parts[0];
                        username = parts[1];
                        timestamp = LocalDateTime.parse(parts[2]);
                        total = Double.parseDouble(parts[3]);
                        itemsData = parts[4];
                    } else {
                        // Legacy format without receiptId
                        username = parts[0];
                        timestamp = LocalDateTime.parse(parts[1]);
                        total = Double.parseDouble(parts[2]);
                        itemsData = parts[3];
                    }

                    User user = userMap.get(username);
                    if (user == null) continue;

                    Map<Product, Integer> items = new HashMap<>();
                    String[] itemParts = itemsData.split("\\|");
                    for (String itemStr : itemParts) {
                        String[] pair = itemStr.split(":");
                        if (pair.length == 2) {
                            Product p = productMap.get(pair[0]);
                            int qty = Integer.parseInt(pair[1]);
                            if (p != null) items.put(p, qty);
                        }
                    }

                    Purchase purchase;
                    if (receiptId != null) {
                        purchase = new Purchase(receiptId, items, timestamp, total);
                    } else {
                        purchase = new Purchase(items, timestamp, total);
                    }
                    user.addPurchase(purchase);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading purchases: " + e.getMessage());
        }
    }

    public void savePurchase(Purchase purchase, User user) {
        // Save to purchases.txt with receipt ID
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PURCHASES_FILE, true))) {
            StringBuilder sb = new StringBuilder();
            sb.append(purchase.getReceiptId()).append(";")
              .append(user.getUsername()).append(";")
              .append(purchase.getTimestamp().toString()).append(";")
              .append(purchase.getTotalAmount()).append(";");

            int i = 0;
            for (Map.Entry<Product, Integer> entry : purchase.getItems().entrySet()) {
                sb.append(entry.getKey().getId()).append(":").append(entry.getValue());
                if (i < purchase.getItems().size() - 1) sb.append("|");
                i++;
            }
            bw.write(sb.toString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error saving purchase: " + e.getMessage());
        }

        // Save detailed receipt to separate file
        saveDetailedReceipt(purchase, user);
    }

    private void saveDetailedReceipt(Purchase purchase, User user) {
        File receiptsDir = new File(RECEIPTS_DIR);
        if (!receiptsDir.exists()) {
            receiptsDir.mkdirs();
        }

        String receiptFile = RECEIPTS_DIR + purchase.getReceiptId() + ".txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(receiptFile))) {
            bw.write("========================================");
            bw.newLine();
            bw.write("         SMART GROCERY RECEIPT");
            bw.newLine();
            bw.write("========================================");
            bw.newLine();
            bw.write("Receipt ID: " + purchase.getReceiptId());
            bw.newLine();
            bw.write("Customer: " + user.getUsername());
            bw.newLine();
            bw.write("Date: " + purchase.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            bw.newLine();
            bw.write("========================================");
            bw.newLine();
            bw.newLine();
            bw.write(String.format("%-30s %5s %8s %10s", "Item", "Qty", "Price", "Subtotal"));
            bw.newLine();
            bw.write("----------------------------------------");
            bw.newLine();

            for (Map.Entry<Product, Integer> entry : purchase.getItems().entrySet()) {
                Product p = entry.getKey();
                int qty = entry.getValue();
                double subtotal = p.getPrice() * qty;
                bw.write(String.format("%-30s %5d $%7.2f $%9.2f", 
                    p.getName().length() > 30 ? p.getName().substring(0, 27) + "..." : p.getName(),
                    qty, p.getPrice(), subtotal));
                bw.newLine();
            }

            bw.write("========================================");
            bw.newLine();
            bw.write(String.format("%-30s %5s %8s $%9.2f", "TOTAL", "", "", purchase.getTotalAmount()));
            bw.newLine();
            bw.write("========================================");
            bw.newLine();
            bw.write("     Thank you for shopping with us!");
            bw.newLine();
            bw.write("========================================");
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error saving detailed receipt: " + e.getMessage());
        }
    }

    public String loadReceipt(String receiptId) {
        String receiptFile = RECEIPTS_DIR + receiptId + ".txt";
        File file = new File(receiptFile);
        if (!file.exists()) {
            return null;
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error loading receipt: " + e.getMessage());
            return null;
        }
        return content.toString();
    }
    
    private void ensureFile(File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }
}
