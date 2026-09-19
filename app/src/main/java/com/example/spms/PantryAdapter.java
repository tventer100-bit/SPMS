package com.example.spms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {
    private final Context context;
    private List<pantryItem> pantryList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onEditClick(pantryItem item);
        void onDeleteClick(pantryItem item);
    }

    public PantryAdapter(Context context, List<pantryItem> pantryList, OnItemClickListener listener){
        this.context = context;
        this.pantryList = pantryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position){
        pantryItem item = pantryList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvQuantity.setText(item.getQuantity() + " " + item.getUnit());

        if(item.getExpiry() != null && !item.getExpiry().trim().isEmpty()){
            holder.tvExpiry.setText("Expires: " + item.getExpiry());
            holder.tvExpiry.setVisibility(View.VISIBLE);
        }else{
            holder.tvExpiry.setVisibility(View.GONE);
        }
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount(){
        return pantryList != null ? pantryList.size() : 0;
    }

    public void updateData(List<pantryItem> newPantryList){
        this.pantryList = newPantryList;
        notifyDataSetChanged();
    }


    public static class PantryViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvQuantity, tvExpiry;
        ImageButton btnEdit, btnDelete;

        public PantryViewHolder(@NonNull View itemView){
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPantryItemName);
            tvQuantity = itemView.findViewById(R.id.tvPantryItemQuantity);
            tvExpiry = itemView.findViewById(R.id.tvPantryItemExpiry);
            btnEdit = itemView.findViewById(R.id.btnEditPantryItem);
            btnDelete = itemView.findViewById(R.id.btnDeletePantryItem);
        }
    }

}
