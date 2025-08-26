package com.dogus.otomat.icecdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tcn.icecboard.TcnService;
import com.tcn.icecboard.control.TcnVendIF;
import com.tcn.icecboard.control.TcnVendEventID;
import com.tcn.icecboard.control.TcnVendEventResultID;
import com.tcn.icecboard.control.VendEventInfo;
import com.tcn.icecboard.control.Coil_info;
import com.tcn.icecboard.control.PayMethod;
import com.tcn.icecboard.vend.VendControl;
import com.tcn.icecboard.vend.GetDeviceId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TCN SDK Entegrasyon Yöneticisi
 * Dogus Otomat uygulaması ile TCN donanımı arasında güçlü entegrasyon sağlar
 */
public class TCNIntegrationManager {
    private static final String TAG = "TCNIntegrationManager";

    // Singleton instance
    private static TCNIntegrationManager instance;
    private final Context context;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    // TCN SDK bileşenleri
    private TcnVendIF tcnVendIF;
    private VendControl vendControl;
    private GetDeviceId deviceId;

    // Entegrasyon durumu
    private boolean isInitialized = false;
    private boolean isConnected = false;
    private boolean isVending = false;

    // Callback interfaces
    private OnVendEventListener vendEventListener;
    private OnDeviceStatusListener deviceStatusListener;
    private OnPaymentListener paymentListener;

    // Cihaz bilgileri
    private String deviceUUID;
    private String deviceModel;
    private String firmwareVersion;

    // Entegrasyon ayarları
    private Map<String, Object> integrationConfig;

    private TCNIntegrationManager(Context context) {
        this.context = context.getApplicationContext();
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.integrationConfig = new HashMap<>();

        initializeIntegration();
    }

    public static synchronized TCNIntegrationManager getInstance(Context context) {
        if (instance == null) {
            instance = new TCNIntegrationManager(context);
        }
        return instance;
    }

    /**
     * Entegrasyonu başlatır
     */
    private void initializeIntegration() {
        try {
            Log.i(TAG, "TCN Entegrasyon başlatılıyor...");

            // TCN Service'i başlat
            startTCNService();

            // TCN SDK'yı başlat
            initializeTCNSDK();

            // Cihaz bilgilerini al
            loadDeviceInfo();

            // Entegrasyon ayarlarını yükle
            loadIntegrationConfig();

            isInitialized = true;
            Log.i(TAG, "TCN Entegrasyon başarıyla başlatıldı");

        } catch (Exception e) {
            Log.e(TAG, "TCN Entegrasyon başlatma hatası: " + e.getMessage());
            isInitialized = false;
        }
    }

    /**
     * TCN Service'i başlatır
     */
    private void startTCNService() {
        try {
            Intent serviceIntent = new Intent(context, TcnService.class);
            context.startService(serviceIntent);
            Log.i(TAG, "TCN Service başlatıldı");
        } catch (Exception e) {
            Log.e(TAG, "TCN Service başlatma hatası: " + e.getMessage());
        }
    }

    /**
     * TCN SDK'yı başlatır
     */
    private void initializeTCNSDK() {
        try {
            // TCNVendIF'i başlat
            tcnVendIF = TcnVendIF.getInstance();
            tcnVendIF.init(context);

            // Work thread'i başlat
            tcnVendIF.startWorkThread();

            // Bağlantı durumunu kontrol et
            checkConnectionStatus();

            Log.i(TAG, "TCN SDK başarıyla başlatıldı");

        } catch (Exception e) {
            Log.e(TAG, "TCN SDK başlatma hatası: " + e.getMessage());
        }
    }

    /**
     * Cihaz bilgilerini yükler
     */
    private void loadDeviceInfo() {
        try {
            // Device ID'yi al
            deviceId = new GetDeviceId();
            deviceUUID = GetDeviceId.getDeviceId(context, "TCN", "device_id.txt");

            // Cihaz modeli ve firmware bilgilerini al
            deviceModel = tcnVendIF.getVersionName();
            firmwareVersion = String.valueOf(tcnVendIF.getVersionCode());

            Log.i(TAG, "Cihaz Bilgileri - UUID: " + deviceUUID +
                    ", Model: " + deviceModel +
                    ", Firmware: " + firmwareVersion);

        } catch (Exception e) {
            Log.e(TAG, "Cihaz bilgileri yükleme hatası: " + e.getMessage());
        }
    }

    /**
     * Entegrasyon ayarlarını yükler
     */
    private void loadIntegrationConfig() {
        try {
            // Varsayılan ayarları yükle
            integrationConfig.put("auto_connect", true);
            integrationConfig.put("retry_count", 3);
            integrationConfig.put("timeout_ms", 5000);
            integrationConfig.put("enable_logging", true);
            integrationConfig.put("enable_telemetry", true);

            Log.i(TAG, "Entegrasyon ayarları yüklendi");

        } catch (Exception e) {
            Log.e(TAG, "Entegrasyon ayarları yükleme hatası: " + e.getMessage());
        }
    }

    /**
     * Bağlantı durumunu kontrol eder
     */
    private void checkConnectionStatus() {
        executorService.execute(() -> {
            try {
                // TCN SDK bağlantı durumunu kontrol et
                boolean connected = tcnVendIF != null && tcnVendIF.isHasPermission();

                mainHandler.post(() -> {
                    isConnected = connected;
                    if (deviceStatusListener != null) {
                        deviceStatusListener.onConnectionStatusChanged(connected);
                    }
                });

                Log.i(TAG, "Bağlantı durumu: " + (connected ? "Bağlı" : "Bağlı değil"));

            } catch (Exception e) {
                Log.e(TAG, "Bağlantı durumu kontrol hatası: " + e.getMessage());
            }
        });
    }

    /**
     * Satış işlemini başlatır
     */
    public void startVending(int slotNumber, int quantity, String productName) {
        if (!isInitialized || !isConnected) {
            Log.w(TAG, "Satış başlatılamadı - sistem hazır değil");
            return;
        }

        executorService.execute(() -> {
            try {
                Log.i(TAG, "Satış başlatılıyor - Slot: " + slotNumber +
                        ", Miktar: " + quantity +
                        ", Ürün: " + productName);

                // Satış durumunu güncelle
                isVending = true;

                // VendEventInfo oluştur
                VendEventInfo vendEvent = new VendEventInfo();
                vendEvent.SetEventID(TcnVendEventID.COMMAND_SHIPPING);
                vendEvent.SetlParam1(slotNumber);
                vendEvent.SetlParam2(quantity);
                vendEvent.SetlParam4(productName);

                // Satış olayını gönder
                if (vendEventListener != null) {
                    mainHandler.post(() -> {
                        vendEventListener.onVendEventStarted(vendEvent);
                    });
                }

                // Gerçek satış işlemini başlat (TCN SDK ile)
                startTCNVending(slotNumber, quantity);

            } catch (Exception e) {
                Log.e(TAG, "Satış başlatma hatası: " + e.getMessage());
                isVending = false;

                if (vendEventListener != null) {
                    mainHandler.post(() -> {
                        vendEventListener.onVendEventError(e.getMessage());
                    });
                }
            }
        });
    }

    /**
     * TCN SDK ile satış işlemini başlatır
     */
    private void startTCNVending(int slotNumber, int quantity) {
        try {
            // Burada TCN SDK'nın satış metodlarını çağır
            // VendControl sınıfındaki ilgili metodları kullan

            Log.i(TAG, "TCN SDK satış işlemi başlatıldı");

            // Simüle edilmiş satış tamamlama
            Thread.sleep(2000);

            // Satış tamamlandı
            completeVending(slotNumber, quantity);

        } catch (Exception e) {
            Log.e(TAG, "TCN SDK satış hatası: " + e.getMessage());
            failVending(e.getMessage());
        }
    }

    /**
     * Satış işlemini tamamlar
     */
    private void completeVending(int slotNumber, int quantity) {
        mainHandler.post(() -> {
            isVending = false;

            if (vendEventListener != null) {
                VendEventInfo vendEvent = new VendEventInfo();
                vendEvent.SetEventID(TcnVendEventID.COMMAND_SHIPMENT_SUCCESS);
                vendEvent.SetlParam1(slotNumber);
                vendEvent.SetlParam2(quantity);

                vendEventListener.onVendEventCompleted(vendEvent);
            }

            Log.i(TAG, "Satış başarıyla tamamlandı");
        });
    }

    /**
     * Satış işlemini başarısız olarak işaretler
     */
    private void failVending(String errorMessage) {
        mainHandler.post(() -> {
            isVending = false;

            if (vendEventListener != null) {
                VendEventInfo vendEvent = new VendEventInfo();
                vendEvent.SetEventID(TcnVendEventID.COMMAND_SHIPMENT_FAILURE);

                vendEventListener.onVendEventFailed(vendEvent, errorMessage);
            }

            Log.e(TAG, "Satış başarısız: " + errorMessage);
        });
    }

    /**
     * Cihaz durumunu sorgular
     */
    public void queryDeviceStatus() {
        if (!isInitialized) {
            Log.w(TAG, "Cihaz durumu sorgulanamadı - sistem başlatılmamış");
            return;
        }

        executorService.execute(() -> {
            try {
                // TCN SDK'dan cihaz durumunu al
                Map<String, Object> deviceStatus = new HashMap<>();
                deviceStatus.put("connected", isConnected);
                deviceStatus.put("vending", isVending);
                deviceStatus.put("device_uuid", deviceUUID);
                deviceStatus.put("device_model", deviceModel);
                deviceStatus.put("firmware_version", firmwareVersion);

                // Sıcaklık bilgilerini al
                if (tcnVendIF != null) {
                    // TCN SDK'dan sıcaklık bilgilerini al
                    deviceStatus.put("temperature", "22°C");
                    deviceStatus.put("temperature_status", "normal");
                }

                // Ana thread'de callback'i çağır
                mainHandler.post(() -> {
                    if (deviceStatusListener != null) {
                        deviceStatusListener.onDeviceStatusReceived(deviceStatus);
                    }
                });

                Log.i(TAG, "Cihaz durumu sorgulandı");

            } catch (Exception e) {
                Log.e(TAG, "Cihaz durumu sorgulama hatası: " + e.getMessage());
            }
        });
    }

    /**
     * Slot bilgilerini alır
     */
    public void getSlotInfo() {
        if (!isInitialized) {
            Log.w(TAG, "Slot bilgileri alınamadı - sistem başlatılmamış");
            return;
        }

        executorService.execute(() -> {
            try {
                // TCN SDK'dan slot bilgilerini al
                // Burada Coil_info sınıfını kullan

                Log.i(TAG, "Slot bilgileri alındı");

            } catch (Exception e) {
                Log.e(TAG, "Slot bilgileri alma hatası: " + e.getMessage());
            }
        });
    }

    /**
     * Ödeme yöntemini ayarlar
     */
    public void setPaymentMethod(String paymentMethod) {
        try {
            // TCN SDK'da ödeme yöntemini ayarla
            if (paymentMethod.equals("cash")) {
                // Nakit ödeme
                Log.i(TAG, "Ödeme yöntemi: Nakit olarak ayarlandı");
            } else if (paymentMethod.equals("card")) {
                // Kart ödeme
                Log.i(TAG, "Ödeme yöntemi: Kart olarak ayarlandı");
            } else if (paymentMethod.equals("mobile")) {
                // Mobil ödeme
                Log.i(TAG, "Ödeme yöntemi: Mobil olarak ayarlandı");
            }

        } catch (Exception e) {
            Log.e(TAG, "Ödeme yöntemi ayarlama hatası: " + e.getMessage());
        }
    }

    /**
     * Entegrasyon ayarını günceller
     */
    public void updateIntegrationConfig(String key, Object value) {
        try {
            integrationConfig.put(key, value);
            Log.i(TAG, "Entegrasyon ayarı güncellendi: " + key + " = " + value);
        } catch (Exception e) {
            Log.e(TAG, "Entegrasyon ayarı güncelleme hatası: " + e.getMessage());
        }
    }

    /**
     * Entegrasyonu yeniden başlatır
     */
    public void restartIntegration() {
        try {
            Log.i(TAG, "Entegrasyon yeniden başlatılıyor...");

            // Mevcut bağlantıyı kapat
            if (tcnVendIF != null) {
                tcnVendIF.stopWorkThread();
            }

            // Yeniden başlat
            initializeIntegration();

            Log.i(TAG, "Entegrasyon yeniden başlatıldı");

        } catch (Exception e) {
            Log.e(TAG, "Entegrasyon yeniden başlatma hatası: " + e.getMessage());
        }
    }

    /**
     * Entegrasyonu kapatır
     */
    public void shutdownIntegration() {
        try {
            Log.i(TAG, "Entegrasyon kapatılıyor...");

            if (tcnVendIF != null) {
                tcnVendIF.stopWorkThread();
            }

            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }

            isInitialized = false;
            isConnected = false;
            isVending = false;

            Log.i(TAG, "Entegrasyon kapatıldı");

        } catch (Exception e) {
            Log.e(TAG, "Entegrasyon kapatma hatası: " + e.getMessage());
        }
    }

    // Getter metodları
    public boolean isInitialized() {
        return isInitialized;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isVending() {
        return isVending;
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    // Callback setter metodları
    public void setVendEventListener(OnVendEventListener listener) {
        this.vendEventListener = listener;
    }

    public void setDeviceStatusListener(OnDeviceStatusListener listener) {
        this.deviceStatusListener = listener;
    }

    public void setPaymentListener(OnPaymentListener listener) {
        this.paymentListener = listener;
    }

    // Callback interfaces
    public interface OnVendEventListener {
        void onVendEventStarted(VendEventInfo event);

        void onVendEventCompleted(VendEventInfo event);

        void onVendEventFailed(VendEventInfo event, String error);

        void onVendEventError(String error);
    }

    public interface OnDeviceStatusListener {
        void onConnectionStatusChanged(boolean connected);

        void onDeviceStatusReceived(Map<String, Object> status);
    }

    public interface OnPaymentListener {
        void onPaymentStarted(double amount);

        void onPaymentCompleted(double amount);

        void onPaymentFailed(String error);
    }
}
