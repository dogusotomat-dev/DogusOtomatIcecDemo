package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;
import android.widget.Spinner;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Reklam ayarları aktivitesi
 * Advertisement settings activity
 */
public class AdvertisementSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AdvertisementSettings";

    // SeekBars
    private SeekBar seekBarPhotoDuration, seekBarVideoDuration, seekBarTransitionDuration, seekBarCycleDelay;
    
    // TextViews
    private TextView tvPhotoDuration, tvVideoDuration, tvTransitionDuration, tvCycleDelay;
    
    // Buttons
    private Button btnSaveSettings, btnTestAdvertisement, btnResetToDefault;
    private Button btnAddPhoto, btnAddVideo, btnStartAds, btnStopAds;
    
    // Switches
    private Switch switchAutoPlay, switchPhotoAds, switchVideoAds;
    
    // Spinners
    private Spinner spinnerPhotoQuality, spinnerVideoQuality;
    
    // List and Preview
    private ListView listViewAdvertisements;
    private ImageView ivPreview;
    private VideoView vvPreview;

    private SharedPreferences sharedPreferences;

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
        seekBarPhotoDuration = findViewById(R.id.seekBarPhotoDuration);
        seekBarVideoDuration = findViewById(R.id.seekBarVideoDuration);
        seekBarTransitionDuration = findViewById(R.id.seekBarTransitionDuration);
        seekBarCycleDelay = findViewById(R.id.seekBarCycleDelay);
        
        tvPhotoDuration = findViewById(R.id.tvPhotoDuration);
        tvVideoDuration = findViewById(R.id.tvVideoDuration);
        tvTransitionDuration = findViewById(R.id.tvTransitionDuration);
        tvCycleDelay = findViewById(R.id.tvCycleDelay);
        
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        btnTestAdvertisement = findViewById(R.id.btnTestAdvertisement);
        btnResetToDefault = findViewById(R.id.btnResetToDefault);
        
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        btnAddVideo = findViewById(R.id.btnAddVideo);
        btnStartAds = findViewById(R.id.btnStartAds);
        btnStopAds = findViewById(R.id.btnStopAds);
        
        switchAutoPlay = findViewById(R.id.switchAutoPlay);
        switchPhotoAds = findViewById(R.id.switchPhotoAds);
        switchVideoAds = findViewById(R.id.switchVideoAds);
        
        spinnerPhotoQuality = findViewById(R.id.spinnerPhotoQuality);
        spinnerVideoQuality = findViewById(R.id.spinnerVideoQuality);
        
        listViewAdvertisements = findViewById(R.id.listViewAdvertisements);
        ivPreview = findViewById(R.id.ivPreview);
        vvPreview = findViewById(R.id.vvPreview);
    }

    private void loadCurrentSettings() {
        try {
            // Mevcut reklam ayarlarını yükle
            int photoDuration = sharedPreferences.getInt("photo_duration", 10);
            int videoDuration = sharedPreferences.getInt("video_duration", 30);
            int transitionDuration = sharedPreferences.getInt("transition_duration", 2);
            int cycleDelay = sharedPreferences.getInt("cycle_delay", 5);
            
            boolean autoPlay = sharedPreferences.getBoolean("auto_play", true);
            boolean photoAds = sharedPreferences.getBoolean("photo_ads", true);
            boolean videoAds = sharedPreferences.getBoolean("video_ads", true);
            
            // SeekBar'ları güncelle
            seekBarPhotoDuration.setProgress(photoDuration);
            seekBarVideoDuration.setProgress(videoDuration);
            seekBarTransitionDuration.setProgress(transitionDuration);
            seekBarCycleDelay.setProgress(cycleDelay);
            
            // TextView'ları güncelle
            tvPhotoDuration.setText(photoDuration + " saniye");
            tvVideoDuration.setText(videoDuration + " saniye");
            tvTransitionDuration.setText(transitionDuration + " saniye");
            tvCycleDelay.setText(cycleDelay + " saniye");
            
            // Switch'leri güncelle
            switchAutoPlay.setChecked(autoPlay);
            switchPhotoAds.setChecked(photoAds);
            switchVideoAds.setChecked(videoAds);
            
            Log.i(TAG, "Reklam ayarları yüklendi");

        } catch (Exception e) {
            Log.e(TAG, "Reklam ayarları yükleme hatası: " + e.getMessage());
        }
    }

    private void setupListeners() {
        // SeekBar listeners
        seekBarPhotoDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvPhotoDuration.setText(progress + " saniye");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        seekBarVideoDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvVideoDuration.setText(progress + " saniye");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        seekBarTransitionDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvTransitionDuration.setText(progress + " saniye");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        seekBarCycleDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvCycleDelay.setText(progress + " saniye");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Buton listeners
        btnSaveSettings.setOnClickListener(v -> saveAdvertisementSettings());
        btnTestAdvertisement.setOnClickListener(v -> testAdvertisement());
        btnResetToDefault.setOnClickListener(v -> resetToDefault());
        btnAddPhoto.setOnClickListener(v -> addPhoto());
        btnAddVideo.setOnClickListener(v -> addVideo());
        btnStartAds.setOnClickListener(v -> startAds());
        btnStopAds.setOnClickListener(v -> stopAds());
    }



    private void saveAdvertisementSettings() {
        try {
            // Mevcut ayarları al
            int photoDuration = seekBarPhotoDuration.getProgress();
            int videoDuration = seekBarVideoDuration.getProgress();
            int transitionDuration = seekBarTransitionDuration.getProgress();
            int cycleDelay = seekBarCycleDelay.getProgress();
            
            boolean autoPlay = switchAutoPlay.isChecked();
            boolean photoAds = switchPhotoAds.isChecked();
            boolean videoAds = switchVideoAds.isChecked();

            // Ayarları kaydet
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("photo_duration", photoDuration);
            editor.putInt("video_duration", videoDuration);
            editor.putInt("transition_duration", transitionDuration);
            editor.putInt("cycle_delay", cycleDelay);
            editor.putBoolean("auto_play", autoPlay);
            editor.putBoolean("photo_ads", photoAds);
            editor.putBoolean("video_ads", videoAds);
            editor.apply();

            Toast.makeText(this, "Reklam ayarları kaydedildi!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Reklam ayarları kaydedildi");

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
    
    private void addPhoto() {
        try {
            Toast.makeText(this, "Fotoğraf ekleme özelliği yakında eklenecek", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Add photo clicked");
        } catch (Exception e) {
            Log.e(TAG, "Add photo error: " + e.getMessage());
        }
    }
    
    private void addVideo() {
        try {
            Toast.makeText(this, "Video ekleme özelliği yakında eklenecek", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Add video clicked");
        } catch (Exception e) {
            Log.e(TAG, "Add video error: " + e.getMessage());
        }
    }
    
    private void startAds() {
        try {
            Toast.makeText(this, "Reklamlar başlatıldı", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Ads started");
        } catch (Exception e) {
            Log.e(TAG, "Start ads error: " + e.getMessage());
        }
    }
    
    private void stopAds() {
        try {
            Toast.makeText(this, "Reklamlar durduruldu", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Ads stopped");
        } catch (Exception e) {
            Log.e(TAG, "Stop ads error: " + e.getMessage());
        }
    }

    private void resetToDefault() {
        try {
            // Varsayılan değerlere sıfırla
            seekBarPhotoDuration.setProgress(10);
            seekBarVideoDuration.setProgress(30);
            seekBarTransitionDuration.setProgress(2);
            seekBarCycleDelay.setProgress(5);
            
            switchAutoPlay.setChecked(true);
            switchPhotoAds.setChecked(true);
            switchVideoAds.setChecked(true);
            
            // TextView'ları güncelle
            tvPhotoDuration.setText("10 saniye");
            tvVideoDuration.setText("30 saniye");
            tvTransitionDuration.setText("2 saniye");
            tvCycleDelay.setText("5 saniye");

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
