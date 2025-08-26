package com.dogus.otomat.icecdemo;

import android.content.Context;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

/**
 * Telemetri Yöneticisi
 * Makine durumu ve performans verilerini Firebase'e gönderir
 */
public class TelemetryManager {
    private static final String TAG = "TelemetryManager";
    private static TelemetryManager instance;
    private final Context context;
    private final DatabaseReference databaseRef;
    private boolean isEnabled = true;

    private TelemetryManager(Context context) {
        this.context = context;
        this.databaseRef = FirebaseDatabase.getInstance().getReference("telemetry");
    }

    public static synchronized TelemetryManager getInstance(Context context) {
        if (instance == null) {
            instance = new TelemetryManager(context);
        }
        return instance;
    }

    /**
     * Makine durumunu gönderir
     */
    public void sendMachineStatus(String status, String message) {
        if (!isEnabled)
            return;

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("status", status);
            data.put("message", message);
            data.put("timestamp", System.currentTimeMillis());
            data.put("device_id", getDeviceId());

            databaseRef.child("machine_status").push().setValue(data);
            Log.i(TAG, "Makine durumu gönderildi: " + status);
        } catch (Exception e) {
            Log.e(TAG, "Makine durumu gönderilemedi: " + e.getMessage());
        }
    }

    /**
     * Performans verilerini gönderir
     */
    public void sendPerformanceData(String metric, long value, String unit) {
        if (!isEnabled)
            return;

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("metric", metric);
            data.put("value", value);
            data.put("unit", unit);
            data.put("timestamp", System.currentTimeMillis());
            data.put("device_id", getDeviceId());

            databaseRef.child("performance").push().setValue(data);
            Log.i(TAG, "Performans verisi gönderildi: " + metric + " = " + value + " " + unit);
        } catch (Exception e) {
            Log.e(TAG, "Performans verisi gönderilemedi: " + e.getMessage());
        }
    }

    /**
     * Satış verilerini gönderir
     */
    public void sendSalesData(int slotNo, String productName, double amount, String paymentMethod, boolean success) {
        if (!isEnabled)
            return;

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("slot_no", slotNo);
            data.put("product_name", productName);
            data.put("amount", amount);
            data.put("payment_method", paymentMethod);
            data.put("success", success);
            data.put("timestamp", System.currentTimeMillis());
            data.put("device_id", getDeviceId());

            databaseRef.child("sales").push().setValue(data);
            Log.i(TAG, "Satış verisi gönderildi: " + productName + " - " + amount);
        } catch (Exception e) {
            Log.e(TAG, "Satış verisi gönderilemedi: " + e.getMessage());
        }
    }

    /**
     * Veriyi asenkron olarak gönderir
     */
    public void sendDataAsync(String dataType, Map<String, Object> data) {
        if (!isEnabled)
            return;

        try {
            data.put("timestamp", System.currentTimeMillis());
            data.put("device_id", getDeviceId());

            databaseRef.child(dataType).push().setValue(data);
            Log.i(TAG, "Veri gönderildi: " + dataType);
        } catch (Exception e) {
            Log.e(TAG, "Veri gönderilemedi: " + e.getMessage());
        }
    }

    /**
     * Bağlantı testi yapar
     */
    public void testConnection(ConnectionTestCallback callback) {
        if (!isEnabled) {
            if (callback != null) {
                callback.onConnectionTestResult(false, "Telemetri devre dışı");
            }
            return;
        }

        try {
            // Basit bağlantı testi
            Map<String, Object> testData = new HashMap<>();
            testData.put("test", true);
            testData.put("timestamp", System.currentTimeMillis());

            databaseRef.child("connection_test").push().setValue(testData)
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, "Bağlantı testi başarılı");
                        if (callback != null) {
                            callback.onConnectionTestResult(true, "Bağlantı başarılı");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Bağlantı testi başarısız: " + e.getMessage());
                        if (callback != null) {
                            callback.onConnectionTestResult(false, "Bağlantı başarısız: " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Bağlantı testi hatası: " + e.getMessage());
            if (callback != null) {
                callback.onConnectionTestResult(false, "Test hatası: " + e.getMessage());
            }
        }
    }

    /**
     * Telemetri sistemini temizler
     */
    public void cleanup() {
        try {
            Log.i(TAG, "Telemetri sistemi temizleniyor");
            // Gerekli temizlik işlemleri
        } catch (Exception e) {
            Log.e(TAG, "Telemetri temizleme hatası: " + e.getMessage());
        }
    }

    /**
     * Telemetri sistemini etkinleştirir/devre dışı bırakır
     */
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        Log.i(TAG, "Telemetri sistemi " + (enabled ? "etkinleştirildi" : "devre dışı bırakıldı"));
    }

    /**
     * Cihaz ID'sini alır
     */
    private String getDeviceId() {
        try {
            return android.provider.Settings.Secure.getString(
                    context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            return "unknown_device";
        }
    }

    /**
     * Makine bilgilerini günceller
     */
    public void updateMachineInfo(String machineId, String iotNumber, String serialNumber,
            String location, String operatorId, String machineIP, int machinePort) {
        try {
            Map<String, Object> machineInfo = new HashMap<>();
            machineInfo.put("machine_id", machineId);
            machineInfo.put("iot_number", iotNumber);
            machineInfo.put("serial_number", serialNumber);
            machineInfo.put("location", location);
            machineInfo.put("operator_id", operatorId);
            machineInfo.put("machine_ip", machineIP);
            machineInfo.put("machine_port", machinePort);
            machineInfo.put("timestamp", System.currentTimeMillis());
            machineInfo.put("device_id", getDeviceId());

            databaseRef.child("machine_info").push().setValue(machineInfo);
            Log.i(TAG, "Makine bilgileri güncellendi: " + machineId);
        } catch (Exception e) {
            Log.e(TAG, "Makine bilgileri güncellenemedi: " + e.getMessage());
        }
    }

    /**
     * Bağlantı test callback interface'i
     */
    public interface ConnectionTestCallback {
        void onConnectionTestResult(boolean success, String message);
    }
}
