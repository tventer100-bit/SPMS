package com.example.spms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


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
}