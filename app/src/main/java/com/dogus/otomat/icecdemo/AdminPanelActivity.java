package com.dogus.otomat.icecdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {
    private static final String TAG = "AdminPanel";

    private Button btnPasswordSettings;
    private Button btnProductSettings;
    private Button btnMachineSettings;
    private Button btnSystemSettings;
    private Button btnAdvertisementSettings;
    private Button btnBackToSales;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_admin_panel);

            Log.i(TAG, "AdminPanelActivity onCreate started");

            sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
            initializeViews();
            setupClickListeners();

            Log.i(TAG, "AdminPanelActivity onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "onCreate error: " + e.getMessage(), e);
            finish();
        }
    }

    private void initializeViews() {
        try {
            btnPasswordSettings = findViewById(R.id.btnPasswordSettings);
            btnProductSettings = findViewById(R.id.btnProductSettings);
            btnMachineSettings = findViewById(R.id.btnMachineSettings);
            btnSystemSettings = findViewById(R.id.btnSystemSettings);
            btnAdvertisementSettings = findViewById(R.id.btnAdvertisementSettings);
            btnBackToSales = findViewById(R.id.btnBackToSales);

            Log.d(TAG, "Views initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "Initialize views error: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            if (btnPasswordSettings != null) {
                btnPasswordSettings.setOnClickListener(v -> openPasswordSettings());
            }
            if (btnProductSettings != null) {
                btnProductSettings.setOnClickListener(v -> openProductSettings());
            }
            if (btnMachineSettings != null) {
                btnMachineSettings.setOnClickListener(v -> openMachineSettings());
            }
            if (btnSystemSettings != null) {
                btnSystemSettings.setOnClickListener(v -> openSystemSettings());
            }
            if (btnAdvertisementSettings != null) {
                btnAdvertisementSettings.setOnClickListener(v -> openAdvertisementSettings());
            }
            if (btnBackToSales != null) {
                btnBackToSales.setOnClickListener(v -> finish());
            }

            Log.d(TAG, "Click listeners setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Setup click listeners error: " + e.getMessage(), e);
        }
    }

    private void openPasswordSettings() {
        try {
            Intent intent = new Intent(this, PasswordSettingsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Open password settings error: " + e.getMessage(), e);
        }
    }

    private void openProductSettings() {
        try {
            Intent intent = new Intent(this, ProductSettingsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Open product settings error: " + e.getMessage(), e);
        }
    }

    private void openMachineSettings() {
        try {
            Intent intent = new Intent(this, MachineSettingsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Open machine settings error: " + e.getMessage(), e);
        }
    }

    private void openSystemSettings() {
        try {
            Intent intent = new Intent(this, SystemSettingsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Open system settings error: " + e.getMessage(), e);
        }
    }

    private void openAdvertisementSettings() {
        try {
            Intent intent = new Intent(this, AdvertisementSettingsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Open advertisement settings error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            Log.i(TAG, "AdminPanelActivity onDestroy");
            super.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "onDestroy error: " + e.getMessage(), e);
        }
    }
}
