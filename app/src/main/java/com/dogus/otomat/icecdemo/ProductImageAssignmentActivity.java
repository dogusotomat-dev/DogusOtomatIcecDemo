package com.dogus.otomat.icecdemo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
 * Ürün görsel atama aktivitesi
 * Product image assignment activity
 */
public class ProductImageAssignmentActivity extends AppCompatActivity {
    private static final String TAG = "ProductImageAssignment";
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 1002;

    private Spinner spinnerProductType;
    private Spinner spinnerProductItem;
    private ImageView imageViewPreview;
    private Button btnSelectImage;
    private Button btnRemoveImage;
    private Button btnSaveAssignment;
    private Button btnTestDisplay;
    private TextView tvCurrentImage;
    private TextView tvProductInfo;

    private SharedPreferences sharedPreferences;
    private String selectedProductType;
    private String selectedProductItem;
    private String currentImagePath;
    private Bitmap selectedImage;

    // Ürün türleri
    private static final String PRODUCT_TYPE_ICE_CREAM = "ice_cream";
    private static final String PRODUCT_TYPE_SAUCE = "sauce";
    private static final String PRODUCT_TYPE_TOPPING = "topping";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_image_assignment);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);

        initViews();
        setupSpinners();
        setupListeners();
        loadCurrentImage();
    }

    private void initViews() {
        spinnerProductType = findViewById(R.id.spinner_product_type);
        spinnerProductItem = findViewById(R.id.spinner_product_item);
        imageViewPreview = findViewById(R.id.imageview_product_preview);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnRemoveImage = findViewById(R.id.btn_remove_image);
        btnSaveAssignment = findViewById(R.id.btn_save_assignment);
        btnTestDisplay = findViewById(R.id.btn_test_display);
        tvCurrentImage = findViewById(R.id.tv_current_image);
        tvProductInfo = findViewById(R.id.tv_product_info);
    }

    private void setupSpinners() {
        try {
            // Ürün türü spinner'ı
            List<String> productTypes = new ArrayList<>();
            productTypes.add("Dondurma");
            productTypes.add("Sos");
            productTypes.add("Süsleme");

            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, productTypes);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProductType.setAdapter(typeAdapter);

            // Ürün türü değiştiğinde ürün listesini güncelle
            spinnerProductType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateProductItemsList(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Gerekli değil
                }
            });

            // İlk seçimi yap
            spinnerProductType.setSelection(0);

        } catch (Exception e) {
            Log.e(TAG, "Spinner kurulum hatası: " + e.getMessage());
        }
    }

    private void updateProductItemsList(int productTypePosition) {
        try {
            List<String> productItems = new ArrayList<>();

            switch (productTypePosition) {
                case 0: // Dondurma
                    selectedProductType = PRODUCT_TYPE_ICE_CREAM;
                    productItems.add("Vanilya");
                    productItems.add("Çikolata");
                    productItems.add("Çilek");
                    productItems.add("Muz");
                    productItems.add("Fındık");
                    break;

                case 1: // Sos
                    selectedProductType = PRODUCT_TYPE_SAUCE;
                    productItems.add("Çikolata Sosu");
                    productItems.add("Karamel Sosu");
                    productItems.add("Meyve Sosu");
                    productItems.add("Fındık Sosu");
                    break;

                case 2: // Süsleme
                    selectedProductType = PRODUCT_TYPE_TOPPING;
                    productItems.add("Fındık");
                    productItems.add("Badem");
                    productItems.add("Çikolata Parçaları");
                    productItems.add("Meyve Dilimleri");
                    productItems.add("Şeker");
                    break;
            }

            ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, productItems);
            itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProductItem.setAdapter(itemAdapter);

            // İlk ürünü seç
            spinnerProductItem.setSelection(0);

            // Ürün seçimi değiştiğinde görseli yükle
            spinnerProductItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedProductItem = productItems.get(position);
                    loadProductImage();
                    updateProductInfo();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Gerekli değil
                }
            });

            // İlk ürünü seç
            if (!productItems.isEmpty()) {
                selectedProductItem = productItems.get(0);
                loadProductImage();
                updateProductInfo();
            }

        } catch (Exception e) {
            Log.e(TAG, "Ürün listesi güncelleme hatası: " + e.getMessage());
        }
    }

    private void setupListeners() {
        btnSelectImage.setOnClickListener(v -> selectImage());
        btnRemoveImage.setOnClickListener(v -> removeImage());
        btnSaveAssignment.setOnClickListener(v -> saveImageAssignment());
        btnTestDisplay.setOnClickListener(v -> testImageDisplay());
    }

    private void selectImage() {
        try {
            // Kamera izni kontrolü
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                        REQUEST_CAMERA_PERMISSION);
                return;
            }

            // Galeriden resim seç
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);

        } catch (Exception e) {
            Log.e(TAG, "Resim seçme hatası: " + e.getMessage());
            Toast.makeText(this, "Resim seçilemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeImage() {
        try {
            // Görseli kaldır
            selectedImage = null;
            currentImagePath = "";

            // Preview'i temizle
            imageViewPreview.setImageResource(android.R.drawable.ic_menu_gallery);

            // UI'ı güncelle
            updateImageInfo();

            Toast.makeText(this, "Görsel kaldırıldı", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Görsel kaldırma hatası: " + e.getMessage());
        }
    }

    private void saveImageAssignment() {
        try {
            if (selectedImage == null) {
                Toast.makeText(this, "Lütfen bir görsel seçin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedProductType == null || selectedProductItem == null) {
                Toast.makeText(this, "Lütfen ürün türü ve ürün seçin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Görseli kaydet
            String imagePath = saveImageToStorage(selectedImage);

            // Ayarları kaydet
            String key = getImageKey(selectedProductType, selectedProductItem);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, imagePath);
            editor.apply();

            currentImagePath = imagePath;

            Toast.makeText(this, "Görsel ataması kaydedildi!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Görsel ataması kaydedildi: " + key + " -> " + imagePath);

            updateImageInfo();

        } catch (Exception e) {
            Log.e(TAG, "Görsel atama kaydetme hatası: " + e.getMessage());
            Toast.makeText(this, "Kaydetme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void testImageDisplay() {
        try {
            if (currentImagePath == null || currentImagePath.isEmpty()) {
                Toast.makeText(this, "Test edilecek görsel yok", Toast.LENGTH_SHORT).show();
                return;
            }

            // Test görselini göster
            showTestImage();

            Toast.makeText(this, "Test görseli gösteriliyor...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Test görsel hatası: " + e.getMessage());
            Toast.makeText(this, "Test hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProductImage() {
        try {
            if (selectedProductType == null || selectedProductItem == null) {
                return;
            }

            // Kayıtlı görseli yükle
            String key = getImageKey(selectedProductType, selectedProductItem);
            String imagePath = sharedPreferences.getString(key, "");

            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    selectedImage = BitmapFactory.decodeFile(imagePath);
                    currentImagePath = imagePath;
                    imageViewPreview.setImageBitmap(selectedImage);
                } else {
                    // Dosya bulunamadı, varsayılan görsel göster
                    imageViewPreview.setImageResource(android.R.drawable.ic_menu_gallery);
                    selectedImage = null;
                    currentImagePath = "";
                }
            } else {
                // Görsel atanmamış, varsayılan görsel göster
                imageViewPreview.setImageResource(android.R.drawable.ic_menu_gallery);
                selectedImage = null;
                currentImagePath = "";
            }

            updateImageInfo();

        } catch (Exception e) {
            Log.e(TAG, "Ürün görseli yükleme hatası: " + e.getMessage());
        }
    }

    private void loadCurrentImage() {
        try {
            // Mevcut görseli yükle
            loadProductImage();

        } catch (Exception e) {
            Log.e(TAG, "Mevcut görsel yükleme hatası: " + e.getMessage());
        }
    }

    private void updateImageInfo() {
        try {
            if (currentImagePath != null && !currentImagePath.isEmpty()) {
                File imageFile = new File(currentImagePath);
                if (imageFile.exists()) {
                    tvCurrentImage.setText("Mevcut Görsel: " + imageFile.getName());
                } else {
                    tvCurrentImage.setText("Mevcut Görsel: Bulunamadı");
                }
            } else {
                tvCurrentImage.setText("Mevcut Görsel: Atanmamış");
            }

        } catch (Exception e) {
            Log.e(TAG, "Görsel bilgi güncelleme hatası: " + e.getMessage());
        }
    }

    private void updateProductInfo() {
        try {
            if (selectedProductType != null && selectedProductItem != null) {
                String typeText = getProductTypeText(selectedProductType);
                tvProductInfo.setText("Seçili Ürün: " + typeText + " - " + selectedProductItem);
            } else {
                tvProductInfo.setText("Seçili Ürün: Yok");
            }

        } catch (Exception e) {
            Log.e(TAG, "Ürün bilgi güncelleme hatası: " + e.getMessage());
        }
    }

    private String getProductTypeText(String productType) {
        switch (productType) {
            case PRODUCT_TYPE_ICE_CREAM:
                return "Dondurma";
            case PRODUCT_TYPE_SAUCE:
                return "Sos";
            case PRODUCT_TYPE_TOPPING:
                return "Süsleme";
            default:
                return "Bilinmeyen";
        }
    }

    private String getImageKey(String productType, String productItem) {
        return "product_image_" + productType + "_" + productItem.toLowerCase().replace(" ", "_");
    }

    private String saveImageToStorage(Bitmap bitmap) throws IOException {
        try {
            // Uygulama özel dizininde görseli kaydet
            File imagesDir = new File(getFilesDir(), "product_images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            String fileName = selectedProductType + "_" + selectedProductItem.toLowerCase().replace(" ", "_") + ".jpg";
            File imageFile = new File(imagesDir, fileName);

            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            Log.i(TAG, "Görsel kaydedildi: " + imageFile.getAbsolutePath());
            return imageFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(TAG, "Görsel kaydetme hatası: " + e.getMessage());
            throw new IOException("Görsel kaydedilemedi: " + e.getMessage());
        }
    }

    private void showTestImage() {
        try {
            // Test görselini büyük boyutta göster
            // Bu implementasyon daha sonra geliştirilebilir

            Log.i(TAG, "Test görseli gösteriliyor: " + currentImagePath);

        } catch (Exception e) {
            Log.e(TAG, "Test görsel gösterme hatası: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            try {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Seçilen görseli yükle
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    selectedImage = BitmapFactory.decodeStream(inputStream);

                    if (selectedImage != null) {
                        // Preview'i güncelle
                        imageViewPreview.setImageBitmap(selectedImage);

                        // UI'ı güncelle
                        updateImageInfo();

                        Toast.makeText(this, "Görsel seçildi", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Görsel yüklenemedi", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Görsel yükleme hatası: " + e.getMessage());
                Toast.makeText(this, "Görsel yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi, resim seç
                selectImage();
            } else {
                Toast.makeText(this, "Galeri erişim izni gerekli", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Görseli yeniden yükle
        loadProductImage();
    }
}
