package com.smartgrocery.models;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class Purchase {
    private String receiptId;
    private Map<Product, Integer> items;
    private LocalDateTime timestamp;
    private double totalAmount;

    public Purchase(Map<Product, Integer> items, double totalAmount) {
        this.receiptId = generateReceiptId();
        this.items = items;
        this.timestamp = LocalDateTime.now();
        this.totalAmount = totalAmount;
    }
    
    public Purchase(Map<Product, Integer> items, LocalDateTime timestamp, double totalAmount) {
        this.receiptId = generateReceiptId();
        this.items = items;
        this.timestamp = timestamp;
        this.totalAmount = totalAmount;
    }
    
    public Purchase(String receiptId, Map<Product, Integer> items, LocalDateTime timestamp, double totalAmount) {
        this.receiptId = receiptId;
        this.items = items;
        this.timestamp = timestamp;
        this.totalAmount = totalAmount;
    }

    private String generateReceiptId() {
        return "RCP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String getReceiptId() { return receiptId; }
    public Map<Product, Integer> getItems() { return items; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getTotalAmount() { return totalAmount; }
}
