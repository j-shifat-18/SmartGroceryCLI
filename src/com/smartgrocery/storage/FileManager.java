package com.smartgrocery.storage;

import com.smartgrocery.auth.UserRole;
import com.smartgrocery.models.Product;
import com.smartgrocery.models.Purchase;
import com.smartgrocery.models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileManager {
    private static final String USERS_FILE = "data/users/users.txt";
    private static final String PRODUCTS_FILE = "data/inventory/products.txt";
    private static final String PURCHASES_FILE = "data/transactions/purchases.txt";

    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return products;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0];
                    String category = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    int stock = Integer.parseInt(parts[3]);
                    products.add(new Product(name, category, price, stock));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading products: " + e.getMessage());
        }
        return products;
    }

    public void saveProducts(List<Product> products) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product p : products) {
                bw.write(p.getName() + "," + p.getCategory() + "," + p.getPrice() + "," + p.getStock());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving products: " + e.getMessage());
        }
    }

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    UserRole role = UserRole.valueOf(parts[2]);
                    users.add(new User(username, password, role));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                bw.write(u.getUsername() + "," + u.getPassword() + "," + u.getRole());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
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
                sb.append(entry.getKey().getName()).append(":").append(entry.getValue());
                if (i < purchase.getItems().size() - 1) sb.append("|");
                i++;
            }
            bw.write(sb.toString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error saving purchase: " + e.getMessage());
        }
    }

    

}
