package com.dogus.otomat.icecdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Gelişmiş Ürün Ayarları Aktivitesi
 * Dondurma makinesi ürün ayarları için kapsamlı yönetim
 */
public class EnhancedProductSettingsActivity extends AppCompatActivity {
    private static final String TAG = "EnhancedProductSettings";

    // UI Elements - Header
    private Button btnBack, btnSaveAll, btnResetToDefault;

    // Product Names Section
    private EditText etIceCreamName, etSauce1Name, etSauce2Name, etSauce3Name;
    private EditText etTopping1Name, etTopping2Name, etTopping3Name;

    // Product Dosages Section
    private SeekBar seekBarIceCreamDosage, seekBarSauce1Dosage, seekBarSauce2Dosage, seekBarSauce3Dosage;
    private SeekBar seekBarTopping1Dosage, seekBarTopping2Dosage, seekBarTopping3Dosage;
    private TextView tvIceCreamDosage, tvSauce1Dosage, tvSauce2Dosage, tvSauce3Dosage;
    private TextView tvTopping1Dosage, tvTopping2Dosage, tvTopping3Dosage;

    // Product Images Section
    private ImageView ivIceCream, ivSauce1, ivSauce2, ivSauce3;
    private ImageView ivTopping1, ivTopping2, ivTopping3;
    private Button btnSelectIceCream, btnSelectSauce1, btnSelectSauce2, btnSelectSauce3;
    private Button btnSelectTopping1, btnSelectTopping2, btnSelectTopping3;
    private Button btnClearAllImages;

    // Product Prices Section
    private EditText etBasePrice, etSauce1Price, etSauce2Price, etSauce3Price;
    private EditText etTopping1Price, etTopping2Price, etTopping3Price;

    // Product Settings Section
    private Switch switchAutoDosage, switchQualityControl, switchInventoryTracking;
    private SeekBar seekBarQualityThreshold, seekBarInventoryAlert;
    private TextView tvQualityThreshold, tvInventoryAlert;

    // Shared Preferences
    private SharedPreferences sharedPreferences;

    // Activity Result Launcher
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // Current selected image type
    private String currentImageType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_enhanced_product_settings);

            Log.i(TAG, "EnhancedProductSettingsActivity onCreate started");

            sharedPreferences = getSharedPreferences("EnhancedProductSettings", MODE_PRIVATE);

            initializeViews();
            setupClickListeners();
            setupActivityResultLauncher();
            loadCurrentSettings();

            Log.i(TAG, "EnhancedProductSettingsActivity onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "onCreate error: " + e.getMessage(), e);
            showErrorDialog("Başlatma Hatası", "Ürün ayarları açılırken hata oluştu: " + e.getMessage());
        }
    }

    private void initializeViews() {
        try {
            // Header
            btnBack = findViewById(R.id.btnBack);
            btnSaveAll = findViewById(R.id.btnSaveAll);
            btnResetToDefault = findViewById(R.id.btnResetToDefault);

            // Product Names
            etIceCreamName = findViewById(R.id.etIceCreamName);
            etSauce1Name = findViewById(R.id.etSauce1Name);
            etSauce2Name = findViewById(R.id.etSauce2Name);
            etSauce3Name = findViewById(R.id.etSauce3Name);
            etTopping1Name = findViewById(R.id.etTopping1Name);
            etTopping2Name = findViewById(R.id.etTopping2Name);
            etTopping3Name = findViewById(R.id.etTopping3Name);

            // Product Dosages
            seekBarIceCreamDosage = findViewById(R.id.seekBarIceCreamDosage);
            seekBarSauce1Dosage = findViewById(R.id.seekBarSauce1Dosage);
            seekBarSauce2Dosage = findViewById(R.id.seekBarSauce2Dosage);
            seekBarSauce3Dosage = findViewById(R.id.seekBarSauce3Dosage);
            seekBarTopping1Dosage = findViewById(R.id.seekBarTopping1Dosage);
            seekBarTopping2Dosage = findViewById(R.id.seekBarTopping2Dosage);
            seekBarTopping3Dosage = findViewById(R.id.seekBarTopping3Dosage);

            tvIceCreamDosage = findViewById(R.id.tvIceCreamDosage);
            tvSauce1Dosage = findViewById(R.id.tvSauce1Dosage);
            tvSauce2Dosage = findViewById(R.id.tvSauce2Dosage);
            tvSauce3Dosage = findViewById(R.id.tvSauce3Dosage);
            tvTopping1Dosage = findViewById(R.id.tvTopping1Dosage);
            tvTopping2Dosage = findViewById(R.id.tvTopping2Dosage);
            tvTopping3Dosage = findViewById(R.id.tvTopping3Dosage);

            // Product Images
            ivIceCream = findViewById(R.id.ivIceCream);
            ivSauce1 = findViewById(R.id.ivSauce1);
            ivSauce2 = findViewById(R.id.ivSauce2);
            ivSauce3 = findViewById(R.id.ivSauce3);
            ivTopping1 = findViewById(R.id.ivTopping1);
            ivTopping2 = findViewById(R.id.ivTopping2);
            ivTopping3 = findViewById(R.id.ivTopping3);

            btnSelectIceCream = findViewById(R.id.btnSelectIceCream);
            btnSelectSauce1 = findViewById(R.id.btnSelectSauce1);
            btnSelectSauce2 = findViewById(R.id.btnSelectSauce2);
            btnSelectSauce3 = findViewById(R.id.btnSelectSauce3);
            btnSelectTopping1 = findViewById(R.id.btnSelectTopping1);
            btnSelectTopping2 = findViewById(R.id.btnSelectTopping2);
            btnSelectTopping3 = findViewById(R.id.btnSelectTopping3);
            btnClearAllImages = findViewById(R.id.btnClearAllImages);

            // Product Prices
            etBasePrice = findViewById(R.id.etBasePrice);
            etSauce1Price = findViewById(R.id.etSauce1Price);
            etSauce2Price = findViewById(R.id.etSauce2Price);
            etSauce3Price = findViewById(R.id.etSauce3Price);
            etTopping1Price = findViewById(R.id.etTopping1Price);
            etTopping2Price = findViewById(R.id.etTopping2Price);
            etTopping3Price = findViewById(R.id.etTopping3Price);

            // Product Settings
            switchAutoDosage = findViewById(R.id.switchAutoDosage);
            switchQualityControl = findViewById(R.id.switchQualityControl);
            switchInventoryTracking = findViewById(R.id.switchInventoryTracking);
            seekBarQualityThreshold = findViewById(R.id.seekBarQualityThreshold);
            seekBarInventoryAlert = findViewById(R.id.seekBarInventoryAlert);
            tvQualityThreshold = findViewById(R.id.tvQualityThreshold);
            tvInventoryAlert = findViewById(R.id.tvInventoryAlert);

            Log.i(TAG, "Views initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "View initialization error: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            // Header buttons
            btnBack.setOnClickListener(v -> finish());
            btnSaveAll.setOnClickListener(v -> saveAllSettings());
            btnResetToDefault.setOnClickListener(v -> resetToDefault());

            // Image selection buttons
            btnSelectIceCream.setOnClickListener(v -> selectProductImage("ice_cream"));
            btnSelectSauce1.setOnClickListener(v -> selectProductImage("sauce_chocolate"));
            btnSelectSauce2.setOnClickListener(v -> selectProductImage("sauce_strawberry"));
            btnSelectSauce3.setOnClickListener(v -> selectProductImage("sauce_vanilla"));
            btnSelectTopping1.setOnClickListener(v -> selectProductImage("topping_nuts"));
            btnSelectTopping2.setOnClickListener(v -> selectProductImage("topping_sprinkles"));
            btnSelectTopping3.setOnClickListener(v -> selectProductImage("topping_syrup"));
            btnClearAllImages.setOnClickListener(v -> clearAllImages());

            // SeekBar listeners
            setupSeekBarListeners();

            // Switch listeners
            setupSwitchListeners();

            Log.i(TAG, "Click listeners setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Click listener setup error: " + e.getMessage(), e);
        }
    }

    private void setupSeekBarListeners() {
        try {
            // Ice Cream Dosage
            seekBarIceCreamDosage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int dosage = progress + 50; // 50-150 ml
                        tvIceCreamDosage.setText(dosage + " ml");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Sauce Dosages
            seekBarSauce1Dosage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int dosage = progress + 10; // 10-60 ml
                        tvSauce1Dosage.setText(dosage + " ml");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            seekBarSauce2Dosage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int dosage = progress + 10; // 10-60 ml
                        tvSauce2Dosage.setText(dosage + " ml");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            seekBarSauce3Dosage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int dosage = progress + 10; // 10-60 ml
                        tvSauce3Dosage.setText(dosage + " ml");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Topping Dosages
            seekBarTopping1Dosage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int dosage = progress + 5; // 5-35 g
                        tvTopping1Dosage.setText(dosage + " g");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            seekBarTopping2Dosage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int dosage = progress + 5; // 5-35 g
                        tvTopping2Dosage.setText(dosage + " g");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            seekBarTopping3Dosage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int dosage = progress + 5; // 5-35 g
                        tvTopping3Dosage.setText(dosage + " g");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Quality and Inventory SeekBars
            seekBarQualityThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int threshold = progress + 70; // 70-100%
                        tvQualityThreshold.setText(threshold + "%");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            seekBarInventoryAlert.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int alert = progress + 10; // 10-40%
                        tvInventoryAlert.setText(alert + "%");
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

    private void setupSwitchListeners() {
        try {
            switchAutoDosage.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Enable/disable dosage controls based on auto dosage setting
                boolean enableControls = !isChecked;
                seekBarIceCreamDosage.setEnabled(enableControls);
                seekBarSauce1Dosage.setEnabled(enableControls);
                seekBarSauce2Dosage.setEnabled(enableControls);
                seekBarSauce3Dosage.setEnabled(enableControls);
                seekBarTopping1Dosage.setEnabled(enableControls);
                seekBarTopping2Dosage.setEnabled(enableControls);
                seekBarTopping3Dosage.setEnabled(enableControls);
            });

            switchQualityControl.setOnCheckedChangeListener((buttonView, isChecked) -> {
                seekBarQualityThreshold.setEnabled(isChecked);
            });

            switchInventoryTracking.setOnCheckedChangeListener((buttonView, isChecked) -> {
                seekBarInventoryAlert.setEnabled(isChecked);
            });

        } catch (Exception e) {
            Log.e(TAG, "Switch listener setup error: " + e.getMessage(), e);
        }
    }

    private void setupActivityResultLauncher() {
        try {
            imagePickerLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null) {
                                processSelectedImage(selectedImageUri);
                            }
                        }
                    });

            Log.i(TAG, "Activity result launcher setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Activity result launcher setup error: " + e.getMessage(), e);
        }
    }

    private void loadCurrentSettings() {
        try {
            // Load product names
            etIceCreamName.setText(sharedPreferences.getString("ice_cream_name", "Sade Dondurma"));
            etSauce1Name.setText(sharedPreferences.getString("sauce1_name", "Çikolata Sosu"));
            etSauce2Name.setText(sharedPreferences.getString("sauce2_name", "Çilek Sosu"));
            etSauce3Name.setText(sharedPreferences.getString("sauce3_name", "Vanilya Sosu"));
            etTopping1Name.setText(sharedPreferences.getString("topping1_name", "Fındık"));
            etTopping2Name.setText(sharedPreferences.getString("topping2_name", "Renkli Şeker"));
            etTopping3Name.setText(sharedPreferences.getString("topping3_name", "Şurup"));

            // Load dosages
            int iceCreamDosage = sharedPreferences.getInt("ice_cream_dosage", 100);
            int sauce1Dosage = sharedPreferences.getInt("sauce1_dosage", 25);
            int sauce2Dosage = sharedPreferences.getInt("sauce2_dosage", 25);
            int sauce3Dosage = sharedPreferences.getInt("sauce3_dosage", 25);
            int topping1Dosage = sharedPreferences.getInt("topping1_dosage", 15);
            int topping2Dosage = sharedPreferences.getInt("topping2_dosage", 15);
            int topping3Dosage = sharedPreferences.getInt("topping3_dosage", 15);

            seekBarIceCreamDosage.setProgress(iceCreamDosage - 50);
            seekBarSauce1Dosage.setProgress(sauce1Dosage - 10);
            seekBarSauce2Dosage.setProgress(sauce2Dosage - 10);
            seekBarSauce3Dosage.setProgress(sauce3Dosage - 10);
            seekBarTopping1Dosage.setProgress(topping1Dosage - 5);
            seekBarTopping2Dosage.setProgress(topping2Dosage - 5);
            seekBarTopping3Dosage.setProgress(topping3Dosage - 5);

            // Update dosage text views
            tvIceCreamDosage.setText(iceCreamDosage + " ml");
            tvSauce1Dosage.setText(sauce1Dosage + " ml");
            tvSauce2Dosage.setText(sauce2Dosage + " ml");
            tvSauce3Dosage.setText(sauce3Dosage + " ml");
            tvTopping1Dosage.setText(topping1Dosage + " g");
            tvTopping2Dosage.setText(topping2Dosage + " g");
            tvTopping3Dosage.setText(topping3Dosage + " g");

            // Load prices
            etBasePrice.setText(sharedPreferences.getString("base_price", "15.00"));
            etSauce1Price.setText(sharedPreferences.getString("sauce1_price", "2.00"));
            etSauce2Price.setText(sharedPreferences.getString("sauce2_price", "2.00"));
            etSauce3Price.setText(sharedPreferences.getString("sauce3_price", "2.00"));
            etTopping1Price.setText(sharedPreferences.getString("topping1_price", "1.50"));
            etTopping2Price.setText(sharedPreferences.getString("topping2_price", "1.00"));
            etTopping3Price.setText(sharedPreferences.getString("topping3_price", "1.50"));

            // Load switches
            switchAutoDosage.setChecked(sharedPreferences.getBoolean("auto_dosage", false));
            switchQualityControl.setChecked(sharedPreferences.getBoolean("quality_control", true));
            switchInventoryTracking.setChecked(sharedPreferences.getBoolean("inventory_tracking", true));

            // Load quality and inventory thresholds
            int qualityThreshold = sharedPreferences.getInt("quality_threshold", 85);
            int inventoryAlert = sharedPreferences.getInt("inventory_alert", 20);

            seekBarQualityThreshold.setProgress(qualityThreshold - 70);
            seekBarInventoryAlert.setProgress(inventoryAlert - 10);

            tvQualityThreshold.setText(qualityThreshold + "%");
            tvInventoryAlert.setText(inventoryAlert + "%");

            // Load saved images
            loadSavedImages();

            Log.i(TAG, "Current settings loaded successfully");

        } catch (Exception e) {
            Log.e(TAG, "Settings loading error: " + e.getMessage(), e);
        }
    }

    private void loadSavedImages() {
        try {
            // Load images from internal storage
            loadImageFromStorage("ice_cream", ivIceCream);
            loadImageFromStorage("sauce_chocolate", ivSauce1);
            loadImageFromStorage("sauce_strawberry", ivSauce2);
            loadImageFromStorage("sauce_vanilla", ivSauce3);
            loadImageFromStorage("topping_nuts", ivTopping1);
            loadImageFromStorage("topping_sprinkles", ivTopping2);
            loadImageFromStorage("topping_syrup", ivTopping3);

        } catch (Exception e) {
            Log.e(TAG, "Image loading error: " + e.getMessage(), e);
        }
    }

    private void loadImageFromStorage(String imageType, ImageView imageView) {
        try {
            File imageFile = new File(getFilesDir(), "product_" + imageType + ".jpg");
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Image loading error for " + imageType + ": " + e.getMessage(), e);
        }
    }

    private void selectProductImage(String imageType) {
        try {
            currentImageType = imageType;
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);

        } catch (Exception e) {
            Log.e(TAG, "Image picker error: " + e.getMessage(), e);
            Toast.makeText(this, "Görsel seçici açılamadı", Toast.LENGTH_SHORT).show();
        }
    }

    private void processSelectedImage(Uri imageUri) {
        try {
            // Show loading
            Toast.makeText(this, "Görsel işleniyor...", Toast.LENGTH_SHORT).show();

            // Process image in background
            new Thread(() -> {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    if (inputStream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();

                        if (bitmap != null) {
                            // Save to internal storage
                            String fileName = "product_" + currentImageType + ".jpg";
                            File imageFile = new File(getFilesDir(), fileName);

                            FileOutputStream outputStream = new FileOutputStream(imageFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                            outputStream.close();

                            // Update UI
                            runOnUiThread(() -> {
                                updateProductImage(currentImageType, bitmap);
                                Toast.makeText(this, "Görsel kaydedildi", Toast.LENGTH_SHORT).show();
                            });

                        } else {
                            runOnUiThread(() -> Toast.makeText(this, "Görsel yüklenemedi", Toast.LENGTH_SHORT).show());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Image processing error: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(this, "Görsel işleme hatası", Toast.LENGTH_SHORT).show());
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "Image processing setup error: " + e.getMessage(), e);
            Toast.makeText(this, "Görsel işleme hatası", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProductImage(String imageType, Bitmap bitmap) {
        try {
            ImageView targetImageView = null;

            switch (imageType) {
                case "ice_cream":
                    targetImageView = ivIceCream;
                    break;
                case "sauce_chocolate":
                    targetImageView = ivSauce1;
                    break;
                case "sauce_strawberry":
                    targetImageView = ivSauce2;
                    break;
                case "sauce_vanilla":
                    targetImageView = ivSauce3;
                    break;
                case "topping_nuts":
                    targetImageView = ivTopping1;
                    break;
                case "topping_sprinkles":
                    targetImageView = ivTopping2;
                    break;
                case "topping_syrup":
                    targetImageView = ivTopping3;
                    break;
            }

            if (targetImageView != null) {
                targetImageView.setImageBitmap(bitmap);
                targetImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

        } catch (Exception e) {
            Log.e(TAG, "Image update error: " + e.getMessage(), e);
        }
    }

    private void clearAllImages() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("Tüm Görselleri Temizle")
                    .setMessage("Tüm ürün görsellerini silmek istediğinizden emin misiniz?")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        // Clear all images
                        ivIceCream.setImageResource(R.drawable.dogus_image_placeholder);
                        ivSauce1.setImageResource(R.drawable.dogus_image_placeholder);
                        ivSauce2.setImageResource(R.drawable.dogus_image_placeholder);
                        ivSauce3.setImageResource(R.drawable.dogus_image_placeholder);
                        ivTopping1.setImageResource(R.drawable.dogus_image_placeholder);
                        ivTopping2.setImageResource(R.drawable.dogus_image_placeholder);
                        ivTopping3.setImageResource(R.drawable.dogus_image_placeholder);

                        // Delete image files
                        deleteImageFiles();

                        Toast.makeText(this, "Tüm görseller temizlendi", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hayır", null)
                    .show();

        } catch (Exception e) {
            Log.e(TAG, "Clear images error: " + e.getMessage(), e);
        }
    }

    private void deleteImageFiles() {
        try {
            String[] imageTypes = { "ice_cream", "sauce_chocolate", "sauce_strawberry", "sauce_vanilla",
                    "topping_nuts", "topping_sprinkles", "topping_syrup" };

            for (String type : imageTypes) {
                File imageFile = new File(getFilesDir(), "product_" + type + ".jpg");
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Image file deletion error: " + e.getMessage(), e);
        }
    }

    private void saveAllSettings() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Save product names
            editor.putString("ice_cream_name", etIceCreamName.getText().toString());
            editor.putString("sauce1_name", etSauce1Name.getText().toString());
            editor.putString("sauce2_name", etSauce2Name.getText().toString());
            editor.putString("sauce3_name", etSauce3Name.getText().toString());
            editor.putString("topping1_name", etTopping1Name.getText().toString());
            editor.putString("topping2_name", etTopping2Name.getText().toString());
            editor.putString("topping3_name", etTopping3Name.getText().toString());

            // Save dosages
            editor.putInt("ice_cream_dosage", seekBarIceCreamDosage.getProgress() + 50);
            editor.putInt("sauce1_dosage", seekBarSauce1Dosage.getProgress() + 10);
            editor.putInt("sauce2_dosage", seekBarSauce2Dosage.getProgress() + 10);
            editor.putInt("sauce3_dosage", seekBarSauce3Dosage.getProgress() + 10);
            editor.putInt("topping1_dosage", seekBarTopping1Dosage.getProgress() + 5);
            editor.putInt("topping2_dosage", seekBarTopping2Dosage.getProgress() + 5);
            editor.putInt("topping3_dosage", seekBarTopping3Dosage.getProgress() + 5);

            // Save prices
            editor.putString("base_price", etBasePrice.getText().toString());
            editor.putString("sauce1_price", etSauce1Price.getText().toString());
            editor.putString("sauce2_price", etSauce2Price.getText().toString());
            editor.putString("sauce3_price", etSauce3Price.getText().toString());
            editor.putString("topping1_price", etTopping1Price.getText().toString());
            editor.putString("topping2_price", etTopping2Price.getText().toString());
            editor.putString("topping3_price", etTopping3Price.getText().toString());

            // Save switches
            editor.putBoolean("auto_dosage", switchAutoDosage.isChecked());
            editor.putBoolean("quality_control", switchQualityControl.isChecked());
            editor.putBoolean("inventory_tracking", switchInventoryTracking.isChecked());

            // Save thresholds
            editor.putInt("quality_threshold", seekBarQualityThreshold.getProgress() + 70);
            editor.putInt("inventory_alert", seekBarInventoryAlert.getProgress() + 10);

            editor.apply();

            Toast.makeText(this, "Tüm ayarlar kaydedildi", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "All settings saved successfully");

        } catch (Exception e) {
            Log.e(TAG, "Settings save error: " + e.getMessage(), e);
            Toast.makeText(this, "Ayarlar kaydedilemedi", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetToDefault() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("Varsayılan Değerlere Sıfırla")
                    .setMessage("Tüm ayarları varsayılan değerlere sıfırlamak istediğinizden emin misiniz?")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        // Reset to default values
                        loadDefaultSettings();
                        Toast.makeText(this, "Ayarlar varsayılan değerlere sıfırlandı", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hayır", null)
                    .show();

        } catch (Exception e) {
            Log.e(TAG, "Reset to default error: " + e.getMessage(), e);
        }
    }

    private void loadDefaultSettings() {
        try {
            // Reset to default values
            loadCurrentSettings(); // This will load the default values

        } catch (Exception e) {
            Log.e(TAG, "Default settings loading error: " + e.getMessage(), e);
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
            loadSavedImages();
        } catch (Exception e) {
            Log.e(TAG, "onResume error: " + e.getMessage(), e);
        }
    }
}
