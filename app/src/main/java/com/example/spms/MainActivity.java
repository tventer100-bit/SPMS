package com.example.spms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PantryAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private PantryAdapter adapter;
    private List<pantryItem> pantryList;
    private DatabaseHelper dbHelper;
    private TextView tvEmptyState;
    private FloatingActionButton fabAdd;
    private BottomNavigationView bottomNav;

    private final ActivityResultLauncher<Intent> addEditLauncher = rgisterForActivityResult(
         new ActivityResultContract.StartActivityForResult(), result -> {
             if(result.getResultCode() == RESULT_OK){
                 loadPantryItems();
             }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recycleViewPantry);
        tvEmptySate = findViewById(R.id.tvEmptyPantryState);
        fabAdd = findViewById(R.id.fabAddPantryItem);
        bottomNav = findViewById(R.id.bottomNavigation);

        pantryList = new ArrayList<>();
        adapter = new PantryAdapter(this, pantryList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditPantryActivity.class);
            addEditLauncher.launch(intent);
        } );

        // Navagation between Screens
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemID = item.getItemId();
            if(itemID == R.id.nav_pantry){
                return true;
            }else if(itemID == R.id.nav_suggestions){
                startActivity(new Intent(MainActivity.this, suggestedRecipesActivity.class));
                return true;
            }else if(itemID == R.id.nav_settings){
                startActivity((new Intent(MainActivity.this, SettingsActivity.class)));
                return true;
            }
            return false;
        });

        loadPantryItems();
    }
    @Override
    protected void onResume(){
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        loadPantryItems();
    }

    private void loadPantryItems(){
        pantryList = dbHelper.getAllPantryItems();
        adapter.updateData(pantryList);

        if(pantryList.isEmpty()){
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditClick(pantryItem item) {
        Intent intent = new Intent(MainActivity.this, AddEditPantryActivity.class);
        intent.putExtra("EXTRA_PANTRY_ID", item.getPantry_ID());
        intent.putExtra("EXTRA_PANTRY_NAME", item.getName());
        intent.putExtra("EXTRA_PANTRY_Quantity", item.getQuantity());
        intent.putExtra("EXTRA_PANTRY_Unit", item.getUnit());
        intent.putExtra("EXTRA_PANTRY_Expiry", item.getExpiry());
        addEditLauncher.launch(intent);
    }

    @Override
    public void onDeleteClick(pantryItem item) {
        new AlertDialog.Builder(this).setTitle("Delete Ingredients").setMessage("Are you sure you want to delete " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getPantry_ID());
                    Toast.makeText(MainActivity.this, item.getName() + " removed", Toast.LENGTH_SHORT).show();
                    loadPantryItems();
                }).setNegativeButton("Cancel", null).show();
    }
}
