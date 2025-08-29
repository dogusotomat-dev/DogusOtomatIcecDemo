@echo off
title Dogi Soft Ice Cream Demo - Quick Launcher

:: Quick launcher for Dogi Soft Ice Cream Demo
:: This script provides one-click access to common operations

cls
echo ===============================================
echo   Dogi Soft Ice Cream Demo - Quick Launcher
echo ===============================================
echo.

:: Check prerequisites
echo Checking system prerequisites...
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] ADB not found. Please ensure Android SDK is installed.
    echo         Download from: https://developer.android.com/studio
    echo.
    pause
    exit /b 1
)

echo Prerequisites OK.
echo.

:: Connect to device and install if needed
echo Checking device connection...
adb devices | findstr "device" >nul
if %errorlevel% equ 1 (
    echo [WARNING] No device connected. Please connect Android device.
    echo           Make sure USB debugging is enabled in Developer Options.
    echo.
    echo Waiting for device connection... (Ctrl+C to cancel)
    adb wait-for-device
)

echo Device connected.
echo.

:: Check if app is installed
echo Checking if application is installed...
adb shell pm list packages | findstr "com.dogus.otomat.icecdemo" >nul
if %errorlevel% neq 0 (
    echo Application not found. Installing...
    adb install -r app\build\outputs\apk\production\DogusOtomatIcecDemo-1.0.0428-PRODUCTION.apk
    if %errorlevel% neq 0 (
        echo [ERROR] Failed to install application.
        pause
        exit /b 1
    )
    echo Application installed successfully.
) else (
    echo Application already installed.
)

echo.
echo Launching Dogi Soft Ice Cream Demo...
adb shell am start -n com.dogus.otomat.icecdemo/.MainAct
if %errorlevel% neq 0 (
    echo [ERROR] Failed to launch application.
    pause
    exit /b 1
)

echo.
echo ===============================================
echo Dogi Soft Ice Cream Demo is now running!
echo ===============================================
echo.
echo For administration access:
echo   Username: admin
echo   Password: admin123
echo.
echo For technical support:
echo   teknik@dogusotomat.com
echo   Phone: 444-DOGISOFT
echo.
echo Press any key to close this window...
pause >nul