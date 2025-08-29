# TCN Icecboard Library - ProGuard Kuralları

# TCN SDK ve Icecboard sınıflarını koru
-keep class com.tcn.** { *; }
-keep class com.icec.** { *; }
-keep class com.dogus.otomat.tcn.** { *; }

# Native metodları koru
-keepclassmembers class * {
    native <methods>;
}

# Gson koruması
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Microlog koruması
-keep class org.microlog.** { *; }

# AndroidX koruması
-keep class androidx.** { *; }
-dontwarn androidx.**

# Genel optimizasyon
-keepattributes SourceFile,LineNumberTable
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

# R sınıfı koruması
-keep class **.R$* {
    public static <fields>;
}

# Optimizasyon seviyeleri
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# Uyarıları bastır
-dontwarn android.**
-dontwarn com.android.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**