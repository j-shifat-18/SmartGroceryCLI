package com.smartgrocery.shopping;

import com.smartgrocery.models.Product;
import java.util.HashMap;
import java.util.Map;

public class Cart {
    private Map<Product, Integer> items;

    public Cart() {
        this.items = new HashMap<>();
    }

    public void addItem(Product product, int quantity) {
        if (quantity <= 0) return;
        items.put(product, items.getOrDefault(product, 0) + quantity);
    }

    public void removeItem(Product product) {
        items.remove(product);
    }
    
    public void updateQuantity(Product product, int quantity) {
        if (quantity <= 0) {
            removeItem(product);
        } else {
            items.put(product, quantity);
        }
    }

    public double calculateTotal() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public void clearCart() {
        items.clear();
    }

    public Map<Product, Integer> getItems() {
        return items;
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
}