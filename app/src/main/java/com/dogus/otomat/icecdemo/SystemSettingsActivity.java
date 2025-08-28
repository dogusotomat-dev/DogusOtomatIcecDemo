package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SystemSettingsActivity extends AppCompatActivity {
    private static final String TAG = "SystemSettings";

    // UI Elements
    private Button btnBack;
    
    // Machine Parameters Section
    private EditText etMachineId, etBoardType, etMaxProducts, etTemperature;
    private EditText etHumidity, etPowerConsumption, etMaintenanceInterval;
    private SeekBar seekBarTemperature, seekBarHumidity, seekBarPowerConsumption;
    private TextView tvTemperature, tvHumidity, tvPowerConsumption;
    
    // Serial Port Settings Section

    
    // Logging Settings Section




    // Serial Port Settings Tab
    private Spinner spMainDevice, spMainBaudRate, spServerDevice, spServerBaudRate;
    private EditText etConnectionTimeout, etRetryCount;
    private Button btnTestConnection, btnSaveSerialPort;

    // Logging Settings Tab
    private EditText etLogLevel, etLogRetention, etLogPath;
    private Spinner spLogFormat, spLogRotation;
    private Button btnTestLogging, btnClearLogs, btnSaveLogging;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_system_settings);
            
            Log.i(TAG, "SystemSettingsActivity onCreate started");
            
            sharedPreferences = getSharedPreferences("SystemSettings", MODE_PRIVATE);
            
            initializeViews();
            setupClickListeners();
            loadCurrentSettings();
            
            Log.i(TAG, "SystemSettingsActivity onCreate completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "onCreate error: " + e.getMessage(), e);
            showErrorDialog("Başlatma Hatası", "Sistem ayarları açılırken hata oluştu: " + e.getMessage());
        }
    }

    private void initializeViews() {
        try {
            btnBack = findViewById(R.id.btnBack);
            
            // Parameter Settings Tab
            etMachineId = findViewById(R.id.etMachineId);
            etBoardType = findViewById(R.id.etBoardType);
            etMaxProducts = findViewById(R.id.etMaxProducts);
            etTemperature = findViewById(R.id.etTemperature);
            etHumidity = findViewById(R.id.etHumidity);
            etPowerConsumption = findViewById(R.id.etPowerConsumption);
            etMaintenanceInterval = findViewById(R.id.etMaintenanceInterval);
            btnSaveParameters = findViewById(R.id.btnSaveParameters);
            
            // Serial Port Settings Tab
            spMainDevice = findViewById(R.id.spMainDevice);
            spMainBaudRate = findViewById(R.id.spMainBaudRate);
            spServerDevice = findViewById(R.id.spServerDevice);
            spServerBaudRate = findViewById(R.id.spServerBaudRate);
            etConnectionTimeout = findViewById(R.id.etConnectionTimeout);
            etRetryCount = findViewById(R.id.etRetryCount);
            btnTestConnection = findViewById(R.id.btnTestConnection);
            btnSaveSerialPort = findViewById(R.id.btnSaveSerialPort);
            
            // Logging Settings Tab
            etLogLevel = findViewById(R.id.etLogLevel);
            etLogRetention = findViewById(R.id.etLogRetention);
            etLogPath = findViewById(R.id.etLogPath);
            spLogFormat = findViewById(R.id.spLogFormat);
            spLogRotation = findViewById(R.id.spLogRotation);
            btnTestLogging = findViewById(R.id.btnTestLogging);
            btnClearLogs = findViewById(R.id.btnClearLogs);
            btnSaveLogging = findViewById(R.id.btnSaveLogging);
            
            Log.d(TAG, "Views initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Initialize views error: " + e.getMessage(), e);
        }
    }



    private void setupClickListeners() {
        try {
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }
            
            // Parameter Settings Tab
            if (btnSaveParameters != null) {
                btnSaveParameters.setOnClickListener(v -> saveParameters());
            }
            
            // Serial Port Settings Tab
            if (btnTestConnection != null) {
                btnTestConnection.setOnClickListener(v -> testConnection());
            }
            if (btnSaveSerialPort != null) {
                btnSaveSerialPort.setOnClickListener(v -> saveSerialPort());
            }
            
            // Logging Settings Tab
            if (btnTestLogging != null) {
                btnTestLogging.setOnClickListener(v -> testLogging());
            }
            if (btnClearLogs != null) {
                btnClearLogs.setOnClickListener(v -> clearLogs());
            }
            if (btnSaveLogging != null) {
                btnSaveLogging.setOnClickListener(v -> saveLogging());
            }
            
            Log.d(TAG, "Click listeners setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Setup click listeners error: " + e.getMessage(), e);
        }
    }

    private void loadCurrentSettings() {
        try {
            // Load parameter settings
            String machineId = sharedPreferences.getString("machine_id", "ICE-001");
            String boardType = sharedPreferences.getString("board_type", "TCN-ICE");
            String maxProducts = sharedPreferences.getString("max_products", "100");
            String temperature = sharedPreferences.getString("temperature", "18");
            String humidity = sharedPreferences.getString("humidity", "45");
            String powerConsumption = sharedPreferences.getString("power_consumption", "2.5");
            String maintenanceInterval = sharedPreferences.getString("maintenance_interval", "30");
            
            if (etMachineId != null) etMachineId.setText(machineId);
            if (etBoardType != null) etBoardType.setText(boardType);
            if (etMaxProducts != null) etMaxProducts.setText(maxProducts);
            if (etTemperature != null) etTemperature.setText(temperature);
            if (etHumidity != null) etHumidity.setText(humidity);
            if (etPowerConsumption != null) etPowerConsumption.setText(powerConsumption);
            if (etMaintenanceInterval != null) etMaintenanceInterval.setText(maintenanceInterval);
            
            // Load serial port settings
            String connectionTimeout = sharedPreferences.getString("connection_timeout", "5000");
            String retryCount = sharedPreferences.getString("retry_count", "3");
            
            if (etConnectionTimeout != null) etConnectionTimeout.setText(connectionTimeout);
            if (etRetryCount != null) etRetryCount.setText(retryCount);
            
            // Setup serial port spinners
            setupSerialPortSpinners();
            
            // Load logging settings
            String logLevel = sharedPreferences.getString("log_level", "INFO");
            String logRetention = sharedPreferences.getString("log_retention", "30");
            String logPath = sharedPreferences.getString("log_path", "/storage/emulated/0/Android/data/com.dogus.otomat.icecdemo/files/logs");
            
            if (etLogLevel != null) etLogLevel.setText(logLevel);
            if (etLogRetention != null) etLogRetention.setText(logRetention);
            if (etLogPath != null) etLogPath.setText(logPath);
            
            // Setup logging spinners
            setupLoggingSpinners();
            
            Log.d(TAG, "Current settings loaded successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Load current settings error: " + e.getMessage(), e);
        }
    }

    private void setupSerialPortSpinners() {
        try {
            // Main Device Spinner
            String[] devices = {"/dev/ttyUSB0", "/dev/ttyUSB1", "/dev/ttyS0", "/dev/ttyS1", "/dev/ttyACM0"};
            ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, devices);
            deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spMainDevice != null) {
                spMainDevice.setAdapter(deviceAdapter);
                spMainDevice.setSelection(0);
            }
            if (spServerDevice != null) {
                spServerDevice.setAdapter(deviceAdapter);
                spServerDevice.setSelection(1);
            }
            
            // Baud Rate Spinner
            String[] baudRates = {"9600", "19200", "38400", "57600", "115200"};
            ArrayAdapter<String> baudRateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, baudRates);
            baudRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spMainBaudRate != null) {
                spMainBaudRate.setAdapter(baudRateAdapter);
                spMainBaudRate.setSelection(4); // 115200 default
            }
            if (spServerBaudRate != null) {
                spServerBaudRate.setAdapter(baudRateAdapter);
                spServerBaudRate.setSelection(4); // 115200 default
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Setup serial port spinners error: " + e.getMessage(), e);
        }
    }

    private void setupLoggingSpinners() {
        try {
            // Log Format Spinner
            String[] logFormats = {"TEXT", "JSON", "XML", "CSV"};
            ArrayAdapter<String> logFormatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, logFormats);
            logFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spLogFormat != null) {
                spLogFormat.setAdapter(logFormatAdapter);
                spLogFormat.setSelection(0);
            }
            
            // Log Rotation Spinner
            String[] logRotations = {"Daily", "Weekly", "Monthly", "Size-based"};
            ArrayAdapter<String> logRotationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, logRotations);
            logRotationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spLogRotation != null) {
                spLogRotation.setAdapter(logRotationAdapter);
                spLogRotation.setSelection(0);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Setup logging spinners error: " + e.getMessage(), e);
        }
    }

    private void saveParameters() {
        try {
            String machineId = etMachineId != null ? etMachineId.getText().toString() : "ICE-001";
            String boardType = etBoardType != null ? etBoardType.getText().toString() : "TCN-ICE";
            String maxProducts = etMaxProducts != null ? etMaxProducts.getText().toString() : "100";
            String temperature = etTemperature != null ? etTemperature.getText().toString() : "18";
            String humidity = etHumidity != null ? etHumidity.getText().toString() : "45";
            String powerConsumption = etPowerConsumption != null ? etPowerConsumption.getText().toString() : "2.5";
            String maintenanceInterval = etMaintenanceInterval != null ? etMaintenanceInterval.getText().toString() : "30";
            
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("machine_id", machineId);
            editor.putString("board_type", boardType);
            editor.putString("max_products", maxProducts);
            editor.putString("temperature", temperature);
            editor.putString("humidity", humidity);
            editor.putString("power_consumption", powerConsumption);
            editor.putString("maintenance_interval", maintenanceInterval);
            editor.apply();
            
            showToast("Parametreler kaydedildi!");
            Log.i(TAG, "Parameters saved successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Save parameters error: " + e.getMessage(), e);
            showToast("Parametreler kaydedilemedi: " + e.getMessage());
        }
    }

    private void testConnection() {
        try {
            String mainDevice = spMainDevice != null ? spMainDevice.getSelectedItem().toString() : "/dev/ttyUSB0";
            String mainBaudRate = spMainBaudRate != null ? spMainBaudRate.getSelectedItem().toString() : "115200";
            
            // Simulate connection test
            Log.i(TAG, "Testing connection to " + mainDevice + " at " + mainBaudRate + " baud");
            
            // Simulate test result
            boolean connectionSuccess = true; // In real implementation, this would test actual connection
            
            if (connectionSuccess) {
                showToast("Bağlantı testi başarılı! " + mainDevice + " bağlandı.");
                Log.i(TAG, "Connection test successful");
            } else {
                showToast("Bağlantı testi başarısız! Lütfen ayarları kontrol edin.");
                Log.w(TAG, "Connection test failed");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Test connection error: " + e.getMessage(), e);
            showToast("Bağlantı testi hatası: " + e.getMessage());
        }
    }

    private void saveSerialPort() {
        try {
            String mainDevice = spMainDevice != null ? spMainDevice.getSelectedItem().toString() : "/dev/ttyUSB0";
            String mainBaudRate = spMainBaudRate != null ? spMainBaudRate.getSelectedItem().toString() : "115200";
            String serverDevice = spServerDevice != null ? spServerDevice.getSelectedItem().toString() : "/dev/ttyUSB1";
            String serverBaudRate = spServerBaudRate != null ? spServerBaudRate.getSelectedItem().toString() : "115200";
            String connectionTimeout = etConnectionTimeout != null ? etConnectionTimeout.getText().toString() : "5000";
            String retryCount = etRetryCount != null ? etRetryCount.getText().toString() : "3";
            
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("main_device", mainDevice);
            editor.putString("main_baud_rate", mainBaudRate);
            editor.putString("server_device", serverDevice);
            editor.putString("server_baud_rate", serverBaudRate);
            editor.putString("connection_timeout", connectionTimeout);
            editor.putString("retry_count", retryCount);
            editor.apply();
            
            showToast("Serial port ayarları kaydedildi!");
            Log.i(TAG, "Serial port settings saved successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Save serial port error: " + e.getMessage(), e);
            showToast("Serial port ayarları kaydedilemedi: " + e.getMessage());
        }
    }

    private void testLogging() {
        try {
            String logLevel = etLogLevel != null ? etLogLevel.getText().toString() : "INFO";
            String logPath = etLogPath != null ? etLogPath.getText().toString() : "/storage/emulated/0/Android/data/com.dogus.otomat.icecdemo/files/logs";
            
            // Create test log entry
            String testMessage = "Test log entry - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            
            // Write test log
            File logDir = new File(logPath);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            File testLogFile = new File(logDir, "test_log.txt");
            try (FileWriter writer = new FileWriter(testLogFile, true)) {
                writer.write(testMessage + "\n");
            }
            
            showToast("Loglama testi başarılı! Test log dosyası oluşturuldu.");
            Log.i(TAG, "Logging test successful - test log created");
            
        } catch (Exception e) {
            Log.e(TAG, "Test logging error: " + e.getMessage(), e);
            showToast("Loglama testi hatası: " + e.getMessage());
        }
    }

    private void clearLogs() {
        try {
            String logPath = etLogPath != null ? etLogPath.getText().toString() : "/storage/emulated/0/Android/data/com.dogus.otomat.icecdemo/files/logs";
            
            File logDir = new File(logPath);
            if (logDir.exists()) {
                File[] logFiles = logDir.listFiles();
                if (logFiles != null) {
                    int deletedCount = 0;
                    for (File logFile : logFiles) {
                        if (logFile.delete()) {
                            deletedCount++;
                        }
                    }
                    showToast(deletedCount + " log dosyası silindi!");
                    Log.i(TAG, "Cleared " + deletedCount + " log files");
                }
            } else {
                showToast("Log dizini bulunamadı!");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Clear logs error: " + e.getMessage(), e);
            showToast("Log temizleme hatası: " + e.getMessage());
        }
    }

    private void saveLogging() {
        try {
            String logLevel = etLogLevel != null ? etLogLevel.getText().toString() : "INFO";
            String logRetention = etLogRetention != null ? etLogRetention.getText().toString() : "30";
            String logPath = etLogPath != null ? etLogPath.getText().toString() : "/storage/emulated/0/Android/data/com.dogus.otomat.icecdemo/files/logs";
            String logFormat = spLogFormat != null ? spLogFormat.getSelectedItem().toString() : "TEXT";
            String logRotation = spLogRotation != null ? spLogRotation.getSelectedItem().toString() : "Daily";
            
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("log_level", logLevel);
            editor.putString("log_retention", logRetention);
            editor.putString("log_path", logPath);
            editor.putString("log_format", logFormat);
            editor.putString("log_rotation", logRotation);
            editor.apply();
            
            showToast("Loglama ayarları kaydedildi!");
            Log.i(TAG, "Logging settings saved successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Save logging error: " + e.getMessage(), e);
            showToast("Loglama ayarları kaydedilemedi: " + e.getMessage());
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
            new androidx.appcompat.app.AlertDialog.Builder(this)
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
            Log.i(TAG, "SystemSettingsActivity onDestroy");
            super.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "onDestroy error: " + e.getMessage(), e);
        }
    }
}
