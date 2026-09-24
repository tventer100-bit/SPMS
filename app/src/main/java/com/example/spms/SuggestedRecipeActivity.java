package com.example.spms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipeActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> suggestedRecipes;
    private DatabaseHelper dbHelper;
    private TextView tvZeroMatchMessage;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recylerViewSuggestedRecipes);
        tvZeroMatchMessage = findViewById(R.id.tvZeroMatchMessage);
        bottomNav = findViewById(R.id.bottomNavigationSuggestions);

        suggestedRecipes = new ArrayList<>();
        adapter = new RecipeAdapter(this, suggestedRecipes, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        bottomNav.setSelectedItemId(R.id.nav_suggestions);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_pantry) {
                startActivity(new Intent(SuggestedRecipeActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_suggestions) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(SuggestedRecipeActivity.this, SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });

        loadSuggestedRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        // Runs Section 2.3 Strict Matching Engine
        suggestedRecipes = dbHelper.getSuggestedRecipes();
        adapter.updateData(suggestedRecipes);

        // Section 2.2 Zero-match handling
        if (suggestedRecipes.isEmpty()) {
            tvZeroMatchMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvZeroMatchMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(SuggestedRecipeActivity.this, RecipeDetailActivity.class);
        intent.putExtra("EXTRA_RECIPE_ID", recipe.getRecipe_ID());
        startActivity(intent);
    }
}



