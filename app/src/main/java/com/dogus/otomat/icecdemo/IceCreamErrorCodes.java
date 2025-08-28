package com.dogus.otomat.icecdemo;

import java.util.HashMap;
import java.util.Map;

/**
 * Dondurma Otomatı Hata Kodları
 * Ice Cream Vending Machine Error Codes
 */
public class IceCreamErrorCodes {

    // Temel Sistem Hataları (10-19)
    public static final int ERROR_SYSTEM_STARTUP = 10;
    public static final int ERROR_SYSTEM_SHUTDOWN = 11;
    public static final int ERROR_MEMORY_FAULT = 12;
    public static final int ERROR_POWER_SUPPLY = 13;
    public static final int ERROR_TEMPERATURE_SENSOR = 14;
    public static final int ERROR_COMMUNICATION_FAULT = 15;

    // Dondurma Üretim Hataları (20-39)
    public static final int ERROR_ICE_CREAM_PRODUCTION = 20;
    public static final int ERROR_FREEZER_MALFUNCTION = 21;
    public static final int ERROR_COMPRESSOR_FAULT = 22;
    public static final int ERROR_COOLING_SYSTEM = 23;
    public static final int ERROR_ICE_CREAM_CONSISTENCY = 24;
    public static final int ERROR_PRODUCTION_TIMEOUT = 25;
    public static final int ERROR_TEMPERATURE_TOO_HIGH = 26;
    public static final int ERROR_TEMPERATURE_TOO_LOW = 27;
    public static final int ERROR_ICE_CREAM_CHAMBER_FULL = 28;
    public static final int ERROR_PRODUCTION_SENSOR = 29;

    // Sos Sistemi Hataları (40-49)
    public static final int ERROR_SAUCE_DISPENSER = 40;
    public static final int ERROR_SAUCE_PUMP = 41;
    public static final int ERROR_SAUCE_VALVE = 42;
    public static final int ERROR_SAUCE_LEVEL_LOW = 43;
    public static final int ERROR_SAUCE_EMPTY = 44;
    public static final int ERROR_SAUCE_TEMPERATURE = 45;
    public static final int ERROR_SAUCE_CONTAMINATION = 46;

    // Süsleme Sistemi Hataları (50-59)
    public static final int ERROR_TOPPING_DISPENSER = 50;
    public static final int ERROR_TOPPING_MOTOR = 51;
    public static final int ERROR_TOPPING_LEVEL_LOW = 52;
    public static final int ERROR_TOPPING_EMPTY = 53;
    public static final int ERROR_TOPPING_JAM = 54;
    public static final int ERROR_TOPPING_SENSOR = 55;

    // Bardak Sistemi Hataları (60-69)
    public static final int ERROR_CUP_DISPENSER = 60;
    public static final int ERROR_CUP_EMPTY = 61;
    public static final int ERROR_CUP_JAM = 62;
    public static final int ERROR_CUP_SENSOR = 63;
    public static final int ERROR_CUP_SIZE_MISMATCH = 64;

    // Kapı ve Güvenlik Hataları (70-79)
    public static final int ERROR_DOOR_OPEN = 70;
    public static final int ERROR_DOOR_SENSOR = 71;
    public static final int ERROR_SECURITY_BREACH = 72;
    public static final int ERROR_UNAUTHORIZED_ACCESS = 73;
    public static final int ERROR_EMERGENCY_STOP = 74;

    // Ödeme Sistemi Hataları (80-89)
    public static final int ERROR_PAYMENT_TIMEOUT = 80;
    public static final int ERROR_MDB_CONNECTION = 81;
    public static final int ERROR_COIN_MECHANISM = 82;
    public static final int ERROR_BILL_ACCEPTOR = 83;
    public static final int ERROR_CARD_READER = 84;
    public static final int ERROR_PAYMENT_VALIDATION = 85;
    public static final int ERROR_CHANGE_DISPENSER = 86;

    // TCN Board Control Hataları (90-99)
    public static final int ERROR_TCN_COMMUNICATION = 90;
    public static final int ERROR_TCN_BOARD_FAULT = 91;
    public static final int ERROR_SERIAL_PORT = 92;
    public static final int ERROR_PROTOCOL_ERROR = 93;
    public static final int ERROR_BOARD_TIMEOUT = 94;
    public static final int ERROR_FIRMWARE_MISMATCH = 95;

    // Hata mesajları
    private static final Map<Integer, String> errorMessages = new HashMap<>();
    private static final Map<Integer, String> errorSolutions = new HashMap<>();

    static {
        // Temel Sistem Hataları
        errorMessages.put(ERROR_SYSTEM_STARTUP, "Sistem başlatma hatası");
        errorMessages.put(ERROR_SYSTEM_SHUTDOWN, "Sistem kapatma hatası");
        errorMessages.put(ERROR_MEMORY_FAULT, "Bellek hatası");
        errorMessages.put(ERROR_POWER_SUPPLY, "Güç kaynağı hatası");
        errorMessages.put(ERROR_TEMPERATURE_SENSOR, "Sıcaklık sensörü hatası");
        errorMessages.put(ERROR_COMMUNICATION_FAULT, "İletişim hatası");

        // Dondurma Üretim Hataları
        errorMessages.put(ERROR_ICE_CREAM_PRODUCTION, "Dondurma üretim hatası");
        errorMessages.put(ERROR_FREEZER_MALFUNCTION, "Dondurucu arızası");
        errorMessages.put(ERROR_COMPRESSOR_FAULT, "Kompresör hatası");
        errorMessages.put(ERROR_COOLING_SYSTEM, "Soğutma sistemi hatası");
        errorMessages.put(ERROR_ICE_CREAM_CONSISTENCY, "Dondurma kıvamı hatası");
        errorMessages.put(ERROR_PRODUCTION_TIMEOUT, "Üretim zaman aşımı");
        errorMessages.put(ERROR_TEMPERATURE_TOO_HIGH, "Sıcaklık çok yüksek");
        errorMessages.put(ERROR_TEMPERATURE_TOO_LOW, "Sıcaklık çok düşük");
        errorMessages.put(ERROR_ICE_CREAM_CHAMBER_FULL, "Dondurma haznesi dolu");
        errorMessages.put(ERROR_PRODUCTION_SENSOR, "Üretim sensörü hatası");

        // Sos Sistemi Hataları
        errorMessages.put(ERROR_SAUCE_DISPENSER, "Sos dispenseri hatası");
        errorMessages.put(ERROR_SAUCE_PUMP, "Sos pompası hatası");
        errorMessages.put(ERROR_SAUCE_VALVE, "Sos valfi hatası");
        errorMessages.put(ERROR_SAUCE_LEVEL_LOW, "Sos seviyesi düşük");
        errorMessages.put(ERROR_SAUCE_EMPTY, "Sos tankı boş");
        errorMessages.put(ERROR_SAUCE_TEMPERATURE, "Sos sıcaklığı uygun değil");
        errorMessages.put(ERROR_SAUCE_CONTAMINATION, "Sos kontaminasyonu");

        // Süsleme Sistemi Hataları
        errorMessages.put(ERROR_TOPPING_DISPENSER, "Süsleme dispenseri hatası");
        errorMessages.put(ERROR_TOPPING_MOTOR, "Süsleme motoru hatası");
        errorMessages.put(ERROR_TOPPING_LEVEL_LOW, "Süsleme seviyesi düşük");
        errorMessages.put(ERROR_TOPPING_EMPTY, "Süsleme tankı boş");
        errorMessages.put(ERROR_TOPPING_JAM, "Süsleme sıkışması");
        errorMessages.put(ERROR_TOPPING_SENSOR, "Süsleme sensörü hatası");

        // Bardak Sistemi Hataları
        errorMessages.put(ERROR_CUP_DISPENSER, "Bardak dispenseri hatası");
        errorMessages.put(ERROR_CUP_EMPTY, "Bardak yok");
        errorMessages.put(ERROR_CUP_JAM, "Bardak sıkışması");
        errorMessages.put(ERROR_CUP_SENSOR, "Bardak sensörü hatası");
        errorMessages.put(ERROR_CUP_SIZE_MISMATCH, "Bardak boyutu uyumsuz");

        // Kapı ve Güvenlik Hataları
        errorMessages.put(ERROR_DOOR_OPEN, "Kapı açık");
        errorMessages.put(ERROR_DOOR_SENSOR, "Kapı sensörü hatası");
        errorMessages.put(ERROR_SECURITY_BREACH, "Güvenlik ihlali");
        errorMessages.put(ERROR_UNAUTHORIZED_ACCESS, "Yetkisiz erişim");
        errorMessages.put(ERROR_EMERGENCY_STOP, "Acil durdurma aktif");

        // Ödeme Sistemi Hataları
        errorMessages.put(ERROR_PAYMENT_TIMEOUT, "Ödeme zaman aşımı");
        errorMessages.put(ERROR_MDB_CONNECTION, "MDB bağlantı hatası");
        errorMessages.put(ERROR_COIN_MECHANISM, "Bozuk para mekanizması hatası");
        errorMessages.put(ERROR_BILL_ACCEPTOR, "Banknot kabul edici hatası");
        errorMessages.put(ERROR_CARD_READER, "Kart okuyucu hatası");
        errorMessages.put(ERROR_PAYMENT_VALIDATION, "Ödeme doğrulama hatası");
        errorMessages.put(ERROR_CHANGE_DISPENSER, "Para üstü verme hatası");

        // TCN Board Control Hataları
        errorMessages.put(ERROR_TCN_COMMUNICATION, "TCN iletişim hatası");
        errorMessages.put(ERROR_TCN_BOARD_FAULT, "TCN kart hatası");
        errorMessages.put(ERROR_SERIAL_PORT, "Seri port hatası");
        errorMessages.put(ERROR_PROTOCOL_ERROR, "Protokol hatası");
        errorMessages.put(ERROR_BOARD_TIMEOUT, "Kart zaman aşımı");
        errorMessages.put(ERROR_FIRMWARE_MISMATCH, "Firmware uyumsuzluğu");

        // Çözüm önerileri
        errorSolutions.put(ERROR_SYSTEM_STARTUP, "Makineyi yeniden başlatın");
        errorSolutions.put(ERROR_MEMORY_FAULT, "Sistem yeniden başlatması gerekli");
        errorSolutions.put(ERROR_POWER_SUPPLY, "Güç bağlantılarını kontrol edin");
        errorSolutions.put(ERROR_TEMPERATURE_SENSOR, "Sensör bağlantılarını kontrol edin");

        errorSolutions.put(ERROR_ICE_CREAM_PRODUCTION, "Üretim parametrelerini kontrol edin");
        errorSolutions.put(ERROR_FREEZER_MALFUNCTION, "Soğutma sistemini kontrol edin");
        errorSolutions.put(ERROR_COMPRESSOR_FAULT, "Teknisyen çağırın");
        errorSolutions.put(ERROR_COOLING_SYSTEM, "Soğutma sıvısını kontrol edin");
        errorSolutions.put(ERROR_TEMPERATURE_TOO_HIGH, "Soğutma sistemini kontrol edin");
        errorSolutions.put(ERROR_TEMPERATURE_TOO_LOW, "Isıtma elementini kontrol edin");

        errorSolutions.put(ERROR_SAUCE_LEVEL_LOW, "Sos tankını doldurun");
        errorSolutions.put(ERROR_SAUCE_EMPTY, "Sos tankını değiştirin");
        errorSolutions.put(ERROR_SAUCE_PUMP, "Sos pompasını temizleyin");

        errorSolutions.put(ERROR_TOPPING_LEVEL_LOW, "Süsleme malzemesini doldurun");
        errorSolutions.put(ERROR_TOPPING_EMPTY, "Süsleme malzemesini değiştirin");
        errorSolutions.put(ERROR_TOPPING_JAM, "Dispenseri temizleyin");

        errorSolutions.put(ERROR_CUP_EMPTY, "Bardak yükleyin");
        errorSolutions.put(ERROR_CUP_JAM, "Sıkışan bardağı çıkarın");

        errorSolutions.put(ERROR_DOOR_OPEN, "Kapıyı kapatın");
        errorSolutions.put(ERROR_EMERGENCY_STOP, "Acil durdurma düğmesini sıfırlayın");

        errorSolutions.put(ERROR_MDB_CONNECTION, "MDB kablolarını kontrol edin");
        errorSolutions.put(ERROR_COIN_MECHANISM, "Bozuk para yolunu temizleyin");
        errorSolutions.put(ERROR_BILL_ACCEPTOR, "Banknot yolunu temizleyin");
        errorSolutions.put(ERROR_CARD_READER, "Kart okuyucuyu temizleyin");

        errorSolutions.put(ERROR_TCN_COMMUNICATION, "Seri port bağlantılarını kontrol edin");
        errorSolutions.put(ERROR_SERIAL_PORT, "Seri port ayarlarını kontrol edin");
        errorSolutions.put(ERROR_PROTOCOL_ERROR, "Firmware güncellemesi gerekli olabilir");
        errorSolutions.put(ERROR_BOARD_TIMEOUT, "Kart bağlantılarını kontrol edin");
    }

    /**
     * Hata mesajını döndürür
     */
    public static String getErrorMessage(int errorCode) {
        String message = errorMessages.get(errorCode);
        return message != null ? message : "Bilinmeyen hata (Kod: " + errorCode + ")";
    }

    /**
     * Hata çözümü önerisini döndürür
     */
    public static String getErrorSolution(int errorCode) {
        String solution = errorSolutions.get(errorCode);
        return solution != null ? solution : "Teknisyen desteği alın";
    }

    /**
     * Hatanın kritiklik seviyesini döndürür
     */
    public static int getErrorSeverity(int errorCode) {
        if (errorCode >= 10 && errorCode <= 19)
            return 3; // Kritik sistem hataları
        if (errorCode >= 70 && errorCode <= 79)
            return 3; // Güvenlik hataları
        if (errorCode >= 20 && errorCode <= 29)
            return 2; // Üretim hataları
        if (errorCode >= 80 && errorCode <= 89)
            return 2; // Ödeme hataları
        if (errorCode >= 90 && errorCode <= 99)
            return 2; // Board control hataları
        return 1; // Diğer hataların çoğu düşük seviye
    }

    /**
     * Hatanın kategorisini döndürür
     */
    public static String getErrorCategory(int errorCode) {
        if (errorCode >= 10 && errorCode <= 19)
            return "Sistem";
        if (errorCode >= 20 && errorCode <= 39)
            return "Dondurma Üretimi";
        if (errorCode >= 40 && errorCode <= 49)
            return "Sos Sistemi";
        if (errorCode >= 50 && errorCode <= 59)
            return "Süsleme Sistemi";
        if (errorCode >= 60 && errorCode <= 69)
            return "Bardak Sistemi";
        if (errorCode >= 70 && errorCode <= 79)
            return "Güvenlik";
        if (errorCode >= 80 && errorCode <= 89)
            return "Ödeme";
        if (errorCode >= 90 && errorCode <= 99)
            return "Kontrol Kartı";
        return "Genel";
    }

    /**
     * Hata hakkında detaylı bilgi döndürür
     */
    public static String getDetailedErrorInfo(int errorCode) {
        StringBuilder info = new StringBuilder();
        info.append("Hata Kodu: ").append(errorCode).append("\n");
        info.append("Kategori: ").append(getErrorCategory(errorCode)).append("\n");
        info.append("Mesaj: ").append(getErrorMessage(errorCode)).append("\n");
        info.append("Çözüm: ").append(getErrorSolution(errorCode)).append("\n");
        info.append("Kritiklik: ");

        int severity = getErrorSeverity(errorCode);
        switch (severity) {
            case 3:
                info.append("Kritik");
                break;
            case 2:
                info.append("Orta");
                break;
            case 1:
                info.append("Düşük");
                break;
            default:
                info.append("Bilinmiyor");
                break;
        }

        return info.toString();
    }
}
