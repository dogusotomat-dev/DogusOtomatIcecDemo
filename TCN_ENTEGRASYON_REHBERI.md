# 🚀 TCN SDK Entegrasyon Rehberi

## 📋 Genel Bakış

Bu rehber, Dogus Otomat Dondurma Otomatı uygulaması ile TCN SDK arasında kurulan güçlü entegrasyonu açıklar. TCN SDK, donanım seviyesinde otomat kontrolü sağlayan profesyonel bir çözümdür.

## 🏗️ Mimari Yapı

### Ana Bileşenler

```
┌─────────────────────────────────────────────────────────────┐
│                    Dogus Otomat App                        │
├─────────────────────────────────────────────────────────────┤
│  MainAct.java                    AdminLoginActivity.java   │
│  ├─ TCNIntegrationManager        ├─ Modern UI              │
│  ├─ MDBPaymentManager            └─ Admin Controls         │
│  └─ TelemetryManager                                      │
├─────────────────────────────────────────────────────────────┤
│                TCN Integration Layer                       │
│  ├─ TCNIntegrationManager.java                            │
│  ├─ TCNIntegrationTest.java                               │
│  └─ Event Listeners                                        │
├─────────────────────────────────────────────────────────────┤
│                    TCN SDK                                 │
│  ├─ TcnService.java                                        │
│  ├─ TcnVendIF.java                                        │
│  ├─ VendControl.java                                       │
│  ├─ GetDeviceId.java                                       │
│  └─ Control Classes                                        │
├─────────────────────────────────────────────────────────────┤
│                   Hardware Layer                           │
│  ├─ Vending Machine                                        │
│  ├─ Payment Systems                                        │
│  ├─ Temperature Control                                     │
│  └─ Slot Management                                        │
└─────────────────────────────────────────────────────────────┘
```

## 🔧 Kurulum ve Konfigürasyon

### 1. TCN SDK Entegrasyonu

```java
// MainAct.java'da entegrasyon başlatma
private void initializeTCNIntegration() {
    try {
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
```

### 2. Event Listener Kurulumu

```java
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
            }
        });
    }
}
```

## 🎯 Ana Özellikler

### 1. Otomatik Entegrasyon Başlatma

- **Uygulama Başlangıcında**: TCN Service otomatik olarak başlatılır
- **SDK Başlatma**: TCNVendIF ve VendControl otomatik olarak başlatılır
- **Bağlantı Kontrolü**: Cihaz bağlantısı sürekli olarak izlenir

### 2. Akıllı Satış Yönetimi

```java
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
            simulateVending();
        }
        
    } catch (Exception e) {
        Log.e(TAG, "TCN satış başlatma hatası: " + e.getMessage());
        simulateVending();
    }
}
```

### 3. Slot Numarası Hesaplama

```java
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
```

### 4. Hata Yönetimi ve Fallback

- **TCN Bağlantısı Yok**: Satış simülasyonu ile devam eder
- **Donanım Hatası**: Kullanıcıya bilgi verir ve alternatif çözüm sunar
- **Bağlantı Kesintisi**: Otomatik yeniden bağlanma denemesi

## 🧪 Test ve Doğrulama

### TCNIntegrationTest Sınıfı

```java
// Test sınıfını başlat
TCNIntegrationTest test = new TCNIntegrationTest(context);

// Tüm testleri çalıştır
test.runAllTests();

// Entegrasyon raporu oluştur
test.generateIntegrationReport();

// Entegrasyonu yeniden başlat
test.restartIntegration();
```

### Test Senaryoları

1. **Temel Bağlantı Testi**: SDK başlatma ve cihaz bağlantısı
2. **SDK Başlatma Testi**: TCNVendIF ve VendControl erişimi
3. **Cihaz Bilgileri Testi**: UUID, model ve firmware bilgileri
4. **Slot Bilgileri Testi**: Slot durumu ve konfigürasyonu
5. **Ödeme Yöntemleri Testi**: Farklı ödeme seçenekleri
6. **Satış Simülasyonu Testi**: Gerçek satış işlemi simülasyonu

## 📊 Monitoring ve Logging

### Log Seviyeleri

- **INFO**: Normal işlemler ve başarılı entegrasyon
- **WARNING**: Dikkat edilmesi gereken durumlar
- **ERROR**: Hatalar ve başarısız işlemler

### Örnek Log Çıktıları

```
I/TCNIntegrationManager: TCN Entegrasyon başlatılıyor...
I/TCNIntegrationManager: TCN Service başlatıldı
I/TCNIntegrationManager: TCN SDK başarıyla başlatıldı
I/TCNIntegrationManager: Cihaz Bilgileri - UUID: abc123, Model: v1.0, Firmware: 1.0.0428
I/TCNIntegrationManager: TCN Entegrasyon başarıyla başlatıldı
```

## 🔄 Entegrasyon Yaşam Döngüsü

### 1. Başlatma (Initialization)
```
Uygulama Başlangıcı → TCN Service → SDK Başlatma → Cihaz Bağlantısı
```

### 2. Çalışma (Operation)
```
Kullanıcı Siparişi → Slot Hesaplama → TCN Satış → Event Callback
```

### 3. İzleme (Monitoring)
```
Sürekli Bağlantı Kontrolü → Durum Güncellemesi → Hata Yakalama
```

### 4. Kapatma (Shutdown)
```
Uygulama Kapanışı → Event Listener Temizleme → SDK Kapatma
```

## 🚨 Hata Kodları ve Çözümleri

### Yaygın Hatalar

| Hata Kodu | Açıklama | Çözüm |
|-----------|----------|-------|
| `CONNECTION_FAILED` | TCN cihazına bağlanılamadı | USB bağlantısını kontrol et |
| `SDK_INIT_ERROR` | TCN SDK başlatılamadı | Uygulamayı yeniden başlat |
| `DEVICE_NOT_FOUND` | Cihaz bulunamadı | Donanım bağlantısını kontrol et |
| `SLOT_ERROR` | Slot erişim hatası | Slot konfigürasyonunu kontrol et |

### Hata Yönetimi Stratejisi

```java
try {
    // TCN işlemi
    tcnIntegrationManager.startVending(slotNumber, quantity, productName);
    
} catch (TCNConnectionException e) {
    // Bağlantı hatası - yeniden deneme
    Log.w(TAG, "Bağlantı hatası, yeniden deneniyor...");
    retryConnection();
    
} catch (TCNDeviceException e) {
    // Cihaz hatası - kullanıcıya bilgi ver
    Log.e(TAG, "Cihaz hatası: " + e.getMessage());
    showDeviceErrorDialog(e.getMessage());
    
} catch (Exception e) {
    // Genel hata - simülasyon moduna geç
    Log.e(TAG, "Genel hata: " + e.getMessage());
    fallbackToSimulation();
}
```

## 📱 Kullanıcı Arayüzü Entegrasyonu

### Durum Göstergeleri

- **🟢 Bağlı**: TCN cihazı aktif ve hazır
- **🟡 Bağlanıyor**: Bağlantı kuruluyor
- **🔴 Bağlı Değil**: Cihaz bağlantısı yok
- **⚙️ Satış Yapılıyor**: Aktif satış işlemi

### Bildirimler

```java
// Başarılı satış
showToast("Dondurmanız hazır! Afiyet olsun! 🍦");

// Hata durumu
showToast("Cihaz hatası: " + errorMessage);

// Bağlantı durumu
showToast("TCN cihazı bağlantısı kesildi!");
```

## 🔧 Gelişmiş Konfigürasyon

### Entegrasyon Ayarları

```java
// Otomatik bağlantı
integrationManager.updateIntegrationConfig("auto_connect", true);

// Yeniden deneme sayısı
integrationManager.updateIntegrationConfig("retry_count", 5);

// Timeout süresi
integrationManager.updateIntegrationConfig("timeout_ms", 10000);

// Logging aktif
integrationManager.updateIntegrationConfig("enable_logging", true);

// Telemetri aktif
integrationManager.updateIntegrationConfig("enable_telemetry", true);
```

### Özel Event Handler'lar

```java
// Özel satış event'i
integrationManager.setVendEventListener(new CustomVendEventHandler());

// Özel cihaz durum handler'ı
integrationManager.setDeviceStatusListener(new CustomDeviceStatusHandler());

// Özel ödeme handler'ı
integrationManager.setPaymentListener(new CustomPaymentHandler());
```

## 📈 Performans Optimizasyonu

### Thread Yönetimi

- **Main Thread**: UI güncellemeleri ve kullanıcı etkileşimi
- **Background Thread**: TCN SDK işlemleri ve donanım iletişimi
- **Executor Service**: Asenkron işlemler ve event handling

### Memory Management

- **Weak References**: Event listener'lar için
- **Resource Cleanup**: Uygulama kapanışında
- **Connection Pooling**: TCN bağlantıları için

## 🔒 Güvenlik

### Yetkilendirme

- **Admin Girişi**: Sadece yetkili kullanıcılar TCN ayarlarını değiştirebilir
- **Dolum Girişi**: Sınırlı erişim ile malzeme dolumu
- **Güvenli Bağlantı**: Şifreli iletişim ve kimlik doğrulama

### Veri Koruma

- **Cihaz UUID**: Benzersiz tanımlayıcı ile güvenlik
- **Event Logging**: Tüm işlemler loglanır
- **Hata Raporlama**: Güvenlik ihlalleri raporlanır

## 📚 API Referansı

### TCNIntegrationManager

#### Ana Metodlar

```java
// Singleton instance
public static TCNIntegrationManager getInstance(Context context)

// Entegrasyon durumu
public boolean isInitialized()
public boolean isConnected()
public boolean isVending()

// Cihaz bilgileri
public String getDeviceUUID()
public String getDeviceModel()
public String getFirmwareVersion()

// Satış işlemleri
public void startVending(int slotNumber, int quantity, String productName)

// Cihaz durumu
public void queryDeviceStatus()
public void getSlotInfo()

// Konfigürasyon
public void updateIntegrationConfig(String key, Object value)
public void setPaymentMethod(String paymentMethod)

// Yaşam döngüsü
public void restartIntegration()
public void shutdownIntegration()
```

#### Event Listener'lar

```java
// Satış event'leri
public void setVendEventListener(OnVendEventListener listener)

// Cihaz durum event'leri
public void setDeviceStatusListener(OnDeviceStatusListener listener)

// Ödeme event'leri
public void setPaymentListener(OnPaymentListener listener)
```

### Callback Interfaces

```java
public interface OnVendEventListener {
    void onVendEventStarted(VendEventInfo event);
    void onVendEventCompleted(VendEventInfo event);
    void onVendEventFailed(VendEventInfo event, String error);
    void onVendEventError(String error);
}

public interface OnDeviceStatusListener {
    void onConnectionStatusChanged(boolean connected);
    void onDeviceStatusReceived(Map<String, Object> status);
}

public interface OnPaymentListener {
    void onPaymentStarted(double amount);
    void onPaymentCompleted(double amount);
    void onPaymentFailed(String error);
}
```

## 🎉 Sonuç

TCN SDK entegrasyonu sayesinde Dogus Otomat uygulaması:

✅ **Donanım Seviyesinde Kontrol**: Gerçek otomat donanımını yönetir
✅ **Profesyonel Satış**: Endüstri standardında satış işlemleri
✅ **Güvenilir Bağlantı**: Kararlı donanım iletişimi
✅ **Hata Toleransı**: Fallback mekanizmaları ile sürekli çalışma
✅ **Monitoring**: Kapsamlı izleme ve loglama
✅ **Test Edilebilirlik**: Kapsamlı test ve doğrulama araçları

Bu entegrasyon, uygulamanın profesyonel seviyede çalışmasını ve gerçek donanım ile mükemmel uyum sağlamasını garanti eder.

---

**Not**: Bu rehber, TCN SDK entegrasyonunun temel özelliklerini kapsar. Gelişmiş özellikler ve özel konfigürasyonlar için TCN SDK dokümantasyonuna başvurunuz.
