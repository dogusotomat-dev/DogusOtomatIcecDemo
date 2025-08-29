# DOGİ Dondurma Otomatı - Production ProGuard Kuralları
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ===========================================
# TCN SDK ve Icecboard Koruması
# ===========================================
-keep class com.tcn.** { *; }
-keep class com.icec.** { *; }
-keep class com.dogus.otomat.icecdemo.** { *; }
-keep class com.dogus.otomat.tcn.** { *; }

# TCN SDK native metodları
-keepclassmembers class * {
    native <methods>;
}

# ===========================================
# Firebase Koruması
# ===========================================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }
-keep class com.google.firebase.database.** { *; }
-keep class com.google.firebase.perf.** { *; }
-keep class com.google.firebase.crashlytics.** { *; }

# ===========================================
# Gson Koruması
# ===========================================
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ===========================================
# AndroidX ve Support Kütüphaneleri
# ===========================================
-keep class androidx.** { *; }
-keep class android.support.** { *; }
-dontwarn androidx.**
-dontwarn android.support.**

# ===========================================
# Serial Port ve MDB Koruması
# ===========================================
-keep class android.serialport.** { *; }
-keep class com.dogus.otomat.icecdemo.MDBPaymentManager { *; }
-keep class com.dogus.otomat.icecdemo.SDKIntegrationHelper { *; }
-keep class com.dogus.otomat.icecdemo.SerialPortController { *; }

# ===========================================
# Advertisement ve UI Koruması
# ===========================================
-keep class com.dogus.otomat.icecdemo.AdvertisementManager { *; }
-keep class com.dogus.otomat.icecdemo.MainAct { *; }
-keep class com.dogus.otomat.icecdemo.ProductSettingsActivity { *; }
-keep class com.dogus.otomat.icecdemo.AdvertisementSettingsActivity { *; }

# ===========================================
# Logging ve Error Handling
# ===========================================
-keep class com.dogus.otomat.icecdemo.AdvancedLoggingSystem { *; }
-keep class com.dogus.otomat.icecdemo.PeriodicLoggingService { *; }
-keep class com.dogus.otomat.icecdemo.IceCreamErrorCodes { *; }

# ===========================================
# SharedPreferences ve Data Storage
# ===========================================
-keep class com.dogus.otomat.icecdemo.** {
    @android.content.SharedPreferences *;
}

# ===========================================
# Genel Optimizasyon Kuralları
# ===========================================
# Hata ayıklama bilgilerini koru
-keepattributes SourceFile,LineNumberTable

# Reflection kullanımı için
-keepattributes *Annotation*
-keepattributes InnerClasses

# Parcelable implementasyonları
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Serializable sınıfları
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ===========================================
# Native Kütüphaneler
# ===========================================
-keep class **.R$* {
    public static <fields>;
}

# ===========================================
# WebView ve JavaScript Interface
# ===========================================
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# ===========================================
# Test Sınıfları (Production'da kaldır)
# ===========================================
-dontwarn junit.**
-dontwarn org.junit.**
-dontwarn org.mockito.**
-dontwarn org.robolectric.**

# ===========================================
# Genel Uyarıları Bastır
# ===========================================
-dontwarn android.**
-dontwarn com.android.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ===========================================
# Optimizasyon Seviyeleri
# ===========================================
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# ===========================================
# Dosya Adı Gizleme
# ===========================================
-renamesourcefileattribute SourceFile
