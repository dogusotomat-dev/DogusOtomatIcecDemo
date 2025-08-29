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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProductSettingsActivity extends AppCompatActivity {
    private static final String TAG = "ProductSettings";

    // UI Elements
    private Button btnBack;

    // Product Names Tab
    private EditText etIceCreamName, etSauce1Name, etSauce2Name, etSauce3Name;
    private EditText etTopping1Name, etTopping2Name, etTopping3Name;
    private Button btnSaveNames;

    // Product Dosages Tab
    private EditText etIceCreamDosage, etSauce1Dosage, etSauce2Dosage, etSauce3Dosage;
    private EditText etTopping1Dosage, etTopping2Dosage, etTopping3Dosage;
    private Button btnSaveDosages;

    // Product Images Tab
    private ImageView ivIceCream, ivSauce1, ivSauce2, ivSauce3;
    private ImageView ivTopping1, ivTopping2, ivTopping3;
    private Button btnSelectIceCream, btnSelectSauce1, btnSelectSauce2, btnSelectSauce3;
    private Button btnSelectTopping1, btnSelectTopping2, btnSelectTopping3;
    private Button btnSaveImages;

    // Product Prices Tab
    private EditText etIceCreamPrice, etSauce1Price, etSauce2Price, etSauce3Price;
    private EditText etTopping1Price, etTopping2Price, etTopping3Price;
    private Button btnSavePrices;

    // Reset Button
    private Button btnResetToDefault;

    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_product_settings);

            Log.i(TAG, "ProductSettingsActivity onCreate started");

            sharedPreferences = getSharedPreferences("ProductSettings", MODE_PRIVATE);

            initializeViews();
            setupClickListeners();
            loadCurrentSettings();

            Log.i(TAG, "ProductSettingsActivity onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "onCreate error: " + e.getMessage(), e);
            showErrorDialog("Başlatma Hatası", "Ürün ayarları açılırken hata oluştu: " + e.getMessage());
        }
    }

    private void initializeViews() {
        try {
            btnBack = findViewById(R.id.btnBack);
            btnResetToDefault = findViewById(R.id.btnResetToDefault);

            // Product Names
            etIceCreamName = findViewById(R.id.etIceCreamName);
            etSauce1Name = findViewById(R.id.etSauce1Name);
            etSauce2Name = findViewById(R.id.etSauce2Name);
            etSauce3Name = findViewById(R.id.etSauce3Name);
            etTopping1Name = findViewById(R.id.etTopping1Name);
            etTopping2Name = findViewById(R.id.etTopping2Name);
            etTopping3Name = findViewById(R.id.etTopping3Name);
            btnSaveNames = findViewById(R.id.btnSaveNames);

            // Product Dosages
            etIceCreamDosage = findViewById(R.id.etIceCreamDosage);
            etSauce1Dosage = findViewById(R.id.etSauce1Dosage);
            etSauce2Dosage = findViewById(R.id.etSauce2Dosage);
            etSauce3Dosage = findViewById(R.id.etSauce3Dosage);
            etTopping1Dosage = findViewById(R.id.etTopping1Dosage);
            etTopping2Dosage = findViewById(R.id.etTopping2Dosage);
            etTopping3Dosage = findViewById(R.id.etTopping3Dosage);
            btnSaveDosages = findViewById(R.id.btnSaveDosages);

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
            btnSaveImages = findViewById(R.id.btnSaveImages);

            // Product Prices
            etIceCreamPrice = findViewById(R.id.etIceCreamPrice);
            etSauce1Price = findViewById(R.id.etSauce1Price);
            etSauce2Price = findViewById(R.id.etSauce2Price);
            etSauce3Price = findViewById(R.id.etSauce3Price);
            etTopping1Price = findViewById(R.id.etTopping1Price);
            etTopping2Price = findViewById(R.id.etTopping2Price);
            etTopping3Price = findViewById(R.id.etTopping3Price);
            btnSavePrices = findViewById(R.id.btnSavePrices);

            Log.d(TAG, "Views initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "Initialize views error: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            // Product Names Tab
            if (btnSaveNames != null) {
                btnSaveNames.setOnClickListener(v -> saveProductNames());
            }

            // Product Dosages Tab
            if (btnSaveDosages != null) {
                btnSaveDosages.setOnClickListener(v -> saveProductDosages());
            }

            // Product Images Tab
            if (btnSelectIceCream != null) {
                btnSelectIceCream.setOnClickListener(v -> selectProductImage("ice_cream"));
            }
            if (btnSelectSauce1 != null) {
                btnSelectSauce1.setOnClickListener(v -> selectProductImage("sauce_chocolate"));
            }
            if (btnSelectSauce2 != null) {
                btnSelectSauce2.setOnClickListener(v -> selectProductImage("sauce_caramel"));
            }
            if (btnSelectSauce3 != null) {
                btnSelectSauce3.setOnClickListener(v -> selectProductImage("sauce_strawberry"));
            }
            if (btnSelectTopping1 != null) {
                btnSelectTopping1.setOnClickListener(v -> selectProductImage("decor_nuts"));
            }
            if (btnSelectTopping2 != null) {
                btnSelectTopping2.setOnClickListener(v -> selectProductImage("decor_sprinkles"));
            }
            if (btnSelectTopping3 != null) {
                btnSelectTopping3.setOnClickListener(v -> selectProductImage("decor_whipped_cream"));
            }
            if (btnSaveImages != null) {
                btnSaveImages.setOnClickListener(v -> saveProductImages());
            }

            // Product Prices Tab
            if (btnSavePrices != null) {
                btnSavePrices.setOnClickListener(v -> saveProductPrices());
            }

            // Setup image picker launcher
            setupImagePickerLauncher();

            Log.d(TAG, "Click listeners setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Setup click listeners error: " + e.getMessage(), e);
        }
    }

    private void setupImagePickerLauncher() {
        try {
            imagePickerLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null) {
                                // Save the selected image
                                saveProductImage(selectedImageUri, getCurrentSelectedProductType());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Setup image picker launcher error: " + e.getMessage(), e);
        }
    }

    private String getCurrentSelectedProductType() {
        // This should be set when a button is clicked
        return "ice_cream"; // Default
    }

    private void selectProductImage(String productType) {
        try {
            setCurrentSelectedProductType(productType);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Select product image error: " + e.getMessage(), e);
            showToast("Görsel seçilemedi: " + e.getMessage());
        }
    }

    private void setCurrentSelectedProductType(String productType) {
        // Store the current selected product type for image picker
        sharedPreferences.edit().putString("current_selected_product", productType).apply();
    }

    private void loadCurrentSettings() {
        try {
            // Load product names
            String iceCreamName = sharedPreferences.getString("ice_cream_name", "Sade Dondurma");
            String sauce1Name = sharedPreferences.getString("sauce1_name", "Çikolata Sos");
            String sauce2Name = sharedPreferences.getString("sauce2_name", "Karamel Sos");
            String sauce3Name = sharedPreferences.getString("sauce3_name", "Çilek Sos");
            String topping1Name = sharedPreferences.getString("topping1_name", "Fındık");
            String topping2Name = sharedPreferences.getString("topping2_name", "Renkli Şeker");
            String topping3Name = sharedPreferences.getString("topping3_name", "Krem Şanti");

            if (etIceCreamName != null)
                etIceCreamName.setText(iceCreamName);
            if (etSauce1Name != null)
                etSauce1Name.setText(sauce1Name);
            if (etSauce2Name != null)
                etSauce2Name.setText(sauce2Name);
            if (etSauce3Name != null)
                etSauce3Name.setText(sauce3Name);
            if (etTopping1Name != null)
                etTopping1Name.setText(topping1Name);
            if (etTopping2Name != null)
                etTopping2Name.setText(topping2Name);
            if (etTopping3Name != null)
                etTopping3Name.setText(topping3Name);

            // Load product dosages (motor rotation values 0-255)
            String iceCreamDosage = sharedPreferences.getString("ice_cream_dosage", "100");
            String sauce1Dosage = sharedPreferences.getString("sauce1_dosage", "20");
            String sauce2Dosage = sharedPreferences.getString("sauce2_dosage", "20");
            String sauce3Dosage = sharedPreferences.getString("sauce3_dosage", "20");
            String topping1Dosage = sharedPreferences.getString("topping1_dosage", "15");
            String topping2Dosage = sharedPreferences.getString("topping2_dosage", "10");
            String topping3Dosage = sharedPreferences.getString("topping3_dosage", "15");

            if (etIceCreamDosage != null)
                etIceCreamDosage.setText(iceCreamDosage);
            if (etSauce1Dosage != null)
                etSauce1Dosage.setText(sauce1Dosage);
            if (etSauce2Dosage != null)
                etSauce2Dosage.setText(sauce2Dosage);
            if (etSauce3Dosage != null)
                etSauce3Dosage.setText(sauce3Dosage);
            if (etTopping1Dosage != null)
                etTopping1Dosage.setText(topping1Dosage);
            if (etTopping2Dosage != null)
                etTopping2Dosage.setText(topping2Dosage);
            if (etTopping3Dosage != null)
                etTopping3Dosage.setText(topping3Dosage);

            // Load product prices
            float iceCreamPrice = sharedPreferences.getFloat("ice_cream_price", 8.0f);
            float sauce1Price = sharedPreferences.getFloat("sauce1_price", 2.0f);
            float sauce2Price = sharedPreferences.getFloat("sauce2_price", 2.5f);
            float sauce3Price = sharedPreferences.getFloat("sauce3_price", 2.0f);
            float topping1Price = sharedPreferences.getFloat("topping1_price", 1.5f);
            float topping2Price = sharedPreferences.getFloat("topping2_price", 1.0f);
            float topping3Price = sharedPreferences.getFloat("topping3_price", 1.5f);

            if (etIceCreamPrice != null)
                etIceCreamPrice.setText(String.valueOf(iceCreamPrice));
            if (etSauce1Price != null)
                etSauce1Price.setText(String.valueOf(sauce1Price));
            if (etSauce2Price != null)
                etSauce2Price.setText(String.valueOf(sauce2Price));
            if (etSauce3Price != null)
                etSauce3Price.setText(String.valueOf(sauce3Price));
            if (etTopping1Price != null)
                etTopping1Price.setText(String.valueOf(topping1Price));
            if (etTopping2Price != null)
                etTopping2Price.setText(String.valueOf(topping2Price));
            if (etTopping3Price != null)
                etTopping3Price.setText(String.valueOf(topping3Price));

            // Load product images
            loadProductImages();

            Log.d(TAG, "Current settings loaded successfully");

        } catch (Exception e) {
            Log.e(TAG, "Load current settings error: " + e.getMessage(), e);
        }
    }

    private void loadProductImages() {
        try {
            // Load saved product image paths and display them
            String[] productTypes = { "ice_cream", "sauce_chocolate", "sauce_caramel", "sauce_strawberry", "decor_nuts",
                    "decor_sprinkles", "decor_whipped_cream" };
            ImageView[] imageViews = { ivIceCream, ivSauce1, ivSauce2, ivSauce3, ivTopping1, ivTopping2, ivTopping3 };

            for (int i = 0; i < productTypes.length; i++) {
                String imagePath = sharedPreferences.getString("product_image_" + productTypes[i], "");
                if (!imagePath.isEmpty() && imageViews[i] != null) {
                    loadImageFromPath(imageViews[i], imagePath);
                }
            }

            Log.d(TAG, "Product images loaded successfully");

        } catch (Exception e) {
            Log.e(TAG, "Load product images error: " + e.getMessage(), e);
        }
    }

    private void loadImageFromPath(ImageView imageView, String imagePath) {
        try {
            if (imageView == null || imagePath == null || imagePath.isEmpty())
                return;

            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Load image from path error: " + e.getMessage(), e);
        }
    }

    private void saveProductNames() {
        try {
            String iceCreamName = etIceCreamName != null ? etIceCreamName.getText().toString() : "Sade Dondurma";
            String sauce1Name = etSauce1Name != null ? etSauce1Name.getText().toString() : "Çikolata Sos";
            String sauce2Name = etSauce2Name != null ? etSauce2Name.getText().toString() : "Karamel Sos";
            String sauce3Name = etSauce3Name != null ? etSauce3Name.getText().toString() : "Çilek Sos";
            String topping1Name = etTopping1Name != null ? etTopping1Name.getText().toString() : "Fındık";
            String topping2Name = etTopping2Name != null ? etTopping2Name.getText().toString() : "Renkli Şeker";
            String topping3Name = etTopping3Name != null ? etTopping3Name.getText().toString() : "Krem Şanti";

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ice_cream_name", iceCreamName);
            editor.putString("sauce1_name", sauce1Name);
            editor.putString("sauce2_name", sauce2Name);
            editor.putString("sauce3_name", sauce3Name);
            editor.putString("topping1_name", topping1Name);
            editor.putString("topping2_name", topping2Name);
            editor.putString("topping3_name", topping3Name);
            editor.apply();

            // Ana ekrana yansıyacak şekilde ProductPrefs'e de kaydet
            SharedPreferences productPrefs = getSharedPreferences("ProductPrefs", MODE_PRIVATE);
            SharedPreferences.Editor productEditor = productPrefs.edit();
            productEditor.putString("sauce_chocolate_name", sauce1Name);
            productEditor.putString("sauce_caramel_name", sauce2Name);
            productEditor.putString("sauce_fruit_name", sauce3Name);
            productEditor.putString("topping_chocolate_name", topping1Name);
            productEditor.putString("topping_hazelnut_name", topping2Name);
            productEditor.putString("topping_fruit_name", topping3Name);
            productEditor.apply();

            showToast("Ürün isimleri kaydedildi ve ana ekrana yansıtıldı!");
            Log.i(TAG, "Product names saved successfully and synced to main screen");

        } catch (Exception e) {
            Log.e(TAG, "Save product names error: " + e.getMessage(), e);
            showToast("Ürün isimleri kaydedilemedi: " + e.getMessage());
        }
    }

    private void saveProductDosages() {
        try {
            String iceCreamDosage = etIceCreamDosage != null ? etIceCreamDosage.getText().toString() : "100ml";
            String sauce1Dosage = etSauce1Dosage != null ? etSauce1Dosage.getText().toString() : "20ml";
            String sauce2Dosage = etSauce2Dosage != null ? etSauce2Dosage.getText().toString() : "20ml";
            String sauce3Dosage = etSauce3Dosage != null ? etSauce3Dosage.getText().toString() : "20ml";
            String topping1Dosage = etTopping1Dosage != null ? etTopping1Dosage.getText().toString() : "15g";
            String topping2Dosage = etTopping2Dosage != null ? etTopping2Dosage.getText().toString() : "10g";
            String topping3Dosage = etTopping3Dosage != null ? etTopping3Dosage.getText().toString() : "15g";

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ice_cream_dosage", iceCreamDosage);
            editor.putString("sauce1_dosage", sauce1Dosage);
            editor.putString("sauce2_dosage", sauce2Dosage);
            editor.putString("sauce3_dosage", sauce3Dosage);
            editor.putString("topping1_dosage", topping1Dosage);
            editor.putString("topping2_dosage", topping2Dosage);
            editor.putString("topping3_dosage", topping3Dosage);
            editor.apply();

            showToast("Ürün dozajları kaydedildi!");
            Log.i(TAG, "Product dosages saved successfully");

        } catch (Exception e) {
            Log.e(TAG, "Save product dosages error: " + e.getMessage(), e);
            showToast("Ürün dozajları kaydedilemedi: " + e.getMessage());
        }
    }

    private void saveProductImages() {
        try {
            showToast("Ürün görselleri kaydedildi!");
            Log.i(TAG, "Product images saved successfully");

        } catch (Exception e) {
            Log.e(TAG, "Save product images error: " + e.getMessage(), e);
            showToast("Ürün görselleri kaydedilemedi: " + e.getMessage());
        }
    }

    private void saveProductPrices() {
        try {
            float iceCreamPrice = Float
                    .parseFloat(etIceCreamPrice != null ? etIceCreamPrice.getText().toString() : "8.0");
            float sauce1Price = Float.parseFloat(etSauce1Price != null ? etSauce1Price.getText().toString() : "2.0");
            float sauce2Price = Float.parseFloat(etSauce2Price != null ? etSauce2Price.getText().toString() : "2.5");
            float sauce3Price = Float.parseFloat(etSauce3Price != null ? etSauce3Price.getText().toString() : "2.0");
            float topping1Price = Float
                    .parseFloat(etTopping1Price != null ? etTopping1Price.getText().toString() : "1.5");
            float topping2Price = Float
                    .parseFloat(etTopping2Price != null ? etTopping2Price.getText().toString() : "1.0");
            float topping3Price = Float
                    .parseFloat(etTopping3Price != null ? etTopping3Price.getText().toString() : "1.5");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("ice_cream_price", iceCreamPrice);
            editor.putFloat("sauce1_price", sauce1Price);
            editor.putFloat("sauce2_price", sauce2Price);
            editor.putFloat("sauce3_price", sauce3Price);
            editor.putFloat("topping1_price", topping1Price);
            editor.putFloat("topping2_price", topping2Price);
            editor.putFloat("topping3_price", topping3Price);
            editor.apply();

            // Ana ekrana yansıyacak şekilde ProductPrefs'e de kaydet
            SharedPreferences productPrefs = getSharedPreferences("ProductPrefs", MODE_PRIVATE);
            SharedPreferences.Editor productEditor = productPrefs.edit();
            productEditor.putFloat("sauce_chocolate_price", sauce1Price);
            productEditor.putFloat("sauce_caramel_price", sauce2Price);
            productEditor.putFloat("sauce_fruit_price", sauce3Price);
            productEditor.putFloat("topping_chocolate_price", topping1Price);
            productEditor.putFloat("topping_hazelnut_price", topping2Price);
            productEditor.putFloat("topping_fruit_price", topping3Price);
            productEditor.apply();

            showToast("Ürün fiyatları kaydedildi ve ana ekrana yansıtıldı!");
            Log.i(TAG, "Product prices saved successfully and synced to main screen");

        } catch (Exception e) {
            Log.e(TAG, "Save product prices error: " + e.getMessage(), e);
            showToast("Ürün fiyatları kaydedilemedi: " + e.getMessage());
        }
    }

    private void saveProductImage(Uri imageUri, String productType) {
        try {
            if (imageUri == null || productType == null)
                return;

            String fileName = "product_" + productType + "_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(getExternalFilesDir("ProductImages"), fileName);

            // Ensure directory exists
            if (!imageFile.getParentFile().exists()) {
                imageFile.getParentFile().mkdirs();
            }

            // Copy image to app directory
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            // Save path to preferences
            sharedPreferences.edit().putString("product_image_" + productType, imageFile.getAbsolutePath()).apply();

            // Update UI
            updateProductImage(productType, imageFile.getAbsolutePath());
            showToast("Ürün görseli kaydedildi!");

            Log.i(TAG, "Product image saved: " + fileName);

        } catch (Exception e) {
            Log.e(TAG, "Save product image error: " + e.getMessage(), e);
            showToast("Görsel kaydedilemedi: " + e.getMessage());
        }
    }

    private void updateProductImage(String productType, String imagePath) {
        try {
            ImageView imageView = null;
            switch (productType) {
                case "ice_cream":
                    imageView = ivIceCream;
                    break;
                case "sauce_chocolate":
                    imageView = ivSauce1;
                    break;
                case "sauce_caramel":
                    imageView = ivSauce2;
                    break;
                case "sauce_strawberry":
                    imageView = ivSauce3;
                    break;
                case "decor_nuts":
                    imageView = ivTopping1;
                    break;
                case "decor_sprinkles":
                    imageView = ivTopping2;
                    break;
                case "decor_whipped_cream":
                    imageView = ivTopping3;
                    break;
            }

            if (imageView != null) {
                loadImageFromPath(imageView, imagePath);
                // Force refresh the UI
                imageView.invalidate();
                Log.d(TAG, "Product image updated for: " + productType + " at path: " + imagePath);
            } else {
                Log.e(TAG, "ImageView not found for product type: " + productType);
            }

        } catch (Exception e) {
            Log.e(TAG, "Update product image error: " + e.getMessage(), e);
        }
    }

    private void showToast(String message) {
        try {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Show toast error: " + e.getMessage(), e);
        }
    }

    private void showErrorDialog(String title, String message) {
        try {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Tamam", (dialog, which) -> {
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error dialog error: " + e.getMessage(), e);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            Log.i(TAG, "ProductSettingsActivity onDestroy");
            super.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "onDestroy error: " + e.getMessage(), e);
        }
    }
}
