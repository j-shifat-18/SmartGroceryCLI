package com.smartgrocery.models;

import java.util.UUID;

public class Category {
    private String id;
    private String name;
    private UnitType unitType;

    public Category(String name, UnitType unitType) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.unitType = unitType;
    }
    
    public Category(String id, String name, UnitType unitType) {
        this.id = id;
        this.name = name;
        this.unitType = unitType;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public UnitType getUnitType() { return unitType; }
    
    @Override
    public String toString() {
        return name + " (" + unitType + ")";
    }
}
