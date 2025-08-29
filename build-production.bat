@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM DOGİ Dondurma Otomatı - Production Build Script (Windows)
REM Bu script production-ready APK oluşturur

echo ==========================================
echo DOGİ Dondurma Otomatı - Production Build
echo ==========================================

REM Hata kontrolü
set EXIT_CODE=0

REM Temizlik
echo 🧹 Temizlik yapılıyor...
call gradlew.bat clean
if %ERRORLEVEL% neq 0 (
    echo ❌ Temizlik başarısız!
    set EXIT_CODE=1
    goto :end
)

REM Lint kontrolü
echo 🔍 Lint kontrolü yapılıyor...
call gradlew.bat lintProduction
if %ERRORLEVEL% neq 0 (
    echo ⚠️  Lint uyarıları var, devam ediliyor...
)

REM Production build
echo 🏗️  Production build başlatılıyor...
call gradlew.bat assembleProduction
if %ERRORLEVEL% neq 0 (
    echo ❌ Production build başarısız!
    set EXIT_CODE=1
    goto :end
)

echo ✅ Production build başarılı!

REM APK konumu
set APK_PATH=app\build\outputs\apk\production\DogusOtomatIcecDemo-1.0.0428-PRODUCTION.apk

REM APK varlık kontrolü
if exist "%APK_PATH%" (
    echo 📱 APK oluşturuldu: %APK_PATH%
    
    REM APK boyutu
    for %%A in ("%APK_PATH%") do set APK_SIZE=%%~zA
    echo 📏 APK Boyutu: %APK_SIZE% bytes
    
    REM Production klasörü oluştur
    if not exist "production" mkdir production
    
    REM APK'yı production klasörüne kopyala
    copy "%APK_PATH%" "production\" >nul
    if %ERRORLEVEL% equ 0 (
        echo 📁 APK production\ klasörüne kopyalandı
    ) else (
        echo ❌ APK kopyalama başarısız!
    )
    
) else (
    echo ❌ APK bulunamadı: %APK_PATH%
    set EXIT_CODE=1
    goto :end
)

REM Bundle oluştur (isteğe bağlı)
echo 📦 AAB bundle oluşturuluyor...
call gradlew.bat bundleProduction
if %ERRORLEVEL% equ 0 (
    echo ✅ AAB bundle başarılı!
    
    REM Bundle konumu
    set BUNDLE_PATH=app\build\outputs\bundle\production\DogusOtomatIcecDemo-1.0.0428-production.aab
    
    if exist "%BUNDLE_PATH%" (
        echo 📦 Bundle oluşturuldu: %BUNDLE_PATH%
        
        REM Bundle boyutu
        for %%A in ("%BUNDLE_PATH%") do set BUNDLE_SIZE=%%~zA
        echo 📏 Bundle Boyutu: %BUNDLE_SIZE% bytes
        
        REM Bundle'ı production klasörüne kopyala
        copy "%BUNDLE_PATH%" "production\" >nul
        if %ERRORLEVEL% equ 0 (
            echo 📁 Bundle production\ klasörüne kopyalandı
        )
    )
) else (
    echo ⚠️  Bundle oluşturulamadı, devam ediliyor...
)

echo.
echo ==========================================
echo 🎉 Production Build Tamamlandı!
echo ==========================================
echo.
echo 📱 APK: production\DogusOtomatIcecDemo-1.0.0428-PRODUCTION.apk
echo 📦 Bundle: production\DogusOtomatIcecDemo-1.0.0428-production.aab
echo.
echo ⚠️  Önemli Notlar:
echo 1. APK production ortamında test edilmeli
echo 2. Tüm özellikler çalışır durumda olmalı
echo 3. Performance testleri yapılmalı
echo 4. Memory leak kontrolü yapılmalı
echo.
echo 🚀 Production'a hazır!

:end
if %EXIT_CODE% equ 0 (
    echo.
    echo ✅ Build başarıyla tamamlandı!
) else (
    echo.
    echo ❌ Build sırasında hata oluştu!
)

pause
exit /b %EXIT_CODE%
