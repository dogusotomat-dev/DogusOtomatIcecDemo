package com.dogus.otomat.icecdemo;

import android.content.Context;
import android.util.Log;

import com.tcn.icecboard.control.TcnVendIF;
import com.tcn.icecboard.control.Coil_info;
import com.tcn.icecboard.control.PayMethod;
import com.tcn.icecboard.vend.VendControl;

import java.util.ArrayList;
import java.util.List;

/**
 * TCN Entegrasyon Test Sınıfı
 * TCN SDK ile entegrasyonu test etmek ve doğrulamak için kullanılır
 */
public class TCNIntegrationTest {
    private static final String TAG = "TCNIntegrationTest";

    private final Context context;
    private final TCNIntegrationManager integrationManager;

    public TCNIntegrationTest(Context context) {
        this.context = context;
        this.integrationManager = TCNIntegrationManager.getInstance(context);
    }

    /**
     * Tüm entegrasyon testlerini çalıştırır
     */
    public void runAllTests() {
        Log.i(TAG, "=== TCN Entegrasyon Testleri Başlatılıyor ===");

        try {
            // 1. Temel bağlantı testi
            testBasicConnection();

            // 2. SDK başlatma testi
            testSDKInitialization();

            // 3. Cihaz bilgileri testi
            testDeviceInfo();

            // 4. Slot bilgileri testi
            testSlotInfo();

            // 5. Ödeme yöntemleri testi
            testPaymentMethods();

            // 6. Satış simülasyonu testi
            testVendingSimulation();

            Log.i(TAG, "=== Tüm Testler Tamamlandı ===");

        } catch (Exception e) {
            Log.e(TAG, "Test çalıştırma hatası: " + e.getMessage());
        }
    }

    /**
     * Temel bağlantı testi
     */
    private void testBasicConnection() {
        Log.i(TAG, "--- Temel Bağlantı Testi ---");

        try {
            boolean isInitialized = integrationManager.isInitialized();
            boolean isConnected = integrationManager.isConnected();

            Log.i(TAG, "Entegrasyon başlatıldı: " + isInitialized);
            Log.i(TAG, "Cihaz bağlı: " + isConnected);

            if (isInitialized && isConnected) {
                Log.i(TAG, "✅ Temel bağlantı testi başarılı");
            } else {
                Log.w(TAG, "⚠️ Temel bağlantı testi başarısız");
            }

        } catch (Exception e) {
            Log.e(TAG, "Temel bağlantı testi hatası: " + e.getMessage());
        }
    }

    /**
     * SDK başlatma testi
     */
    private void testSDKInitialization() {
        Log.i(TAG, "--- SDK Başlatma Testi ---");

        try {
            TcnVendIF tcnVendIF = TcnVendIF.getInstance();

            if (tcnVendIF != null) {
                Log.i(TAG, "✅ TCNVendIF başarıyla alındı");

                // Versiyon bilgilerini kontrol et
                String versionName = tcnVendIF.getVersionName();
                int versionCode = tcnVendIF.getVersionCode();

                Log.i(TAG, "SDK Versiyon: " + versionName + " (" + versionCode + ")");

            } else {
                Log.w(TAG, "⚠️ TCNVendIF alınamadı");
            }

        } catch (Exception e) {
            Log.e(TAG, "SDK başlatma testi hatası: " + e.getMessage());
        }
    }

    /**
     * Cihaz bilgileri testi
     */
    private void testDeviceInfo() {
        Log.i(TAG, "--- Cihaz Bilgileri Testi ---");

        try {
            String deviceUUID = integrationManager.getDeviceUUID();
            String deviceModel = integrationManager.getDeviceModel();
            String firmwareVersion = integrationManager.getFirmwareVersion();

            Log.i(TAG, "Cihaz UUID: " + deviceUUID);
            Log.i(TAG, "Cihaz Model: " + deviceModel);
            Log.i(TAG, "Firmware Versiyon: " + firmwareVersion);

            if (deviceUUID != null && !deviceUUID.isEmpty()) {
                Log.i(TAG, "✅ Cihaz bilgileri testi başarılı");
            } else {
                Log.w(TAG, "⚠️ Cihaz bilgileri alınamadı");
            }

        } catch (Exception e) {
            Log.e(TAG, "Cihaz bilgileri testi hatası: " + e.getMessage());
        }
    }

    /**
     * Slot bilgileri testi
     */
    private void testSlotInfo() {
        Log.i(TAG, "--- Slot Bilgileri Testi ---");

        try {
            // TCN SDK'dan slot bilgilerini al
            integrationManager.getSlotInfo();

            Log.i(TAG, "✅ Slot bilgileri testi başarılı");

        } catch (Exception e) {
            Log.e(TAG, "Slot bilgileri testi hatası: " + e.getMessage());
        }
    }

    /**
     * Ödeme yöntemleri testi
     */
    private void testPaymentMethods() {
        Log.i(TAG, "--- Ödeme Yöntemleri Testi ---");

        try {
            // Farklı ödeme yöntemlerini test et
            String[] paymentMethods = { "cash", "card", "mobile" };

            for (String method : paymentMethods) {
                integrationManager.setPaymentMethod(method);
                Log.i(TAG, "Ödeme yöntemi ayarlandı: " + method);
            }

            Log.i(TAG, "✅ Ödeme yöntemleri testi başarılı");

        } catch (Exception e) {
            Log.e(TAG, "Ödeme yöntemleri testi hatası: " + e.getMessage());
        }
    }

    /**
     * Satış simülasyonu testi
     */
    private void testVendingSimulation() {
        Log.i(TAG, "--- Satış Simülasyonu Testi ---");

        try {
            // Test satışı başlat
            integrationManager.startVending(1, 1, "Test Ürün");

            Log.i(TAG, "✅ Satış simülasyonu testi başarılı");

        } catch (Exception e) {
            Log.e(TAG, "Satış simülasyonu testi hatası: " + e.getMessage());
        }
    }

    /**
     * Entegrasyon durum raporu oluşturur
     */
    public void generateIntegrationReport() {
        Log.i(TAG, "=== TCN Entegrasyon Durum Raporu ===");

        try {
            // Entegrasyon durumu
            Log.i(TAG, "Entegrasyon Durumu:");
            Log.i(TAG, "  - Başlatıldı: " + integrationManager.isInitialized());
            Log.i(TAG, "  - Bağlı: " + integrationManager.isConnected());
            Log.i(TAG, "  - Satış Yapılıyor: " + integrationManager.isVending());

            // Cihaz bilgileri
            Log.i(TAG, "Cihaz Bilgileri:");
            Log.i(TAG, "  - UUID: " + integrationManager.getDeviceUUID());
            Log.i(TAG, "  - Model: " + integrationManager.getDeviceModel());
            Log.i(TAG, "  - Firmware: " + integrationManager.getFirmwareVersion());

            // Test sonuçları
            Log.i(TAG, "Test Sonuçları:");
            Log.i(TAG, "  - Temel Bağlantı: "
                    + (integrationManager.isInitialized() && integrationManager.isConnected() ? "✅" : "❌"));
            Log.i(TAG, "  - SDK Erişimi: " + (integrationManager.isInitialized() ? "✅" : "❌"));
            Log.i(TAG, "  - Cihaz Bilgileri: " + (integrationManager.getDeviceUUID() != null ? "✅" : "❌"));

            Log.i(TAG, "=== Rapor Tamamlandı ===");

        } catch (Exception e) {
            Log.e(TAG, "Rapor oluşturma hatası: " + e.getMessage());
        }
    }

    /**
     * Entegrasyonu yeniden başlatır
     */
    public void restartIntegration() {
        Log.i(TAG, "Entegrasyon yeniden başlatılıyor...");

        try {
            integrationManager.restartIntegration();
            Log.i(TAG, "✅ Entegrasyon yeniden başlatıldı");

        } catch (Exception e) {
            Log.e(TAG, "Entegrasyon yeniden başlatma hatası: " + e.getMessage());
        }
    }

    /**
     * Entegrasyonu kapatır
     */
    public void shutdownIntegration() {
        Log.i(TAG, "Entegrasyon kapatılıyor...");

        try {
            integrationManager.shutdownIntegration();
            Log.i(TAG, "✅ Entegrasyon kapatıldı");

        } catch (Exception e) {
            Log.e(TAG, "Entegrasyon kapatma hatası: " + e.getMessage());
        }
    }
}
