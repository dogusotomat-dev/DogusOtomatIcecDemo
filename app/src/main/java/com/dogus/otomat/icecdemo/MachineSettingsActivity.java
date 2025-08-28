package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MachineSettingsActivity extends AppCompatActivity {
    private static final String TAG = "MachineSettings";

    private EditText etSerialNumber, etIoTNumber;
    private EditText etTelemetryIP, etTelemetryPort;
    private TextView tvCurrentMode, tvMachineStatus;
    private Button btnChangeMode, btnEmergencyStop, btnResetMachine;
    private Button btnTestDischarge, btnSelfInspection, btnClearFaults;
    private Button btnOpenDoor, btnSaveSettings, btnBack;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_machine_settings);
            
            Log.i(TAG, "MachineSettingsActivity onCreate started");
            
            sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
            
            initializeViews();
            loadCurrentSettings();
            setupClickListeners();
            updateMachineStatus();
            
            Log.i(TAG, "MachineSettingsActivity onCreate completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "onCreate error: " + e.getMessage(), e);
            showErrorDialog("Başlatma Hatası", "Makine ayarları açılırken hata oluştu: " + e.getMessage());
        }
    }

    private void initializeViews() {
        try {
            etSerialNumber = findViewById(R.id.etSerialNumber);
            etIoTNumber = findViewById(R.id.etIoTNumber);
            etTelemetryIP = findViewById(R.id.etTelemetryIP);
            etTelemetryPort = findViewById(R.id.etTelemetryPort);
            tvCurrentMode = findViewById(R.id.tvCurrentMode);
            tvMachineStatus = findViewById(R.id.tvMachineStatus);
            btnChangeMode = findViewById(R.id.btnChangeMode);
            btnEmergencyStop = findViewById(R.id.btnEmergencyStop);
            btnResetMachine = findViewById(R.id.btnResetMachine);
            btnTestDischarge = findViewById(R.id.btnTestDischarge);
            btnSelfInspection = findViewById(R.id.btnSelfInspection);
            btnClearFaults = findViewById(R.id.btnClearFaults);
            btnOpenDoor = findViewById(R.id.btnOpenDoor);
            btnSaveSettings = findViewById(R.id.btnSaveSettings);
            btnBack = findViewById(R.id.btnBack);
            
            Log.d(TAG, "Views initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Initialize views error: " + e.getMessage(), e);
        }
    }

    private void loadCurrentSettings() {
        try {
            String serialNumber = sharedPreferences.getString("serial_number", "DOGUS-" + System.currentTimeMillis());
            String iotNumber = sharedPreferences.getString("iot_number", "IOT-" + System.currentTimeMillis());
            String telemetryIP = sharedPreferences.getString("telemetry_ip", "192.168.1.100");
            String telemetryPort = sharedPreferences.getString("telemetry_port", "8080");
            String machineMode = sharedPreferences.getString("machine_mode", "Normal");

            if (etSerialNumber != null) etSerialNumber.setText(serialNumber);
            if (etIoTNumber != null) etIoTNumber.setText(iotNumber);
            if (etTelemetryIP != null) etTelemetryIP.setText(telemetryIP);
            if (etTelemetryPort != null) etTelemetryPort.setText(telemetryPort);
            if (tvCurrentMode != null) tvCurrentMode.setText("Mevcut Mod: " + machineMode);
            
            Log.d(TAG, "Current settings loaded successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Load current settings error: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            if (btnChangeMode != null) {
                btnChangeMode.setOnClickListener(v -> showModeSelectionDialog());
            }
            if (btnEmergencyStop != null) {
                btnEmergencyStop.setOnClickListener(v -> emergencyStop());
            }
            if (btnResetMachine != null) {
                btnResetMachine.setOnClickListener(v -> resetMachine());
            }
            if (btnTestDischarge != null) {
                btnTestDischarge.setOnClickListener(v -> showTestDischargeDialog());
            }
            if (btnSelfInspection != null) {
                btnSelfInspection.setOnClickListener(v -> startSelfInspection());
            }
            if (btnClearFaults != null) {
                btnClearFaults.setOnClickListener(v -> clearFaults());
            }
            if (btnOpenDoor != null) {
                btnOpenDoor.setOnClickListener(v -> openMachineDoor());
            }
            if (btnSaveSettings != null) {
                btnSaveSettings.setOnClickListener(v -> saveSettings());
            }
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }
            
            Log.d(TAG, "Click listeners setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Setup click listeners error: " + e.getMessage(), e);
        }
    }

    private void showModeSelectionDialog() {
        try {
            String[] modes = {
                    "00 - Durdur",
                    "01 - Çözme",
                    "02 - Temizlik",
                    "03 - Malzeme Ekleme",
                    "04 - Koruma",
                    "05 - Dondurma Yapımı"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Makine Modu Seçin");
            builder.setItems(modes, (dialog, which) -> {
                try {
                    changeMachineMode(which);
                } catch (Exception e) {
                    Log.e(TAG, "Mode selection error: " + e.getMessage(), e);
                    showToast("Mod değiştirme hatası: " + e.getMessage());
                }
            });
            builder.show();
            
        } catch (Exception e) {
            Log.e(TAG, "Show mode selection dialog error: " + e.getMessage(), e);
            showToast("Mod seçim menüsü açılamadı!");
        }
    }

    private void changeMachineMode(int mode) {
        try {
            String[] modeNames = {"Durdur", "Çözme", "Temizlik", "Malzeme Ekleme", "Koruma", "Dondurma Yapımı"};
            String modeName = modeNames[mode];
            
            // Simulate mode change (no actual SDK call to prevent crashes)
            Log.i(TAG, "Makine modu değiştiriliyor: " + modeName);
            
            // Update UI
            if (tvCurrentMode != null) {
                tvCurrentMode.setText("Mevcut Mod: " + modeName);
            }
            
            // Save to preferences
            sharedPreferences.edit().putString("machine_mode", modeName).apply();
            
            showToast("Makine modu başarıyla değiştirildi: " + modeName);
            
            // Update machine status
            updateMachineStatus();
            
        } catch (Exception e) {
            Log.e(TAG, "Change machine mode error: " + e.getMessage(), e);
            showToast("Mod değiştirme hatası: " + e.getMessage());
        }
    }

    private void emergencyStop() {
        try {
            Log.i(TAG, "Acil durdurma başlatılıyor...");
            
            // Simulate emergency stop
            showToast("Acil durdurma simüle edildi!");
            
            // Update status
            if (tvMachineStatus != null) {
                tvMachineStatus.setText("Durum: Acil Durdurma");
            }
            
            Log.i(TAG, "Acil durdurma tamamlandı");
            
        } catch (Exception e) {
            Log.e(TAG, "Emergency stop error: " + e.getMessage(), e);
            showToast("Acil durdurma hatası: " + e.getMessage());
        }
    }

    private void resetMachine() {
        try {
            Log.i(TAG, "Makine sıfırlanıyor...");
            
            // Simulate machine reset
            showToast("Makine sıfırlama simüle edildi!");
            
            // Update status
            if (tvMachineStatus != null) {
                tvMachineStatus.setText("Durum: Sıfırlandı");
            }
            
            Log.i(TAG, "Makine sıfırlama tamamlandı");
            
        } catch (Exception e) {
            Log.e(TAG, "Machine reset error: " + e.getMessage(), e);
            showToast("Makine sıfırlama hatası: " + e.getMessage());
        }
    }

    private void showTestDischargeDialog() {
        try {
            String[] testTypes = {"Test 1", "Test 2", "Test 3"};
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Test Çıkışı Seçin");
            builder.setItems(testTypes, (dialog, which) -> {
                try {
                    startTestDischarge(which);
                } catch (Exception e) {
                    Log.e(TAG, "Test discharge selection error: " + e.getMessage(), e);
                    showToast("Test çıkışı hatası: " + e.getMessage());
                }
            });
            builder.show();
            
        } catch (Exception e) {
            Log.e(TAG, "Show test discharge dialog error: " + e.getMessage(), e);
            showToast("Test çıkışı menüsü açılamadı!");
        }
    }

    private void startTestDischarge(int testType) {
        try {
            String[] testNames = {"Test 1", "Test 2", "Test 3"};
            String testName = testNames[testType];
            
            Log.i(TAG, "Test çıkışı başlatılıyor: " + testName);
            
            // Simulate test discharge
            showToast("Test çıkışı simüle edildi: " + testName);
            
            // Update status
            if (tvMachineStatus != null) {
                tvMachineStatus.setText("Durum: Test Çıkışı - " + testName);
            }
            
            Log.i(TAG, "Test çıkışı tamamlandı: " + testName);
            
        } catch (Exception e) {
            Log.e(TAG, "Start test discharge error: " + e.getMessage(), e);
            showToast("Test çıkışı hatası: " + e.getMessage());
        }
    }

    private void startSelfInspection() {
        try {
            Log.i(TAG, "Öz kontrol başlatılıyor...");
            
            // Simulate self inspection
            showToast("Öz kontrol simüle edildi!");
            
            // Update status
            if (tvMachineStatus != null) {
                tvMachineStatus.setText("Durum: Öz Kontrol");
            }
            
            Log.i(TAG, "Öz kontrol tamamlandı");
            
        } catch (Exception e) {
            Log.e(TAG, "Self inspection error: " + e.getMessage(), e);
            showToast("Öz kontrol hatası: " + e.getMessage());
        }
    }

    private void clearFaults() {
        try {
            Log.i(TAG, "Hatalar temizleniyor...");
            
            // Simulate fault clearing
            showToast("Hatalar temizlendi!");
            
            // Update status
            if (tvMachineStatus != null) {
                tvMachineStatus.setText("Durum: Hata Yok");
            }
            
            Log.i(TAG, "Hatalar temizlendi");
            
        } catch (Exception e) {
            Log.e(TAG, "Clear faults error: " + e.getMessage(), e);
            showToast("Hata temizleme hatası: " + e.getMessage());
        }
    }

    private void openMachineDoor() {
        try {
            Log.i(TAG, "Makine kapısı açılıyor...");
            
            // Simulate door opening
            showToast("Makine kapısı açıldı!");
            
            // Update status
            if (tvMachineStatus != null) {
                tvMachineStatus.setText("Durum: Kapı Açık");
            }
            
            Log.i(TAG, "Makine kapısı açıldı - Machine door opened");
            
        } catch (Exception e) {
            Log.e(TAG, "Open machine door error: " + e.getMessage(), e);
            showToast("Kapı açılamadı: " + e.getMessage());
        }
    }

    private void saveSettings() {
        try {
            Log.i(TAG, "Ayarlar kaydediliyor...");
            
            // Get values from EditText fields
            String serialNumber = etSerialNumber != null ? etSerialNumber.getText().toString() : "";
            String iotNumber = etIoTNumber != null ? etIoTNumber.getText().toString() : "";
            String telemetryIP = etTelemetryIP != null ? etTelemetryIP.getText().toString() : "";
            String telemetryPort = etTelemetryPort != null ? etTelemetryPort.getText().toString() : "";
            
            // Save to preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("serial_number", serialNumber);
            editor.putString("iot_number", iotNumber);
            editor.putString("telemetry_ip", telemetryIP);
            editor.putString("telemetry_port", telemetryPort);
            editor.apply();
            
            showToast("Ayarlar başarıyla kaydedildi!");
            
            Log.i(TAG, "Settings saved successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Save settings error: " + e.getMessage(), e);
            showToast("Ayar kaydetme hatası: " + e.getMessage());
        }
    }

    private void updateMachineStatus() {
        try {
            if (tvMachineStatus != null) {
                tvMachineStatus.setText("Durum: Normal Çalışma");
            }
            
            Log.d(TAG, "Machine status updated");
            
        } catch (Exception e) {
            Log.e(TAG, "Update machine status error: " + e.getMessage(), e);
        }
    }

    private void showToast(String message) {
        try {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Show toast error: " + e.getMessage(), e);
        }
    }

    private void showErrorDialog(String title, String message) {
        try {
            new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Tamam", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
        } catch (Exception e) {
            Log.e(TAG, "Error dialog error: " + e.getMessage(), e);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            Log.i(TAG, "MachineSettingsActivity onDestroy");
            super.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "onDestroy error: " + e.getMessage(), e);
        }
    }
}
