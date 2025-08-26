package com.dogus.otomat.icecdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {

    private Button btnPasswordSettings;
    private Button btnPriceSettings;
    private Button btnMachineSettings;
    private Button btnTelemetrySettings;
    private Button btnSerialPortSettings;
    private Button btnRecipeManagement;
    private Button btnSauceToppingNames;
    private Button btnSalesData;
    private Button btnFileManagement;
    private Button btnLogSystem;
    private Button btnBackToSales;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnPasswordSettings = findViewById(R.id.btnPasswordSettings);
        btnPriceSettings = findViewById(R.id.btnPriceSettings);
        btnMachineSettings = findViewById(R.id.btnMachineSettings);
        btnTelemetrySettings = findViewById(R.id.btnTelemetrySettings);
        btnSerialPortSettings = findViewById(R.id.btnSerialPortSettings);
        btnRecipeManagement = findViewById(R.id.btnRecipeManagement);
        btnSauceToppingNames = findViewById(R.id.btnSauceToppingNames);
        btnSalesData = findViewById(R.id.btnSalesData);
        btnFileManagement = findViewById(R.id.btnFileManagement);
        btnLogSystem = findViewById(R.id.btnLogSystem);
        btnBackToSales = findViewById(R.id.btnBackToSales);
    }

    private void setupClickListeners() {
        btnPasswordSettings.setOnClickListener(v -> openPasswordSettings());
        btnPriceSettings.setOnClickListener(v -> openPriceSettings());
        btnMachineSettings.setOnClickListener(v -> openMachineParameters());
        btnTelemetrySettings.setOnClickListener(v -> openTelemetrySettings());
        btnSerialPortSettings.setOnClickListener(v -> openSerialPortSettings());
        btnRecipeManagement.setOnClickListener(v -> openRecipeManagement());
        btnSauceToppingNames.setOnClickListener(v -> openSauceToppingNames());
        btnSalesData.setOnClickListener(v -> openSalesData());
        btnFileManagement.setOnClickListener(v -> openFileManagement());
        btnLogSystem.setOnClickListener(v -> openLogSystem());
        btnBackToSales.setOnClickListener(v -> finish());
    }

    private void openPasswordSettings() {
        Intent intent = new Intent(this, PasswordSettingsActivity.class);
        startActivity(intent);
    }

    private void openPriceSettings() {
        Intent intent = new Intent(this, PriceSettingsActivity.class);
        startActivity(intent);
    }

    private void openMachineSettings() {
        Intent intent = new Intent(this, MachineSettingsActivity.class);
        startActivity(intent);
    }

    private void openTelemetrySettings() {
        Intent intent = new Intent(this, TelemetrySettingsActivity.class);
        startActivity(intent);
    }

    private void openMachineParameters() {
        Intent intent = new Intent(this, MachineParametersActivity.class);
        startActivity(intent);
    }

    private void openSerialPortSettings() {
        Intent intent = new Intent(this, SerialPortSettingsActivity.class);
        startActivity(intent);
    }

    private void openRecipeManagement() {
        Intent intent = new Intent(this, RecipeManagementActivity.class);
        startActivity(intent);
    }

    private void openSauceToppingNames() {
        Intent intent = new Intent(this, SauceToppingNamesActivity.class);
        startActivity(intent);
    }

    private void openSalesData() {
        Intent intent = new Intent(this, SalesDataActivity.class);
        startActivity(intent);
    }

    private void openFileManagement() {
        Intent intent = new Intent(this, FileManagementActivity.class);
        startActivity(intent);
    }

    private void openLogSystem() {
        Intent intent = new Intent(this, LogSystemActivity.class);
        startActivity(intent);
    }
}
