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

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>{
    private final Context context;
    private List<Recipe> recipeList;
    private final OnRecipeClickListener listener;
    private boolean isAlmostThereMode = false;
    private DatabaseHelper dbHelper;

    public interface OnRecipeClickListener{
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(Context context, List<Recipe> recipeList, OnRecipeClickListener listener){
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }

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

    public void updateData(List<Recipe> newRecipeList){
        if (newRecipeList != null) {
            this.recipeList = new ArrayList<>(newRecipeList);
        } else {
            this.recipeList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder{
        TextView tvRecipeName, tvIngredientCount;

        public RecipeViewHolder(@NonNull View itemView){
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvIngredientCount = itemView.findViewById(R.id.tvRecipeIngredientCount);
        }
    }
}
