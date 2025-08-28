package controller;

import android.app.Application;
import android.util.Log;

/**
 * Basitleştirilmiş Uygulama Sınıfı
 * TCN SDK başlatma hatalarını önler
 */
public class VendApplication extends Application {
    private static final String TAG = "VendApplication";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            Log.i(TAG, "VendApplication onCreate started");
            
            // Basit başlatma - TCN SDK olmadan
            Log.i(TAG, "Application initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Application initialization error: " + e.getMessage(), e);
        }
    }
}
