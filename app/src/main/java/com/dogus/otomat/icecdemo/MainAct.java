package com.dogus.otomat.icecdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.tcn.icecboard.control.VendEventInfo;

/**
 * Dogi Soft Ice Cream DGS-DIC-S Ana Aktivite
 * Dondurma otomatı satış ekranı
 */
public class MainAct extends AppCompatActivity {

    private static final String TAG = "MainAct";

    // UI bileşenleri
    private Button btnAdminLogin;
    private TextView tvCartItems, tvTotalPrice, tvBasePrice;

    // Sos butonları (sadece 3 tane)
    private Button btnSauceChocolate, btnSauceCaramel, btnSauceStrawberry;

    // Süsleme butonları (sadece 3 tane)
    private Button btnDecorNuts, btnDecorSprinkles, btnDecorWhippedCream;

    // Sepet ve ödeme butonları
    private Button btnClearCart, btnCheckout;
    private Button btnPlainIceCream;

    // Sepet bilgileri
    private List<CartItem> cartItems;
    private double basePrice = 8.00;
    private double totalPrice = 0.0;

    // Fiyatlar
    private double sauce1Price, sauce2Price, sauce3Price;
    private double topping1Price, topping2Price, topping3Price;

    private SharedPreferences sharedPreferences;

    // TCN Entegrasyon Yöneticisi
    private TCNIntegrationManager tcnIntegrationManager;

    // Dosya Yönetim Sistemi
    private FileManagementSystem fileManagementSystem;

    // Gelişmiş Log Sistemi
    private AdvancedLoggingSystem advancedLoggingSystem;

    // Seçim sınırları - Artık esnek
    private static final int MAX_SAUCES = 10; // Çoklu seçim için yüksek limit
    private static final int MAX_TOPPINGS = 10; // Çoklu seçim için yüksek limit

    // Seçili sos ve süslemeler
    private List<String> selectedSauces = new ArrayList<>();
    private List<String> selectedToppings = new ArrayList<>();

    // Fiyatlar artık SharedPreferences'dan yükleniyor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        loadPrices();
        initView();
        setupClickListeners();
        initializeCart();
        updateDisplay();

        // MDB ödeme sistemini başlat
        initializePaymentSystem();

        // TCN entegrasyonunu başlat
        initializeTCNIntegration();

        // Dosya yönetim sistemini başlat
        initializeFileManagement();
    }

    /**
     * TCN entegrasyonunu başlatır
     */
    private void initializeTCNIntegration() {
        try {
            Log.i(TAG, "TCN entegrasyonu başlatılıyor...");

            // TCN entegrasyon yöneticisini başlat
            tcnIntegrationManager = TCNIntegrationManager.getInstance(this);

            // Event listener'ları ayarla
            setupTCNEventListeners();

            // Cihaz durumunu sorgula
            tcnIntegrationManager.queryDeviceStatus();

            Log.i(TAG, "TCN entegrasyonu başarıyla başlatıldı");

        } catch (Exception e) {
            Log.e(TAG, "TCN entegrasyonu başlatma hatası: " + e.getMessage());
        }
    }

    private void initializeFileManagement() {
        try {
            Log.i(TAG, "Dosya yönetim sistemi başlatılıyor...");
            fileManagementSystem = FileManagementSystem.getInstance(this);
            advancedLoggingSystem = AdvancedLoggingSystem.getInstance(this);

            // Sistem raporu oluştur
            fileManagementSystem.generateSystemReport();
            advancedLoggingSystem.generateLogSystemReport();

            Log.i(TAG, "Dosya yönetim sistemi başarıyla başlatıldı");
        } catch (Exception e) {
            Log.e(TAG, "Dosya yönetim sistemi başlatma hatası: " + e.getMessage());
        }
    }

    /**
     * TCN event listener'larını ayarlar
     */
    private void setupTCNEventListeners() {
        if (tcnIntegrationManager != null) {
            // Satış event listener'ı
            tcnIntegrationManager.setVendEventListener(new TCNIntegrationManager.OnVendEventListener() {
                @Override
                public void onVendEventStarted(VendEventInfo event) {
                    Log.i(TAG, "Satış başladı: Slot " + event.GetlParam1() + ", Miktar " + event.GetlParam2());
                    showToast("Dondurma hazırlanıyor...");
                }

                @Override
                public void onVendEventCompleted(VendEventInfo event) {
                    Log.i(TAG, "Satış tamamlandı: Slot " + event.GetlParam1() + ", Miktar " + event.GetlParam2());
                    showToast("Dondurmanız hazır! Afiyet olsun! 🍦");
                }

                @Override
                public void onVendEventFailed(VendEventInfo event, String error) {
                    Log.e(TAG, "Satış başarısız: " + error);
                    showToast("Satış hatası: " + error);
                }

                @Override
                public void onVendEventError(String error) {
                    Log.e(TAG, "Satış event hatası: " + error);
                    showToast("Sistem hatası: " + error);
                }
            });

            // Cihaz durum listener'ı
            tcnIntegrationManager.setDeviceStatusListener(new TCNIntegrationManager.OnDeviceStatusListener() {
                @Override
                public void onConnectionStatusChanged(boolean connected) {
                    Log.i(TAG, "TCN bağlantı durumu: " + (connected ? "Bağlı" : "Bağlı değil"));
                    if (!connected) {
                        showToast("TCN cihazı bağlantısı kesildi!");
                    }
                }

                @Override
                public void onDeviceStatusReceived(Map<String, Object> status) {
                    Log.i(TAG, "Cihaz durumu alındı: " + status.toString());
                    // Cihaz durumunu UI'da gösterebiliriz
                }
            });
        }
    }

    /**
     * Ödeme sistemini başlatır
     */
    private void initializePaymentSystem() {
        try {
            // MDB Payment Manager'ı başlat
            MDBPaymentManager mdbManager = MDBPaymentManager.getInstance(this);

            if (mdbManager.initializeMDB()) {
                Log.i(TAG, "MDB ödeme sistemi başarıyla başlatıldı");

                // MDB Level 3 durumunu kontrol et
                if (mdbManager.isLevel3Enabled()) {
                    Log.i(TAG, "MDB Level 3 aktif - Gelişmiş ödeme özellikleri kullanılabilir");
                } else {
                    Log.w(TAG, "MDB Level 3 devre dışı - Temel ödeme özellikleri kullanılıyor");
                }

            } else {
                Log.e(TAG, "MDB ödeme sistemi başlatılamadı");
            }

        } catch (Exception e) {
            Log.e(TAG, "Ödeme sistemi başlatma hatası: " + e.getMessage());
        }
    }

    private void loadPrices() {
        // Temel fiyatı yükle
        basePrice = sharedPreferences.getFloat("base_price", 8.0f);

        // Sos fiyatlarını yükle
        sauce1Price = sharedPreferences.getFloat("sauce1_price", 2.0f);
        sauce2Price = sharedPreferences.getFloat("sauce2_price", 2.5f);
        sauce3Price = sharedPreferences.getFloat("sauce3_price", 2.0f);

        // Süsleme fiyatlarını yükle
        topping1Price = sharedPreferences.getFloat("topping1_price", 1.5f);
        topping2Price = sharedPreferences.getFloat("topping2_price", 1.0f);
        topping3Price = sharedPreferences.getFloat("topping3_price", 1.5f);
    }

    private void initView() {
        // Admin giriş butonu
        btnAdminLogin = findViewById(R.id.btn_admin_login);

        // Sepet bileşenleri
        tvCartItems = findViewById(R.id.tv_cart_items);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvBasePrice = findViewById(R.id.tv_base_price);

        // Sos butonları (sadece 3 tane)
        btnSauceChocolate = findViewById(R.id.btn_sauce_chocolate);
        btnSauceCaramel = findViewById(R.id.btn_sauce_caramel);
        btnSauceStrawberry = findViewById(R.id.btn_sauce_strawberry);

        // Süsleme butonları (sadece 3 tane)
        btnDecorNuts = findViewById(R.id.btn_decor_nuts);
        btnDecorSprinkles = findViewById(R.id.btn_decor_sprinkles);
        btnDecorWhippedCream = findViewById(R.id.btn_decor_whipped_cream);

        // Sepet ve ödeme butonları
        btnClearCart = findViewById(R.id.btn_clear_cart);
        btnCheckout = findViewById(R.id.btn_checkout);
        btnPlainIceCream = findViewById(R.id.btn_plain_ice_cream);
    }

    private void setupClickListeners() {
        // Admin giriş
        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdminLogin();
            }
        });

        // Sos butonları
        btnSauceChocolate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSauce("🍫 Çikolata Sos", sauce1Price, btnSauceChocolate);
            }
        });

        btnSauceCaramel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSauce("🍯 Karamel Sos", sauce2Price, btnSauceCaramel);
            }
        });

        btnSauceStrawberry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSauce("🍓 Çilek Sos", sauce3Price, btnSauceStrawberry);
            }
        });

        // Süsleme butonları
        btnDecorNuts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTopping("🥜 Fındık", topping1Price, btnDecorNuts);
            }
        });

        btnDecorSprinkles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTopping("✨ Renkli Şeker", topping2Price, btnDecorSprinkles);
            }
        });

        btnDecorWhippedCream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTopping("💨 Krem Şanti", topping3Price, btnDecorWhippedCream);
            }
        });

        // Sade dondurma butonu
        btnPlainIceCream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderPlainIceCream();
            }
        });

        // Sepet işlemleri
        btnClearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCart();
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });
    }

    private void initializeCart() {
        cartItems = new ArrayList<>();
        totalPrice = basePrice;
    }

    private void addToCart(String itemName, double price) {
        addToCart(itemName, price, "base");
    }

    private void addToCart(String itemName, double price, String type) {
        // Aynı ürün zaten sepette var mı kontrol et
        for (CartItem item : cartItems) {
            if (item.getName().equals(itemName) && item.getType().equals(type)) {
                item.incrementQuantity();
                updateDisplay();
                showToast(itemName + " miktarı artırıldı!");
                return;
            }
        }

        // Yeni ürün ekle
        CartItem newItem = new CartItem(itemName, price, type);
        cartItems.add(newItem);
        totalPrice += price;
        updateDisplay();
        showToast(itemName + " sepete eklendi!");
    }

    private void clearCart() {
        if (cartItems.isEmpty()) {
            showToast("Sepet zaten boş!");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Sepeti Temizle")
                .setMessage("Sepetteki tüm ürünleri kaldırmak istediğinizden emin misiniz?")
                .setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cartItems.clear();
                        totalPrice = basePrice;
                        selectedSauces.clear();
                        selectedToppings.clear();
                        resetButtonAppearance();
                        updateDisplay();
                        updateSlotInfo();
                        showToast("Sepet temizlendi!");
                    }
                })
                .setNegativeButton("HAYIR", null)
                .show();
    }

    private void checkout() {
        // Sade dondurma da alınabilir (sos ve süsleme zorunlu değil)
        if (totalPrice < basePrice) {
            totalPrice = basePrice; // Minimum fiyat garantisi
        }

        try {
            // MDB ödeme sistemini kullan
            MDBPaymentManager mdbManager = MDBPaymentManager.getInstance(this);

            if (mdbManager.isMdbEnabled()) {
                // Ödeme işlemini başlat
                if (mdbManager.startPayment(totalPrice, "Credit Card")) {
                    // Ödeme başarıyla başlatıldı
                    showPaymentDialog();
                } else {
                    showToast("Ödeme başlatılamadı!");
                }
            } else {
                // MDB devre dışı, manuel ödeme
                showManualPaymentDialog();
            }

        } catch (Exception e) {
            Log.e(TAG, "Ödeme işlemi hatası: " + e.getMessage());
            showToast("Ödeme hatası: " + e.getMessage());
        }
    }

    /**
     * Ödeme dialog'unu gösterir
     */
    private void showPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ödeme İşlemi")
                .setMessage("Ödeme işlemi devam ediyor...\nTutar: " + String.format("%.2f", totalPrice) + " TL")
                .setPositiveButton("Onayla", (dialog, which) -> {
                    // Ödemeyi onayla
                    MDBPaymentManager mdbManager = MDBPaymentManager.getInstance(this);
                    if (mdbManager.approvePayment()) {
                        showToast("Ödeme onaylandı!");

                        // Seçilen ürünlerin tam açıklamasını oluştur
                        String productDetails = createProductDescription();

                        // Telemetri verisi gönder
                        TelemetryManager telemetryManager = TelemetryManager.getInstance(this);
                        if (telemetryManager != null) {
                            telemetryManager.sendSalesData(1, productDetails, totalPrice, "credit_card", true);
                        }

                        // TCN entegrasyonu ile satış işlemini başlat
                        startTCNVending(productDetails);

                        // Sepeti temizle ve sistemi sıfırla
                        clearCart();
                        resetPaymentSystem();
                        updateDisplay();
                    } else {
                        showToast("Ödeme onaylanamadı!");
                    }
                })
                .setNegativeButton("İptal", (dialog, which) -> {
                    // Ödemeyi iptal et
                    MDBPaymentManager mdbManager = MDBPaymentManager.getInstance(this);
                    mdbManager.cancelPayment();
                    showToast("Ödeme iptal edildi!");
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Manuel ödeme dialog'unu gösterir
     */
    private void showManualPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manuel Ödeme")
                .setMessage("MDB sistemi devre dışı.\nTutar: " + String.format("%.2f", totalPrice)
                        + " TL\n\nLütfen manuel olarak ödeme alın.")
                .setPositiveButton("Tamam", (dialog, which) -> {
                    // Manuel ödeme tamamlandı olarak işaretle
                    showToast("Manuel ödeme tamamlandı!");

                    // Seçilen ürünlerin tam açıklamasını oluştur
                    String productDetails = createProductDescription();

                    // Telemetri verisi gönder
                    TelemetryManager telemetryManager = TelemetryManager.getInstance(this);
                    if (telemetryManager != null) {
                        telemetryManager.sendSalesData(1, productDetails, totalPrice, "cash", true);
                    }

                    // Sepeti temizle ve sistemi sıfırla
                    clearCart();
                    resetPaymentSystem();
                    updateDisplay();
                })
                .setCancelable(false)
                .show();
    }

    private String createProductDescription() {
        StringBuilder description = new StringBuilder("Dondurma");

        // Seçilen sosları ekle (dinamik isimlerden)
        if (!selectedSauces.isEmpty()) {
            description.append(" + ");
            for (int i = 0; i < selectedSauces.size(); i++) {
                if (i > 0)
                    description.append(", ");
                String sauce = selectedSauces.get(i);
                String sauceName = getSauceNameFromSettings(sauce);
                description.append(sauceName);
            }
        }

        // Seçilen süslemeleri ekle (dinamik isimlerden)
        if (!selectedToppings.isEmpty()) {
            description.append(" + ");
            for (int i = 0; i < selectedToppings.size(); i++) {
                if (i > 0)
                    description.append(", ");
                String topping = selectedToppings.get(i);
                String toppingName = getToppingNameFromSettings(topping);
                description.append(toppingName);
            }
        }

        return description.toString();
    }

    /**
     * Ayarlardan sos ismini alır
     */
    private String getSauceNameFromSettings(String sauceKey) {
        // Varsayılan isimler (fallback)
        String defaultName = sauceKey;

        // SharedPreferences'dan özel isimleri al
        String customName = sharedPreferences.getString("sauce_name_" + sauceKey, "");
        if (!customName.isEmpty()) {
            return customName;
        }

        // Eğer özel isim yoksa, varsayılan isimleri kullan
        if (sauceKey.contains("🍫"))
            return "Çikolata Sos";
        if (sauceKey.contains("🍯"))
            return "Karamel Sos";
        if (sauceKey.contains("🍓"))
            return "Çilek Sos";

        return defaultName;
    }

    /**
     * Ayarlardan süsleme ismini alır
     */
    private String getToppingNameFromSettings(String toppingKey) {
        // Varsayılan isimler (fallback)
        String defaultName = toppingKey;

        // SharedPreferences'dan özel isimleri al
        String customName = sharedPreferences.getString("topping_name_" + toppingKey, "");
        if (!customName.isEmpty()) {
            return customName;
        }

        // Eğer özel isim yoksa, varsayılan isimleri kullan
        if (toppingKey.contains("🥜"))
            return "Fındık";
        if (toppingKey.contains("🌈"))
            return "Renkli Şeker";
        if (toppingKey.contains("🍰"))
            return "Krem Şanti";

        return defaultName;
    }

    /**
     * Sos için dinamik slot numarası hesaplar
     */
    private int calculateSauceSlotNumber(String sauceName) {
        // SharedPreferences'dan slot numarasını al
        int slotNumber = sharedPreferences.getInt("sauce_slot_" + sauceName, 0);
        if (slotNumber > 0) {
            return slotNumber;
        }

        // Varsayılan slot numaraları (kullanıcı değiştirebilir)
        if (sauceName.contains("Çikolata") || sauceName.contains("Chocolate"))
            return 1;
        if (sauceName.contains("Karamel") || sauceName.contains("Caramel"))
            return 2;
        if (sauceName.contains("Çilek") || sauceName.contains("Strawberry"))
            return 3;

        return 1; // Varsayılan
    }

    /**
     * Süsleme için dinamik slot numarası hesaplar
     */
    private int calculateToppingSlotNumber(String toppingName) {
        // SharedPreferences'dan slot numarasını al
        int slotNumber = sharedPreferences.getInt("topping_slot_" + toppingName, 0);
        if (slotNumber > 0) {
            return slotNumber;
        }

        // Varsayılan slot numaraları (kullanıcı değiştirebilir)
        if (toppingName.contains("Fındık") || toppingName.contains("Hazelnut"))
            return 1;
        if (toppingName.contains("Renkli Şeker") || toppingName.contains("Sprinkles"))
            return 2;
        if (toppingName.contains("Krem Şanti") || toppingName.contains("Whipped Cream"))
            return 3;

        return 1; // Varsayılan
    }

    private void updateDisplay() {
        // Sepet öğelerini göster
        if (cartItems.isEmpty()) {
            tvCartItems.setText("Henüz ürün seçilmedi");
        } else {
            StringBuilder cartText = new StringBuilder();
            for (CartItem item : cartItems) {
                cartText.append(item.getName()).append(" x").append(item.getQuantity()).append("\n");
            }
            tvCartItems.setText(cartText.toString().trim());
        }

        // Toplam fiyatı göster
        tvTotalPrice.setText("Toplam: " + String.format("%.2f", totalPrice) + " TL");

        // Temel fiyatı göster
        tvBasePrice.setText("Temel Fiyat: " + String.format("%.2f", basePrice) + " TL");
    }

    private void openAdminLogin() {
        Intent intent = new Intent(this, AdminLoginActivity.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Tamam", null)
                .show();
    }

    private void toggleSauce(String sauceName, double price, Button button) {
        // Esnek seçim sistemi kontrolü
        boolean flexibleSelection = sharedPreferences.getBoolean("flexible_selection", true);

        if (selectedSauces.contains(sauceName)) {
            // Sos zaten seçili, kaldır
            selectedSauces.remove(sauceName);
            removeFromCart(sauceName);
            button.setAlpha(1.0f); // Normal görünüm
            button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200);
        } else {
            // Yeni sos ekle
            if (flexibleSelection || selectedSauces.size() < 3) {
                selectedSauces.add(sauceName);
                addToCart(sauceName, price, "sauce");
                button.setAlpha(0.7f); // Seçili görünüm
                button.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
            } else {
                showToast("Esnek seçim kapalı! En fazla 3 sos seçebilirsiniz!");
                return;
            }
        }
        updateDisplay();
        updateSlotInfo();
    }

    private void toggleTopping(String toppingName, double price, Button button) {
        // Esnek seçim sistemi kontrolü
        boolean flexibleSelection = sharedPreferences.getBoolean("flexible_selection", true);

        if (selectedToppings.contains(toppingName)) {
            // Süsleme zaten seçili, kaldır
            selectedToppings.remove(toppingName);
            removeFromCart(toppingName);
            button.setAlpha(1.0f); // Normal görünüm
            button.animate().scaleX(1.0f).scaleY(1.1f).setDuration(200);
        } else {
            // Yeni süsleme ekle
            if (flexibleSelection || selectedToppings.size() < 3) {
                selectedToppings.add(toppingName);
                addToCart(toppingName, price, "topping");
                button.setAlpha(0.7f); // Seçili görünüm
                button.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
            } else {
                showToast("Esnek seçim kapalı! En fazla 3 süsleme seçebilirsinız!");
                return;
            }
        }
        updateDisplay();
        updateSlotInfo();
    }

    private void updateSlotInfo() {
        // Seçilen sos ve süslemelere göre slot numarasını hesapla
        int[] selectedSauceNumbers = new int[selectedSauces.size()];
        int[] selectedToppingNumbers = new int[selectedToppings.size()];

        // Sos numaralarını belirle (dinamik isimlerden)
        int sauceIndex = 0;
        for (String sauce : selectedSauces) {
            String sauceName = getSauceNameFromSettings(sauce);
            // Dinamik slot numarası hesaplama
            selectedSauceNumbers[sauceIndex++] = calculateSauceSlotNumber(sauceName);
        }

        // Süsleme numaralarını belirle (dinamik isimlerden)
        int toppingIndex = 0;
        for (String topping : selectedToppings) {
            String toppingName = getToppingNameFromSettings(topping);
            // Dinamik slot numarası hesaplama
            selectedToppingNumbers[toppingIndex++] = calculateToppingSlotNumber(toppingName);
        }

        // Seçilen ürünlere göre ürün açıklamasını oluştur
        StringBuilder productDescription = new StringBuilder("Dondurma");

        // Ürün görsellerini güncelle
        updateProductImages();

        // Seçilen sosları ekle (dinamik isimlerden)
        if (!selectedSauces.isEmpty()) {
            productDescription.append(" + ");
            for (int i = 0; i < selectedSauces.size(); i++) {
                if (i > 0)
                    productDescription.append(", ");
                String sauce = selectedSauces.get(i);
                String sauceName = getSauceNameFromSettings(sauce);
                productDescription.append(sauceName);
            }
        }

        // Seçilen süslemeleri ekle (dinamik isimlerden)
        if (!selectedToppings.isEmpty()) {
            productDescription.append(" + ");
            for (int i = 0; i < selectedToppings.size(); i++) {
                if (i > 0)
                    productDescription.append(", ");
                String topping = selectedToppings.get(i);
                String toppingName = getToppingNameFromSettings(topping);
                productDescription.append(toppingName);
            }
        }

        // Ürün açıklamasını göster
        if (tvBasePrice != null) {
            tvBasePrice.setText(productDescription.toString());
        }
    }

    private void updateProductImages() {
        try {
            if (fileManagementSystem != null) {
                // Seçilen sosların görsellerini güncelle
                for (String sauce : selectedSauces) {
                    String sauceName = getSauceNameFromSettings(sauce);
                    updateButtonImage(sauce, sauceName, "sauce");
                }

                // Seçilen süslemelerin görsellerini güncelle
                for (String topping : selectedToppings) {
                    String toppingName = getToppingNameFromSettings(topping);
                    updateButtonImage(topping, toppingName, "topping");
                }

                // Log olayını kaydet
                if (advancedLoggingSystem != null) {
                    advancedLoggingSystem.logUserAction("product_images_updated",
                            "product_update", "Seçilen ürünler: " + selectedSauces.size() + " sos, " + selectedToppings.size()
                                    + " süsleme");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Ürün görselleri güncelleme hatası: " + e.getMessage());
        }
    }

    private void updateButtonImage(String productKey, String productName, String productType) {
        try {
            if (fileManagementSystem != null) {
                // Ürün görselini dosya sisteminden al
                Bitmap productImage = fileManagementSystem.getProductImage(productName);

                if (productImage != null) {
                    // Buton görselini güncelle
                    Button targetButton = getButtonForProduct(productKey, productType);
                    if (targetButton != null) {
                        targetButton.setBackground(new BitmapDrawable(getResources(), productImage));
                        Log.i(TAG, "Ürün görseli güncellendi: " + productName);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Buton görseli güncelleme hatası: " + e.getMessage());
        }
    }

    private Button getButtonForProduct(String productKey, String productType) {
        if ("sauce".equals(productType)) {
            if (productKey.contains("🍫"))
                return btnSauceChocolate;
            if (productKey.contains("🍯"))
                return btnSauceCaramel;
            if (productKey.contains("🍓"))
                return btnSauceStrawberry;
        } else if ("topping".equals(productType)) {
            if (productKey.contains("🥜"))
                return btnDecorNuts;
            if (productKey.contains("✨"))
                return btnDecorSprinkles;
            if (productKey.contains("💨"))
                return btnDecorWhippedCream;
        }
        return null;
    }

    private void removeFromCart(String itemName) {
        removeFromCart(itemName, "base");
    }

    private void removeFromCart(String itemName, String type) {
        for (int i = cartItems.size() - 1; i >= 0; i--) {
            CartItem item = cartItems.get(i);
            if (item.getName().equals(itemName) && item.getType().equals(type)) {
                totalPrice -= item.getTotalPrice();
                cartItems.remove(i);
                break;
            }
        }
    }

    private void resetButtonAppearance() {
        // Sos butonları (sadece 3 tane)
        btnSauceChocolate.setAlpha(1.0f);
        btnSauceCaramel.setAlpha(1.0f);
        btnSauceStrawberry.setAlpha(1.0f);

        // Süsleme butonları (sadece 3 tane)
        btnDecorNuts.setAlpha(1.0f);
        btnDecorSprinkles.setAlpha(1.0f);
        btnDecorWhippedCream.setAlpha(1.0f);
    }

    /**
     * Sade dondurma siparişi verir
     */
    private void orderPlainIceCream() {
        try {
            // Sepeti temizle ve sade dondurma ekle
            cartItems.clear();
            selectedSauces.clear();
            selectedToppings.clear();

            // Sade dondurma için temel fiyat
            totalPrice = basePrice;

            // Sade dondurma öğesini sepete ekle
            CartItem plainIceCream = new CartItem("🍦 Sade Dondurma", basePrice, "base");
            cartItems.add(plainIceCream);

            // Buton görünümlerini sıfırla
            resetButtonAppearance();

            // Sepeti güncelle
            updateDisplay();
            updateSlotInfo();

            // TCN entegrasyonu ile satış işlemini başlat
            startTCNVending("🍦 Sade Dondurma");

            // Direkt ödeme ekranına git
            checkout();

            showToast("Sade dondurma siparişi verildi!");

        } catch (Exception e) {
            Log.e(TAG, "Sade dondurma siparişi hatası: " + e.getMessage());
            showToast("Sipariş hatası: " + e.getMessage());
        }
    }

    /**
     * TCN entegrasyonu ile satış işlemini başlatır
     */
    private void startTCNVending(String productDetails) {
        try {
            if (tcnIntegrationManager != null && tcnIntegrationManager.isConnected()) {
                // Slot numarasını hesapla (ürün tipine göre)
                int slotNumber = calculateSlotNumber();

                // TCN entegrasyonu ile satış başlat
                tcnIntegrationManager.startVending(slotNumber, 1, productDetails);

                Log.i(TAG, "TCN satış işlemi başlatıldı - Slot: " + slotNumber + ", Ürün: " + productDetails);

            } else {
                Log.w(TAG, "TCN entegrasyonu hazır değil, satış simüle ediliyor");
                // TCN entegrasyonu yoksa simüle et
                simulateVending();
            }

        } catch (Exception e) {
            Log.e(TAG, "TCN satış başlatma hatası: " + e.getMessage());
            // Hata durumunda simüle et
            simulateVending();
        }
    }

    /**
     * Slot numarasını hesaplar
     */
    private int calculateSlotNumber() {
        // Ürün tipine göre slot numarası hesapla
        if (selectedSauces.contains("🍫 Çikolata Sos")) {
            return 1; // Çikolata sos slot'u
        } else if (selectedSauces.contains("🍯 Karamel Sos")) {
            return 2; // Karamel sos slot'u
        } else if (selectedSauces.contains("🍓 Çilek Sos")) {
            return 3; // Çilek sos slot'u
        } else if (selectedToppings.contains("🥜 Fındık")) {
            return 4; // Fındık slot'u
        } else if (selectedToppings.contains("✨ Renkli Şeker")) {
            return 5; // Renkli şeker slot'u
        } else if (selectedToppings.contains("💨 Krem Şanti")) {
            return 6; // Krem şanti slot'u
        } else {
            return 1; // Varsayılan slot
        }
    }

    /**
     * Satış işlemini simüle eder
     */
    private void simulateVending() {
        try {
            Log.i(TAG, "Satış simülasyonu başlatılıyor...");

            // 2 saniye bekle (simüle edilmiş hazırlama süresi)
            new Handler().postDelayed(() -> {
                showToast("Dondurmanız hazır! Afiyet olsun! 🍦");
                Log.i(TAG, "Satış simülasyonu tamamlandı");
            }, 2000);

        } catch (Exception e) {
            Log.e(TAG, "Satış simülasyonu hatası: " + e.getMessage());
        }
    }

    /**
     * Ödeme sistemini sıfırlar ve yeni ödeme için hazırlar
     */
    private void resetPaymentSystem() {
        try {
            // MDB ödeme sistemini sıfırla
            MDBPaymentManager mdbManager = MDBPaymentManager.getInstance(this);
            mdbManager.resetPaymentStatus();

            // Telemetri olayını gönder
            TelemetryManager telemetryManager = TelemetryManager.getInstance(this);
            if (telemetryManager != null) {
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("event_type", "payment_system_reset");
                eventData.put("timestamp", System.currentTimeMillis());
                telemetryManager.sendDataAsync("payment_event", eventData);
            }

            Log.i(TAG, "Ödeme sistemi sıfırlandı - yeni ödeme için hazır");
        } catch (Exception e) {
            Log.e(TAG, "Ödeme sistemi sıfırlama hatası: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Aktivite yeniden açıldığında fiyatları ve sepeti güncelle
        loadPrices();
        updateDisplay();

        // Reklam ve ekran koruyucu görsellerini kontrol et
        checkAdvertisementAndScreensaver();
    }

    private void checkAdvertisementAndScreensaver() {
        try {
            if (fileManagementSystem != null) {
                // Reklam görsellerini kontrol et
                List<String> advertisements = fileManagementSystem.listFilesInFolder("Advertisements");
                if (!advertisements.isEmpty()) {
                    // Rastgele bir reklam seç ve göster
                    String randomAd = advertisements.get((int) (Math.random() * advertisements.size()));
                    showAdvertisement(randomAd);
                }

                // Ekran koruyucu görsellerini kontrol et
                List<String> screensavers = fileManagementSystem.listFilesInFolder("Screensavers");
                if (!screensavers.isEmpty()) {
                    // Ekran koruyucu için hazırla
                    prepareScreensaver(screensavers.get(0)); // İlk ekran koruyucuyu kullan
                }

                // Log olayını kaydet
                if (advancedLoggingSystem != null) {
                    advancedLoggingSystem.logSystemEvent("advertisement_check",
                            "Reklam: " + advertisements.size() + ", Ekran koruyucu: " + screensavers.size());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Reklam ve ekran koruyucu kontrol hatası: " + e.getMessage());
        }
    }

    private void showAdvertisement(String adName) {
        try {
            if (fileManagementSystem != null) {
                Bitmap adImage = fileManagementSystem.getAdvertisement(adName);
                if (adImage != null) {
                    // Reklam görselini UI'da göster (örneğin bir ImageView'da)
                    Log.i(TAG, "Reklam gösteriliyor: " + adName);

                    // Burada reklam görselini UI'da gösterebilirsiniz
                    // Örnek: advertisementImageView.setImageBitmap(adImage);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Reklam gösterme hatası: " + e.getMessage());
        }
    }

    private void prepareScreensaver(String screensavers) {
        try {
            if (fileManagementSystem != null) {
                // Ekran koruyucu için hazırlık yap
                Log.i(TAG, "Ekran koruyucu hazırlanıyor: " + screensavers);

                // Burada ekran koruyucu mantığını uygulayabilirsiniz
                // Örnek: Belirli bir süre sonra ekran koruyucuyu göster
            }
        } catch (Exception e) {
            Log.e(TAG, "Ekran koruyucu hazırlama hatası: " + e.getMessage());
        }
    }

}
