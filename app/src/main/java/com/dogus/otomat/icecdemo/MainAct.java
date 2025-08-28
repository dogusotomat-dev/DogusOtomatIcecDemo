package com.dogus.otomat.icecdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainAct extends AppCompatActivity {
    private static final String TAG = "MainAct";

    // UI Elements
    private TextView tvCartItems;
    private TextView tvTotalPrice;
    private TextView tvBasePrice;
    private TextView tvMachineInfo;
    private Button btnSauceChocolate;
    private Button btnSauceCaramel;
    private Button btnSauceStrawberry;
    private Button btnDecorNuts;
    private Button btnDecorSprinkles;
    private Button btnDecorWhippedCream;
    private Button btnClearCart;
    private Button btnCheckout;
    private Button btnAdminLogin;
    private ImageView advertisementImageView;
    private ImageView screensaverImageView;
    private VideoView advertisementVideoView;

    // Data
    private List<CartItem> cartItems;
    private Set<String> selectedSauces;
    private Set<String> selectedToppings;
    private double totalPrice;
    private double basePrice = 8.0;
    private long advertisementDuration = 15000; // 15 saniye
    private long screensaverDelay = 60000; // 1 dakika

    // Machine Identity - Sadece seri numarası gösterilecek
    private String machineSerialNumber = "";
    private boolean isMachineIdentified = false;

    // Managers - Sadece gerekli olanlar
    private SharedPreferences sharedPreferences;
    private MDBPaymentManager mdbManager;

    // Advertisement and Screensaver
    private Handler mainHandler;
    private Runnable advertisementRunnable;
    private Runnable screensaverRunnable;
    private boolean isAdvertisementActive = false;
    private boolean isScreensaverActive = false;
    private boolean isUserInteracting = false;

    // Product Images
    private Map<String, String> productImagePaths = new HashMap<>();
    private String advertisementImagePath = "";
    private String advertisementVideoPath = "";
    private String screensaverImagePath = "";

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> videoPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Force portrait orientation
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.activity_main);

        Log.i(TAG, "MainAct onCreate started");
        
        try {
            // Initialize all systems
            initializeData();
        initView();
        setupClickListeners();
            setupActivityResultLaunchers();
            loadMachineIdentity();
            loadAdvertisementSettings();
            loadProductImages();
        initializePaymentSystem();
            initializeAdvertisementSystem();
            initializeScreensaverSystem();
            startUserInteractionTimer();
            
            Log.i(TAG, "MainAct onCreate completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "onCreate error: " + e.getMessage(), e);
            showErrorDialog("Başlatma Hatası", "Uygulama başlatılırken hata oluştu: " + e.getMessage());
        }
    }

    private void initializeData() {
        try {
            sharedPreferences = getSharedPreferences("MachineSettings", MODE_PRIVATE);
            cartItems = new ArrayList<>();
            selectedSauces = new HashSet<>();
            selectedToppings = new HashSet<>();
            totalPrice = basePrice;
            
            mainHandler = new Handler(Looper.getMainLooper());
            
            Log.d(TAG, "Data initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Data initialization error: " + e.getMessage(), e);
        }
    }

    private void initView() {
        try {
            // UI Elements
            tvCartItems = findViewById(R.id.tv_cart_items);
            tvTotalPrice = findViewById(R.id.tv_total_price);
            tvBasePrice = findViewById(R.id.tv_base_price);
            tvMachineInfo = findViewById(R.id.tv_machine_info);
            
            // Sauce Buttons
            btnSauceChocolate = findViewById(R.id.btn_sauce_chocolate);
            btnSauceCaramel = findViewById(R.id.btn_sauce_caramel);
            btnSauceStrawberry = findViewById(R.id.btn_sauce_strawberry);
            
            // Topping Buttons
            btnDecorNuts = findViewById(R.id.btn_decor_nuts);
            btnDecorSprinkles = findViewById(R.id.btn_decor_sprinkles);
            btnDecorWhippedCream = findViewById(R.id.btn_decor_whipped_cream);
            
            // Action Buttons
            btnClearCart = findViewById(R.id.btn_clear_cart);
            btnCheckout = findViewById(R.id.btn_checkout);
            btnAdminLogin = findViewById(R.id.btn_admin_login);

            
            // Advertisement and Screensaver
            advertisementImageView = findViewById(R.id.advertisement_image);
            screensaverImageView = findViewById(R.id.screensaver_image);
            advertisementVideoView = findViewById(R.id.advertisement_video);

            updateDisplay();
            updateProductImages();
            
            Log.d(TAG, "View initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "View initialization error: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            // Sauce buttons
            if (btnSauceChocolate != null) {
                btnSauceChocolate.setOnClickListener(v -> toggleSauce("🍫 Çikolata Sos", 2.0, btnSauceChocolate));
            }
            if (btnSauceCaramel != null) {
                btnSauceCaramel.setOnClickListener(v -> toggleSauce("🍯 Karamel Sos", 2.5, btnSauceCaramel));
            }
            if (btnSauceStrawberry != null) {
                btnSauceStrawberry.setOnClickListener(v -> toggleSauce("🍓 Çilek Sos", 2.0, btnSauceStrawberry));
            }
            
            // Topping buttons
            if (btnDecorNuts != null) {
                btnDecorNuts.setOnClickListener(v -> toggleTopping("🥜 Fındık", 1.5, btnDecorNuts));
            }
            if (btnDecorSprinkles != null) {
                btnDecorSprinkles.setOnClickListener(v -> toggleTopping("🌈 Renkli Şeker", 1.0, btnDecorSprinkles));
            }
            if (btnDecorWhippedCream != null) {
                btnDecorWhippedCream.setOnClickListener(v -> toggleTopping("💨 Krem Şanti", 1.5, btnDecorWhippedCream));
            }
            
            // Action buttons
            if (btnClearCart != null) {
                btnClearCart.setOnClickListener(v -> clearCart());
            }
            if (btnCheckout != null) {
                btnCheckout.setOnClickListener(v -> checkout());
            }
            if (btnAdminLogin != null) {
                btnAdminLogin.setOnClickListener(v -> openAdminLogin());
            }

            
            Log.d(TAG, "Click listeners setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Click listeners setup error: " + e.getMessage(), e);
        }
    }

    private void setupActivityResultLaunchers() {
        try {
            imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            saveProductImage(selectedImageUri, "product");
                        }
                    }
                });

            videoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedVideoUri = result.getData().getData();
                        if (selectedVideoUri != null) {
                            saveAdvertisementVideo(selectedVideoUri);
                        }
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "Setup activity result launchers error: " + e.getMessage(), e);
        }
    }

    private void loadMachineIdentity() {
        try {
            String serialFromStorage = getMachineSerialFromStorage();

            if (serialFromStorage != null && !serialFromStorage.isEmpty()) {
                machineSerialNumber = serialFromStorage;
                isMachineIdentified = true;
                } else {
                generateNewMachineIdentity();
            }

            updateMachineIdentityDisplay();
            Log.d(TAG, "Machine identity loaded: " + machineSerialNumber);
            
        } catch (Exception e) {
            Log.e(TAG, "Machine identity loading error: " + e.getMessage(), e);
        }
    }

    private void loadAdvertisementSettings() {
        try {
            advertisementDuration = sharedPreferences.getLong("advertisement_duration", 15000);
            Log.d(TAG, "Advertisement duration loaded: " + advertisementDuration + "ms");
        } catch (Exception e) {
            Log.e(TAG, "Advertisement settings loading error: " + e.getMessage(), e);
        }
    }

    private void loadProductImages() {
        try {
            // Load saved product image paths
            for (String productType : new String[]{"ice_cream", "sauce_chocolate", "sauce_caramel", "sauce_strawberry", "decor_nuts", "decor_sprinkles", "decor_whipped_cream"}) {
                String imagePath = sharedPreferences.getString("product_image_" + productType, "");
                if (!imagePath.isEmpty()) {
                    productImagePaths.put(productType, imagePath);
                }
            }
            
            // Load advertisement and screensaver paths
            advertisementImagePath = sharedPreferences.getString("advertisement_image", "");
            advertisementVideoPath = sharedPreferences.getString("advertisement_video", "");
            screensaverImagePath = sharedPreferences.getString("screensaver_image", "");
            
            Log.d(TAG, "Product images loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Load product images error: " + e.getMessage(), e);
        }
    }

    private void initializePaymentSystem() {
        try {
            this.mdbManager = MDBPaymentManager.getInstance(this);
            Log.d(TAG, "Payment system initialized");
        } catch (Exception e) {
            Log.e(TAG, "Payment system initialization error: " + e.getMessage(), e);
        }
    }

    private void initializeAdvertisementSystem() {
        try {
            startAdvertisementRotation();
            Log.d(TAG, "Advertisement system initialized");
        } catch (Exception e) {
            Log.e(TAG, "Advertisement system initialization error: " + e.getMessage(), e);
        }
    }

    private void initializeScreensaverSystem() {
        try {
            startScreensaverTimer();
            Log.d(TAG, "Screensaver system initialized");
        } catch (Exception e) {
            Log.e(TAG, "Screensaver system initialization error: " + e.getMessage(), e);
        }
    }

    private void startUserInteractionTimer() {
        try {
            mainHandler.postDelayed(new Runnable() {
            @Override
                public void run() {
                    if (!isUserInteracting) {
                        showScreensaver();
                    }
                    mainHandler.postDelayed(this, screensaverDelay);
                }
            }, screensaverDelay);
        } catch (Exception e) {
            Log.e(TAG, "Start user interaction timer error: " + e.getMessage(), e);
        }
    }

    private void startAdvertisementRotation() {
        try {
            advertisementRunnable = new Runnable() {
            @Override
                public void run() {
                    if (!isScreensaverActive && !isUserInteracting) {
                        showAdvertisement();
                    }
                    mainHandler.postDelayed(this, advertisementDuration);
                }
            };
            mainHandler.post(advertisementRunnable);
        } catch (Exception e) {
            Log.e(TAG, "Start advertisement rotation error: " + e.getMessage(), e);
        }
    }

    private void startScreensaverTimer() {
        try {
            screensaverRunnable = new Runnable() {
            @Override
                public void run() {
                    if (!isAdvertisementActive && !isUserInteracting) {
                        showScreensaver();
                    }
                    mainHandler.postDelayed(this, screensaverDelay);
                }
            };
            mainHandler.post(screensaverRunnable);
        } catch (Exception e) {
            Log.e(TAG, "Start screensaver timer error: " + e.getMessage(), e);
        }
    }

    private void showAdvertisement() {
        try {
            if (isAdvertisementActive) return;
            
            isAdvertisementActive = true;
            
            // Hide other views
            if (advertisementImageView != null) advertisementImageView.setVisibility(View.GONE);
            if (screensaverImageView != null) screensaverImageView.setVisibility(View.GONE);
            
            // Show advertisement (image or video)
            if (!advertisementVideoPath.isEmpty() && advertisementVideoView != null) {
                advertisementVideoView.setVideoPath(advertisementVideoPath);
                advertisementVideoView.setVisibility(View.VISIBLE);
                advertisementVideoView.start();
            } else if (!advertisementImagePath.isEmpty() && advertisementImageView != null) {
                advertisementImageView.setVisibility(View.VISIBLE);
                loadImageFromPath(advertisementImageView, advertisementImagePath);
            }
            
            // Hide advertisement after duration
            mainHandler.postDelayed(() -> {
                hideAdvertisement();
            }, advertisementDuration);
            
        } catch (Exception e) {
            Log.e(TAG, "Show advertisement error: " + e.getMessage(), e);
            hideAdvertisement();
        }
    }

    private void hideAdvertisement() {
        try {
            isAdvertisementActive = false;
            if (advertisementImageView != null) advertisementImageView.setVisibility(View.GONE);
            if (advertisementVideoView != null) {
                advertisementVideoView.stopPlayback();
                advertisementVideoView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Hide advertisement error: " + e.getMessage(), e);
        }
    }

    private void showScreensaver() {
        try {
            if (isScreensaverActive) return;
            
            isScreensaverActive = true;
            
            // Hide other views
            if (advertisementImageView != null) advertisementImageView.setVisibility(View.GONE);
            if (advertisementVideoView != null) advertisementVideoView.setVisibility(View.GONE);
            
            // Show screensaver
            if (!screensaverImagePath.isEmpty() && screensaverImageView != null) {
                screensaverImageView.setVisibility(View.VISIBLE);
                loadImageFromPath(screensaverImageView, screensaverImagePath);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Show screensaver error: " + e.getMessage(), e);
        }
    }

    private void hideScreensaver() {
        try {
            isScreensaverActive = false;
            if (screensaverImageView != null) screensaverImageView.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, "Hide screensaver error: " + e.getMessage(), e);
        }
    }

    private void loadImageFromPath(ImageView imageView, String imagePath) {
        try {
            if (imageView == null || imagePath == null || imagePath.isEmpty()) return;
            
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

    private void updateDisplay() {
        try {
            // Cart items
            if (tvCartItems != null) {
                StringBuilder cartText = new StringBuilder();
                cartText.append("Sade Dondurma x1\n"); // Her zaman sade dondurma
                
                if (!cartItems.isEmpty()) {
                    for (CartItem item : cartItems) {
                        if (item != null && item.getName() != null) {
                            cartText.append(item.getName()).append(" x").append(item.getQuantity()).append("\n");
                        }
                    }
                }
                
                tvCartItems.setText(cartText.toString().trim());
            }

            // Total price
            if (tvTotalPrice != null) {
                tvTotalPrice.setText("Toplam: " + String.format("%.2f", totalPrice) + " TL");
            }

            // Base price
            if (tvBasePrice != null) {
                tvBasePrice.setText("Temel Fiyat: " + String.format("%.2f", basePrice) + " TL");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Display update error: " + e.getMessage(), e);
        }
    }

    private void updateProductImages() {
        try {
            // Update button images based on saved paths
            updateButtonImage(btnSauceChocolate, "sauce_chocolate");
            updateButtonImage(btnSauceCaramel, "sauce_caramel");
            updateButtonImage(btnSauceStrawberry, "sauce_strawberry");
            updateButtonImage(btnDecorNuts, "decor_nuts");
            updateButtonImage(btnDecorSprinkles, "decor_sprinkles");
            updateButtonImage(btnDecorWhippedCream, "decor_whipped_cream");
        } catch (Exception e) {
            Log.e(TAG, "Update product images error: " + e.getMessage(), e);
        }
    }

    private void updateButtonImage(Button button, String productType) {
        try {
            if (button == null || productType == null) return;
            
            if (productImagePaths.containsKey(productType)) {
                String imagePath = productImagePaths.get(productType);
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        // Button background images require more complex handling
                        // For now, just log that image is available
                        Log.d(TAG, "Product image available for " + productType + ": " + imagePath);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Update button image error: " + e.getMessage(), e);
        }
    }

    private void clearCart() {
        try {
            cartItems.clear();
            selectedSauces.clear();
            selectedToppings.clear();
            totalPrice = basePrice; // Sade dondurma fiyatına sıfırla
            updateDisplay();
            showToast("Sepet temizlendi!");
            resetUserInteractionTimer();
            
            // Log the action
            Log.i(TAG, "Sepet temizlendi - Cart cleared");
            
        } catch (Exception e) {
            Log.e(TAG, "Clear cart error: " + e.getMessage(), e);
            showToast("Sepet temizlenirken hata oluştu!");
        }
    }

    private void checkout() {
        try {
            // Her zaman sade dondurma + seçilen sos ve süslemeler
            totalPrice = basePrice;
            
            // Seçilen sosları ekle
            for (String sauce : selectedSauces) {
                if (sauce.contains("Karamel")) {
                    totalPrice += 2.5;
                } else {
                    totalPrice += 2.0;
                }
            }
            
            // Seçilen süslemeleri ekle
            for (String topping : selectedToppings) {
                if (topping.contains("Fındık") || topping.contains("Krem Şanti")) {
                    totalPrice += 1.5;
                } else {
                    totalPrice += 1.0;
                }
            }

            // Start real payment
            if (mdbManager != null) {
                if (mdbManager.startPayment(totalPrice, "Credit Card")) {
                    showToast("Ödeme başlatıldı! Toplam: " + String.format("%.2f", totalPrice) + " TL");
                    
                    // Log the payment
                    Log.i(TAG, "Ödeme başlatıldı - Payment started: " + totalPrice + " TL");
                    
                    clearCart();
                } else {
                    showToast("Ödeme başlatılamadı!");
                    
                    // Log the payment failure
                    Log.i(TAG, "Ödeme başarısız - Payment failed: " + totalPrice + " TL");
                }
            } else {
                showToast("Ödeme sistemi bağlantısı yok!");
            }
            
            resetUserInteractionTimer();
            
        } catch (Exception e) {
            Log.e(TAG, "Checkout error: " + e.getMessage(), e);
            showToast("Ödeme hatası: " + e.getMessage());
        }
    }

    private void addToCart(String itemName, double price, String type) {
        try {
            if (itemName == null || type == null) return;
            
            // Check existing items
            for (CartItem item : cartItems) {
                if (item != null && item.getName() != null && item.getName().equals(itemName)) {
                    item.incrementQuantity();
                    totalPrice += price;
                    updateDisplay();
                    return;
                }
            }

            // Add new item
            CartItem newItem = new CartItem(itemName, price, type);
            cartItems.add(newItem);
            totalPrice += price;
            updateDisplay();
            
        } catch (Exception e) {
            Log.e(TAG, "Add to cart error: " + e.getMessage(), e);
        }
    }

    private void toggleSauce(String sauceName, double price, Button button) {
        try {
            if (sauceName == null || button == null) return;

            if (selectedSauces.contains(sauceName)) {
                selectedSauces.remove(sauceName);
                removeFromCart(sauceName);
                button.setAlpha(1.0f);
            } else {
                selectedSauces.add(sauceName);
                addToCart(sauceName, price, "sauce");
                button.setAlpha(0.7f);
            }
            updateDisplay();
            resetUserInteractionTimer();
            
        } catch (Exception e) {
            Log.e(TAG, "Toggle sauce error: " + e.getMessage(), e);
        }
    }

    private void toggleTopping(String toppingName, double price, Button button) {
        try {
            if (toppingName == null || button == null) return;

            if (selectedToppings.contains(toppingName)) {
                selectedToppings.remove(toppingName);
                removeFromCart(toppingName);
                button.setAlpha(1.0f);
            } else {
                selectedToppings.add(toppingName);
                addToCart(toppingName, price, "topping");
                button.setAlpha(0.7f);
            }
            updateDisplay();
            resetUserInteractionTimer();
            
        } catch (Exception e) {
            Log.e(TAG, "Toggle topping error: " + e.getMessage(), e);
        }
    }

    private void removeFromCart(String itemName) {
        try {
            if (itemName == null) return;
            
            for (int i = cartItems.size() - 1; i >= 0; i--) {
                CartItem item = cartItems.get(i);
                if (item != null && item.getName() != null && item.getName().equals(itemName)) {
                    totalPrice -= (item.getPrice() * item.getQuantity());
                    cartItems.remove(i);
                    break;
                }
            }
            updateDisplay();
            
        } catch (Exception e) {
            Log.e(TAG, "Remove from cart error: " + e.getMessage(), e);
        }
    }



    private void openAdminLogin() {
        try {
            Intent intent = new Intent(this, AdminLoginActivity.class);
            startActivity(intent);
            resetUserInteractionTimer();
            
            // Log the action
            Log.i(TAG, "Admin paneline erişim - Admin panel accessed");
            
        } catch (Exception e) {
            Log.e(TAG, "Admin login error: " + e.getMessage(), e);
            showToast("Admin paneli açılamadı: " + e.getMessage());
        }
    }

    private void resetUserInteractionTimer() {
        try {
            isUserInteracting = true;
            hideScreensaver();
            hideAdvertisement();
            
            // Reset timer
            mainHandler.postDelayed(() -> {
                isUserInteracting = false;
            }, 5000); // 5 seconds of interaction
            
        } catch (Exception e) {
            Log.e(TAG, "Reset user interaction timer error: " + e.getMessage(), e);
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
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
        } catch (Exception e) {
            Log.e(TAG, "Error dialog error: " + e.getMessage(), e);
            finish();
        }
    }

    private void saveProductImage(Uri imageUri, String productType) {
        try {
            if (imageUri == null || productType == null) return;
            
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
            productImagePaths.put(productType, imageFile.getAbsolutePath());
            sharedPreferences.edit().putString("product_image_" + productType, imageFile.getAbsolutePath()).apply();
            
            // Update UI
            updateProductImages();
            showToast("Ürün görseli kaydedildi!");
            
            // Log the action
            Log.i(TAG, "Ürün görseli kaydedildi - Product image saved: " + fileName);
            
            Log.d(TAG, "Product image saved: " + imageFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e(TAG, "Save product image error: " + e.getMessage(), e);
            showToast("Görsel kaydedilemedi: " + e.getMessage());
        }
    }

    private void saveAdvertisementVideo(Uri videoUri) {
        try {
            if (videoUri == null) return;
            
            String fileName = "advertisement_" + System.currentTimeMillis() + ".mp4";
            File videoFile = new File(getExternalFilesDir("Advertisements"), fileName);
            
            // Ensure directory exists
            if (!videoFile.getParentFile().exists()) {
                videoFile.getParentFile().mkdirs();
            }
            
            // Copy video to app directory
            InputStream inputStream = getContentResolver().openInputStream(videoUri);
            FileOutputStream outputStream = new FileOutputStream(videoFile);
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            outputStream.close();
            
            // Save path to preferences
            advertisementVideoPath = videoFile.getAbsolutePath();
            sharedPreferences.edit().putString("advertisement_video", videoFile.getAbsolutePath()).apply();
            
            showToast("Reklam videosu kaydedildi!");
            
            // Log the action
            Log.i(TAG, "Reklam videosu kaydedildi - Advertisement video saved: " + fileName);
            
            Log.d(TAG, "Advertisement video saved: " + videoFile.getAbsolutePath());
            
        } catch (Exception e) {
            Log.e(TAG, "Save advertisement video error: " + e.getMessage(), e);
            showToast("Video kaydedilemedi: " + e.getMessage());
        }
    }

    private void generateNewMachineIdentity() {
        try {
            machineSerialNumber = "DGS-ICE-" + System.currentTimeMillis();
            isMachineIdentified = true;
            saveMachineIdentityToStorage();
            Log.d(TAG, "New machine identity generated");
        } catch (Exception e) {
            Log.e(TAG, "Generate machine identity error: " + e.getMessage(), e);
        }
    }

    private void saveMachineIdentityToStorage() {
        try {
            String encryptedSerial = encryptIdentityData(machineSerialNumber);

            FileOutputStream fos = openFileOutput("machine_serial.dat", MODE_PRIVATE);
            fos.write(encryptedSerial.getBytes());
            fos.close();

            Log.d(TAG, "Machine identity saved to storage");
        } catch (Exception e) {
            Log.e(TAG, "Save machine identity error: " + e.getMessage(), e);
        }
    }

    private String getMachineSerialFromStorage() {
        try {
            File file = new File(getFilesDir(), "machine_serial.dat");
            if (file.exists()) {
                FileInputStream fis = openFileInput("machine_serial.dat");
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                return decryptIdentityData(new String(data));
            }
        } catch (Exception e) {
            Log.e(TAG, "Get machine serial error: " + e.getMessage(), e);
        }
        return null;
    }

    private String encryptIdentityData(String data) {
        try {
            if (data == null) return "";
            
            // Simple XOR encryption
            StringBuilder encrypted = new StringBuilder();
            int key = 42; // Simple key
            for (char c : data.toCharArray()) {
                encrypted.append((char) (c ^ key));
            }
            return encrypted.toString();
        } catch (Exception e) {
            Log.e(TAG, "Encrypt identity data error: " + e.getMessage(), e);
            return data;
        }
    }

    private String decryptIdentityData(String encryptedData) {
        try {
            if (encryptedData == null) return "";
            
            // Simple XOR decryption
            StringBuilder decrypted = new StringBuilder();
            int key = 42; // Same key
            for (char c : encryptedData.toCharArray()) {
                decrypted.append((char) (c ^ key));
            }
            return decrypted.toString();
        } catch (Exception e) {
            Log.e(TAG, "Decrypt identity data error: " + e.getMessage(), e);
            return encryptedData;
        }
    }

    private void updateMachineIdentityDisplay() {
        try {
            // Sadece seri numarası gösterilecek, IoT numarası gizli
            if (tvMachineInfo != null) {
                tvMachineInfo.setText("Makine Seri No: " + machineSerialNumber);
            }
            Log.i(TAG, "Machine Identity - Serial: " + machineSerialNumber);
        } catch (Exception e) {
            Log.e(TAG, "Update machine identity display error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            // Clean up handlers
            if (mainHandler != null) {
                mainHandler.removeCallbacksAndMessages(null);
            }
            super.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "onDestroy error: " + e.getMessage(), e);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetUserInteractionTimer();
    }
}
