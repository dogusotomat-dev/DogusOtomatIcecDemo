package com.dogus.otomat.icecdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Gelişmiş Reklam Ayarları Aktivitesi
 * Fotoğraf ve video reklamları için kapsamlı yönetim
 */
public class EnhancedAdvertisementSettingsActivity extends AppCompatActivity {
    private static final String TAG = "EnhancedAdSettings";
    private static final int PERMISSION_REQUEST_CODE = 1001;

    // UI Elements
    private Button btnBack, btnAddPhoto, btnAddVideo, btnStartAds, btnStopAds, btnSaveSettings;
    private Switch switchAutoPlay, switchPhotoAds, switchVideoAds;
    private SeekBar seekBarPhotoDuration, seekBarVideoDuration, seekBarTransitionDuration, seekBarCycleDelay;
    private TextView tvPhotoDuration, tvVideoDuration, tvTransitionDuration, tvCycleDelay;
    private Spinner spinnerPhotoQuality, spinnerVideoQuality;
    private ListView listViewAdvertisements;
    private ImageView ivPreview;
    private VideoView vvPreview;

    // Advertisement Manager
    private AdvertisementManager advertisementManager;
    private AdvertisementAdapter advertisementAdapter;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> photoPickerLauncher;
    private ActivityResultLauncher<Intent> videoPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_enhanced_advertisement_settings);

            Log.i(TAG, "EnhancedAdvertisementSettingsActivity onCreate started");

            // Advertisement Manager'ı başlat
            advertisementManager = AdvertisementManager.getInstance(this);

            initializeViews();
            setupClickListeners();
            setupActivityResultLaunchers();
            loadCurrentSettings();
            setupAdvertisementList();

            Log.i(TAG, "EnhancedAdvertisementSettingsActivity onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "onCreate error: " + e.getMessage(), e);
            showErrorDialog("Başlatma Hatası", "Reklam ayarları açılırken hata oluştu: " + e.getMessage());
        }
    }

    private void initializeViews() {
        try {
            // Header
            btnBack = findViewById(R.id.btnBack);

            // Control Buttons
            btnAddPhoto = findViewById(R.id.btnAddPhoto);
            btnAddVideo = findViewById(R.id.btnAddVideo);
            btnStartAds = findViewById(R.id.btnStartAds);
            btnStopAds = findViewById(R.id.btnStopAds);
            btnSaveSettings = findViewById(R.id.btnSaveSettings);

            // Switches
            switchAutoPlay = findViewById(R.id.switchAutoPlay);
            switchPhotoAds = findViewById(R.id.switchPhotoAds);
            switchVideoAds = findViewById(R.id.switchVideoAds);

            // SeekBars
            seekBarPhotoDuration = findViewById(R.id.seekBarPhotoDuration);
            seekBarVideoDuration = findViewById(R.id.seekBarVideoDuration);
            seekBarTransitionDuration = findViewById(R.id.seekBarTransitionDuration);
            seekBarCycleDelay = findViewById(R.id.seekBarCycleDelay);

            // Duration TextViews
            tvPhotoDuration = findViewById(R.id.tvPhotoDuration);
            tvVideoDuration = findViewById(R.id.tvVideoDuration);
            tvTransitionDuration = findViewById(R.id.tvTransitionDuration);
            tvCycleDelay = findViewById(R.id.tvCycleDelay);

            // Quality Spinners
            spinnerPhotoQuality = findViewById(R.id.spinnerPhotoQuality);
            spinnerVideoQuality = findViewById(R.id.spinnerVideoQuality);

            // Advertisement List
            listViewAdvertisements = findViewById(R.id.listViewAdvertisements);

            // Preview Views
            ivPreview = findViewById(R.id.ivPreview);
            vvPreview = findViewById(R.id.vvPreview);

            Log.i(TAG, "Views initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "View initialization error: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            // Back Button
            btnBack.setOnClickListener(v -> finish());

            // Add Photo Button
            btnAddPhoto.setOnClickListener(v -> {
                if (checkPermission()) {
                    openPhotoPicker();
                } else {
                    requestPermission();
                }
            });

            // Add Video Button
            btnAddVideo.setOnClickListener(v -> {
                if (checkPermission()) {
                    openVideoPicker();
                } else {
                    requestPermission();
                }
            });

            // Start Ads Button
            btnStartAds.setOnClickListener(v -> startAdvertisement());

            // Stop Ads Button
            btnStopAds.setOnClickListener(v -> stopAdvertisement());

            // Save Settings Button
            btnSaveSettings.setOnClickListener(v -> saveAllSettings());

            // Auto Play Switch
            switchAutoPlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
                advertisementManager.updateSetting("auto_play_enabled", isChecked);
                updateStartStopButtons();
            });

            // Photo Ads Switch
            switchPhotoAds.setOnCheckedChangeListener((buttonView, isChecked) -> {
                seekBarPhotoDuration.setEnabled(isChecked);
                spinnerPhotoQuality.setEnabled(isChecked);
            });

            // Video Ads Switch
            switchVideoAds.setOnCheckedChangeListener((buttonView, isChecked) -> {
                seekBarVideoDuration.setEnabled(isChecked);
                spinnerVideoQuality.setEnabled(isChecked);
            });

            // SeekBar Listeners
            setupSeekBarListeners();

            // Quality Spinner Listeners
            setupQualitySpinnerListeners();

            Log.i(TAG, "Click listeners setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Click listener setup error: " + e.getMessage(), e);
        }
    }

    private void setupSeekBarListeners() {
        try {
            // Photo Duration SeekBar
            seekBarPhotoDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int duration = (progress + 1) * 1000; // 1-60 saniye
                        tvPhotoDuration.setText(duration / 1000 + " saniye");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Video Duration SeekBar
            seekBarVideoDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int duration = (progress + 5) * 1000; // 5-120 saniye
                        tvVideoDuration.setText(duration / 1000 + " saniye");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Transition Duration SeekBar
            seekBarTransitionDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int duration = (progress + 1) * 100; // 0.1-5 saniye
                        tvTransitionDuration.setText(duration / 1000.0 + " saniye");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Cycle Delay SeekBar
            seekBarCycleDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int delay = progress * 1000; // 0-30 saniye
                        tvCycleDelay.setText(delay / 1000 + " saniye");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "SeekBar listener setup error: " + e.getMessage(), e);
        }
    }

    private void setupQualitySpinnerListeners() {
        try {
            // Photo Quality Spinner
            spinnerPhotoQuality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String quality = parent.getItemAtPosition(position).toString();
                    Log.i(TAG, "Photo quality selected: " + quality);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            // Video Quality Spinner
            spinnerVideoQuality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String quality = parent.getItemAtPosition(position).toString();
                    Log.i(TAG, "Video quality selected: " + quality);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Quality spinner listener setup error: " + e.getMessage(), e);
        }
    }

    private void setupActivityResultLaunchers() {
        try {
            // Photo Picker Launcher
            photoPickerLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null) {
                                processSelectedPhoto(selectedImageUri);
                            }
                        }
                    });

            // Video Picker Launcher
            videoPickerLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri selectedVideoUri = result.getData().getData();
                            if (selectedVideoUri != null) {
                                processSelectedVideo(selectedVideoUri);
                            }
                        }
                    });

            Log.i(TAG, "Activity result launchers setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Activity result launcher setup error: " + e.getMessage(), e);
        }
    }

    private void loadCurrentSettings() {
        try {
            AdvertisementManager.AdvertisementSettings settings = advertisementManager.getCurrentSettings();

            // Duration settings
            int photoDuration = settings.getPhotoDuration();
            int videoDuration = settings.getVideoDuration();
            int transitionDuration = settings.getTransitionDuration();
            int cycleDelay = settings.getCycleDelay();

            // Update SeekBars
            seekBarPhotoDuration.setProgress((photoDuration / 1000) - 1);
            seekBarVideoDuration.setProgress((videoDuration / 1000) - 5);
            seekBarTransitionDuration.setProgress((transitionDuration / 100) - 1);
            seekBarCycleDelay.setProgress(cycleDelay / 1000);

            // Update TextViews
            tvPhotoDuration.setText(photoDuration / 1000 + " saniye");
            tvVideoDuration.setText(videoDuration / 1000 + " saniye");
            tvTransitionDuration.setText(transitionDuration / 1000.0 + " saniye");
            tvCycleDelay.setText(cycleDelay / 1000 + " saniye");

            // Update Switches
            switchAutoPlay.setChecked(settings.isAutoPlayEnabled());
            switchPhotoAds.setChecked(true);
            switchVideoAds.setChecked(true);

            // Update Start/Stop buttons
            updateStartStopButtons();

            Log.i(TAG, "Current settings loaded successfully");

        } catch (Exception e) {
            Log.e(TAG, "Settings loading error: " + e.getMessage(), e);
        }
    }

    private void setupAdvertisementList() {
        try {
            advertisementAdapter = new AdvertisementAdapter(this, new ArrayList<>());
            listViewAdvertisements.setAdapter(advertisementAdapter);

            // Advertisement list item click listener
            listViewAdvertisements.setOnItemClickListener((parent, view, position, id) -> {
                AdvertisementManager.AdvertisementItem item = advertisementAdapter.getItem(position);
                if (item != null) {
                    showAdvertisementDetails(item);
                }
            });

            // Advertisement list item long click listener for deletion
            listViewAdvertisements.setOnItemLongClickListener((parent, view, position, id) -> {
                AdvertisementManager.AdvertisementItem item = advertisementAdapter.getItem(position);
                if (item != null) {
                    showDeleteConfirmationDialog(item);
                }
                return true;
            });

            refreshAdvertisementList();

            Log.i(TAG, "Advertisement list setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Advertisement list setup error: " + e.getMessage(), e);
        }
    }

    private void refreshAdvertisementList() {
        try {
            List<AdvertisementManager.AdvertisementItem> advertisements = advertisementManager.getAdvertisementList();
            advertisementAdapter.clear();
            advertisementAdapter.addAll(advertisements);
            advertisementAdapter.notifyDataSetChanged();

            Log.i(TAG, "Advertisement list refreshed: " + advertisements.size() + " items");

        } catch (Exception e) {
            Log.e(TAG, "Advertisement list refresh error: " + e.getMessage(), e);
        }
    }

    private void updateStartStopButtons() {
        try {
            boolean isActive = advertisementManager.isAdvertisementActive();
            boolean isAutoPlayEnabled = advertisementManager.isAutoPlayEnabled();

            btnStartAds.setEnabled(!isActive && isAutoPlayEnabled);
            btnStopAds.setEnabled(isActive);

        } catch (Exception e) {
            Log.e(TAG, "Button state update error: " + e.getMessage(), e);
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                PERMISSION_REQUEST_CODE);
    }

    private void openPhotoPicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            photoPickerLauncher.launch(intent);

        } catch (Exception e) {
            Log.e(TAG, "Photo picker error: " + e.getMessage(), e);
            Toast.makeText(this, "Fotoğraf seçici açılamadı", Toast.LENGTH_SHORT).show();
        }
    }

    private void openVideoPicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            videoPickerLauncher.launch(intent);

        } catch (Exception e) {
            Log.e(TAG, "Video picker error: " + e.getMessage(), e);
            Toast.makeText(this, "Video seçici açılamadı", Toast.LENGTH_SHORT).show();
        }
    }

    private void processSelectedPhoto(Uri imageUri) {
        try {
            // Show loading
            Toast.makeText(this, "Fotoğraf işleniyor...", Toast.LENGTH_SHORT).show();

            // Process image in background
            new Thread(() -> {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    if (inputStream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();

                        if (bitmap != null) {
                            // Save to internal storage
                            String fileName = "photo_" + System.currentTimeMillis() + ".jpg";
                            File photoFile = new File(getFilesDir(), fileName);

                            FileOutputStream outputStream = new FileOutputStream(photoFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                            outputStream.close();

                            // Add to advertisement manager
                            boolean success = advertisementManager.addAdvertisement(photoFile.getAbsolutePath(),
                                    AdvertisementManager.AD_TYPE_PHOTO);

                            runOnUiThread(() -> {
                                if (success) {
                                    Toast.makeText(this, "Fotoğraf reklamı eklendi", Toast.LENGTH_SHORT).show();
                                    refreshAdvertisementList();
                                } else {
                                    Toast.makeText(this, "Fotoğraf eklenemedi", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            runOnUiThread(
                                    () -> Toast.makeText(this, "Fotoğraf yüklenemedi", Toast.LENGTH_SHORT).show());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Photo processing error: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(this, "Fotoğraf işleme hatası", Toast.LENGTH_SHORT).show());
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "Photo processing setup error: " + e.getMessage(), e);
            Toast.makeText(this, "Fotoğraf işleme hatası", Toast.LENGTH_SHORT).show();
        }
    }

    private void processSelectedVideo(Uri videoUri) {
        try {
            // Show loading
            Toast.makeText(this, "Video işleniyor...", Toast.LENGTH_SHORT).show();

            // Process video in background
            new Thread(() -> {
                try {
                    // Copy video to internal storage
                    String fileName = "video_" + System.currentTimeMillis() + ".mp4";
                    File videoFile = new File(getFilesDir(), fileName);

                    InputStream inputStream = getContentResolver().openInputStream(videoUri);
                    if (inputStream != null) {
                        FileOutputStream outputStream = new FileOutputStream(videoFile);

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }

                        inputStream.close();
                        outputStream.close();

                        // Add to advertisement manager
                        boolean success = advertisementManager.addAdvertisement(videoFile.getAbsolutePath(),
                                AdvertisementManager.AD_TYPE_VIDEO);

                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(this, "Video reklamı eklendi", Toast.LENGTH_SHORT).show();
                                refreshAdvertisementList();
                            } else {
                                Toast.makeText(this, "Video eklenemedi", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Video yüklenemedi", Toast.LENGTH_SHORT).show());
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Video processing error: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(this, "Video işleme hatası", Toast.LENGTH_SHORT).show());
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "Video processing setup error: " + e.getMessage(), e);
            Toast.makeText(this, "Video işleme hatası", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAdvertisement() {
        try {
            advertisementManager.startAdvertisement();
            updateStartStopButtons();
            Toast.makeText(this, "Reklamlar başlatıldı", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Advertisement start error: " + e.getMessage(), e);
            Toast.makeText(this, "Reklam başlatılamadı", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAdvertisement() {
        try {
            advertisementManager.stopAdvertisement();
            updateStartStopButtons();
            Toast.makeText(this, "Reklamlar durduruldu", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Advertisement stop error: " + e.getMessage(), e);
            Toast.makeText(this, "Reklam durdurulamadı", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAllSettings() {
        try {
            // Get current values from UI
            int photoDuration = (seekBarPhotoDuration.getProgress() + 1) * 1000;
            int videoDuration = (seekBarVideoDuration.getProgress() + 5) * 1000;
            int transitionDuration = (seekBarTransitionDuration.getProgress() + 1) * 100;
            int cycleDelay = seekBarCycleDelay.getProgress() * 1000;

            // Update advertisement manager
            advertisementManager.updateSetting("photo_duration", photoDuration);
            advertisementManager.updateSetting("video_duration", videoDuration);
            advertisementManager.updateSetting("transition_duration", transitionDuration);
            advertisementManager.updateSetting("cycle_delay", cycleDelay);

            Toast.makeText(this, "Ayarlar kaydedildi", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "All settings saved successfully");

        } catch (Exception e) {
            Log.e(TAG, "Settings save error: " + e.getMessage(), e);
            Toast.makeText(this, "Ayarlar kaydedilemedi", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAdvertisementDetails(AdvertisementManager.AdvertisementItem item) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reklam Detayları");

            String details = "ID: " + item.getId() + "\n" +
                    "Tür: " + (item.getType() == AdvertisementManager.AD_TYPE_PHOTO ? "Fotoğraf" : "Video") + "\n" +
                    "Dosya: " + item.getFilePath() + "\n" +
                    "Süre: " + (item.getDuration() / 1000) + " saniye";

            if (item.getTitle() != null && !item.getTitle().isEmpty()) {
                details += "\nBaşlık: " + item.getTitle();
            }

            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                details += "\nAçıklama: " + item.getDescription();
            }

            builder.setMessage(details);
            builder.setPositiveButton("Tamam", null);
            builder.show();

        } catch (Exception e) {
            Log.e(TAG, "Advertisement details dialog error: " + e.getMessage(), e);
        }
    }

    private void showDeleteConfirmationDialog(AdvertisementManager.AdvertisementItem item) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reklam Sil");
            builder.setMessage("Bu reklamı silmek istediğinizden emin misiniz?\n\n" + item.getId());

            builder.setPositiveButton("Sil", (dialog, which) -> {
                boolean success = advertisementManager.removeAdvertisement(item.getId());
                if (success) {
                    Toast.makeText(this, "Reklam silindi", Toast.LENGTH_SHORT).show();
                    refreshAdvertisementList();
                } else {
                    Toast.makeText(this, "Reklam silinemedi", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("İptal", null);
            builder.show();

        } catch (Exception e) {
            Log.e(TAG, "Delete confirmation dialog error: " + e.getMessage(), e);
        }
    }

    private void showErrorDialog(String title, String message) {
        try {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Tamam", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error dialog show error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            refreshAdvertisementList();
            updateStartStopButtons();
        } catch (Exception e) {
            Log.e(TAG, "onResume error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (advertisementManager != null) {
                advertisementManager.stopAdvertisement();
            }
        } catch (Exception e) {
            Log.e(TAG, "onDestroy error: " + e.getMessage(), e);
        }
    }
}
