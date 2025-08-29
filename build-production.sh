#!/bin/bash

# DOGİ Dondurma Otomatı - Production Build Script
# Bu script production-ready APK oluşturur

echo "=========================================="
echo "DOGİ Dondurma Otomatı - Production Build"
echo "=========================================="

# Renk kodları
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Hata kontrolü
set -e

# Temizlik
echo -e "${BLUE}🧹 Temizlik yapılıyor...${NC}"
./gradlew clean

# Lint kontrolü
echo -e "${BLUE}🔍 Lint kontrolü yapılıyor...${NC}"
./gradlew lintProduction

# Production build
echo -e "${BLUE}🏗️  Production build başlatılıyor...${NC}"
./gradlew assembleProduction

# Build başarı kontrolü
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Production build başarılı!${NC}"
    
    # APK konumu
    APK_PATH="app/build/outputs/apk/production/DogusOtomatIcecDemo-1.0.0428-PRODUCTION.apk"
    
    if [ -f "$APK_PATH" ]; then
        echo -e "${GREEN}📱 APK oluşturuldu: $APK_PATH${NC}"
        
        # APK boyutu
        APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
        echo -e "${GREEN}📏 APK Boyutu: $APK_SIZE${NC}"
        
        # APK hash
        echo -e "${BLUE}🔐 APK SHA256 Hash:${NC}"
        sha256sum "$APK_PATH"
        
        # Production klasörüne kopyala
        mkdir -p production
        cp "$APK_PATH" "production/"
        echo -e "${GREEN}📁 APK production/ klasörüne kopyalandı${NC}"
        
    else
        echo -e "${RED}❌ APK bulunamadı: $APK_PATH${NC}"
        exit 1
    fi
    
else
    echo -e "${RED}❌ Production build başarısız!${NC}"
    exit 1
fi

# Bundle oluştur (isteğe bağlı)
echo -e "${BLUE}📦 AAB bundle oluşturuluyor...${NC}"
./gradlew bundleProduction

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ AAB bundle başarılı!${NC}"
    
    # Bundle konumu
    BUNDLE_PATH="app/build/outputs/bundle/production/DogusOtomatIcecDemo-1.0.0428-production.aab"
    
    if [ -f "$BUNDLE_PATH" ]; then
        echo -e "${GREEN}📦 Bundle oluşturuldu: $BUNDLE_PATH${NC}"
        
        # Bundle boyutu
        BUNDLE_SIZE=$(du -h "$BUNDLE_PATH" | cut -f1)
        echo -e "${GREEN}📏 Bundle Boyutu: $BUNDLE_SIZE${NC}"
        
        # Production klasörüne kopyala
        cp "$BUNDLE_PATH" "production/"
        echo -e "${GREEN}📁 Bundle production/ klasörüne kopyalandı${NC}"
        
    fi
fi

echo ""
echo -e "${GREEN}=========================================="
echo "🎉 Production Build Tamamlandı!"
echo "=========================================="
echo ""
echo -e "${BLUE}📱 APK: production/DogusOtomatIcecDemo-1.0.0428-PRODUCTION.apk${NC}"
echo -e "${BLUE}📦 Bundle: production/DogusOtomatIcecDemo-1.0.0428-production.aab${NC}"
echo ""
echo -e "${YELLOW}⚠️  Önemli Notlar:${NC}"
echo "1. APK production ortamında test edilmeli"
echo "2. Tüm özellikler çalışır durumda olmalı"
echo "3. Performance testleri yapılmalı"
echo "4. Memory leak kontrolü yapılmalı"
echo ""
echo -e "${GREEN}🚀 Production'a hazır!${NC}"
