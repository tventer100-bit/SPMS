package com.example.spms;

public class RecipeIngredient {
    private String name;
    private double quantity;
    private String unit;

    public RecipeIngredient(String name, double quantity, String unit){
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
    public String getName(){return name;}
    public double getQuantity(){return quantity;}
    public String getUnit(){return unit;}
}

