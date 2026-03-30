package com.smartgrocery.ui;

import com.smartgrocery.models.Category;
import com.smartgrocery.models.Product;

import java.util.List;


public abstract class BaseUI {
    protected final UIContext context;

    public BaseUI(UIContext context) {
        this.context = context;
    }

   
    protected void printProductTable(List<Product> products) {
        System.out.printf("%-20s %-15s %-10s %-10s\n", "Name", "Category", "Price", "Stock");
        System.out.println("-----------------------------------------------------------");
        for (Product p : products) {
            Category cat = context.getInventory().getCategory(p.getCategoryId());
            String catName = (cat != null) ? cat.getName() : p.getCategoryId();
            System.out.printf("%-20s %-15s $%-9.2f %-10d\n", p.getName(), catName, p.getPrice(), p.getStock());
        }
    }

    
    protected int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(context.getScanner().nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }

   
    protected double getDoubleInput(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(context.getScanner().nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return -1;
        }
    }

   
    protected String getStringInput(String prompt) {
        System.out.print(prompt);
        return context.getScanner().nextLine().trim();
    }
}