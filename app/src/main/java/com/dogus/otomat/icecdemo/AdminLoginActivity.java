package com.dogus.otomat.icecdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {
    private static final String TAG = "AdminLogin";
    
    // UI Components
    private EditText etUsername, etPassword;
    private Button btnLogin, btnBack;
    private TextView tvTitle;
    
    // Shared preferences for storing admin credentials
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AdminCredentials";
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        
        Log.i(TAG, "AdminLoginActivity onCreate started");
        
        initializeViews();
        initializeSharedPreferences();
        setupClickListeners();
        
        Log.i(TAG, "AdminLoginActivity onCreate completed");
    }
    
    private void initializeViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_title);
        
        tvTitle.setText("Yönetici Girişi");
    }
    
    private void initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }
    
    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });
    }
    
    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showToast("Lütfen tüm alanları doldurun");
            return;
        }
        
        // Check credentials
        if (validateCredentials(username, password)) {
            showToast("Giriş başarılı!");
            openAdminPanel();
        } else {
            showToast("Geçersiz kullanıcı adı veya şifre");
        }
    }
    
    private boolean validateCredentials(String username, String password) {
        // For demo purposes, using hardcoded credentials
        // In production, this would check against stored credentials
        return DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password);
    }
    
    private void openAdminPanel() {
        Intent intent = new Intent(AdminLoginActivity.this, AdminPanelActivity.class);
        startActivity(intent);
        finish(); // Close login activity
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "AdminLoginActivity onDestroy");
    }
}