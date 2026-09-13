package com.example.spms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smart_pantry_management.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context cont) {
        super(cont, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Foreign key support
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // enables foreign key constraints in SQLite
    }

    // Create the database tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry(pantry_ID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, quantity REAL, unit TEXT, expiry TEXT);");
        db.execSQL("CREATE TABLE recipes(recipes_ID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, instructions TEXT); ");
        db.execSQL("CREATE TABLE recipe_ingredients(RI_ID INTEGER PRIMARY KEY AUTOINCREMENT, recipe_ID INTEGER, name TEXT, quantity REAL, unit TEXT, FOREIGN KEY (recipe_ID) REFERENCES recipes(recipes_ID) ON DELETE CASCADE);");
    }

    // Used if you are creating a new database version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    // Pantry Crud Operations
    public long addPantryItem(PantryItem item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName().trim());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry", item.getExpiry());
        return db.insert("pantry", null, values);
    }

    public List<PantryItem> getAllPantryItems(){
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM pantry ORDER BY name ASC", null);
        if (cur.moveToFirst()){
            do {
                int pantry_ID = cur.getInt(cur.getColumnIndexOrThrow("pantry_ID"));
                String name = cur.getString(cur.getColumnIndexOrThrow("name"));
                double quantity = cur.getDouble(cur.getColumnIndexOrThrow("quantity"));
                String unit = cur.getString(cur.getColumnIndexOrThrow("unit"));
                String expiry = cur.getString(cur.getColumnIndexOrThrow("expiry"));
                list.add(new PantryItem(pantry_ID, name, quantity, unit, expiry));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public int updatePantryItem(PantryItem item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName().trim());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry", item.getExpiry());
        return db.update("pantry", values, "pantry_ID = ?", new String[]{String.valueOf(item.getPantry_ID())});
    }

    public void deletePantryItem(int pantry_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("pantry", "pantry_ID = ?", new String[]{String.valueOf(pantry_ID)});
    }
}