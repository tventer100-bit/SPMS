package com.example.spms;
import java.util.List;
public class Recipe {
    private int recipe_ID;
    private String name;
    private String instructions;
    private List<RecipeIngredient> ingredients;

    public Recipe(int recipe_ID, String name, String instructions){
        this.recipe_ID = recipe_ID;
        this.name = name;
        this.instructions = instructions;
    }

    public int getRecipe_ID(){return recipe_ID;}
    public String getName(){return name;}
    public String getInstructions(){return instructions;}
    public List<RecipeIngredient> getIngredients(){return ingredients;}
    public void setIngredients(List<RecipeIngredient>ingredients){this.ingredients = ingredients;}
}