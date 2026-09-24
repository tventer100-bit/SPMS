package com.example.spms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {
    private SwitchCompat switchExpiryAlerts;
    private Spinner spinnerDefaultUnit;
    private BottomNavigationView bottomNav;
    private SharedPreferences preferences;

    private static final String PREFS_NAME = "SmartPamtryPrefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_DEFAULT_UNIT = "defalt_unit";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        spinnerDefaultUnit = findViewById(R.id.spinnerDefaultUnit);
        bottomNav = findViewById(R.id.bottomNavigationSettings);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.unit_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDefaultUnit.setAdapter(adapter);

        // Load the Saved preferences
        boolean alertEnabled = preferences.getBoolean(KEY_EXPIRY_ALERTS, true);
        String defaultUnit = preferences.getString(KEY_DEFAULT_UNIT, "pcs");

        switchExpiryAlerts.setChecked(alertEnabled);
        int position = adapter.getPosition(defaultUnit);
        if(position >= 0){
            spinnerDefaultUnit.setSelection(position);
        }

        // Save switch preference
        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply();
        });

        bottomNav.setSelectedItemId(R.id.nav_settings);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.nav_pantry){
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
                return true;
            }else if(itemId == R.id.nav_suggestions){
                startActivity(new Intent(SettingsActivity.this, SuggestedRecipeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true;
            }
            return false;
            });
    }
}
