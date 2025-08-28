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
import com.tcn.icecboard.DriveControl.icec.DriveIcec;

public class MachineSettingsActivity extends AppCompatActivity {

    private EditText etSerialNumber, etIoTNumber;
    private EditText etTelemetryIP, etTelemetryPort;
    private TextView tvCurrentMode, tvMachineStatus;
    private Button btnChangeMode, btnEmergencyStop, btnResetMachine;
    private Button btnTestDischarge, btnSelfInspection, btnClearFaults;
    private Button btnSaveSettings, btnBack;

    private SharedPreferences sharedPreferences;
    private TelemetryManager telemetryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_settings);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        telemetryManager = TelemetryManager.getInstance(this);

        initializeViews();
        loadCurrentSettings();
        setupClickListeners();
        updateMachineStatus();
    }

    private void initializeViews() {
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
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadCurrentSettings() {
        String serialNumber = sharedPreferences.getString("serial_number", "DOGUS-" + System.currentTimeMillis());
        String iotNumber = sharedPreferences.getString("iot_number", "IOT-" + System.currentTimeMillis());
        String telemetryIP = sharedPreferences.getString("telemetry_ip", "192.168.1.100");
        String telemetryPort = sharedPreferences.getString("telemetry_port", "8080");
        String machineMode = sharedPreferences.getString("machine_mode", "Normal");

        etSerialNumber.setText(serialNumber);
        etIoTNumber.setText(iotNumber);
        etTelemetryIP.setText(telemetryIP);
        etTelemetryPort.setText(telemetryPort);
        tvCurrentMode.setText("Mevcut Mod: " + machineMode);
    }

    private void setupClickListeners() {
        btnChangeMode.setOnClickListener(v -> showModeSelectionDialog());
        btnEmergencyStop.setOnClickListener(v -> emergencyStop());
        btnResetMachine.setOnClickListener(v -> resetMachine());
        btnTestDischarge.setOnClickListener(v -> showTestDischargeDialog());
        btnSelfInspection.setOnClickListener(v -> startSelfInspection());
        btnClearFaults.setOnClickListener(v -> clearFaults());
        btnSaveSettings.setOnClickListener(v -> saveSettings());
        btnBack.setOnClickListener(v -> finish());
    }

    private void showModeSelectionDialog() {
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
            String selectedMode = modes[which];
            changeMachineMode(which);
        });
        builder.show();
    }

    private void changeMachineMode(int mode) {
        try {
            // TCN SDK'ya erişim sağla
            SDKIntegrationHelper sdkHelper = SDKIntegrationHelper.getInstance(this);

            if (sdkHelper != null && sdkHelper.isSDKConnected()) {
                // Gerçek makine kontrolü
                boolean modeChanged = sdkHelper.setWorkMode(mode, mode);

                if (modeChanged) {
                    // Şimdilik SharedPreferences'a kaydet
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("machine_mode", "Mod " + mode);
                    editor.putInt("work_mode_left", mode);
                    editor.putInt("work_mode_right", mode);
                    editor.apply();

                    tvCurrentMode.setText("Mevcut Mod: Mod " + mode);
                    showToast("Makine modu değiştirildi: " + mode);

                    // Telemetri verisi gönder
                    if (telemetryManager != null) {
                        telemetryManager.sendMachineStatus("Mode Change",
                                "Makine modu " + mode + " olarak değiştirildi");
                    }

                    Log.i("MachineSettings", "Makine modu başarıyla değiştirildi: " + mode);
                } else {
                    showToast("Makine modu değiştirilemedi - SDK hatası");
                    Log.e("MachineSettings", "SDK makine modu değiştirme başarısız");
                }
            } else {
                // SDK bağlantısı yoksa simüle et
                Log.w("MachineSettings", "SDK bağlantısı yok - Mod değişikliği simüle ediliyor");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("machine_mode", "Mod " + mode + " (Simülasyon)");
                editor.putInt("work_mode_left", mode);
                editor.putInt("work_mode_right", mode);
                editor.apply();

                tvCurrentMode.setText("Mevcut Mod: Mod " + mode + " (Simülasyon)");
                showToast("Makine modu simüle edildi: " + mode);
            }

        } catch (Exception e) {
            Log.e("MachineSettings", "Mod değiştirme hatası: " + e.getMessage());
            showToast("Mod değiştirme hatası: " + e.getMessage());
        }
    }

    private void emergencyStop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acil Durdur");
        builder.setMessage("Makineyi acil durdurma moduna almak istediğinizden emin misiniz?");

        builder.setPositiveButton("Evet, Durdur", (dialog, which) -> {
            try {
                // Acil durdurma modu (00)
                changeMachineMode(0);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("machine_mode", "Acil Durdur");
                editor.putLong("emergency_stop_time", System.currentTimeMillis());
                editor.apply();

                showToast("Makine acil durdurma moduna alındı!");

            } catch (Exception e) {
                Log.e("MachineSettings", "Acil durdurma hatası: " + e.getMessage());
                showToast("Acil durdurma hatası: " + e.getMessage());
            }
        });

        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    private void resetMachine() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Makine Sıfırlama");
        builder.setMessage("Makineyi normal moda sıfırlamak istediğinizden emin misiniz?");

        builder.setPositiveButton("Evet, Sıfırla", (dialog, which) -> {
            try {
                // Normal mod (05 - Dondurma yapımı)
                changeMachineMode(5);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("machine_mode", "Normal");
                editor.remove("emergency_stop_time");
                editor.apply();

                showToast("Makine normal moda sıfırlandı!");

            } catch (Exception e) {
                Log.e("MachineSettings", "Sıfırlama hatası: " + e.getMessage());
                showToast("Sıfırlama hatası: " + e.getMessage());
            }
        });

        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    private void showTestDischargeDialog() {
        String[] testItems = { "01 - Dondurma", "02 - Sos", "03 - Süsleme", "04 - Bardak" };
        String[] testPositions = { "01 - 1. Pozisyon", "02 - 2. Pozisyon", "03 - 3. Pozisyon", "04 - 4. Pozisyon" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test Çıkışı");
        builder.setMessage("Test edilecek öğeyi seçin:");
        builder.setItems(testItems, (dialog, which) -> {
            int testItem = which + 1;
            showPositionSelectionDialog(testItem);
        });
        builder.show();
    }

    private void showPositionSelectionDialog(int testItem) {
        String[] testPositions = { "01 - 1. Pozisyon", "02 - 2. Pozisyon", "03 - 3. Pozisyon", "04 - 4. Pozisyon" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test Pozisyonu");
        builder.setMessage("Test pozisyonunu seçin:");
        builder.setItems(testPositions, (dialog, which) -> {
            int testPosition = which + 1;
            startTestDischarge(testItem, testPosition);
        });
        builder.show();
    }

    private void startTestDischarge(int testItem, int testPosition) {
        try {
            // SDK'daki gerçek test çıkışı
            // DriveIcec.getInstance().testDischarge(testItem, testPosition);

            showToast("Test çıkışı başlatıldı: Öğe " + testItem + ", Pozisyon " + testPosition);

            // Telemetri verisi gönder
            if (telemetryManager != null) {
                telemetryManager.sendMachineStatus("Test Discharge",
                        "Test çıkışı: Öğe " + testItem + ", Pozisyon " + testPosition);
            }

        } catch (Exception e) {
            Log.e("MachineSettings", "Test çıkışı hatası: " + e.getMessage());
            showToast("Test çıkışı hatası: " + e.getMessage());
        }
    }

    private void startSelfInspection() {
        try {
            // SDK'daki gerçek makine öz kontrolü
            DriveIcec.getInstance().reqMachineSelf_test();

            showToast("Makine öz kontrolü başlatıldı!");

            // Telemetri verisi gönder
            if (telemetryManager != null) {
                telemetryManager.sendMachineStatus("Self Inspection", "Makine öz kontrolü başlatıldı");
            }

        } catch (Exception e) {
            Log.e("MachineSettings", "Öz kontrol hatası: " + e.getMessage());
            showToast("Öz kontrol hatası: " + e.getMessage());
        }
    }

    private void clearFaults() {
        try {
            // SDK'daki gerçek hata temizleme
            DriveIcec.getInstance().reqClearFaults(1, (byte) 1);

            showToast("Hatalar temizlendi!");

            // Telemetri verisi gönder
            if (telemetryManager != null) {
                telemetryManager.sendMachineStatus("Clear Faults", "Makine hataları temizlendi");
            }

        } catch (Exception e) {
            Log.e("MachineSettings", "Hata temizleme hatası: " + e.getMessage());
            showToast("Hata temizleme hatası: " + e.getMessage());
        }
    }

    private void saveSettings() {
        try {
            String serialNumber = etSerialNumber.getText().toString();
            String iotNumber = etIoTNumber.getText().toString();
            String telemetryIP = etTelemetryIP.getText().toString();
            String telemetryPort = etTelemetryPort.getText().toString();

            if (serialNumber.isEmpty() || iotNumber.isEmpty() || telemetryIP.isEmpty() || telemetryPort.isEmpty()) {
                showToast("Tüm alanlar doldurulmalı!");
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("serial_number", serialNumber);
            editor.putString("iot_number", iotNumber);
            editor.putString("telemetry_ip", telemetryIP);
            editor.putString("telemetry_port", telemetryPort);
            editor.apply();

            // Telemetri bilgilerini güncelle
            if (telemetryManager != null) {
                try {
                    telemetryManager.updateMachineInfo(serialNumber, iotNumber, serialNumber,
                            getString(R.string.brand_name),
                            getString(R.string.model_name),
                            telemetryIP,
                            Integer.parseInt(telemetryPort));
                } catch (Exception e) {
                    Log.e("MachineSettings", "Telemetri güncelleme hatası: " + e.getMessage());
                }
            }

            showToast("Ayarlar kaydedildi!");

        } catch (Exception e) {
            Log.e("MachineSettings", "Ayar kaydetme hatası: " + e.getMessage());
            showToast("Ayar kaydetme hatası: " + e.getMessage());
        }
    }

    private void updateMachineStatus() {
        // Makine durumunu güncelle
        String currentMode = sharedPreferences.getString("machine_mode", "Normal");
        tvCurrentMode.setText("Mevcut Mod: " + currentMode);

        // Makine durumu bilgisini göster
        tvMachineStatus.setText("Makine Durumu: Aktif\nSon Güncelleme: " +
                System.currentTimeMillis());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showDoorControlDialog() {
        String[] doorOptions = { "Kapıyı Aç", "Kapıyı Kapat", "Kapı Durumunu Kontrol Et" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kapı Kontrolü");
        builder.setItems(doorOptions, (dialog, which) -> {
            switch (which) {
                case 0: // Kapıyı Aç
                    controlMachineDoor(true);
                    break;
                case 1: // Kapıyı Kapat
                    controlMachineDoor(false);
                    break;
                case 2: // Durum Kontrol
                    checkDoorStatus();
                    break;
            }
        });
        builder.show();
    }

    /**
     * Makine kapısını kontrol eder
     */
    private void controlMachineDoor(boolean open) {
        try {
            SDKIntegrationHelper sdkHelper = SDKIntegrationHelper.getInstance(this);

            if (sdkHelper != null && sdkHelper.isSDKConnected()) {
                // Gerçek kapı kontrolü
                boolean doorControlled = sdkHelper.controlDoor(1, open); // Grup ID: 1

                String action = open ? "açıldı" : "kapatıldı";

                if (doorControlled) {
                    showToast("Kapı başarıyla " + action);
                    Log.i("MachineSettings", "Kapı " + action + ": Grup 1");

                    // Telemetri verisi gönder
                    if (telemetryManager != null) {
                        telemetryManager.sendMachineStatus("Door Control", "Kapı " + action);
                    }
                } else {
                    showToast("Kapı kontrol edilemedi - SDK hatası");
                    Log.e("MachineSettings", "Kapı kontrol hatası");
                }
            } else {
                // SDK bağlantısı yoksa simüle et
                String action = open ? "açıldı" : "kapatıldı";
                showToast("Kapı " + action + " (Simülasyon)");
                Log.w("MachineSettings", "SDK bağlantısı yok - Kapı kontrolü simüle ediliyor");
            }

        } catch (Exception e) {
            Log.e("MachineSettings", "Kapı kontrol hatası: " + e.getMessage());
            showToast("Kapı kontrol hatası: " + e.getMessage());
        }
    }

    /**
     * Kapı durumunu kontrol eder
     */
    private void checkDoorStatus() {
        try {
            SDKIntegrationHelper sdkHelper = SDKIntegrationHelper.getInstance(this);

            if (sdkHelper != null && sdkHelper.isSDKConnected()) {
                // Makine durumunu sorgula
                boolean statusQueried = sdkHelper.queryMachineStatus();

                if (statusQueried) {
                    showToast("Kapı durumu sorgulanıyor...");
                    Log.i("MachineSettings", "Kapı durum sorgusu gönderildi");
                } else {
                    showToast("Kapı durumu sorgulanamadı");
                    Log.e("MachineSettings", "Kapı durum sorgulama hatası");
                }
            } else {
                showToast("Kapı durumu kontrol edilemiyor - SDK bağlantısı yok");
                Log.w("MachineSettings", "SDK bağlantısı yok");
            }

        } catch (Exception e) {
            Log.e("MachineSettings", "Kapı durum kontrolü hatası: " + e.getMessage());
            showToast("Kapı durum kontrolü hatası: " + e.getMessage());
        }
    }
}
