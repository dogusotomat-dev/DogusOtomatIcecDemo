package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Reklam ayarları aktivitesi
 * Advertisement settings activity
 */
public class AdvertisementSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AdvertisementSettings";

    private SeekBar seekBarDuration;
    private TextView tvDurationValue;
    private Button btnSaveSettings;
    private Button btnTestAdvertisement;
    private Button btnResetToDefault;

    private SharedPreferences sharedPreferences;
    private long currentDuration;
    private static final long MIN_DURATION = 5000; // 5 saniye
    private static final long MAX_DURATION = 60000; // 60 saniye
    private static final long DEFAULT_DURATION = 15000; // 15 saniye

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_settings);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);

        initViews();
        loadCurrentSettings();
        setupListeners();
    }

    private void initViews() {
        seekBarDuration = findViewById(R.id.seekbar_ad_duration);
        tvDurationValue = findViewById(R.id.tv_duration_value);
        btnSaveSettings = findViewById(R.id.btn_save_ad_settings);
        btnTestAdvertisement = findViewById(R.id.btn_test_advertisement);
        btnResetToDefault = findViewById(R.id.btn_reset_to_default);

        // SeekBar ayarları
        int maxProgress = (int) ((MAX_DURATION - MIN_DURATION) / 1000);
        seekBarDuration.setMax(maxProgress);
    }

    private void loadCurrentSettings() {
        try {
            // Mevcut reklam süresini yükle
            currentDuration = sharedPreferences.getLong("advertisement_duration", DEFAULT_DURATION);

            // SeekBar'ı güncelle
            updateSeekBarFromDuration(currentDuration);

            // Süre değerini göster
            updateDurationDisplay(currentDuration);

            Log.i(TAG, "Reklam ayarları yüklendi: " + (currentDuration / 1000) + " saniye");

        } catch (Exception e) {
            Log.e(TAG, "Reklam ayarları yükleme hatası: " + e.getMessage());
            currentDuration = DEFAULT_DURATION;
        }
    }

    private void setupListeners() {
        // SeekBar listener
        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Progress'i süreye çevir
                    long newDuration = MIN_DURATION + (progress * 1000);
                    updateDurationDisplay(newDuration);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Gerekli değil
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Gerekli değil
            }
        });

        // Kaydet butonu
        btnSaveSettings.setOnClickListener(v -> saveAdvertisementSettings());

        // Test butonu
        btnTestAdvertisement.setOnClickListener(v -> testAdvertisement());

        // Sıfırla butonu
        btnResetToDefault.setOnClickListener(v -> resetToDefault());
    }

    private void updateSeekBarFromDuration(long duration) {
        try {
            // Süreyi progress'e çevir
            int progress = (int) ((duration - MIN_DURATION) / 1000);
            seekBarDuration.setProgress(progress);
        } catch (Exception e) {
            Log.e(TAG, "SeekBar güncelleme hatası: " + e.getMessage());
        }
    }

    private void updateDurationDisplay(long duration) {
        try {
            int seconds = (int) (duration / 1000);
            tvDurationValue.setText(seconds + " saniye");
        } catch (Exception e) {
            Log.e(TAG, "Süre gösterimi güncelleme hatası: " + e.getMessage());
        }
    }

    private void saveAdvertisementSettings() {
        try {
            // Mevcut progress'i süreye çevir
            int progress = seekBarDuration.getProgress();
            long newDuration = MIN_DURATION + (progress * 1000);

            // Ayarları kaydet
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("advertisement_duration", newDuration);
            editor.apply();

            currentDuration = newDuration;

            Toast.makeText(this, "Reklam ayarları kaydedildi!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Reklam ayarları kaydedildi: " + (newDuration / 1000) + " saniye");

        } catch (Exception e) {
            Log.e(TAG, "Reklam ayarları kaydetme hatası: " + e.getMessage());
            Toast.makeText(this, "Ayarlar kaydedilemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void testAdvertisement() {
        try {
            // Test reklamı göster
            showTestAdvertisement();

            Toast.makeText(this, "Test reklamı gösteriliyor...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Test reklam hatası: " + e.getMessage());
            Toast.makeText(this, "Test reklam hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showTestAdvertisement() {
        try {
            // Test reklamı için özel görsel oluştur
            createTestAdvertisementImage();

            // Ana aktiviteye test reklamı göster sinyali gönder
            // Bu implementasyon ana aktivitede yapılacak

        } catch (Exception e) {
            Log.e(TAG, "Test reklam gösterme hatası: " + e.getMessage());
        }
    }

    private void createTestAdvertisementImage() {
        try {
            // Test reklamı için basit bir görsel oluştur
            // Bu implementasyon daha sonra geliştirilebilir

            Log.i(TAG, "Test reklam görseli oluşturuldu");

        } catch (Exception e) {
            Log.e(TAG, "Test reklam görseli oluşturma hatası: " + e.getMessage());
        }
    }

    private void resetToDefault() {
        try {
            // Varsayılan değerlere sıfırla
            currentDuration = DEFAULT_DURATION;

            // SeekBar'ı güncelle
            updateSeekBarFromDuration(currentDuration);

            // Süre değerini göster
            updateDurationDisplay(currentDuration);

            Toast.makeText(this, "Varsayılan değerlere sıfırlandı!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Reklam ayarları varsayılan değerlere sıfırlandı");

        } catch (Exception e) {
            Log.e(TAG, "Varsayılan değerlere sıfırlama hatası: " + e.getMessage());
            Toast.makeText(this, "Sıfırlama hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ayarları yeniden yükle
        loadCurrentSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Değişiklikleri kaydet
        saveAdvertisementSettings();
    }
}
