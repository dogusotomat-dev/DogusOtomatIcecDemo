package com.tcn.sdk.icecdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/**
 * Türkiye Ödeme Sistemi Yöneticisi
 * TL para birimi, ödeme yöntemleri ve Türkiye'ye özel özellikleri yönetir
 */
public class TurkeyPaymentManager {
    private static final String TAG = "TurkeyPaymentManager";
    private static final String PREF_NAME = "turkey_payment_config";
    
    // Para birimi sabitleri
    public static final String CURRENCY_TL = "TRY";
    public static final String CURRENCY_SYMBOL = "₺";
    
    // Ödeme yöntemleri
    public static final String PAYMENT_CASH = "cash";
    public static final String PAYMENT_CREDIT_CARD = "credit_card";
    public static final String PAYMENT_DEBIT_CARD = "debit_card";
    public static final String PAYMENT_MOBILE = "mobile_payment";
    public static final String PAYMENT_QR = "qr_payment";
    
    // Türkiye'ye özel ödeme sağlayıcıları
    public static final String PROVIDER_ISBANK = "isbank";
    public static final String PROVIDER_GARANTI = "garanti";
    public static final String PROVIDER_YKB = "ykb";
    public static final String PROVIDER_AKBANK = "akbank";
    public static final String PROVIDER_ZIRAAT = "ziraat";
    
    private static TurkeyPaymentManager instance;
    private final Context context;
    private final SharedPreferences preferences;
    
    private String defaultCurrency = CURRENCY_TL;
    private String defaultPaymentMethod = PAYMENT_CREDIT_CARD;
    private boolean enableCashPayment = true;
    private boolean enableCardPayment = true;
    private boolean enableMobilePayment = true;
    private boolean enableQRPayment = true;
    
    private TurkeyPaymentManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadConfiguration();
    }
    
    public static synchronized TurkeyPaymentManager getInstance(Context context) {
        if (instance == null) {
            instance = new TurkeyPaymentManager(context);
        }
        return instance;
    }
    
    /**
     * Konfigürasyonu yükler
     */
    private void loadConfiguration() {
        defaultCurrency = preferences.getString("default_currency", CURRENCY_TL);
        defaultPaymentMethod = preferences.getString("default_payment_method", PAYMENT_CREDIT_CARD);
        enableCashPayment = preferences.getBoolean("enable_cash_payment", true);
        enableCardPayment = preferences.getBoolean("enable_card_payment", true);
        enableMobilePayment = preferences.getBoolean("enable_mobile_payment", true);
        enableQRPayment = preferences.getBoolean("enable_qr_payment", true);
    }
    
    /**
     * Konfigürasyonu kaydeder
     */
    private void saveConfiguration() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("default_currency", defaultCurrency);
        editor.putString("default_payment_method", defaultPaymentMethod);
        editor.putBoolean("enable_cash_payment", enableCashPayment);
        editor.putBoolean("enable_card_payment", enableCardPayment);
        editor.putBoolean("enable_mobile_payment", enableMobilePayment);
        editor.putBoolean("enable_qr_payment", enableQRPayment);
        editor.apply();
    }
    
    /**
     * Para birimini formatlar (TL formatında)
     */
    public String formatCurrency(double amount) {
        return String.format("%.2f %s", amount, CURRENCY_SYMBOL);
    }
    
    /**
     * Para birimini formatlar (sayısal format)
     */
    public String formatCurrencyNumeric(double amount) {
        return String.format("%.2f", amount);
    }
    
    /**
     * Kuruş cinsinden fiyatı TL'ye çevirir
     */
    public double kuruşToTL(int kuruş) {
        return kuruş / 100.0;
    }
    
    /**
     * TL'yi kuruş cinsine çevirir
     */
    public int tlToKuruş(double tl) {
        return (int) Math.round(tl * 100);
    }
    
    /**
     * KDV hesaplar (Türkiye'de %18)
     */
    public double calculateVAT(double amount) {
        return amount * 0.18;
    }
    
    /**
     * KDV dahil fiyat hesaplar
     */
    public double calculatePriceWithVAT(double amount) {
        return amount + calculateVAT(amount);
    }
    
    /**
     * KDV hariç fiyat hesaplar
     */
    public double calculatePriceWithoutVAT(double amountWithVAT) {
        return amountWithVAT / 1.18;
    }
    
    /**
     * Ödeme yöntemlerini alır
     */
    public Map<String, String> getAvailablePaymentMethods() {
        Map<String, String> methods = new HashMap<>();
        
        if (enableCashPayment) {
            methods.put(PAYMENT_CASH, "Nakit");
        }
        if (enableCardPayment) {
            methods.put(PAYMENT_CREDIT_CARD, "Kredi Kartı");
            methods.put(PAYMENT_DEBIT_CARD, "Banka Kartı");
        }
        if (enableMobilePayment) {
            methods.put(PAYMENT_MOBILE, "Mobil Ödeme");
        }
        if (enableQRPayment) {
            methods.put(PAYMENT_QR, "QR Kod ile Ödeme");
        }
        
        return methods;
    }
    
    /**
     * Ödeme yöntemini etkinleştirir/devre dışı bırakır
     */
    public void setPaymentMethodEnabled(String paymentMethod, boolean enabled) {
        switch (paymentMethod) {
            case PAYMENT_CASH:
                enableCashPayment = enabled;
                break;
            case PAYMENT_CREDIT_CARD:
            case PAYMENT_DEBIT_CARD:
                enableCardPayment = enabled;
                break;
            case PAYMENT_MOBILE:
                enableMobilePayment = enabled;
                break;
            case PAYMENT_QR:
                enableQRPayment = enabled;
                break;
        }
        saveConfiguration();
    }
    
    /**
     * Varsayılan ödeme yöntemini ayarlar
     */
    public void setDefaultPaymentMethod(String paymentMethod) {
        if (getAvailablePaymentMethods().containsKey(paymentMethod)) {
            defaultPaymentMethod = paymentMethod;
            saveConfiguration();
        }
    }
    
    /**
     * Varsayılan para birimini ayarlar
     */
    public void setDefaultCurrency(String currency) {
        if (CURRENCY_TL.equals(currency)) {
            defaultCurrency = currency;
            saveConfiguration();
        }
    }
    
    /**
     * Ödeme işlemini doğrular
     */
    public boolean validatePayment(double amount, String paymentMethod) {
        if (amount <= 0) {
            Log.w(TAG, "Geçersiz ödeme tutarı: " + amount);
            return false;
        }
        
        if (!getAvailablePaymentMethods().containsKey(paymentMethod)) {
            Log.w(TAG, "Geçersiz ödeme yöntemi: " + paymentMethod);
            return false;
        }
        
        return true;
    }
    
    /**
     * Ödeme makbuzu oluşturur
     */
    public String generateReceipt(double amount, String paymentMethod, String productName) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=== DOGİ SOFT ICE CREAM ===\n");
        receipt.append("Marka: Dogi Soft Ice Cream\n");
        receipt.append("Model: DGS-DIC-S\n");
        receipt.append("Ürün: ").append(productName).append("\n");
        receipt.append("Tutar: ").append(formatCurrency(amount)).append("\n");
        receipt.append("KDV (%18): ").append(formatCurrency(calculateVAT(amount))).append("\n");
        receipt.append("Toplam: ").append(formatCurrency(calculatePriceWithVAT(amount))).append("\n");
        receipt.append("Ödeme Yöntemi: ").append(getAvailablePaymentMethods().get(paymentMethod)).append("\n");
        receipt.append("Tarih: ").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("\n");
        receipt.append("==============================\n");
        
        return receipt.toString();
    }
    
    /**
     * Türkiye'ye özel vergi bilgilerini alır
     */
    public Map<String, Object> getTaxInfo() {
        Map<String, Object> taxInfo = new HashMap<>();
        taxInfo.put("country", "Turkey");
        taxInfo.put("vat_rate", 0.18);
        taxInfo.put("currency", CURRENCY_TL);
        taxInfo.put("currency_symbol", CURRENCY_SYMBOL);
        taxInfo.put("timezone", "Europe/Istanbul");
        return taxInfo;
    }
    
    // Getter metodları
    public String getDefaultCurrency() { return defaultCurrency; }
    public String getDefaultPaymentMethod() { return defaultPaymentMethod; }
    public boolean isCashPaymentEnabled() { return enableCashPayment; }
    public boolean isCardPaymentEnabled() { return enableCardPayment; }
    public boolean isMobilePaymentEnabled() { return enableMobilePayment; }
    public boolean isQRPaymentEnabled() { return enableQRPayment; }
}
