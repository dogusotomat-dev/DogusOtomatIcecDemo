package com.dogus.otomat.icecdemo;

import java.util.HashMap;
import java.util.Map;

/**
 * Dondurma Makinesi Hata Kodları
 * SDK'dan alınan gerçek hata kodları ve açıklamaları
 */
public class IceCreamErrorCodes {
    
    // Hata kodları ve açıklamaları
    private static final Map<Integer, String> ERROR_CODES = new HashMap<>();
    
    static {
        // Genel sistem hataları
        ERROR_CODES.put(-10, "Sistem meşgul");
        ERROR_CODES.put(-11, "Çıkış işlemi devam ediyor");
        ERROR_CODES.put(-12, "Ürün alımı bekleniyor");
        ERROR_CODES.put(-13, "Hazırlanıyor");
        
        // Dondurma makinesi özel hataları
        ERROR_CODES.put(0, "Normal - Hata yok");
        ERROR_CODES.put(1, "Kilit anahtarı algılanamadı");
        ERROR_CODES.put(2, "Kapı anahtarı algılanamadı");
        ERROR_CODES.put(3, "Motor akımı çok yüksek");
        ERROR_CODES.put(4, "Limit aşımı - maksimum adım sayısına ulaşıldı");
        ERROR_CODES.put(5, "Maksimum kat sayısı yetersiz");
        ERROR_CODES.put(6, "Orijin noktasına dönüş zaman aşımı");
        ERROR_CODES.put(7, "Normal çalışma zaman aşımı");
        ERROR_CODES.put(8, "İniş zaman aşımı");
        ERROR_CODES.put(9, "Açılış sırasında kilit anahtarı algılanamadı");
        ERROR_CODES.put(10, "Kat algılama zaman aşımı");
        ERROR_CODES.put(11, "Asansör ışık sensörü engellendi");
        ERROR_CODES.put(20, "Asansör ışık sensörü engellendi");
        ERROR_CODES.put(21, "Asansör ışık sensörü sinyal hatası");
        ERROR_CODES.put(30, "X ekseni hareket hatası");
        ERROR_CODES.put(31, "İtme plakası zaman aşımı");
        ERROR_CODES.put(32, "İtme plakası akım hatası");
        ERROR_CODES.put(33, "İtme plakası akım yok");
        ERROR_CODES.put(34, "Çıkış noktasında ürün yok");
        ERROR_CODES.put(35, "Çıkış öncesi hazne dolu");
        ERROR_CODES.put(36, "Ürün çıkış noktasında sıkıştı");
        ERROR_CODES.put(37, "Asansör motoru açık devre");
        ERROR_CODES.put(40, "Sürücü kartı hatası");
        ERROR_CODES.put(41, "FLASH silme hatası");
        ERROR_CODES.put(42, "FLASH yazma hatası");
        ERROR_CODES.put(43, "Geçersiz komut");
        ERROR_CODES.put(44, "Doğrulama hatası");
        ERROR_CODES.put(45, "Kabin kapısı açık");
        ERROR_CODES.put(46, "İkinci satın alma hatası");
        ERROR_CODES.put(47, "1. kat zaman aşımı");
        ERROR_CODES.put(48, "1. kat akım hatası");
        ERROR_CODES.put(49, "1. kat açık devre");
        ERROR_CODES.put(50, "2. kat zaman aşımı");
        ERROR_CODES.put(51, "2. kat akım hatası");
        ERROR_CODES.put(52, "2. kat açık devre");
        ERROR_CODES.put(53, "3. kat zaman aşımı");
        ERROR_CODES.put(54, "3. kat akım hatası");
        ERROR_CODES.put(55, "3. kat açık devre");
        
        // Dondurma makinesi motor hataları
        ERROR_CODES.put(80, "Motor zaman aşımı");
        ERROR_CODES.put(81, "Sürücü kartı yanıt vermiyor");
        ERROR_CODES.put(82, "Veri eksik");
        ERROR_CODES.put(83, "Doğrulama hatası");
        ERROR_CODES.put(84, "Adres hatası");
        ERROR_CODES.put(86, "Slot mevcut değil");
        ERROR_CODES.put(87, "Hata kodu aralık dışı");
        ERROR_CODES.put(90, "Ürün çıkışı algılanamadı");
        ERROR_CODES.put(91, "Diğer hatalar");
        
        // Dondurma makinesi durum kodları
        ERROR_CODES.put(100, "Boşta");
        ERROR_CODES.put(101, "Çıkış işlemi devam ediyor");
        ERROR_CODES.put(102, "Ürün alımı bekleniyor");
        ERROR_CODES.put(103, "Hazırlanıyor");
        ERROR_CODES.put(104, "Hata durumunda");
        ERROR_CODES.put(105, "Kendi kendine test");
    }
    
    /**
     * Hata koduna göre açıklama döndürür
     * @param errorCode Hata kodu
     * @return Hata açıklaması
     */
    public static String getErrorDescription(int errorCode) {
        String description = ERROR_CODES.get(errorCode);
        if (description == null) {
            description = "Bilinmeyen hata kodu: " + errorCode;
        }
        return description;
    }
    
    /**
     * Tüm hata kodlarını döndürür
     * @return Hata kodları map'i
     */
    public static Map<Integer, String> getAllErrorCodes() {
        return new HashMap<>(ERROR_CODES);
    }
    
    /**
     * Hata kodunun ciddiyetini döndürür
     * @param errorCode Hata kodu
     * @return Ciddiyet seviyesi (1: Düşük, 2: Orta, 3: Yüksek)
     */
    public static int getErrorSeverity(int errorCode) {
        if (errorCode == 0) return 0; // Normal
        if (errorCode < 0) return 3; // Sistem hatası - Yüksek
        if (errorCode <= 20) return 2; // Motor/Asansör hatası - Orta
        if (errorCode <= 50) return 2; // Kat hataları - Orta
        if (errorCode <= 91) return 1; // Genel hatalar - Düşük
        if (errorCode <= 105) return 1; // Durum kodları - Düşük
        return 2; // Bilinmeyen - Orta
    }
    
    /**
     * Hata kodunun çözüm önerisini döndürür
     * @param errorCode Hata kodu
     * @return Çözüm önerisi
     */
    public static String getErrorSolution(int errorCode) {
        switch (errorCode) {
            case 0:
                return "Makine normal çalışıyor";
            case -10:
                return "Sistem meşgul, lütfen bekleyin";
            case -11:
                return "Çıkış işlemi tamamlanana kadar bekleyin";
            case -12:
                return "Ürünü alın ve kapıyı kapatın";
            case 1:
            case 2:
                return "Kapı ve kilit mekanizmasını kontrol edin";
            case 3:
                return "Motor akımını kontrol edin, engel olup olmadığını kontrol edin";
            case 4:
                return "Makineyi sıfırlayın ve orijin noktasına döndürün";
            case 34:
                return "Çıkış noktasını kontrol edin, ürün sıkışması olabilir";
            case 35:
                return "Hazneyi temizleyin";
            case 40:
                return "Sürücü kartını kontrol edin, bağlantıları gözden geçirin";
            case 80:
                return "Motor bağlantılarını ve sensörleri kontrol edin";
            case 81:
                return "Sürücü kartı iletişimini kontrol edin";
            default:
                return "Teknik servis ile iletişime geçin";
        }
    }
}
