package com.example.spms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditPantryActivity extends AppCompatActivity {
    private EditText edtName, edtQuantity, edtExpiry;
    private Spinner spinnerUnit;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    private int pantry_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_pantry);

        dbHelper = new DatabaseHelper(this);
        edtName = findViewById(R.id.edtIngredientName);
        edtQuantity = findViewById(R.id.edtIngredientQuantity);
        edtExpiry = findViewById(R.id.edtIngredientExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        btnSave = findViewById(R.id.btnSaveIngredient);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.unit_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);

        Intent intent = getIntent();
        if(intent.hasExtra("EXTRA_PANTRY_ID")){
            pantry_ID = intent.getIntExtra("Extra_Pantry_ID", -1);
            edtName.setText(intent.getStringExtra("EXTRA_PANTRY_NAME"));
            edtQuantity.setText(String.valueOf(intent.getDoubleExtra("EXTRA_PANTRY_QUANTITY", 1.0)));
            edtExpiry.setText(intent.getStringExtra("EXTRA_PANTRY_EXPIRY"));

            String unit = intent.getStringExtra("EXTRA_PANTRY_UNIT");
            if(unit != null){
                int spinnerPosition = adapter.getPosition(unit);
                spinnerUnit.setSelection(spinnerPosition);
            }
            setTitle("Edit Ingredient");
            btnSave.setText("Update Ingredient")
        }else{
            setTitle("Add Ingredient");
            btnSave.setText("Save Ingredient");
        }
        btnSave.setOnClickListener(v -> savePantryItem());
    }
    private void savePantryItem() {
        String name = edtName.getText().toString().trim();
        String qtyStr = edtQuantity.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = edtExpiry.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            edtName.setError("Ingredient name is required");
            edtName.requestFocus();
            return;
        }

        if (qtyStr.isEmpty()) {
            edtQuantity.setError("Quantity is required");
            edtQuantity.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
            if (quantity <= 0) {
                edtQuantity.setError("Quantity must be greater than zero");
                edtQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtQuantity.setError("Enter a valid number");
            edtQuantity.requestFocus();
            return;
        }

        if (pantry_ID == -1) {
            pantryItem newItem = new pantryItem(name, quantity, unit, expiry);
            long id = dbHelper.addPantryItem(newItem);
            if (id != -1) {
                Toast.makeText(this, "Ingredient added successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to add ingredient", Toast.LENGTH_SHORT).show();
            }
        } else {
            pantryItem updatedItem = new pantryItem(pantry_ID, name, quantity, unit, expiry);
            int rowsAffected = dbHelper.updatePantryItem(updatedItem);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Ingredient updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update ingredient", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
