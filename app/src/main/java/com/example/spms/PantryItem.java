package com.example.spms;

public class PantryItem {
    private int pantry_ID;
    private String name;
    private double quantity;
    private String unit;
    private String expiry;

    public PantryItem(int pantry_ID, String name, double quantity, String unit, String expiry){
        this.pantry_ID = pantry_ID;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    public PantryItem(String name, double quantity, String unit, String expiry){
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    public int getPantry_ID(){return pantry_ID;}
    public String getName(){return name;}
    public double getQuantity() {return quantity;}
    public String getUnit() {return unit;}
    public String getExpiry(){return expiry;}
}
