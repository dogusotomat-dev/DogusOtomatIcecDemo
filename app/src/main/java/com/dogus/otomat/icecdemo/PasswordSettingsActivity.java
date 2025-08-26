package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PasswordSettingsActivity extends AppCompatActivity {

    private EditText etAdminPassword, etDolumPassword;
    private Button btnSavePasswords, btnBack;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_settings);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        initializeViews();
        loadCurrentPasswords();
        setupClickListeners();
    }

    private void initializeViews() {
        etAdminPassword = findViewById(R.id.etAdminPassword);
        etDolumPassword = findViewById(R.id.etDolumPassword);
        btnSavePasswords = findViewById(R.id.btnSavePasswords);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadCurrentPasswords() {
        String adminPass = sharedPreferences.getString("admin_password", "Dogusotomat.12");
        String dolumPass = sharedPreferences.getString("dolum_password", "Dogusotomat.12");
        etAdminPassword.setText(adminPass);
        etDolumPassword.setText(dolumPass);
    }

    private void setupClickListeners() {
        btnSavePasswords.setOnClickListener(v -> savePasswords());
        btnBack.setOnClickListener(v -> finish());
    }

    private void savePasswords() {
        String adminPass = etAdminPassword.getText().toString();
        String dolumPass = etDolumPassword.getText().toString();

        if (adminPass.isEmpty() || dolumPass.isEmpty()) {
            showToast("Şifreler boş olamaz!");
            return;
        }

        if (adminPass.length() < 6 || dolumPass.length() < 6) {
            showToast("Şifreler en az 6 karakter olmalı!");
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("admin_password", adminPass);
        editor.putString("dolum_password", dolumPass);
        editor.apply();

        showToast("Şifreler kaydedildi!");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
