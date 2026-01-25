package com.smartgrocery.shopping;

import com.smartgrocery.inventory.Inventory;
import com.smartgrocery.models.Product;
import com.smartgrocery.models.Purchase;
import com.smartgrocery.models.User;
import com.smartgrocery.storage.FileManager;

import java.util.Map;

public class Checkout {
    private Inventory inventory;
    private FileManager fileManager;

    public Checkout(Inventory inventory, FileManager fileManager) {
        this.inventory = inventory;
        this.fileManager = fileManager;
    }

    public Purchase processCheckout(Cart cart, User user) {
        if (cart.isEmpty()) return null;

        // 1. Validate Stock
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            Product cartProduct = entry.getKey();
            int qty = entry.getValue();
            Product inventoryProduct = inventory.searchProduct(cartProduct.getName());
            
            if (inventoryProduct == null || inventoryProduct.getStock() < qty) {
                System.out.println("Checkout Failed: Insufficient stock for " + cartProduct.getName());
                return null;
            }
        }

        // 2. Process Transaction 
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            // Deduct stock (negative quantity)
            inventory.updateProductStock(p.getName(), -qty);
        }

        // 3. Generate Purchase Record
        Purchase purchase = new Purchase(Map.copyOf(cart.getItems()), cart.calculateTotal());
        user.addPurchase(purchase);

        // 4. Save to History
        fileManager.savePurchase(purchase, user);

        // 5. Clear Cart
        cart.clearCart();

        return purchase;
    }
}
