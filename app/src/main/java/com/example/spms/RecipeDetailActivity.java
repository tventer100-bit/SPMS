
package com.example.spms;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvRecipeTitle, tvIngredientsList, tvInstructions;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dbHelper = new DatabaseHelper(this);

        tvRecipeTitle = findViewById(R.id.tvDetailRecipeTitle);
        tvIngredientsList = findViewById(R.id.tvDetailIngredientsList);
        tvInstructions = findViewById(R.id.tvDetailInstructions);

        int recipe_ID = getIntent().getIntExtra("EXTRA_RECIPE_ID", -1);

        if (recipe_ID != -1) {
            Recipe recipe = dbHelper.getRecipeById(recipe_ID);
            if (recipe != null) {
                tvRecipeTitle.setText(recipe.getName());
                tvInstructions.setText(recipe.getInstructions());

                StringBuilder builder = new StringBuilder();
                for (RecipeIngredient ing : recipe.getIngredients()) {
                    builder.append("• ").append(ing.getQuantity()).append(" ").append(ing.getUnit()).append(" ").append(ing.getName()).append("\n");
                }
                tvIngredientsList.setText(builder.toString());
            } else {
                Toast.makeText(this, "Error loading recipe details", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            finish();
        }
    }
}
