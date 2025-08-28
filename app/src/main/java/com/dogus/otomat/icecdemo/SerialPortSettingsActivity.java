package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.tcn.icecboard.control.TcnVendIF;
import android_serialport_api.SerialPortController;
import android_serialport_api.SerialPortFinder;

/**
 * Serial Port Ayarları Ekranı
 * SDK'dan gelen gerçek serial port yapısını kullanır
 */
public class SerialPortSettingsActivity extends AppCompatActivity {

    private EditText etPortName, etBaudRate;
    private Spinner spnPortType;
    private Button btnSaveSettings, btnBack, btnTestConnection, btnScanPorts;
    private SharedPreferences sharedPreferences;

    // SDK Serial Port Controller
    private SerialPortController serialPortController;
    private SerialPortFinder serialPortFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_settings);

        sharedPreferences = getSharedPreferences("SerialPortPrefs", MODE_PRIVATE);

        // Initialize SDK Serial Port components
        serialPortController = SerialPortController.getInstance();
        serialPortFinder = new SerialPortFinder();

        initializeViews();
        loadCurrentSettings();
        setupClickListeners();
    }

    private void initializeViews() {
        // Port Ayarları
        etPortName = findViewById(R.id.etPortName);
        etBaudRate = findViewById(R.id.etBaudRate);

        // Port Tipi
        spnPortType = findViewById(R.id.spnPortType);

        // Butonlar
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        btnBack = findViewById(R.id.btnBack);
        btnTestConnection = findViewById(R.id.btnTestConnection);
        btnScanPorts = findViewById(R.id.btnScanPorts);

        // Port tipi seçeneklerini ayarla
        String[] portTypes = { "Ana Board", "Server Board", "Third Board", "Fourth Board", "MDB Board" };
        ArrayAdapter<String> portTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                portTypes);
        portTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPortType.setAdapter(portTypeAdapter);
    }

    private void setupClickListeners() {
        btnSaveSettings.setOnClickListener(v -> saveSettings());
        btnBack.setOnClickListener(v -> finish());
        btnTestConnection.setOnClickListener(v -> testConnection());
        btnScanPorts.setOnClickListener(v -> scanPorts());
    }

    private void loadCurrentSettings() {
        // Port ayarları
        String portName = sharedPreferences.getString("port_name", "/dev/ttyS1");
        int baudRate = sharedPreferences.getInt("baud_rate", 19200);
        int portType = sharedPreferences.getInt("port_type", 0);

        // UI'ya yükle
        etPortName.setText(portName);
        etBaudRate.setText(String.valueOf(baudRate));
        spnPortType.setSelection(portType);
    }

    private void saveSettings() {
        try {
            // Port ayarları
            String portName = etPortName.getText().toString();
            int baudRate = Integer.parseInt(etBaudRate.getText().toString());
            int portType = spnPortType.getSelectedItemPosition();

            // Değerleri doğrula
            if (portName.isEmpty()) {
                showToast("Port adı boş olamaz!");
                return;
            }
            if (baudRate <= 0) {
                showToast("Baud rate pozitif olmalı!");
                return;
            }

            // SharedPreferences'a kaydet
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("port_name", portName);
            editor.putInt("baud_rate", baudRate);
            editor.putInt("port_type", portType);
            editor.apply();

            showToast("Serial port ayarları kaydedildi!");

        } catch (NumberFormatException e) {
            showToast("Lütfen geçerli sayısal değerler girin!");
        }
    }

    private void testConnection() {
        try {
            String portName = etPortName.getText().toString();
            int baudRate = Integer.parseInt(etBaudRate.getText().toString());

            if (portName.isEmpty()) {
                showToast("Port adı boş olamaz!");
                return;
            }

            // TCN SDK üzerinden bağlantıyı test et
            SDKIntegrationHelper sdkHelper = SDKIntegrationHelper.getInstance(this);
            if (sdkHelper != null) {
                boolean connected = false;
                String portTypeName = "";

                switch (spnPortType.getSelectedItemPosition()) {
                    case 0: // Ana Board (TCN)
                        portTypeName = "TCN Ana Board";
                        connected = sdkHelper.isSDKConnected();
                        if (!connected) {
                            connected = sdkHelper.initializeSDK();
                        }
                        break;
                    case 1: // Server Board
                        portTypeName = "Server Board";
                        if (serialPortController != null) {
                            connected = serialPortController.openSerialPortNew(portName, baudRate);
                        }
                        break;
                    case 2: // Third Board (Dondurma Kontrol)
                        portTypeName = "Dondurma Kontrol Board";
                        if (serialPortController != null) {
                            connected = serialPortController.openSerialPortThird(portName, baudRate);
                        }
                        break;
                    case 3: // Fourth Board
                        portTypeName = "Fourth Board";
                        if (serialPortController != null) {
                            connected = serialPortController.openSerialPortFourth(portName, baudRate);
                        }
                        break;
                    case 4: // MDB Board
                        portTypeName = "MDB Board";
                        connected = sdkHelper.initializeMDB(portName, baudRate);
                        break;
                }

                if (connected) {
                    showToast(portTypeName + " bağlantısı başarılı!");
                    Log.i("SerialPortSettings",
                            portTypeName + " bağlantı testi başarılı: " + portName + "@" + baudRate);

                    // Ayarları kaydet
                    saveSettings();
                } else {
                    showToast(portTypeName + " bağlantısı başarısız! Port ayarlarını kontrol edin.");
                    Log.e("SerialPortSettings", portTypeName + " bağlantı testi başarısız");
                }

            } else {
                showToast("SDK bağlantısı yok - Test modunda çalışıyor");

                // Test modunda simüle et
                String[] portTypes = { "TCN Ana Board", "Server Board", "Dondurma Kontrol Board", "Fourth Board",
                        "MDB Board" };
                String portTypeName = portTypes[spnPortType.getSelectedItemPosition()];
                showToast("Test modu: " + portTypeName + " bağlantısı simüle edildi");
                saveSettings();
            }

        } catch (NumberFormatException e) {
            showToast("Geçersiz baud rate değeri!");
        } catch (Exception e) {
            Log.e("SerialPortSettings", "Bağlantı testi hatası: " + e.getMessage());
            showToast("Bağlantı testi hatası: " + e.getMessage());
        }
    }

    private void scanPorts() {
        if (serialPortFinder != null) {
            try {
                // Mevcut portları tara
                String[] devices = serialPortFinder.getAllDevices();
                String[] devicePaths = serialPortFinder.getAllDevicesPath();

                if (devices != null && devices.length > 0) {
                    StringBuilder portList = new StringBuilder("Bulunan Portlar:\n");
                    for (int i = 0; i < devices.length; i++) {
                        portList.append(devices[i]).append(": ").append(devicePaths[i]).append("\n");
                    }
                    showToast(portList.toString());
                } else {
                    showToast("Hiç port bulunamadı!");
                }

            } catch (Exception e) {
                showToast("Port tarama hatası: " + e.getMessage());
            }
        } else {
            showToast("Serial Port Finder bulunamadı!");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
