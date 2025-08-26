package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DolumPanelActivity extends AppCompatActivity {
    
    private EditText etIceCreamLevel, etSauceLevel, etToppingLevel;
    private EditText etWaterLevel, etMaintenanceNotes;
    private Button btnRefillIceCream, btnRefillSauce, btnRefillTopping;
    private Button btnRefillWater, btnSaveMaintenance, btnBackToSales;
    private Button btnCheckLevels, btnEmergencyStop, btnResetMachine;
    private TextView tvCurrentLevels, tvMaintenanceStatus;
    
    private SharedPreferences sharedPreferences;
    private TelemetryManager telemetryManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dolum_panel);
        
        initializeViews();
        loadCurrentLevels();
        setupClickListeners();
        
        sharedPreferences = getSharedPreferences("DolumPrefs", MODE_PRIVATE);
        telemetryManager = TelemetryManager.getInstance(this);
    }
    
    private void initializeViews() {
        etIceCreamLevel = findViewById(R.id.etIceCreamLevel);
        etSauceLevel = findViewById(R.id.etSauceLevel);
        etToppingLevel = findViewById(R.id.etToppingLevel);
        etWaterLevel = findViewById(R.id.etWaterLevel);
        etMaintenanceNotes = findViewById(R.id.etMaintenanceNotes);
        
        btnRefillIceCream = findViewById(R.id.btnRefillIceCream);
        btnRefillSauce = findViewById(R.id.btnRefillSauce);
        btnRefillTopping = findViewById(R.id.btnRefillTopping);
        btnRefillWater = findViewById(R.id.btnRefillWater);
        btnSaveMaintenance = findViewById(R.id.btnSaveMaintenance);
        btnBackToSales = findViewById(R.id.btnBackToSales);
        btnCheckLevels = findViewById(R.id.btnCheckLevels);
        btnEmergencyStop = findViewById(R.id.btnEmergencyStop);
        btnResetMachine = findViewById(R.id.btnResetMachine);
        
        tvCurrentLevels = findViewById(R.id.tvCurrentLevels);
        tvMaintenanceStatus = findViewById(R.id.tvMaintenanceStatus);
    }
    
    private void loadCurrentLevels() {
        // Load current material levels
        int iceCreamLevel = sharedPreferences.getInt("ice_cream_level", 80);
        int sauceLevel = sharedPreferences.getInt("sauce_level", 70);
        int toppingLevel = sharedPreferences.getInt("topping_level", 75);
        int waterLevel = sharedPreferences.getInt("water_level", 90);
        
        etIceCreamLevel.setText(String.valueOf(iceCreamLevel));
        etSauceLevel.setText(String.valueOf(sauceLevel));
        etToppingLevel.setText(String.valueOf(toppingLevel));
        etWaterLevel.setText(String.valueOf(waterLevel));
        
        // Load maintenance notes
        String maintenanceNotes = sharedPreferences.getString("maintenance_notes", "");
        etMaintenanceNotes.setText(maintenanceNotes);
        
        updateCurrentLevelsDisplay();
        updateMaintenanceStatus();
    }
    
    private void setupClickListeners() {
        btnRefillIceCream.setOnClickListener(v -> refillMaterial("ice_cream", "Dondurma"));
        btnRefillSauce.setOnClickListener(v -> refillMaterial("sauce", "Sos"));
        btnRefillTopping.setOnClickListener(v -> refillMaterial("topping", "Süsleme"));
        btnRefillWater.setOnClickListener(v -> refillMaterial("water", "Su"));
        btnSaveMaintenance.setOnClickListener(v -> saveMaintenanceNotes());
        btnBackToSales.setOnClickListener(v -> finish());
        btnCheckLevels.setOnClickListener(v -> checkAllLevels());
        btnEmergencyStop.setOnClickListener(v -> emergencyStop());
        btnResetMachine.setOnClickListener(v -> resetMachine());
    }
    
    private void refillMaterial(String materialType, String materialName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(materialName + " Dolumu");
        
        final EditText input = new EditText(this);
        input.setHint("Yeni seviye (0-100)");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        
        builder.setPositiveButton("Doldur", (dialog, which) -> {
            try {
                int newLevel = Integer.parseInt(input.getText().toString());
                if (newLevel >= 0 && newLevel <= 100) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(materialType + "_level", newLevel);
                    editor.apply();
                    
                    // Update display
                    switch (materialType) {
                        case "ice_cream":
                            etIceCreamLevel.setText(String.valueOf(newLevel));
                            break;
                        case "sauce":
                            etSauceLevel.setText(String.valueOf(newLevel));
                            break;
                        case "topping":
                            etToppingLevel.setText(String.valueOf(newLevel));
                            break;
                        case "water":
                            etWaterLevel.setText(String.valueOf(newLevel));
                            break;
                    }
                    
                    updateCurrentLevelsDisplay();
                    
                    // Send telemetry data
                    telemetryManager.sendMachineStatus("Material Refilled", 
                                                    materialName + " seviyesi " + newLevel + "% olarak dolduruldu");
                    
                    showToast(materialName + " dolduruldu! Yeni seviye: " + newLevel + "%");
                } else {
                    showToast("Seviye 0-100 arasında olmalı!");
                }
            } catch (NumberFormatException e) {
                showToast("Geçersiz seviye!");
            }
        });
        
        builder.setNegativeButton("İptal", null);
        builder.show();
    }
    
    private void saveMaintenanceNotes() {
        String notes = etMaintenanceNotes.getText().toString();
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("maintenance_notes", notes);
        editor.putLong("last_maintenance", System.currentTimeMillis());
        editor.apply();
        
        updateMaintenanceStatus();
        showToast("Bakım notları kaydedildi!");
        
        // Send telemetry data
        telemetryManager.sendMachineStatus("Maintenance", "Bakım notları güncellendi: " + notes);
    }
    
    private void checkAllLevels() {
        int iceCreamLevel = Integer.parseInt(etIceCreamLevel.getText().toString());
        int sauceLevel = Integer.parseInt(etSauceLevel.getText().toString());
        int toppingLevel = Integer.parseInt(etToppingLevel.getText().toString());
        int waterLevel = Integer.parseInt(etWaterLevel.getText().toString());
        
        StringBuilder status = new StringBuilder();
        status.append("Malzeme Seviyeleri:\n\n");
        status.append("Dondurma: ").append(iceCreamLevel).append("%\n");
        status.append("Sos: ").append(sauceLevel).append("%\n");
        status.append("Süsleme: ").append(toppingLevel).append("%\n");
        status.append("Su: ").append(waterLevel).append("%\n\n");
        
        // Check for low levels
        if (iceCreamLevel < 20) status.append("⚠️ Dondurma seviyesi düşük!\n");
        if (sauceLevel < 20) status.append("⚠️ Sos seviyesi düşük!\n");
        if (toppingLevel < 20) status.append("⚠️ Süsleme seviyesi düşük!\n");
        if (waterLevel < 20) status.append("⚠️ Su seviyesi düşük!\n");
        
        if (iceCreamLevel >= 20 && sauceLevel >= 20 && toppingLevel >= 20 && waterLevel >= 20) {
            status.append("✅ Tüm seviyeler yeterli");
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seviye Kontrolü");
        builder.setMessage(status.toString());
        builder.setPositiveButton("Tamam", null);
        builder.show();
    }
    
    private void emergencyStop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acil Durdur");
        builder.setMessage("Makineyi acil durdurma moduna almak istediğinizden emin misiniz?");
        
        builder.setPositiveButton("Evet, Durdur", (dialog, which) -> {
            // Set machine to emergency mode
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("machine_mode", "Acil Durum");
            editor.putLong("emergency_stop_time", System.currentTimeMillis());
            editor.apply();
            
            // Send telemetry data
            telemetryManager.sendMachineStatus("Emergency Stop", "Makine acil durdurma moduna alındı");
            
            showToast("Makine acil durdurma moduna alındı!");
        });
        
        builder.setNegativeButton("İptal", null);
        builder.show();
    }
    
    private void resetMachine() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Makine Sıfırlama");
        builder.setMessage("Makineyi normal moda sıfırlamak istediğinizden emin misiniz?");
        
        builder.setPositiveButton("Evet, Sıfırla", (dialog, which) -> {
            // Reset machine to normal mode
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("machine_mode", "Normal");
            editor.remove("emergency_stop_time");
            editor.apply();
            
            // Send telemetry data
            telemetryManager.sendMachineStatus("Machine Reset", "Makine normal moda sıfırlandı");
            
            showToast("Makine normal moda sıfırlandı!");
        });
        
        builder.setNegativeButton("İptal", null);
        builder.show();
    }
    
    private void updateCurrentLevelsDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mevcut Seviyeler:\n\n");
        sb.append("Dondurma: ").append(etIceCreamLevel.getText()).append("%\n");
        sb.append("Sos: ").append(etSauceLevel.getText()).append("%\n");
        sb.append("Süsleme: ").append(etToppingLevel.getText()).append("%\n");
        sb.append("Su: ").append(etWaterLevel.getText()).append("%");
        
        tvCurrentLevels.setText(sb.toString());
    }
    
    private void updateMaintenanceStatus() {
        long lastMaintenance = sharedPreferences.getLong("last_maintenance", 0);
        String notes = sharedPreferences.getString("maintenance_notes", "");
        
        StringBuilder sb = new StringBuilder();
        sb.append("Bakım Durumu:\n\n");
        
        if (lastMaintenance > 0) {
            long timeSinceMaintenance = System.currentTimeMillis() - lastMaintenance;
            long daysSinceMaintenance = timeSinceMaintenance / (24 * 60 * 60 * 1000);
            sb.append("Son Bakım: ").append(daysSinceMaintenance).append(" gün önce\n\n");
        } else {
            sb.append("Henüz bakım yapılmamış\n\n");
        }
        
        if (!notes.isEmpty()) {
            sb.append("Son Notlar:\n").append(notes);
        } else {
            sb.append("Bakım notu bulunmuyor");
        }
        
        tvMaintenanceStatus.setText(sb.toString());
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
