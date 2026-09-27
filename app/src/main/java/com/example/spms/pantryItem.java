package com.example.spms;
// Data model class representing an ingredient stored in the user's pantry
public class pantryItem {
    private int pantry_ID;
    private String name;
    private double quantity;
    private String unit;
    private String expiry;

    // Constructor used when retrieving items from SQLite
    public pantryItem(int pantry_ID, String name, double quantity, String unit, String expiry){
        this.pantry_ID = pantry_ID;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    // Overloaded constructor for creating new items prior to DB insertion
    public pantryItem(String name, double quantity, String unit, String expiry){
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    // Getters
    public int getPantry_ID(){return pantry_ID;}
    public String getName(){ return name; }
    public double getQuantity() {return quantity;}
    public String getUnit() {return unit;}
    public String getExpiry(){return expiry;}

    // Setters
    public void setPantry_ID(int pantry_ID){ this.pantry_ID = pantry_ID; }
    public void setName(String name){ this.name = name; }
    public void setQuantity(double quantity){ this.quantity = quantity; }
    public void setUnit(String unit){ this.unit = unit; }
    public void setExpiry(String expiry){ this.expiry = expiry; }
}
