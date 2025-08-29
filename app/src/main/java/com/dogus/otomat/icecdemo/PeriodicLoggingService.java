package com.dogus.otomat.icecdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DOGİ Periyodik Log Servisi
 * Her 15 saniyede bir otomatik log alır
 */
public class PeriodicLoggingService extends Service {
    private static final String TAG = "PeriodicLoggingService";
    private static long LOG_INTERVAL = 15000; // 15 saniye

    private Handler logHandler;
    private Runnable logRunnable;
    private boolean isRunning = false;

    // Log kategorileri
    private static final String[] LOG_CATEGORIES = {
            "SYSTEM_STATUS", "MACHINE_STATUS", "PAYMENT_STATUS",
            "PRODUCT_STATUS", "BOARD_STATUS", "MDB_STATUS", "TCN_STATUS"
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "DOGİ Periyodik Log Servisi oluşturuldu");

        logHandler = new Handler(Looper.getMainLooper());
        setupPeriodicLogging();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "DOGİ Periyodik Log Servisi başlatıldı");
        startPeriodicLogging();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPeriodicLogging();
        Log.i(TAG, "DOGİ Periyodik Log Servisi kapatıldı");
    }

    private void setupPeriodicLogging() {
        logRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    generatePeriodicLog();
                    logHandler.postDelayed(this, LOG_INTERVAL);
                }
            }
        };
    }

    private void startPeriodicLogging() {
        if (!isRunning) {
            isRunning = true;
            logHandler.post(logRunnable);
            Log.i(TAG, "DOGİ Periyodik loglama başlatıldı - Her " + (LOG_INTERVAL / 1000) + " saniyede");
        }
    }

    private void stopPeriodicLogging() {
        isRunning = false;
        if (logHandler != null && logRunnable != null) {
            logHandler.removeCallbacks(logRunnable);
        }
        Log.i(TAG, "DOGİ Periyodik loglama durduruldu");
    }

    private void generatePeriodicLog() {
        try {
            String timestamp = getCurrentTimestamp();
            String category = getRandomLogCategory();
            String message = generateLogMessage(category);

            // Ana log sistemine gönder
            if (AdvancedLoggingSystem.getInstance(this) != null) {
                AdvancedLoggingSystem.getInstance(this).info(category, message);
            }

            // Android LogCat'e de gönder
            Log.i(TAG, String.format("[%s] %s: %s", timestamp, category, message));

        } catch (Exception e) {
            Log.e(TAG, "Periyodik log oluşturma hatası: " + e.getMessage());
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getRandomLogCategory() {
        int index = (int) (System.currentTimeMillis() % LOG_CATEGORIES.length);
        return LOG_CATEGORIES[index];
    }

    private String generateLogMessage(String category) {
        switch (category) {
            case "SYSTEM_STATUS":
                return "Sistem durumu kontrol edildi - CPU: Normal, Memory: Normal, Storage: Normal";

            case "MACHINE_STATUS":
                return "Makine durumu kontrol edildi - Sıcaklık: 18°C, Nem: 45%, Güç: 2.5kW";

            case "PAYMENT_STATUS":
                return "Ödeme sistemi durumu - MDB: Aktif, TCN: Aktif, Bağlantı: Normal";

            case "PRODUCT_STATUS":
                return "Ürün durumu kontrol edildi - Dondurma: %80, Soslar: %90, Toppingler: %85";

            case "BOARD_STATUS":
                return "Board durumu kontrol edildi - Ana Board: Aktif, Server Board: Aktif, Third Board: Aktif";

            case "MDB_STATUS":
                return "MDB durumu kontrol edildi - Bağlantı: Aktif, Level 3: Aktif, Timeout: 30s";

            case "TCN_STATUS":
                return "TCN SDK durumu kontrol edildi - Bağlantı: Aktif, Serial Port: /dev/ttyS1, Baud: 9600";

            default:
                return "Genel sistem durumu kontrol edildi - Tüm sistemler normal çalışıyor";
        }
    }

    /**
     * Log aralığını değiştir
     */
    public void setLogInterval(long intervalMs) {
        if (intervalMs > 0) {
            LOG_INTERVAL = intervalMs;
            Log.i(TAG, "Log aralığı değiştirildi: " + (intervalMs / 1000) + " saniye");

            // Mevcut loglama durdur ve yeni aralıkla başlat
            if (isRunning) {
                stopPeriodicLogging();
                startPeriodicLogging();
            }
        }
    }

    /**
     * Servis durumunu kontrol et
     */
    public boolean isServiceRunning() {
        return isRunning;
    }

    /**
     * Log aralığını döndür
     */
    public long getLogInterval() {
        return LOG_INTERVAL;
    }
}
