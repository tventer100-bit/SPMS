package com.example.spms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

// Activity for displaying recipe suggestions based on current pantry items
public class SuggestedRecipeActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> suggestedRecipes;
    private DatabaseHelper dbHelper;
    private TextView tvZeroMatchMessage;
    private BottomNavigationView bottomNav;
    private Button btnToggleAlmostThere;

    // Tracks current mode
    private boolean showAlmostThere = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewSuggestedRecipes);
        tvZeroMatchMessage = findViewById(R.id.tvZeroMatchMessage);
        bottomNav = findViewById(R.id.bottomNavigationSuggestions);
        btnToggleAlmostThere = findViewById(R.id.btnToggleAlmostThere);

        suggestedRecipes = new ArrayList<>();
        adapter = new RecipeAdapter(this, suggestedRecipes, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Toggle logic for switching view between complete matches and 'Almost There'
        btnToggleAlmostThere.setOnClickListener(v -> {
            showAlmostThere = !showAlmostThere;

            // Safely reset scroll position to top when toggling modes
            if (recyclerView != null) {
                recyclerView.scrollToPosition(0);
            }

            if (showAlmostThere) {
                btnToggleAlmostThere.setText("Show Available Recipes");
            } else {
                btnToggleAlmostThere.setText("Show 'Almost There' Recipes");
            }

            loadSuggestedRecipes();
        });

        // Bottom Nag=vigation setup
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
        if (dbHelper != null && adapter != null) {
            loadSuggestedRecipes();
        }
    }

    // Retrieves recipe suggestions from SQLite based on active toggle state
    private void loadSuggestedRecipes() {
        if (dbHelper == null || adapter == null) return;

        // Set mode FIRST before passing new dataset
        adapter.setAlmostThereMode(showAlmostThere);

        if (showAlmostThere) {
            suggestedRecipes = dbHelper.getAlmostThereRecipes();
        } else {
            suggestedRecipes = dbHelper.getSuggestedRecipes();
        }

        if (suggestedRecipes == null) {
            suggestedRecipes = new ArrayList<>();
        }

        // Update list in adapter
        adapter.updateData(suggestedRecipes);

        // Update UI visibility state safely
        if (suggestedRecipes.isEmpty()) {
            if (showAlmostThere) {
                tvZeroMatchMessage.setText("No recipes found missing 1-2 ingredients!");
            } else {
                tvZeroMatchMessage.setText("No recipes match your pantry yet.\nAdd more ingredients to your pantry to view suggestions!");
            }
            tvZeroMatchMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvZeroMatchMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Opens detailed activity view for the clicked recipe
    @Override
    public void onRecipeClick(Recipe recipe) {
        if (recipe != null) {
            Intent intent = new Intent(SuggestedRecipeActivity.this, RecipeDetailActivity.class);
            intent.putExtra("EXTRA_RECIPE_ID", recipe.getRecipe_ID());
            startActivity(intent);
        }
    }
}