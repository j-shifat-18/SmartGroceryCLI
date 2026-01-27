package com.smartgrocery.models;

import java.time.LocalDateTime;
import java.util.Map;

public class Purchase {
    private Map<Product, Integer> items;
    private LocalDateTime timestamp;
    private double totalAmount;

    public Purchase(Map<Product, Integer> items, double totalAmount) {
        this.items = items;
        this.timestamp = LocalDateTime.now();
        this.totalAmount = totalAmount;
    }
    
    public Purchase(Map<Product, Integer> items, LocalDateTime timestamp, double totalAmount) {
        this.items = items;
        this.timestamp = timestamp;
        this.totalAmount = totalAmount;
    }

    public Map<Product, Integer> getItems() { return items; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getTotalAmount() { return totalAmount; }
}
