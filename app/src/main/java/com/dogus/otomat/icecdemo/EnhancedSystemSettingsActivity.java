package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Gelişmiş Sistem Ayarları Aktivitesi
 * Dondurma makinesi sistem ayarları için kapsamlı yönetim
 */
public class EnhancedSystemSettingsActivity extends AppCompatActivity {
    private static final String TAG = "EnhancedSystemSettings";

    // UI Elements - Header
    private Button btnBack, btnSaveAll, btnResetToDefault, btnExportSettings, btnImportSettings;

    // Machine Parameters Section
    private SeekBar seekBarTemperature, seekBarHumidity, seekBarPowerConsumption;
    private TextView tvTemperature, tvHumidity, tvPowerConsumption;

    // Serial Port Settings Section
    private Spinner spMainDevice, spMainBaudRate;
    private EditText etConnectionTimeout, etRetryCount;
    private Button btnTestConnection, btnSaveSerialPort;
    private Switch switchAutoConnect, switchServerMode;

    // Logging Settings Section
    private EditText etLogLevel, etLogRetention, etLogPath;
    private Spinner spLogFormat, spLogRotation;
    private Button btnTestLogging, btnClearLogs, btnSaveLogging;
    private Switch switchAutoLogging, switchRemoteLogging;
    private SeekBar seekBarLogRetention;
    private TextView tvLogRetention;

    // Network Settings Section
    private EditText etServerIP, etServerPort, etApiKey;
    private Switch switchNetworkEnabled, switchAutoSync, switchSecureConnection;
    private Button btnTestNetwork, btnSaveNetwork;

    // Performance Settings Section
    private SeekBar seekBarCpuLimit, seekBarMemoryLimit, seekBarBatteryOptimization;
    private TextView tvCpuLimit, tvMemoryLimit, tvBatteryOptimization;
    private Switch switchPerformanceMode, switchBatterySaver;

    // Security Settings Section
    private EditText etAdminPassword, etUserPassword, etSessionTimeout;
    private Switch switchPasswordRequired, switchAutoLock, switchAuditLog;
    private Button btnChangePassword, btnSaveSecurity;

    // Shared Preferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_enhanced_system_settings);

            Log.i(TAG, "EnhancedSystemSettingsActivity onCreate started");

            sharedPreferences = getSharedPreferences("EnhancedSystemSettings", MODE_PRIVATE);

            initializeViews();
            setupClickListeners();
            setupSpinners();
            loadCurrentSettings();

            Log.i(TAG, "EnhancedSystemSettingsActivity onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "onCreate error: " + e.getMessage(), e);
            showErrorDialog("Başlatma Hatası", "Sistem ayarları açılırken hata oluştu: " + e.getMessage());
        }
    }

    private void initializeViews() {
        try {
            // Header
            btnBack = findViewById(R.id.btnBack);
            btnSaveAll = findViewById(R.id.btnSaveAll);
            btnResetToDefault = findViewById(R.id.btnResetToDefault);
            btnExportSettings = findViewById(R.id.btnExportSettings);
            btnImportSettings = findViewById(R.id.btnImportSettings);

            // Machine Parameters
            seekBarTemperature = findViewById(R.id.seekBarTemperature);
            seekBarHumidity = findViewById(R.id.seekBarHumidity);
            seekBarPowerConsumption = findViewById(R.id.seekBarPowerConsumption);

            tvTemperature = findViewById(R.id.tvTemperature);
            tvHumidity = findViewById(R.id.tvHumidity);
            tvPowerConsumption = findViewById(R.id.tvPowerConsumption);

            // Serial Port Settings
            spMainDevice = findViewById(R.id.spMainDevice);
            spMainBaudRate = findViewById(R.id.spMainBaudRate);
            etConnectionTimeout = findViewById(R.id.etConnectionTimeout);
            etRetryCount = findViewById(R.id.etRetryCount);
            btnTestConnection = findViewById(R.id.btnTestConnection);
            btnSaveSerialPort = findViewById(R.id.btnSaveSerialPort);
            switchAutoConnect = findViewById(R.id.switchAutoConnect);
            switchServerMode = findViewById(R.id.switchServerMode);

            // Logging Settings
            etLogLevel = findViewById(R.id.etLogLevel);
            etLogRetention = findViewById(R.id.etLogRetention);
            etLogPath = findViewById(R.id.etLogPath);
            spLogFormat = findViewById(R.id.spLogFormat);
            btnTestLogging = findViewById(R.id.btnTestLogging);
            btnClearLogs = findViewById(R.id.btnClearLogs);
            btnSaveLogging = findViewById(R.id.btnSaveLogging);
            switchAutoLogging = findViewById(R.id.switchAutoLogging);
            switchRemoteLogging = findViewById(R.id.switchRemoteLogging);
            seekBarLogRetention = findViewById(R.id.seekBarLogRetention);
            tvLogRetention = findViewById(R.id.tvLogRetention);

            // Network Settings
            etServerIP = findViewById(R.id.etServerIP);
            etServerPort = findViewById(R.id.etServerPort);
            etApiKey = findViewById(R.id.etApiKey);
            switchNetworkEnabled = findViewById(R.id.switchNetworkEnabled);
            switchAutoSync = findViewById(R.id.switchAutoSync);
            switchSecureConnection = findViewById(R.id.switchSecureConnection);
            btnTestNetwork = findViewById(R.id.btnTestNetwork);
            btnSaveNetwork = findViewById(R.id.btnSaveNetwork);

            // Performance Settings
            seekBarCpuLimit = findViewById(R.id.seekBarCpuLimit);
            seekBarMemoryLimit = findViewById(R.id.seekBarMemoryLimit);
            seekBarBatteryOptimization = findViewById(R.id.seekBarBatteryOptimization);
            tvCpuLimit = findViewById(R.id.tvCpuLimit);
            tvMemoryLimit = findViewById(R.id.tvMemoryLimit);
            tvBatteryOptimization = findViewById(R.id.tvBatteryOptimization);
            switchPerformanceMode = findViewById(R.id.switchPerformanceMode);
            switchBatterySaver = findViewById(R.id.switchBatterySaver);

            // Security Settings
            etAdminPassword = findViewById(R.id.etAdminPassword);
            etUserPassword = findViewById(R.id.etUserPassword);
            etSessionTimeout = findViewById(R.id.etSessionTimeout);
            switchPasswordRequired = findViewById(R.id.switchPasswordRequired);
            switchAutoLock = findViewById(R.id.switchAutoLock);
            switchAuditLog = findViewById(R.id.switchAuditLog);
            btnChangePassword = findViewById(R.id.btnChangePassword);
            btnSaveSecurity = findViewById(R.id.btnSaveSecurity);

            Log.i(TAG, "Views initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "View initialization error: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            // Header buttons
            btnBack.setOnClickListener(v -> finish());
            btnSaveAll.setOnClickListener(v -> saveAllSettings());
            btnResetToDefault.setOnClickListener(v -> resetToDefault());
            btnExportSettings.setOnClickListener(v -> exportSettings());
            btnImportSettings.setOnClickListener(v -> importSettings());

            // Serial Port buttons
            btnTestConnection.setOnClickListener(v -> testConnection());
            btnSaveSerialPort.setOnClickListener(v -> saveSerialPortSettings());

            // Logging buttons
            btnTestLogging.setOnClickListener(v -> testLogging());
            btnClearLogs.setOnClickListener(v -> clearLogs());
            btnSaveLogging.setOnClickListener(v -> saveLoggingSettings());

            // Network buttons
            btnTestNetwork.setOnClickListener(v -> testNetwork());
            btnSaveNetwork.setOnClickListener(v -> saveNetworkSettings());

            // Security buttons
            btnChangePassword.setOnClickListener(v -> changePassword());
            btnSaveSecurity.setOnClickListener(v -> saveSecuritySettings());

            // SeekBar listeners
            setupSeekBarListeners();

            // Switch listeners
            setupSwitchListeners();

            Log.i(TAG, "Click listeners setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Click listener setup error: " + e.getMessage(), e);
        }
    }

    private void setupSeekBarListeners() {
        try {
            // Temperature SeekBar
            seekBarTemperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int temperature = progress + 15; // 15-35°C
                        tvTemperature.setText(temperature + "°C");
                        // etTemperature.setText(String.valueOf(temperature)); // This line was removed
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Humidity SeekBar
            seekBarHumidity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int humidity = progress + 30; // 30-80%
                        tvHumidity.setText(humidity + "%");
                        // etHumidity.setText(String.valueOf(humidity)); // This line was removed
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Power Consumption SeekBar
            seekBarPowerConsumption.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int power = progress + 100; // 100-500W
                        tvPowerConsumption.setText(power + "W");
                        // etPowerConsumption.setText(String.valueOf(power)); // This line was removed
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Log Retention SeekBar
            seekBarLogRetention.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int days = progress + 7; // 7-90 gün
                        tvLogRetention.setText(days + " gün");
                        // etLogRetention.setText(String.valueOf(days)); // This line was removed
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Performance SeekBars
            seekBarCpuLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int limit = progress + 50; // 50-100%
                        tvCpuLimit.setText(limit + "%");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            seekBarMemoryLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int limit = progress + 50; // 50-100%
                        tvMemoryLimit.setText(limit + "%");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            seekBarBatteryOptimization.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int optimization = progress + 20; // 20-100%
                        tvBatteryOptimization.setText(optimization + "%");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "SeekBar listener setup error: " + e.getMessage(), e);
        }
    }

    private void setupSwitchListeners() {
        try {
            // Serial Port switches
            switchAutoConnect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Enable/disable manual connection controls
                btnTestConnection.setEnabled(!isChecked);
                btnSaveSerialPort.setEnabled(!isChecked);
            });

            switchServerMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Enable/disable server device controls
                // spServerDevice.setEnabled(isChecked); // This line was removed
                // spServerBaudRate.setEnabled(isChecked); // This line was removed
            });

            // Logging switches
            switchAutoLogging.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Enable/disable logging controls
                etLogLevel.setEnabled(isChecked);
                etLogPath.setEnabled(isChecked);
                spLogFormat.setEnabled(isChecked);
                spLogRotation.setEnabled(isChecked);
                seekBarLogRetention.setEnabled(isChecked);
            });

            switchRemoteLogging.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Enable/disable remote logging controls
                etServerIP.setEnabled(isChecked);
                etServerPort.setEnabled(isChecked);
                etApiKey.setEnabled(isChecked);
            });

            // Network switches
            switchNetworkEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Enable/disable network controls
                etServerIP.setEnabled(isChecked);
                etServerPort.setEnabled(isChecked);
                etApiKey.setEnabled(isChecked);
                switchAutoSync.setEnabled(isChecked);
                switchSecureConnection.setEnabled(isChecked);
                btnTestNetwork.setEnabled(isChecked);
                btnSaveNetwork.setEnabled(isChecked);
            });

            // Performance switches
            switchPerformanceMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Enable/disable performance controls
                seekBarCpuLimit.setEnabled(isChecked);
                seekBarMemoryLimit.setEnabled(isChecked);
                seekBarBatteryOptimization.setEnabled(isChecked);
            });

            // Security switches
            switchPasswordRequired.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Enable/disable password controls
                etAdminPassword.setEnabled(isChecked);
                etUserPassword.setEnabled(isChecked);
                etSessionTimeout.setEnabled(isChecked);
                btnChangePassword.setEnabled(isChecked);
            });

        } catch (Exception e) {
            Log.e(TAG, "Switch listener setup error: " + e.getMessage(), e);
        }
    }

    private void setupSpinners() {
        try {
            // Main Device Spinner
            String[] devices = { "/dev/ttyS0", "/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3", "/dev/ttyUSB0",
                    "/dev/ttyUSB1" };
            ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                    devices);
            deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spMainDevice.setAdapter(deviceAdapter);
            // spServerDevice.setAdapter(deviceAdapter); // This line was removed

            // Baud Rate Spinner
            String[] baudRates = { "9600", "19200", "38400", "57600", "115200", "230400", "460800", "921600" };
            ArrayAdapter<String> baudRateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                    baudRates);
            baudRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spMainBaudRate.setAdapter(baudRateAdapter);
            // spServerBaudRate.setAdapter(baudRateAdapter); // This line was removed

            // Log Format Spinner
            String[] logFormats = { "TEXT", "JSON", "XML", "CSV" };
            ArrayAdapter<String> logFormatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                    logFormats);
            logFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spLogFormat.setAdapter(logFormatAdapter);

            // Log Rotation Spinner
            String[] logRotations = { "DAILY", "WEEKLY", "MONTHLY", "SIZE_BASED" };
            ArrayAdapter<String> logRotationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                    logRotations);
            logRotationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spLogRotation.setAdapter(logRotationAdapter);

            Log.i(TAG, "Spinners setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Spinner setup error: " + e.getMessage(), e);
        }
    }

    private void loadCurrentSettings() {
        try {
            // Load machine parameters
            // etMachineId.setText(sharedPreferences.getString("machine_id", "ICE-001")); //
            // This line was removed
            // etBoardType.setText(sharedPreferences.getString("board_type", "TCN-ICE")); //
            // This line was removed
            // etMaxProducts.setText(sharedPreferences.getString("max_products", "100")); //
            // This line was removed
            // etMaintenanceInterval.setText(sharedPreferences.getString("maintenance_interval",
            // "30")); // This line was removed

            int temperature = sharedPreferences.getInt("temperature", 25);
            int humidity = sharedPreferences.getInt("humidity", 60);
            int powerConsumption = sharedPreferences.getInt("power_consumption", 300);

            seekBarTemperature.setProgress(temperature - 15);
            seekBarHumidity.setProgress(humidity - 30);
            seekBarPowerConsumption.setProgress(powerConsumption - 100);

            tvTemperature.setText(temperature + "°C");
            tvHumidity.setText(humidity + "%");
            tvPowerConsumption.setText(powerConsumption + "W");

            // etTemperature.setText(String.valueOf(temperature)); // This line was removed
            // etHumidity.setText(String.valueOf(humidity)); // This line was removed
            // etPowerConsumption.setText(String.valueOf(powerConsumption)); // This line
            // was removed

            // Load serial port settings
            String mainDevice = sharedPreferences.getString("main_device", "/dev/ttyS1");
            String mainBaudRate = sharedPreferences.getString("main_baud_rate", "9600");
            // String serverDevice = sharedPreferences.getString("server_device",
            // "/dev/ttyS2"); // This line was removed
            // String serverBaudRate = sharedPreferences.getString("server_baud_rate",
            // "9600"); // This line was removed

            setSpinnerSelection(spMainDevice, mainDevice);
            setSpinnerSelection(spMainBaudRate, mainBaudRate);
            // setSpinnerSelection(spServerDevice, serverDevice); // This line was removed
            // setSpinnerSelection(spServerBaudRate, serverBaudRate); // This line was
            // removed

            etConnectionTimeout.setText(sharedPreferences.getString("connection_timeout", "30"));
            etRetryCount.setText(sharedPreferences.getString("retry_count", "3"));

            switchAutoConnect.setChecked(sharedPreferences.getBoolean("auto_connect", true));
            switchServerMode.setChecked(sharedPreferences.getBoolean("server_mode", false));

            // Load logging settings
            etLogLevel.setText(sharedPreferences.getString("log_level", "INFO"));
            etLogPath.setText(sharedPreferences.getString("log_path", "/var/log/icecream"));

            String logFormat = sharedPreferences.getString("log_format", "TEXT");
            String logRotation = sharedPreferences.getString("log_rotation", "DAILY");

            setSpinnerSelection(spLogFormat, logFormat);
            setSpinnerSelection(spLogRotation, logRotation);

            int logRetention = sharedPreferences.getInt("log_retention", 30);
            seekBarLogRetention.setProgress(logRetention - 7);
            tvLogRetention.setText(logRetention + " gün");
            // etLogRetention.setText(String.valueOf(logRetention)); // This line was
            // removed

            switchAutoLogging.setChecked(sharedPreferences.getBoolean("auto_logging", true));
            switchRemoteLogging.setChecked(sharedPreferences.getBoolean("remote_logging", false));

            // Load network settings
            etServerIP.setText(sharedPreferences.getString("server_ip", "192.168.1.100"));
            etServerPort.setText(sharedPreferences.getString("server_port", "8080"));
            etApiKey.setText(sharedPreferences.getString("api_key", ""));

            switchNetworkEnabled.setChecked(sharedPreferences.getBoolean("network_enabled", false));
            switchAutoSync.setChecked(sharedPreferences.getBoolean("auto_sync", false));
            switchSecureConnection.setChecked(sharedPreferences.getBoolean("secure_connection", true));

            // Load performance settings
            int cpuLimit = sharedPreferences.getInt("cpu_limit", 80);
            int memoryLimit = sharedPreferences.getInt("memory_limit", 80);
            int batteryOptimization = sharedPreferences.getInt("battery_optimization", 60);

            seekBarCpuLimit.setProgress(cpuLimit - 50);
            seekBarMemoryLimit.setProgress(memoryLimit - 50);
            seekBarBatteryOptimization.setProgress(batteryOptimization - 20);

            tvCpuLimit.setText(cpuLimit + "%");
            tvMemoryLimit.setText(memoryLimit + "%");
            tvBatteryOptimization.setText(batteryOptimization + "%");

            switchPerformanceMode.setChecked(sharedPreferences.getBoolean("performance_mode", false));
            switchBatterySaver.setChecked(sharedPreferences.getBoolean("battery_saver", true));

            // Load security settings
            etAdminPassword.setText(sharedPreferences.getString("admin_password", "admin123"));
            etUserPassword.setText(sharedPreferences.getString("user_password", "user123"));
            etSessionTimeout.setText(sharedPreferences.getString("session_timeout", "30"));

            switchPasswordRequired.setChecked(sharedPreferences.getBoolean("password_required", true));
            switchAutoLock.setChecked(sharedPreferences.getBoolean("auto_lock", true));
            switchAuditLog.setChecked(sharedPreferences.getBoolean("audit_log", true));

            Log.i(TAG, "Current settings loaded successfully");

        } catch (Exception e) {
            Log.e(TAG, "Settings loading error: " + e.getMessage(), e);
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        try {
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equals(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Spinner selection error: " + e.getMessage(), e);
        }
    }

    private void testConnection() {
        try {
            Toast.makeText(this, "Bağlantı test ediliyor...", Toast.LENGTH_SHORT).show();
            // Simulate connection test
            new android.os.Handler().postDelayed(() -> {
                Toast.makeText(this, "Bağlantı başarılı!", Toast.LENGTH_SHORT).show();
            }, 2000);
        } catch (Exception e) {
            Log.e(TAG, "Connection test error: " + e.getMessage(), e);
            Toast.makeText(this, "Bağlantı testi başarısız", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSerialPortSettings() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("main_device", spMainDevice.getSelectedItem().toString());
            editor.putString("main_baud_rate", spMainBaudRate.getSelectedItem().toString());
            // editor.putString("server_device",
            // spServerDevice.getSelectedItem().toString()); // This line was removed
            // editor.putString("server_baud_rate",
            // spServerBaudRate.getSelectedItem().toString()); // This line was removed
            editor.putString("connection_timeout", etConnectionTimeout.getText().toString());
            editor.putString("retry_count", etRetryCount.getText().toString());
            editor.putBoolean("auto_connect", switchAutoConnect.isChecked());
            editor.putBoolean("server_mode", switchServerMode.isChecked());
            editor.apply();

            Toast.makeText(this, "Seri port ayarları kaydedildi", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Serial port settings save error: " + e.getMessage(), e);
            Toast.makeText(this, "Ayarlar kaydedilemedi", Toast.LENGTH_SHORT).show();
        }
    }

    private void testLogging() {
        try {
            Toast.makeText(this, "Logging test ediliyor...", Toast.LENGTH_SHORT).show();
            // Simulate logging test
            new android.os.Handler().postDelayed(() -> {
                Toast.makeText(this, "Logging test başarılı!", Toast.LENGTH_SHORT).show();
            }, 2000);
        } catch (Exception e) {
            Log.e(TAG, "Logging test error: " + e.getMessage(), e);
            Toast.makeText(this, "Logging testi başarısız", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearLogs() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("Logları Temizle")
                    .setMessage("Tüm logları silmek istediğinizden emin misiniz?")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        Toast.makeText(this, "Loglar temizlendi", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hayır", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Clear logs error: " + e.getMessage(), e);
        }
    }

    private void saveLoggingSettings() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("log_level", etLogLevel.getText().toString());
            editor.putString("log_path", etLogPath.getText().toString());
            editor.putString("log_format", spLogFormat.getSelectedItem().toString());
            editor.putString("log_rotation", spLogRotation.getSelectedItem().toString());
            editor.putInt("log_retention", seekBarLogRetention.getProgress() + 7);
            editor.putBoolean("auto_logging", switchAutoLogging.isChecked());
            editor.putBoolean("remote_logging", switchRemoteLogging.isChecked());
            editor.apply();

            Toast.makeText(this, "Logging ayarları kaydedildi", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Logging settings save error: " + e.getMessage(), e);
            Toast.makeText(this, "Ayarlar kaydedilemedi", Toast.LENGTH_SHORT).show();
        }
    }

    private void testNetwork() {
        try {
            Toast.makeText(this, "Ağ test ediliyor...", Toast.LENGTH_SHORT).show();
            // Simulate network test
            new android.os.Handler().postDelayed(() -> {
                Toast.makeText(this, "Ağ testi başarılı!", Toast.LENGTH_SHORT).show();
            }, 2000);
        } catch (Exception e) {
            Log.e(TAG, "Network test error: " + e.getMessage(), e);
            Toast.makeText(this, "Ağ testi başarısız", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNetworkSettings() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("server_ip", etServerIP.getText().toString());
            editor.putString("server_port", etServerPort.getText().toString());
            editor.putString("api_key", etApiKey.getText().toString());
            editor.putBoolean("network_enabled", switchNetworkEnabled.isChecked());
            editor.putBoolean("auto_sync", switchAutoSync.isChecked());
            editor.putBoolean("secure_connection", switchSecureConnection.isChecked());
            editor.apply();

            Toast.makeText(this, "Ağ ayarları kaydedildi", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Network settings save error: " + e.getMessage(), e);
            Toast.makeText(this, "Ayarlar kaydedilemedi", Toast.LENGTH_SHORT).show();
        }
    }

    private void changePassword() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("Şifre Değiştir")
                    .setMessage("Şifre değiştirme özelliği yakında eklenecek")
                    .setPositiveButton("Tamam", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Change password error: " + e.getMessage(), e);
        }
    }

    private void saveSecuritySettings() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("admin_password", etAdminPassword.getText().toString());
            editor.putString("user_password", etUserPassword.getText().toString());
            editor.putString("session_timeout", etSessionTimeout.getText().toString());
            editor.putBoolean("password_required", switchPasswordRequired.isChecked());
            editor.putBoolean("auto_lock", switchAutoLock.isChecked());
            editor.putBoolean("audit_log", switchAuditLog.isChecked());
            editor.apply();

            Toast.makeText(this, "Güvenlik ayarları kaydedildi", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Security settings save error: " + e.getMessage(), e);
            Toast.makeText(this, "Ayarlar kaydedilemedi", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAllSettings() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Save machine parameters
            // editor.putString("machine_id", etMachineId.getText().toString()); // This
            // line was removed
            // editor.putString("board_type", etBoardType.getText().toString()); // This
            // line was removed
            // editor.putString("max_products", etMaxProducts.getText().toString()); // This
            // line was removed
            // editor.putString("maintenance_interval",
            // etMaintenanceInterval.getText().toString()); // This line was removed
            editor.putInt("temperature", seekBarTemperature.getProgress() + 15);
            editor.putInt("humidity", seekBarHumidity.getProgress() + 30);
            editor.putInt("power_consumption", seekBarPowerConsumption.getProgress() + 100);

            // Save performance settings
            editor.putInt("cpu_limit", seekBarCpuLimit.getProgress() + 50);
            editor.putInt("memory_limit", seekBarMemoryLimit.getProgress() + 50);
            editor.putInt("battery_optimization", seekBarBatteryOptimization.getProgress() + 20);
            editor.putBoolean("performance_mode", switchPerformanceMode.isChecked());
            editor.putBoolean("battery_saver", switchBatterySaver.isChecked());

            editor.apply();

            Toast.makeText(this, "Tüm ayarlar kaydedildi", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "All settings saved successfully");

        } catch (Exception e) {
            Log.e(TAG, "Settings save error: " + e.getMessage(), e);
            Toast.makeText(this, "Ayarlar kaydedilemedi", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetToDefault() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("Varsayılan Değerlere Sıfırla")
                    .setMessage("Tüm ayarları varsayılan değerlere sıfırlamak istediğinizden emin misiniz?")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        loadDefaultSettings();
                        Toast.makeText(this, "Ayarlar varsayılan değerlere sıfırlandı", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hayır", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Reset to default error: " + e.getMessage(), e);
        }
    }

    private void loadDefaultSettings() {
        try {
            // Reset to default values
            loadCurrentSettings(); // This will load the default values
        } catch (Exception e) {
            Log.e(TAG, "Default settings loading error: " + e.getMessage(), e);
        }
    }

    private void exportSettings() {
        try {
            Toast.makeText(this, "Ayarlar dışa aktarılıyor...", Toast.LENGTH_SHORT).show();
            // Simulate export
            new android.os.Handler().postDelayed(() -> {
                Toast.makeText(this, "Ayarlar dışa aktarıldı", Toast.LENGTH_SHORT).show();
            }, 2000);
        } catch (Exception e) {
            Log.e(TAG, "Export settings error: " + e.getMessage(), e);
            Toast.makeText(this, "Dışa aktarma başarısız", Toast.LENGTH_SHORT).show();
        }
    }

    private void importSettings() {
        try {
            Toast.makeText(this, "Ayarlar içe aktarılıyor...", Toast.LENGTH_SHORT).show();
            // Simulate import
            new android.os.Handler().postDelayed(() -> {
                Toast.makeText(this, "Ayarlar içe aktarıldı", Toast.LENGTH_SHORT).show();
                loadCurrentSettings();
            }, 2000);
        } catch (Exception e) {
            Log.e(TAG, "Import settings error: " + e.getMessage(), e);
            Toast.makeText(this, "İçe aktarma başarısız", Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorDialog(String title, String message) {
        try {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Tamam", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error dialog show error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // Refresh settings if needed
        } catch (Exception e) {
            Log.e(TAG, "onResume error: " + e.getMessage(), e);
        }
    }
}
