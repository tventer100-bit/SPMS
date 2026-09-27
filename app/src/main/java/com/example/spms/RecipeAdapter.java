package com.example.spms;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// RecyclerView Adapter used to render suggested recipes and missing ingredients
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>{
    private final Context context;
    private List<Recipe> recipeList;
    private final OnRecipeClickListener listener;
    private boolean isAlmostThereMode = false;
    private DatabaseHelper dbHelper;

    // Interface to listen for selection of a recipe item
    public interface OnRecipeClickListener{
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(Context context, List<Recipe> recipeList, OnRecipeClickListener listener){
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }

    // Toggle between strict logic and 'Almost There'
    public void setAlmostThereMode(boolean almostThereMode) {
        isAlmostThereMode = almostThereMode;
    }

    @NonNull
    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        if (recipe == null) return;

        holder.tvRecipeName.setText(recipe.getName() != null ? recipe.getName() : "Untitled Recipe");

        // Dynamically style status message and colors depending on match state
        if (isAlmostThereMode) {
            int missingCount = dbHelper != null ? dbHelper.getMissingIngredientCount(recipe) : 0;

            if (missingCount == 1) {
                holder.tvIngredientCount.setText("Almost ready! Missing 1 ingredient");
            } else if (missingCount > 1) {
                holder.tvIngredientCount.setText("Almost ready! Missing " + missingCount + " ingredients");
            } else {
                holder.tvIngredientCount.setText("Ready to make!");
            }

            holder.tvIngredientCount.setTextColor(android.graphics.Color.parseColor("#FF9800")); // Orange
        } else {
            holder.tvIngredientCount.setText("Ready to make! All ingredients in pantry");
            holder.tvIngredientCount.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
        }

        // Delegate item click handling to detailed view launcher
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    // Refresh recipe list data in adapter
    public void updateData(List<Recipe> newRecipeList){
        if (newRecipeList != null) {
            this.recipeList = new ArrayList<>(newRecipeList);
        } else {
            this.recipeList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    // ViewHolder caching item text elements
    public static class RecipeViewHolder extends RecyclerView.ViewHolder{
        TextView tvRecipeName, tvIngredientCount;

        public RecipeViewHolder(@NonNull View itemView){
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvIngredientCount = itemView.findViewById(R.id.tvRecipeIngredientCount);
        }
    }
}
