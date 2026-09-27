package com.example.spms;

// Model representing a single ingredient item required by a recipe
public class RecipeIngredient {
    private String name;
    private double quantity;
    private String unit;

    public RecipeIngredient(String name, double quantity, String unit){
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getters and Setters
    public String getName(){return name;}
    public double getQuantity(){return quantity;}
    public String getUnit(){return unit;}
    public void setUnit(String unit){this.unit = unit; }
}

