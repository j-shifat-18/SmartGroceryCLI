package com.smartgrocery.models;

import java.util.UUID;

public class Product {
    private String id;
    private String name;
    private String categoryId;
    private String companyId;
    private double price;
    private int stock;

    public Product(String name, String categoryId, String companyId, double price, int stock) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.categoryId = categoryId;
        this.companyId = companyId;
        this.price = price;
        this.stock = stock;
    }
    
    public Product(String id, String name, String categoryId, String companyId, double price, int stock) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.companyId = companyId;
        this.price = price;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategoryId() { return categoryId; }
    public String getCompanyId() { return companyId; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    public void updateStock(int quantity) {
        this.stock += quantity;
    }
    
    public String getProductCode() {
        return categoryId + "-" + id + "-" + companyId;
    }

    @Override
    public String toString() {
        return name + " [Stock: " + stock + "]";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
