package com.dogus.otomat.icecdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

/**
 * Dogi Soft Ice Cream Makine Konfigürasyonu Aktivitesi
 * IoT numarası, seri numarası, IP ve port bilgilerini yapılandırır
 */
public class MachineConfigActivity extends Activity {
    private static final String TAG = "MachineConfigActivity";
    private static final String PREF_NAME = "dogus_telemetry_config";

    private EditText etMachineId;
    private EditText etIotNumber;
    private EditText etSerialNumber;
    private EditText etLocation;
    private EditText etOperatorId;
    private EditText etMachineIP;
    private EditText etMachinePort;
    private TextView tvMachineModel;
    private TextView tvBrand;
    private Button btnSave;
    private Button btnTestConnection;
    private Button btnBack;

    private TelemetryManager telemetryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout oluştur
        createLayout();

        // Telemetri yöneticisini başlat
        telemetryManager = TelemetryManager.getInstance(this);

        // Mevcut konfigürasyonu yükle
        loadCurrentConfig();

        // Event listener'ları ayarla
        setupEventListeners();
    }

    private void createLayout() {
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 50, 50, 50);

        // Başlık
        TextView tvTitle = new TextView(this);
        tvTitle.setText("Dogi Soft Ice Cream Makine Konfigürasyonu");
        tvTitle.setTextSize(24);
        tvTitle.setPadding(0, 0, 0, 30);
        mainLayout.addView(tvTitle);

        // Marka ve Model bilgileri
        tvBrand = new TextView(this);
        tvBrand.setText("Marka: Dogi Soft Ice Cream");
        tvBrand.setTextSize(16);
        mainLayout.addView(tvBrand);

        tvMachineModel = new TextView(this);
        tvMachineModel.setText("Model: DGS-DIC-S");
        tvMachineModel.setTextSize(16);
        tvMachineModel.setPadding(0, 0, 0, 20);
        mainLayout.addView(tvMachineModel);

        // Makine ID
        TextView tvMachineIdLabel = new TextView(this);
        tvMachineIdLabel.setText("Makine ID:");
        mainLayout.addView(tvMachineIdLabel);

        etMachineId = new EditText(this);
        etMachineId.setHint("Makine ID giriniz");
        mainLayout.addView(etMachineId);

        // IoT Numarası
        TextView tvIotNumberLabel = new TextView(this);
        tvIotNumberLabel.setText("IoT Numarası:");
        mainLayout.addView(tvIotNumberLabel);

        etIotNumber = new EditText(this);
        etIotNumber.setHint("IoT numarası giriniz");
        mainLayout.addView(etIotNumber);

        // Seri Numarası
        TextView tvSerialNumberLabel = new TextView(this);
        tvSerialNumberLabel.setText("Seri Numarası:");
        mainLayout.addView(tvSerialNumberLabel);

        etSerialNumber = new EditText(this);
        etSerialNumber.setHint("Seri numarası giriniz");
        mainLayout.addView(etSerialNumber);

        // Lokasyon
        TextView tvLocationLabel = new TextView(this);
        tvLocationLabel.setText("Lokasyon:");
        mainLayout.addView(tvLocationLabel);

        etLocation = new EditText(this);
        etLocation.setHint("Lokasyon giriniz");
        mainLayout.addView(etLocation);

        // Operatör ID
        TextView tvOperatorIdLabel = new TextView(this);
        tvOperatorIdLabel.setText("Operatör ID:");
        mainLayout.addView(tvOperatorIdLabel);

        etOperatorId = new EditText(this);
        etOperatorId.setHint("Operatör ID giriniz");
        mainLayout.addView(etOperatorId);

        // Makine IP
        TextView tvMachineIPLabel = new TextView(this);
        tvMachineIPLabel.setText("Makine IP Adresi:");
        mainLayout.addView(tvMachineIPLabel);

        etMachineIP = new EditText(this);
        etMachineIP.setHint("192.168.1.100");
        mainLayout.addView(etMachineIP);

        // Makine Port
        TextView tvMachinePortLabel = new TextView(this);
        tvMachinePortLabel.setText("Makine Port:");
        mainLayout.addView(tvMachinePortLabel);

        etMachinePort = new EditText(this);
        etMachinePort.setHint("8080");
        mainLayout.addView(etMachinePort);

        // Butonlar
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        btnSave = new Button(this);
        btnSave.setText("Kaydet");
        buttonLayout.addView(btnSave);

        btnTestConnection = new Button(this);
        btnTestConnection.setText("Bağlantı Testi");
        buttonLayout.addView(btnTestConnection);

        btnBack = new Button(this);
        btnBack.setText("Geri");
        buttonLayout.addView(btnBack);

        mainLayout.addView(buttonLayout);

        setContentView(mainLayout);
    }

    private void loadCurrentConfig() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        etMachineId.setText(prefs.getString("machine_id", ""));
        etIotNumber.setText(prefs.getString("iot_number", ""));
        etSerialNumber.setText(prefs.getString("serial_number", ""));
        etLocation.setText(prefs.getString("location", "Turkey"));
        etOperatorId.setText(prefs.getString("operator_id", ""));
        etMachineIP.setText(prefs.getString("machine_ip", "192.168.1.100"));
        etMachinePort.setText(String.valueOf(prefs.getInt("machine_port", 8080)));
    }

    private void setupEventListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfiguration();
            }
        });

        btnTestConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testConnection();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveConfiguration() {
        String machineId = etMachineId.getText().toString().trim();
        String iotNumber = etIotNumber.getText().toString().trim();
        String serialNumber = etSerialNumber.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String operatorId = etOperatorId.getText().toString().trim();
        String machineIP = etMachineIP.getText().toString().trim();
        String machinePortStr = etMachinePort.getText().toString().trim();

        // Validasyon
        if (machineId.isEmpty() || iotNumber.isEmpty() || serialNumber.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm zorunlu alanları doldurun!", Toast.LENGTH_SHORT).show();
            return;
        }

        int machinePort;
        try {
            machinePort = Integer.parseInt(machinePortStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Geçerli bir port numarası giriniz!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Konfigürasyonu kaydet
        telemetryManager.updateMachineInfo(machineId, iotNumber, serialNumber,
                location, operatorId, machineIP, machinePort);

        Toast.makeText(this, "Konfigürasyon başarıyla kaydedildi!", Toast.LENGTH_SHORT).show();
    }

    private void testConnection() {
        btnTestConnection.setEnabled(false);
        btnTestConnection.setText("Test Ediliyor...");

        telemetryManager.testConnection(new TelemetryManager.ConnectionTestCallback() {
            @Override
            public void onConnectionTestResult(boolean success, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnTestConnection.setEnabled(true);
                        if (success) {
                            btnTestConnection.setText("Bağlantı Başarılı");
                            Toast.makeText(MachineConfigActivity.this,
                                    "Firebase bağlantısı başarılı!", Toast.LENGTH_SHORT).show();
                        } else {
                            btnTestConnection.setText("Bağlantı Başarısız");
                            Toast.makeText(MachineConfigActivity.this,
                                    "Firebase bağlantısı başarısız: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
