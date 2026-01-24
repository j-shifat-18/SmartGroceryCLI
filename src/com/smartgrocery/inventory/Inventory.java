package com.smartgrocery.inventory;

import com.smartgrocery.models.Product;
import com.smartgrocery.storage.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Inventory {
    private List<Product> products;
    private FileManager fileManager;
    private static final int LOW_STOCK_THRESHOLD = 50;

    public Inventory(FileManager fileManager) {
        this.fileManager = fileManager;
        this.products = fileManager.loadProducts();
    }

    public void addProduct(Product product) {
        products.add(product);
        fileManager.saveProducts(products);
    }

    public boolean updateProductPrice(String name, double newPrice) {
        Product p = searchProduct(name);
        if (p != null) {
            p.setPrice(newPrice);
            fileManager.saveProducts(products);
            return true;
        }
        return false;
    }

    public boolean updateProductStock(String name, int quantity) {
        Product p = searchProduct(name);
        if (p != null) {
            p.updateStock(quantity);
            fileManager.saveProducts(products);
            return true;
        }
        return false;
    }

    public boolean deleteProduct(String name) {
        Product p = searchProduct(name);
        if (p != null) {
            products.remove(p);
            fileManager.saveProducts(products);
            return true;
        }
        return false;
    }

    public Product searchProduct(String name) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public List<Product> getLowStockItems() {
        return products.stream()
                .filter(p -> p.getStock() < LOW_STOCK_THRESHOLD)
                .collect(Collectors.toList());
    }

    public List<Product> getAllProducts() {
        return products;
    }
}