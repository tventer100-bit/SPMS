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
        seedInitialRecipes(db);
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
    public long addPantryItem(pantryItem item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName().trim());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry", item.getExpiry());
        return db.insert("pantry", null, values);
    }

    public List<pantryItem> getAllPantryItems(){
        List<pantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM pantry ORDER BY name ASC", null);
        if (cur.moveToFirst()){
            do {
                int pantry_ID = cur.getInt(cur.getColumnIndexOrThrow("pantry_ID"));
                String name = cur.getString(cur.getColumnIndexOrThrow("name"));
                double quantity = cur.getDouble(cur.getColumnIndexOrThrow("quantity"));
                String unit = cur.getString(cur.getColumnIndexOrThrow("unit"));
                String expiry = cur.getString(cur.getColumnIndexOrThrow("expiry"));
                list.add(new pantryItem(pantry_ID, name, quantity, unit, expiry));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public int updatePantryItem(pantryItem item){
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

    // Strict Logic
    public List<Recipe> getSuggestedRecipes(){
        List<Recipe> matchingRecipes = new ArrayList<>();
        List<Recipe> allRecipes = getAllRecipesWithIngredients();

        for (Recipe recipe : allRecipes) {
            int missingCount = getMissingIngredientCount(recipe);
            if (missingCount == 0 && recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                matchingRecipes.add(recipe);
            }
        }
        return matchingRecipes;
    }

    public List<Recipe> getAlmostThereRecipes() {
        List<Recipe> matchingRecipes = new ArrayList<>();
        List<Recipe> allRecipes = getAllRecipesWithIngredients();

        for (Recipe recipe : allRecipes) {
            int missingCount = getMissingIngredientCount(recipe);
            // Strictly include only recipes missing 1 or 2 ingredients
            if (missingCount == 1 || missingCount == 2) {
                matchingRecipes.add(recipe);
            }
        }
        return matchingRecipes;
    }

    public int getMissingIngredientCount(Recipe recipe) {
        if (recipe == null || recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return -1;
        }

        List<pantryItem> pantry = getAllPantryItems();
        if (pantry == null || pantry.isEmpty()) {
            return recipe.getIngredients().size();
        }

        int missingCount = 0;

        for (RecipeIngredient repIng : recipe.getIngredients()) {
            if (repIng == null || repIng.getName() == null) continue;

            boolean foundInPantry = false;

            for (pantryItem item : pantry) {
                if (item == null || item.getName() == null) continue;

                // CRITICAL FIX: Use the fuzzy/plural matcher instead of equalsIgnoreCase
                if (isNameMatch(repIng.getName(), item.getName())) {
                    double pantryQty = normalizeQuantity(item.getQuantity(), item.getUnit());
                    double reqQty = normalizeQuantity(repIng.getQuantity(), repIng.getUnit());

                    if (pantryQty >= reqQty) {
                        foundInPantry = true;
                        break;
                    }
                }
            }

            if (!foundInPantry) {
                missingCount++;
            }
        }

        return missingCount;
    }

    private boolean isNameMatch(String name1, String name2){
        if (name1 == null || name2 == null) return false;

        String n1 = name1.trim().toLowerCase();
        String n2 = name2.trim().toLowerCase();

        if(n1.equals(n2)) return true;
        if((n1 + "s").equals(n2) || (n2 + "s").equals(n1)) return true;
        if((n1 + "es").equals(n2) || (n2 + "es").equals(n1)) return true;

        return false;
    }

    private double normalizeQuantity(double quantity, String unit) {
        if (unit == null) return quantity;
        String u = unit.trim().toLowerCase();

        switch (u) {
            case "kg":
            case "kilogram":
            case "kilograms":
            case "l":
            case "liter":
            case "liters":
                return quantity * 1000.0;
            default:
                return quantity;
        }
    }

    public Recipe getRecipeById(int recipe_ID) {
        Recipe recipe = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM recipes WHERE recipes_ID = ?", new String[]{String.valueOf(recipe_ID)});

        if (cur.moveToFirst()) {
            String name = cur.getString(cur.getColumnIndexOrThrow("name"));
            String instructions = cur.getString(cur.getColumnIndexOrThrow("instructions"));

            recipe = new Recipe(recipe_ID, name, instructions);
            recipe.setIngredients(getIngredientsForRecipe(db, recipe_ID));
        }
        cur.close();

        return recipe;
    }

    private List<Recipe> getAllRecipesWithIngredients() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM recipes", null);

        if (cur.moveToFirst()) {
            do {
                int recipe_ID = cur.getInt(cur.getColumnIndexOrThrow("recipes_ID"));
                String name = cur.getString(cur.getColumnIndexOrThrow("name"));
                String instructions = cur.getString(cur.getColumnIndexOrThrow("instructions"));

                Recipe recipe = new Recipe(recipe_ID, name, instructions);

                List<RecipeIngredient> ingredients = getIngredientsForRecipe(db, recipe_ID);
                recipe.setIngredients(ingredients);

                recipes.add(recipe);
            } while (cur.moveToNext());
        }
        cur.close();
        return recipes;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, int recipe_ID){
        List<RecipeIngredient> ingredients = new ArrayList<>();
        Cursor cur = db.rawQuery("SELECT * FROM recipe_ingredients WHERE recipe_ID = ?", new String[]{String.valueOf(recipe_ID)});

        if(cur.moveToFirst()){
            do{
                String name = cur.getString(cur.getColumnIndexOrThrow("name"));
                double quantity = cur.getDouble(cur.getColumnIndexOrThrow("quantity"));
                String unit = cur.getString(cur.getColumnIndexOrThrow("unit"));
                ingredients.add(new RecipeIngredient(name, quantity, unit));
            }while(cur.moveToNext());
        }
        cur.close();
        return ingredients;
    }

    private void addSeededRecipe(SQLiteDatabase db, String name, String instructions, String[] ingNames, double[] quantity, String[] units){
        ContentValues cValues = new ContentValues();
        cValues.put("name", name);
        cValues.put("instructions", instructions);
        long recipe_ID = db.insert("recipes", null, cValues);

        for(int i = 0; i < ingNames.length; i++){
            ContentValues iValues = new ContentValues();
            iValues.put("recipe_ID", recipe_ID);
            iValues.put("name", ingNames[i]);
            iValues.put("quantity", quantity[i]);
            iValues.put("unit", units[i]);
            db.insert("recipe_ingredients", null, iValues);
        }
    }

    private void seedInitialRecipes(SQLiteDatabase db){
        addSeededRecipe(db, "Scrambled Eggs",  "1. Whisk eggs.\n2. Melt butter in pan.\n3. Cook on low heat.",
                new String[]{"egg", "butter"}, new double[]{2,10}, new String[]{"pcs", "g"});
        addSeededRecipe(db, "Boiled Eggs", "1. Place eggs in boiling water.\n2. Boil for 7 minutes.",
                new String[]{"egg"}, new double[]{2}, new String[]{"pcs"});
        addSeededRecipe(db, "Pancakes", "1. Mix ingredients.\n2. Cook batter on skillet until golden.",
                new String[]{"flour", "egg", "milk", "butter"}, new double[]{200, 1, 250, 15}, new String[]{"g", "pcs", "ml", "g"});
        addSeededRecipe(db, "Omelette", "1. Beat eggs.\n2. Cook with cheese in pan.",
                new String[]{"egg", "cheese", "butter"}, new double[]{3, 50, 10}, new String[]{"pcs", "g", "g"});
        addSeededRecipe(db, "Garlic Toast", "1. Toast bread.\n2. Spread garlic butter.",
                new String[]{"bread", "butter", "garlic"}, new double[]{2, 20, 1}, new String[]{"pcs", "g", "pcs"});
        addSeededRecipe(db, "Grilled Cheese", "1. Butter bread.\n2. Add cheese and grill in pan.",
                new String[]{"bread", "cheese", "butter"}, new double[]{2, 2, 10}, new String[]{"pcs", "pcs", "g"});
        addSeededRecipe(db, "Tomato Rice", "1. Sauté tomatoes and onions.\n2. Mix with cooked rice.",
                new String[]{"rice", "tomato", "onion"}, new double[]{200, 2, 1}, new String[]{"g", "pcs", "pcs"});
        addSeededRecipe(db, "Fried Rice", "1. Sauté rice and egg in oil.",
                new String[]{"rice", "egg", "oil"}, new double[]{250, 2, 15}, new String[]{"g", "pcs", "ml"});
        addSeededRecipe(db, "Pasta Arrabbiata", "1. Boil pasta.\n2. Cook tomatoes and garlic in oil.\n3. Combine.",
                new String[]{"pasta", "tomato", "garlic", "oil"}, new double[]{200, 3, 2, 20}, new String[]{"g", "pcs", "pcs", "ml"});
        addSeededRecipe(db, "Simple Salad", "1. Chop vegetables.\n2. Drizzle with oil.",
                new String[]{"tomato", "onion", "oil"}, new double[]{2, 1, 10}, new String[]{"pcs", "pcs", "ml"});
        addSeededRecipe(db, "Mashed Potatoes", "1. Boil potatoes.\n2. Mash with butter and milk.",
                new String[]{"potato", "butter", "milk"}, new double[]{3, 30, 50}, new String[]{"pcs", "g", "ml"});
        addSeededRecipe(db, "French Fries", "1. Cut potatoes.\n2. Deep fry in hot oil.",
                new String[]{"potato", "oil"}, new double[]{2, 200}, new String[]{"pcs", "ml"});
        addSeededRecipe(db, "Garlic Butter Pasta", "1. Boil pasta.\n2. Toss in garlic butter.",
                new String[]{"pasta", "garlic", "butter"}, new double[]{200, 2, 30}, new String[]{"g", "pcs", "g"});
        addSeededRecipe(db, "Tomato Soup", "1. Puree tomatoes.\n2. Simmer with garlic butter.",
                new String[]{"tomato", "butter", "garlic"}, new double[]{4, 20, 1}, new String[]{"pcs", "g", "pcs"});
        addSeededRecipe(db, "Mac and Cheese", "1. Cook pasta.\n2. Make cheese sauce with butter and milk.\n3. Mix.",
                new String[]{"pasta", "cheese", "milk", "butter"}, new double[]{200, 100, 100, 20}, new String[]{"g", "g", "ml", "g"});
    }
}