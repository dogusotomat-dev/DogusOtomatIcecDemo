package com.dogus.otomat.icecdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileManagementActivity extends AppCompatActivity {
    private static final String TAG = "FileManagementActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    private FileManagementSystem fileManagementSystem;
    private AdvancedLoggingSystem advancedLoggingSystem;

    // UI Components
    private TextView tvStatus;
    private Button btnUploadProductImage;
    private Button btnUploadAdvertisement;
    private Button btnUploadScreensaver;
    private Button btnUploadVideo;
    private Button btnGenerateReport;
    private Button btnCleanup;
    private Button btnBack;

    private LinearLayout llProductImages;
    private LinearLayout llAdvertisements;
    private LinearLayout llScreensavers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_management);

        initializeSystems();
        initializeViews();
        setupClickListeners();
        loadFileSystemStatus();
    }

    private void initializeSystems() {
        try {
            fileManagementSystem = FileManagementSystem.getInstance(this);
            advancedLoggingSystem = AdvancedLoggingSystem.getInstance(this);
            Log.i(TAG, "Dosya yönetim sistemleri başlatıldı");
        } catch (Exception e) {
            Log.e(TAG, "Sistem başlatma hatası: " + e.getMessage());
        }
    }

    private void initializeViews() {
        tvStatus = findViewById(R.id.tv_status);
        btnUploadProductImage = findViewById(R.id.btn_upload_product_image);
        btnUploadAdvertisement = findViewById(R.id.btn_upload_advertisement);
        btnUploadScreensaver = findViewById(R.id.btn_upload_screensaver);
        btnUploadVideo = findViewById(R.id.btn_upload_video);
        btnGenerateReport = findViewById(R.id.btn_generate_report);
        btnCleanup = findViewById(R.id.btn_cleanup);
        btnBack = findViewById(R.id.btn_back);

        llProductImages = findViewById(R.id.ll_product_images);
        llAdvertisements = findViewById(R.id.ll_advertisements);
        llScreensavers = findViewById(R.id.ll_screensavers);
    }

    private void setupClickListeners() {
        btnUploadProductImage.setOnClickListener(v -> selectImage("product"));
        btnUploadAdvertisement.setOnClickListener(v -> selectImage("advertisement"));
        btnUploadScreensaver.setOnClickListener(v -> selectImage("screensaver"));
        btnUploadVideo.setOnClickListener(v -> selectVideo());
        btnGenerateReport.setOnClickListener(v -> generateSystemReport());
        btnCleanup.setOnClickListener(v -> cleanupFileSystem());
        btnBack.setOnClickListener(v -> finish());
    }

    private void selectImage(String type) {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra("type", type);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);

            if (advancedLoggingSystem != null) {
                advancedLoggingSystem.logUserAction("image_selection_started", "image_selection", "Tip: " + type);
            }
        } catch (Exception e) {
            Log.e(TAG, "Görsel seçme hatası: " + e.getMessage());
            showToast("Görsel seçilemedi: " + e.getMessage());
        }
    }

    private void selectVideo() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_VIDEO_REQUEST);

            if (advancedLoggingSystem != null) {
                advancedLoggingSystem.logUserAction("video_selection_started", "video_selection", "Video seçimi başlatıldı");
            }
        } catch (Exception e) {
            Log.e(TAG, "Video seçme hatası: " + e.getMessage());
            showToast("Video seçilemedi: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedFile = data.getData();

            if (requestCode == PICK_IMAGE_REQUEST) {
                String type = data.getStringExtra("type");
                if (type != null && !type.isEmpty()) {
                    handleImageUpload(selectedFile, type);
                } else {
                    // Varsayılan tip olarak "product" kullan
                    handleImageUpload(selectedFile, "product");
                }
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                handleVideoUpload(selectedFile);
            }
        }
    }

    private void handleImageUpload(Uri imageUri, String type) {
        try {
            // Null kontrolü
            if (imageUri == null) {
                showToast("Görsel URI'si null!");
                return;
            }
            
            if (type == null || type.isEmpty()) {
                type = "product"; // Varsayılan tip
            }
            
            String fileName = getFileName(imageUri);
            if (fileName == null || fileName.isEmpty()) {
                fileName = "unknown_file.jpg"; // Varsayılan dosya adı
            }
            
            boolean success = false;

            switch (type) {
                case "product":
                    success = fileManagementSystem.uploadProductImage("Product", "inputStream", fileName);
                    break;
                case "advertisement":
                    success = fileManagementSystem.uploadAdvertisement("Advertisement", "inputStream", fileName);
                    break;
                case "screensaver":
                    success = fileManagementSystem.uploadScreensaver("Screensaver", "inputStream", fileName);
                    break;
                default:
                    success = fileManagementSystem.uploadProductImage("Product", "inputStream", fileName);
                    break;
            }

            if (success) {
                showToast("Dogi Soft Ice Cream görsel başarıyla yüklendi: " + fileName);
                loadFileSystemStatus();

                if (advancedLoggingSystem != null) {
                    advancedLoggingSystem.logUserAction("image_upload_success",
                            "image_upload", "Tip: " + type + ", Dosya: " + fileName);
                }
            } else {
                showToast("Dogi Soft Ice Cream görsel yüklenemedi!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Dogi Soft Ice Cream görsel yükleme hatası: " + e.getMessage());
            showToast("Dogi Soft Ice Cream görsel yükleme hatası: " + e.getMessage());
        }
    }

    private void handleVideoUpload(Uri videoUri) {
        try {
            // Null kontrolü
            if (videoUri == null) {
                showToast("Video URI'si null!");
                return;
            }
            
            String fileName = getFileName(videoUri);
            if (fileName == null || fileName.isEmpty()) {
                fileName = "unknown_video.mp4"; // Varsayılan dosya adı
            }
            
            boolean success = fileManagementSystem.uploadVideo("Video", "inputStream", fileName);
            
            if (success) {
                showToast("Dogi Soft Ice Cream video başarıyla yüklendi: " + fileName);
                loadFileSystemStatus();

                if (advancedLoggingSystem != null) {
                    advancedLoggingSystem.logUserAction("video_upload_success", "video_upload", "Dosya: " + fileName);
                }
            } else {
                showToast("Dogi Soft Ice Cream video yüklenemedi!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Dogi Soft Ice Cream video yükleme hatası: " + e.getMessage());
            showToast("Dogi Soft Ice Cream video yükleme hatası: " + e.getMessage());
        }
    }

    private String getFileName(Uri uri) {
        if (uri == null) {
            return "unknown_file";
        }
        
        String result = null;
        try {
            if ("content".equals(uri.getScheme())) {
                android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                    cursor.close();
                }
            }
            
            if (result == null || result.isEmpty()) {
                String path = uri.getPath();
                if (path != null) {
                    int cut = path.lastIndexOf('/');
                    if (cut != -1) {
                        result = path.substring(cut + 1);
                    } else {
                        result = path;
                    }
                }
            }
            
            // Son kontrol - eğer hala null ise varsayılan değer
            if (result == null || result.isEmpty()) {
                result = "unknown_file";
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Dosya adı alma hatası: " + e.getMessage());
            result = "unknown_file";
        }
        
        return result;
    }

    private void generateSystemReport() {
        try {
            if (fileManagementSystem != null) {
                fileManagementSystem.generateSystemReport();
                showToast("Sistem raporu oluşturuldu!");

                if (advancedLoggingSystem != null) {
                    advancedLoggingSystem.logSystemEvent("system_report_generated", "Dosya sistemi raporu oluşturuldu");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Rapor oluşturma hatası: " + e.getMessage());
            showToast("Rapor oluşturulamadı: " + e.getMessage());
        }
    }

    private void cleanupFileSystem() {
        try {
            if (fileManagementSystem != null) {
                fileManagementSystem.cleanupFileSystem();
                showToast("Dosya sistemi temizlendi!");
                loadFileSystemStatus();

                if (advancedLoggingSystem != null) {
                    advancedLoggingSystem.logSystemEvent("file_system_cleanup", "Dosya sistemi temizlendi");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Temizlik hatası: " + e.getMessage());
            showToast("Temizlik yapılamadı: " + e.getMessage());
        }
    }

    private void loadFileSystemStatus() {
        try {
            if (fileManagementSystem != null) {
                // Ürün görselleri
                List<String> productImages = fileManagementSystem.listFilesInFolder("ProductImages");

                // Reklamlar
                List<String> advertisements = fileManagementSystem.listFilesInFolder("Advertisements");

                // Ekran koruyucular
                List<String> screensavers = fileManagementSystem.listFilesInFolder("Screensavers");

                // Durum bilgisini güncelle
                String status = String.format("Ürün Görselleri: %d | Reklamlar: %d | Ekran Koruyucular: %d",
                        productImages.size(), advertisements.size(), screensavers.size());
                tvStatus.setText(status);

                // LinearLayout'ları güncelle
                updateLinearLayouts(productImages, advertisements, screensavers);

                if (advancedLoggingSystem != null) {
                    advancedLoggingSystem.logSystemEvent("file_system_status_loaded", status);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Dosya sistemi durumu yükleme hatası: " + e.getMessage());
            tvStatus.setText("Durum yüklenemedi: " + e.getMessage());
        }
    }

    private void updateLinearLayouts(List<String> productImages, List<String> advertisements,
            List<String> screensavers) {
        // Burada LinearLayout'ları güncelleyebilirsiniz
        // Şimdilik sadece log kaydı yapıyoruz
        Log.i(TAG, "LinearLayout'lar güncellendi - Ürün: " + productImages.size() +
                ", Reklam: " + advertisements.size() + ", Ekran Koruyucu: " + screensavers.size());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFileSystemStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (advancedLoggingSystem != null) {
            advancedLoggingSystem.logSystemEvent("file_management_activity_closed",
                    "Dosya yönetim aktivitesi kapatıldı");
        }
    }
}
