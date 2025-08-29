# DOGİ Dondurma Otomatı - Production Build Rehberi

## 🚀 Production Build Nedir?

Production build, uygulamanın canlı ortamda (production) kullanılmaya hazır, optimize edilmiş ve güvenli versiyonudur. Bu build türü:

- ✅ **Minified**: Kod boyutu küçültülür
- ✅ **Obfuscated**: Kod karıştırılır (güvenlik)
- ✅ **Optimized**: Performans optimize edilir
- ✅ **Release Ready**: Canlı ortama hazır
- ✅ **Signed**: Güvenli imzalanmış

## 📋 Ön Gereksinimler

### 1. Sistem Gereksinimleri
- **Java**: JDK 11 veya üzeri
- **Android SDK**: API 33 (Android 13)
- **Gradle**: 7.4.2 veya üzeri
- **RAM**: En az 8GB (build için)
- **Disk**: En az 10GB boş alan

### 2. Gerekli Dosyalar
- `google-services.json` (Firebase için)
- TCN SDK kütüphaneleri
- Tüm bağımlılıklar

## 🏗️ Build Türleri

### 1. Debug Build
```bash
./gradlew assembleDebug
```
- **Özellikler**: Debugging aktif, kod karıştırılmamış
- **Kullanım**: Geliştirme ve test
- **Boyut**: Büyük (debug bilgileri dahil)

### 2. Release Build
```bash
./gradlew assembleRelease
```
- **Özellikler**: Optimize edilmiş, kod karıştırılmış
- **Kullanım**: Test ve demo
- **Boyut**: Orta (production'a yakın)

### 3. Production Build ⭐
```bash
./gradlew assembleProduction
```
- **Özellikler**: Tam optimize, production hazır
- **Kullanım**: Canlı ortam
- **Boyut**: Küçük (maksimum optimize)

## 🛠️ Build Scriptleri

### Linux/Mac için
```bash
chmod +x build-production.sh
./build-production.sh
```

### Windows için
```cmd
build-production.bat
```

## 📱 Build Çıktıları

### APK Dosyası
- **Konum**: `app/build/outputs/apk/production/`
- **Dosya Adı**: `DogusOtomatIcecDemo-1.0.0428-PRODUCTION.apk`
- **Kullanım**: Doğrudan cihaza kurulum

### AAB Bundle
- **Konum**: `app/build/outputs/bundle/production/`
- **Dosya Adı**: `DogusOtomatIcecDemo-1.0.0428-production.aab`
- **Kullanım**: Google Play Store

## 🔧 Build Konfigürasyonu

### build.gradle Ayarları
```gradle
production {
    initWith release
    minifyEnabled true          // Kod küçültme
    shrinkResources true        // Kaynak küçültme
    debuggable false           // Debug kapalı
    jniDebuggable false        // JNI debug kapalı
    zipAlignEnabled true       // APK optimizasyonu
}
```

### ProGuard Kuralları
- **TCN SDK Koruması**: Tüm TCN sınıfları korunur
- **Firebase Koruması**: Firebase servisleri korunur
- **UI Koruması**: Ana aktiviteler korunur
- **Native Koruması**: Native metodlar korunur

## ✅ Build Öncesi Kontroller

### 1. Kod Kalitesi
- [ ] Lint hataları giderildi
- [ ] Compile hataları yok
- [ ] Test'ler geçiyor
- [ ] Code review tamamlandı

### 2. Güvenlik
- [ ] API anahtarları gizlendi
- [ ] Debug kodları kaldırıldı
- [ ] Log seviyeleri production için ayarlandı
- [ ] ProGuard kuralları test edildi

### 3. Performans
- [ ] Memory leak kontrolü yapıldı
- [ ] UI performans test edildi
- [ ] Network çağrıları optimize edildi
- [ ] Database sorguları optimize edildi

## 🧪 Build Sonrası Testler

### 1. Temel Fonksiyonlar
- [ ] Uygulama açılıyor
- [ ] Ana sayfa yükleniyor
- [ ] Ürün seçimi çalışıyor
- [ ] Ödeme sistemi çalışıyor
- [ ] Admin paneli erişilebilir

### 2. TCN SDK Entegrasyonu
- [ ] Serial port bağlantısı
- [ ] MDB komutları
- [ ] Board kontrolü
- [ ] Hata kodları

### 3. Reklam ve UI
- [ ] Reklamlar gösteriliyor
- [ ] Fotoğraf/video oynatma
- [ ] Ürün ayarları
- [ ] Fiyat güncellemeleri

### 4. Log Sistemi
- [ ] Periyodik loglama (15 saniye)
- [ ] Hata logları
- [ ] Performance logları
- [ ] Log dosyaları oluşuyor

## 🚨 Hata Durumları

### Build Hataları
```bash
# Gradle cache temizleme
./gradlew clean
./gradlew --stop

# Dependencies yenileme
./gradlew --refresh-dependencies
```

### ProGuard Hataları
```bash
# ProGuard mapping dosyası
app/build/outputs/mapping/production/mapping.txt

# ProGuard log dosyası
app/build/outputs/mapping/production/seeds.txt
```

### APK Kurulum Hataları
```bash
# APK doğrulama
aapt dump badging DogusOtomatIcecDemo-1.0.0428-PRODUCTION.apk

# APK imza kontrolü
jarsigner -verify -verbose -certs DogusOtomatIcecDemo-1.0.0428-PRODUCTION.apk
```

## 📊 Performance Metrikleri

### APK Boyutu
- **Debug**: ~50-80 MB
- **Release**: ~30-50 MB
- **Production**: ~25-40 MB

### Build Süresi
- **Clean Build**: 5-10 dakika
- **Incremental**: 1-3 dakika
- **Production**: 3-7 dakika

### Memory Kullanımı
- **Runtime**: 100-200 MB
- **Peak**: 300-500 MB
- **Stable**: 150-250 MB

## 🔐 Güvenlik Önlemleri

### 1. Kod Obfuscation
- Sınıf isimleri karıştırılır
- Metod isimleri karıştırılır
- String değerler korunur

### 2. Resource Shrinking
- Kullanılmayan kaynaklar kaldırılır
- Drawable dosyaları optimize edilir
- Layout dosyaları sıkıştırılır

### 3. Native Library Protection
- JNI metodları korunur
- Native kütüphaneler korunur
- Platform-specific kodlar korunur

## 📈 Monitoring ve Analytics

### Firebase Performance
- APK kurulum süresi
- Uygulama açılış süresi
- UI render performansı
- Network çağrı performansı

### Crashlytics
- Crash raporları
- ANR (Application Not Responding)
- Memory leak tespiti
- Performance bottlenecks

## 🎯 Production Deployment

### 1. Test Ortamı
- [ ] APK test cihazlarında kuruldu
- [ ] Tüm özellikler test edildi
- [ ] Performance testleri yapıldı
- [ ] Güvenlik testleri yapıldı

### 2. Canlı Ortam
- [ ] Production cihazlara kuruldu
- [ ] Monitoring aktif
- [ ] Backup planı hazır
- [ ] Rollback planı hazır

### 3. Post-Deployment
- [ ] Performance monitoring
- [ ] Error tracking
- [ ] User feedback
- [ ] Continuous improvement

## 📞 Destek ve İletişim

### Teknik Destek
- **Build Issues**: Gradle ve ProGuard
- **Runtime Issues**: TCN SDK ve MDB
- **Performance Issues**: Memory ve CPU
- **Security Issues**: Code obfuscation

### Dokümantasyon
- **API Reference**: TCN SDK
- **Error Codes**: IceCreamErrorCodes
- **Build Config**: build.gradle
- **ProGuard Rules**: proguard-rules.pro

---

**Not**: Bu rehber production build sürecini kapsar. Her build öncesi tüm kontrollerin yapılması önerilir.
