package com.dogus.otomat.icecdemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Yönetici Giriş Ekranı
 * Yetkili ve Dolum giriş seçenekleri
 */
public class AdminLoginActivity extends AppCompatActivity {

    private static final String TAG = "AdminLoginActivity";
    private static final String DEFAULT_PASSWORD = "Dogusotomat.12";
    private static final String PREFS_NAME = "AdminPrefs";
    private static final String KEY_ADMIN_PASSWORD = "admin_password";
    private static final String KEY_DOLUM_PASSWORD = "dolum_password";

    // UI bileşenleri
    private EditText etAdminPassword, etDolumPassword;
    private Button btnAdminLogin, btnDolumLogin, btnBackToSales;

    // SharedPreferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        initView();
        setupClickListeners();
        loadPasswords();
    }

    private void initView() {
        etAdminPassword = findViewById(R.id.et_admin_password);
        etDolumPassword = findViewById(R.id.et_dolum_password);
        btnAdminLogin = findViewById(R.id.btn_admin_login);
        btnDolumLogin = findViewById(R.id.btn_dolum_login);
        btnBackToSales = findViewById(R.id.btn_back_to_sales);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void setupClickListeners() {
        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAdminPassword();
            }
        });

        btnDolumLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDolumPassword();
            }
        });

        btnBackToSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadPasswords() {
        // Eğer şifreler kaydedilmemişse default şifreleri kullan
        String adminPass = sharedPreferences.getString(KEY_ADMIN_PASSWORD, DEFAULT_PASSWORD);
        String dolumPass = sharedPreferences.getString(KEY_DOLUM_PASSWORD, DEFAULT_PASSWORD);

        // Şifreleri kaydet (ilk kez)
        if (!sharedPreferences.contains(KEY_ADMIN_PASSWORD)) {
            sharedPreferences.edit()
                .putString(KEY_ADMIN_PASSWORD, adminPass)
                .putString(KEY_DOLUM_PASSWORD, dolumPass)
                .apply();
        }
    }

    private void checkAdminPassword() {
        String inputPassword = etAdminPassword.getText().toString().trim();
        String savedPassword = sharedPreferences.getString(KEY_ADMIN_PASSWORD, DEFAULT_PASSWORD);

        if (inputPassword.isEmpty()) {
            showMessage("Uyarı", "Lütfen şifre girin");
            return;
        }

        if (inputPassword.equals(savedPassword)) {
            // Başarılı giriş - Admin panelini aç
            Intent intent = new Intent(this, AdminPanelActivity.class);
            startActivity(intent);
            finish();
        } else {
            showMessage("Hata", "Yanlış şifre!");
            etAdminPassword.setText("");
        }
    }

    private void checkDolumPassword() {
        String inputPassword = etDolumPassword.getText().toString().trim();
        String savedPassword = sharedPreferences.getString(KEY_DOLUM_PASSWORD, DEFAULT_PASSWORD);

        if (inputPassword.isEmpty()) {
            showMessage("Uyarı", "Lütfen şifre girin");
            return;
        }

        if (inputPassword.equals(savedPassword)) {
            // Başarılı giriş - Dolum panelini aç
            Intent intent = new Intent(this, DolumPanelActivity.class);
            startActivity(intent);
            finish();
        } else {
            showMessage("Hata", "Yanlış şifre!");
            etDolumPassword.setText("");
        }
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Tamam", null)
            .show();
    }

    @Override
    public void onBackPressed() {
        // Geri tuşuna basıldığında satış ekranına dön
        finish();
    }
}
