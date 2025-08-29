package com.dogus.otomat.icecdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {
    private static final String TAG = "AdminPanel";
    
    // UI Components
    private TextView tvTitle;
    private Button btnMachineSettings, btnProductSettings;
    private Button btnPriceSettings, btnPaymentSettings;
    private Button btnAdvertisementSettings, btnSystemSettings;
    private Button btnFileManagement, btnLogSystem;
    private Button btnBackToMain;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        
        Log.i(TAG, "AdminPanelActivity onCreate started");
        
        initializeViews();
        setupClickListeners();
        
        Log.i(TAG, "AdminPanelActivity onCreate completed");
    }
    
    private void initializeViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnMachineSettings = findViewById(R.id.btn_machine_settings);
        btnProductSettings = findViewById(R.id.btn_product_settings);
        btnPriceSettings = findViewById(R.id.btn_price_settings);
        btnPaymentSettings = findViewById(R.id.btn_payment_settings);
        btnAdvertisementSettings = findViewById(R.id.btn_advertisement_settings);
        btnSystemSettings = findViewById(R.id.btn_system_settings);
        btnFileManagement = findViewById(R.id.btn_file_management);
        btnLogSystem = findViewById(R.id.btn_log_system);
        btnBackToMain = findViewById(R.id.btn_back_to_main);
        
        tvTitle.setText("Yönetici Paneli");
    }
    
    private void setupClickListeners() {
        btnMachineSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMachineSettings();
            }
        });
        
        btnProductSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductSettings();
            }
        });
        
        btnPriceSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPriceSettings();
            }
        });
        
        btnPaymentSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPaymentSettings();
            }
        });
        
        btnAdvertisementSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdvertisementSettings();
            }
        });
        
        btnSystemSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSystemSettings();
            }
        });
        
        btnFileManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileManagement();
            }
        });
        
        btnLogSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogSystem();
            }
        });
        
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });
    }
    
    private void openMachineSettings() {
        Intent intent = new Intent(AdminPanelActivity.this, MachineSettingsActivity.class);
        startActivity(intent);
    }
    
    private void openProductSettings() {
        Intent intent = new Intent(AdminPanelActivity.this, ProductSettingsActivity.class);
        startActivity(intent);
    }
    
    private void openPriceSettings() {
        Intent intent = new Intent(AdminPanelActivity.this, PriceSettingsActivity.class);
        startActivity(intent);
    }
    
    private void openPaymentSettings() {
        Intent intent = new Intent(AdminPanelActivity.this, PaymentSettingsActivity.class);
        startActivity(intent);
    }
    
    private void openAdvertisementSettings() {
        Intent intent = new Intent(AdminPanelActivity.this, AdvertisementSettingsActivity.class);
        startActivity(intent);
    }
    
    private void openSystemSettings() {
        Intent intent = new Intent(AdminPanelActivity.this, SystemSettingsActivity.class);
        startActivity(intent);
    }
    
    private void openFileManagement() {
        Intent intent = new Intent(AdminPanelActivity.this, FileManagementActivity.class);
        startActivity(intent);
    }
    
    private void openLogSystem() {
        Intent intent = new Intent(AdminPanelActivity.this, LogSystemActivity.class);
        startActivity(intent);
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "AdminPanelActivity onDestroy");
    }
}