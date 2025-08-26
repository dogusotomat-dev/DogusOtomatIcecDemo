package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TelemetrySettingsActivity extends AppCompatActivity {

    private EditText etTelemetryIP, etTelemetryPort;
    private Button btnSaveTelemetry, btnBack;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemetry_settings);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        initializeViews();
        loadCurrentSettings();
        setupClickListeners();
    }

    private void initializeViews() {
        etTelemetryIP = findViewById(R.id.etTelemetryIP);
        etTelemetryPort = findViewById(R.id.etTelemetryPort);
        btnSaveTelemetry = findViewById(R.id.btnSaveTelemetry);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadCurrentSettings() {
        String telemetryIP = sharedPreferences.getString("telemetry_ip", "192.168.1.100");
        String telemetryPort = sharedPreferences.getString("telemetry_port", "8080");
        etTelemetryIP.setText(telemetryIP);
        etTelemetryPort.setText(telemetryPort);
    }

    private void setupClickListeners() {
        btnSaveTelemetry.setOnClickListener(v -> saveSettings());
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveSettings() {
        String telemetryIP = etTelemetryIP.getText().toString();
        String telemetryPort = etTelemetryPort.getText().toString();

        if (telemetryIP.isEmpty() || telemetryPort.isEmpty()) {
            showToast("IP ve Port alanları doldurulmalı!");
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("telemetry_ip", telemetryIP);
        editor.putString("telemetry_port", telemetryPort);
        editor.apply();

        showToast("Telemetri ayarları kaydedildi!");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
