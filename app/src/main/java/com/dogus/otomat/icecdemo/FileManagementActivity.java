package com.dogus.otomat.icecdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FileManagementActivity extends AppCompatActivity {
    private static final String TAG = "FileManagement";
    
    // UI Components
    private TextView tvTitle;
    private Button btnBack, btnSave;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_management);
        
        Log.i(TAG, "FileManagementActivity onCreate started");
        
        initializeViews();
        setupClickListeners();
        
        Log.i(TAG, "FileManagementActivity onCreate completed");
    }
    
    private void initializeViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        
        tvTitle.setText("Dosya Yönetimi");
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });
        
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }
    
    private void saveSettings() {
        // In a real implementation, this would save file management settings
        showToast("Dosya yönetimi ayarları kaydedildi");
        finish();
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "FileManagementActivity onDestroy");
    }
}