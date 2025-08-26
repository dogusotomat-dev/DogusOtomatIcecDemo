package com.dogus.otomat.icecdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Doğuş Otomat DGS-DIC-S Ana Aktivite
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

    // Sepet bilgileri
    private List<CartItem> cartItems;
    private double basePrice = 8.00;
    private double totalPrice = 0.0;

    // Fiyatlar
    private double sauce1Price, sauce2Price, sauce3Price;
    private double topping1Price, topping2Price, topping3Price;

    private SharedPreferences sharedPreferences;

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
        // Aynı ürün zaten sepette var mı kontrol et
        for (CartItem item : cartItems) {
            if (item.getName().equals(itemName)) {
                item.incrementQuantity();
                updateDisplay();
                showToast(itemName + " miktarı artırıldı!");
                return;
            }
        }

        // Yeni ürün ekle
        CartItem newItem = new CartItem(itemName, price);
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
        if (cartItems.isEmpty()) {
            showToast("Sepette ürün bulunmuyor!");
            return;
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

                        // Sepeti temizle
                        clearCart();
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

                    // Sepeti temizle
                    clearCart();
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
                addToCart(sauceName, price);
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
                addToCart(toppingName, price);
                button.setAlpha(0.7f); // Seçili görünüm
                button.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
            } else {
                showToast("Esnek seçim kapalı! En fazla 3 süsleme seçebilirsiniz!");
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

    private void removeFromCart(String itemName) {
        for (int i = cartItems.size() - 1; i >= 0; i--) {
            if (cartItems.get(i).getName().equals(itemName)) {
                CartItem item = cartItems.get(i);
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

    @Override
    protected void onResume() {
        super.onResume();
        // Aktivite yeniden açıldığında fiyatları ve sepeti güncelle
        loadPrices();
        updateDisplay();
    }

    // Sepet öğesi sınıfı
    private static class CartItem {
        private String name;
        private double price;
        private int quantity;

        public CartItem(String name, double price) {
            this.name = name;
            this.price = price;
            this.quantity = 1;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void incrementQuantity() {
            quantity++;
        }

        public double getTotalPrice() {
            return price * quantity;
        }
    }
}
