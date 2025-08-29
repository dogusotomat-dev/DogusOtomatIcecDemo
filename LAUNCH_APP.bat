@echo off
title Dogi Soft Ice Cream Demo - Launcher

echo ===============================================
echo   Dogi Soft Ice Cream Demo - Application Launcher
echo ===============================================
echo.

echo Checking system requirements...
echo.

:: Check if ADB is available
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] ADB not found in PATH
    echo          USB debugging may not work properly
    echo.
)

:: Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java not found in PATH
    echo         Please install Java JDK 8 or higher
    echo.
    pause
    exit /b 1
)

:: Check if Android SDK is available
if "%ANDROID_HOME%"=="" (
    echo [WARNING] ANDROID_HOME environment variable not set
    echo          Some features may not work properly
    echo.
)

echo System check completed.
echo.

:: Display available actions
echo Available actions:
echo   1. Install Production APK to connected device
echo   2. Launch application on connected device
echo   3. View application logs
echo   4. Uninstall application from device
echo   5. Show device information
echo   6. Exit
echo.

:menu
set /p choice=Enter your choice (1-6): 

if "%choice%"=="1" goto install_apk
if "%choice%"=="2" goto launch_app
if "%choice%"=="3" goto view_logs
if "%choice%"=="4" goto uninstall_app
if "%choice%"=="5" goto device_info
if "%choice%"=="6" goto exit_script

echo Invalid choice. Please enter a number between 1-6.
echo.
goto menu

:install_apk
echo.
echo Installing Production APK to connected device...
echo.

:: Check if device is connected
adb devices | findstr "device" >nul
if %errorlevel% equ 1 (
    echo [ERROR] No Android device found
    echo         Please connect an Android device and enable USB debugging
    echo.
    goto menu
)

:: Install APK
adb install -r app\build\outputs\apk\production\DogusOtomatIcecDemo-1.0.0428-PRODUCTION.apk
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Failed to install APK
    echo         Check device connection and USB debugging settings
    echo.
    pause
    goto menu
)

echo.
echo [SUCCESS] APK installed successfully
echo.
goto menu

:launch_app
echo.
echo Launching application on connected device...
echo.

:: Check if device is connected
adb devices | findstr "device" >nul
if %errorlevel% equ 1 (
    echo [ERROR] No Android device found
    echo         Please connect an Android device and enable USB debugging
    echo.
    goto menu
)

:: Launch application
adb shell am start -n com.dogus.otomat.icecdemo/.MainAct
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Failed to launch application
    echo         Make sure the app is installed on the device
    echo.
    pause
    goto menu
)

echo.
echo [SUCCESS] Application launched
echo.
goto menu

:view_logs
echo.
echo Viewing application logs (Press Ctrl+C to stop)...
echo.
echo ===============================================

:: Check if device is connected
adb devices | findstr "device" >nul
if %errorlevel% equ 1 (
    echo [ERROR] No Android device found
    echo         Please connect an Android device and enable USB debugging
    echo.
    goto menu
)

:: View logs
adb logcat | findstr "com.dogus.otomat.icecdemo"
goto menu

:uninstall_app
echo.
echo Uninstalling application from device...
echo.

:: Check if device is connected
adb devices | findstr "device" >nul
if %errorlevel% equ 1 (
    echo [ERROR] No Android device found
    echo         Please connect an Android device and enable USB debugging
    echo.
    goto menu
)

:: Uninstall application
adb uninstall com.dogus.otomat.icecdemo
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Failed to uninstall application
    echo         Make sure the app is installed on the device
    echo.
    pause
    goto menu
)

echo.
echo [SUCCESS] Application uninstalled
echo.
goto menu

:device_info
echo.
echo Device Information:
echo ===============================================
adb devices
echo.
adb shell getprop ro.product.model
adb shell getprop ro.build.version.release
adb shell getprop ro.build.version.sdk
echo.
goto menu

:exit_script
echo.
echo Thank you for using Dogi Soft Ice Cream Demo!
echo For support, contact: teknik@dogusotomat.com
echo.
pause
exit /b 0