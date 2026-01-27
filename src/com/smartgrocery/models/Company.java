package com.smartgrocery.models;

import java.util.UUID;

public class Company {
    private String id;
    private String name;

    public Company(String name) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
    }
    
    public Company(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    
    @Override
    public String toString() {
        return name;
    }
}
