package com.dogus.otomat.icecdemo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DOGİ Gelişmiş Log Sistemi
 * Gerçek dosya sistemi entegrasyonu ile
 */
public class AdvancedLoggingSystem {
    private static final String TAG = "DOGİLogSystem";
    private static AdvancedLoggingSystem instance;
    private final Context context;
    
    // Log seviyeleri
    public static final int LOG_LEVEL_VERBOSE = 0;
    public static final int LOG_LEVEL_DEBUG = 1;
    public static final int LOG_LEVEL_INFO = 2;
    public static final int LOG_LEVEL_WARNING = 3;
    public static final int LOG_LEVEL_ERROR = 4;
    public static final int LOG_LEVEL_FATAL = 5;
    
    // Log dosya ayarları - DOGİ isimleri
    private static final String LOG_FOLDER = "DOGİ/DOGİ_Logs";
    private static final String LOG_FILE_PREFIX = "dogi_log_";
    private static final String LOG_FILE_EXTENSION = ".log";
    private static final int MAX_LOG_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_LOG_FILES = 10;
    
    private int currentLogLevel = LOG_LEVEL_INFO;
    private boolean isFileLoggingEnabled = true;
    private boolean isConsoleLoggingEnabled = true;
    
    // Log dosya yöneticisi
    private File currentLogFile;
    private FileWriter logFileWriter;
    private long currentLogFileSize = 0;
    
    // Log formatı
    private final SimpleDateFormat logDateFormat;
    private final SimpleDateFormat logFileDateFormat;
    
    private AdvancedLoggingSystem(Context context) {
        this.context = context.getApplicationContext();
        this.logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        this.logFileDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        Log.i(TAG, "DOGİ log sistemi başlatılıyor...");
        initializeLoggingSystem();
        Log.i(TAG, "DOGİ log sistemi başarıyla başlatıldı");
    }
    
    public static synchronized AdvancedLoggingSystem getInstance(Context context) {
        if (instance == null) {
            instance = new AdvancedLoggingSystem(context);
        }
        return instance;
    }
    
    public void setLogLevel(int level) {
        this.currentLogLevel = level;
        Log.i(TAG, "DOGİ log seviyesi değiştirildi: " + getLogLevelString(level));
    }
    
    public void setFileLoggingEnabled(boolean enabled) {
        this.isFileLoggingEnabled = enabled;
        Log.i(TAG, "DOGİ dosya log'u " + (enabled ? "açıldı" : "kapatıldı"));
    }
    
    public void setConsoleLoggingEnabled(boolean enabled) {
        this.isConsoleLoggingEnabled = enabled;
        Log.i(TAG, "DOGİ console log'u " + (enabled ? "açıldı" : "kapatıldı"));
    }
    
    private void initializeLoggingSystem() {
        try {
            Log.i(TAG, "DOGİ log sistemi başlatılıyor...");

            // Log klasörünü oluştur
            createLogFolder();

            // Log dosyasını oluştur
            createNewLogFile();

            // Eski log dosyalarını temizle
            cleanupOldLogFiles();

            Log.i(TAG, "DOGİ log sistemi başarıyla başlatıldı");

        } catch (Exception e) {
            Log.e(TAG, "DOGİ log sistemi başlatma hatası: " + e.getMessage());
        }
    }

    private void createLogFolder() {
        try {
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String logPath = rootPath + "/" + LOG_FOLDER;
            File folder = new File(logPath);
            boolean created = folder.mkdirs();
            
            if (created) {
                Log.i(TAG, "DOGİ log klasörü oluşturuldu: " + logPath);
            } else {
                Log.e(TAG, "DOGİ log klasörü oluşturulamadı: " + logPath);
            }

        } catch (Exception e) {
            Log.e(TAG, "DOGİ log klasörü oluşturma hatası: " + e.getMessage());
        }
    }

    private void createNewLogFile() {
        try {
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String logPath = rootPath + "/" + LOG_FOLDER;
            String fileName = LOG_FILE_PREFIX + logFileDateFormat.format(new Date()) + LOG_FILE_EXTENSION;
            String fullPath = logPath + "/" + fileName;

            currentLogFile = new File(fullPath);

            // Dosya yoksa oluştur
            if (!currentLogFile.exists()) {
                currentLogFile.createNewFile();
                currentLogFileSize = 0;
                Log.i(TAG, "DOGİ yeni log dosyası oluşturuldu: " + fullPath);
            } else {
                currentLogFileSize = currentLogFile.length();
                Log.i(TAG, "DOGİ mevcut log dosyası kullanılıyor: " + fullPath + " (Boyut: "
                        + formatFileSize(currentLogFileSize) + ")");
            }

            // FileWriter'ı başlat
            logFileWriter = new FileWriter(currentLogFile, true);

        } catch (Exception e) {
            Log.e(TAG, "DOGİ log dosyası oluşturma hatası: " + e.getMessage());
        }
    }

    private void cleanupOldLogFiles() {
        try {
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String logPath = rootPath + "/" + LOG_FOLDER;
            File logFolder = new File(logPath);

            if (!logFolder.exists() || !logFolder.isDirectory()) {
                return;
            }

            File[] logFiles = logFolder
                    .listFiles((dir, name) -> name.startsWith(LOG_FILE_PREFIX) && name.endsWith(LOG_FILE_EXTENSION));

            if (logFiles != null && logFiles.length > MAX_LOG_FILES) {
                // Dosyaları tarihe göre sırala
                java.util.Arrays.sort(logFiles, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

                // En eski dosyaları sil
                int filesToDelete = logFiles.length - MAX_LOG_FILES;
                for (int i = 0; i < filesToDelete; i++) {
                    boolean deleted = logFiles[i].delete();
                    if (deleted) {
                        Log.i(TAG, "DOGİ eski log dosyası silindi: " + logFiles[i].getName());
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "DOGİ eski log dosyaları temizleme hatası: " + e.getMessage());
        }
    }
    
    public void log(int level, String tag, String message, Throwable throwable) {
        if (level >= currentLogLevel) {
            writeConsoleLog(level, tag, message, throwable);
            writeFileLog(level, tag, message, throwable);
        }
    }
    
    private void writeConsoleLog(int level, String tag, String message, Throwable throwable) {
        if (!isConsoleLoggingEnabled) return;
        
        String timestamp = logDateFormat.format(new Date());
        String levelStr = getLogLevelString(level);
        String logMessage = String.format("[%s] %s/%s: %s", timestamp, levelStr, tag, message);
        
        switch (level) {
            case LOG_LEVEL_VERBOSE:
                Log.v(tag, logMessage, throwable);
                break;
            case LOG_LEVEL_DEBUG:
                Log.d(tag, logMessage, throwable);
                break;
            case LOG_LEVEL_INFO:
                Log.i(tag, logMessage, throwable);
                break;
            case LOG_LEVEL_WARNING:
                Log.w(tag, logMessage, throwable);
                break;
            case LOG_LEVEL_ERROR:
                Log.e(tag, logMessage, throwable);
                break;
            case LOG_LEVEL_FATAL:
                Log.wtf(tag, logMessage, throwable);
                break;
        }
    }

    private void writeFileLog(int level, String tag, String message, Throwable throwable) {
        if (!isFileLoggingEnabled || logFileWriter == null) return;
        
        try {
            // Log dosyası boyutunu kontrol et
            checkLogFileSize();

            // Log mesajını formatla
            String formattedMessage = formatLogMessage(level, tag, message, throwable);

            // Dosyaya yaz
            logFileWriter.write(formattedMessage);
            logFileWriter.flush();
            currentLogFileSize += formattedMessage.length();

        } catch (Exception e) {
            Log.e(TAG, "DOGİ dosya log yazma hatası: " + e.getMessage());
        }
    }

    private void checkLogFileSize() {
        try {
            if (currentLogFileSize > MAX_LOG_FILE_SIZE) {
                Log.i(TAG, "DOGİ log dosyası boyutu aşıldı, yeni dosya oluşturuluyor...");

                // Mevcut dosyayı kapat
                if (logFileWriter != null) {
                    logFileWriter.close();
                }

                // Yeni dosya oluştur
                createNewLogFile();
            }

        } catch (Exception e) {
            Log.e(TAG, "DOGİ log dosyası boyut kontrol hatası: " + e.getMessage());
        }
    }

    private String formatLogMessage(int level, String tag, String message, Throwable throwable) {
        StringBuilder sb = new StringBuilder();

        // Tarih ve saat
        sb.append(logDateFormat.format(new Date()));
        sb.append(" ");

        // Log seviyesi
        sb.append(getLogLevelString(level));
        sb.append(" ");

        // Tag
        sb.append(tag);
        sb.append(": ");

        // Mesaj
        sb.append(message);

        // Throwable
        if (throwable != null) {
            sb.append("\n");
            sb.append(getStackTraceString(throwable));
        }

        sb.append("\n");

        return sb.toString();
    }
    
    public void verbose(String tag, String message) {
        log(LOG_LEVEL_VERBOSE, tag, message, null);
    }
    
    public void verbose(String tag, String message, Throwable throwable) {
        log(LOG_LEVEL_VERBOSE, tag, message, throwable);
    }
    
    public void debug(String tag, String message) {
        log(LOG_LEVEL_DEBUG, tag, message, null);
    }
    
    public void debug(String tag, String message, Throwable throwable) {
        log(LOG_LEVEL_DEBUG, tag, message, throwable);
    }
    
    public void info(String tag, String message) {
        log(LOG_LEVEL_INFO, tag, message, null);
    }
    
    public void info(String tag, String message, Throwable throwable) {
        log(LOG_LEVEL_INFO, tag, message, throwable);
    }
    
    public void warning(String tag, String message) {
        log(LOG_LEVEL_WARNING, tag, message, null);
    }
    
    public void warning(String tag, String message, Throwable throwable) {
        log(LOG_LEVEL_WARNING, tag, message, throwable);
    }
    
    public void error(String tag, String message) {
        log(LOG_LEVEL_ERROR, tag, message, null);
    }
    
    public void error(String tag, String message, Throwable throwable) {
        log(LOG_LEVEL_ERROR, tag, message, throwable);
    }
    
    public void fatal(String tag, String message) {
        log(LOG_LEVEL_FATAL, tag, message, null);
    }
    
    public void fatal(String tag, String message, Throwable throwable) {
        log(LOG_LEVEL_FATAL, tag, message, throwable);
    }
    
    public void getLogFileInfo() {
        try {
            Log.i(TAG, "=== DOGİ Log Sistemi Durum Raporu ===");
            
            if (currentLogFile != null) {
                info(TAG, "DOGİ Mevcut log dosyası: " + currentLogFile.getName());
                info(TAG, "DOGİ Log dosyası boyutu: " + formatFileSize(currentLogFileSize));
                info(TAG, "DOGİ Log dosyası yolu: " + currentLogFile.getAbsolutePath());
            }

            // Log klasöründeki dosyaları listele
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String logPath = rootPath + "/" + LOG_FOLDER;
            File logFolder = new File(logPath);

            if (logFolder.exists() && logFolder.isDirectory()) {
                File[] logFiles = logFolder.listFiles(
                        (dir, name) -> name.startsWith(LOG_FILE_PREFIX) && name.endsWith(LOG_FILE_EXTENSION));

                if (logFiles != null) {
                    info(TAG, "DOGİ Toplam log dosyası sayısı: " + logFiles.length);
                    for (File logFile : logFiles) {
                        info(TAG, "DOGİ   - " + logFile.getName() + " (" + formatFileSize(logFile.length()) + ")");
                    }
                }
            }

        } catch (Exception e) {
            error(TAG, "DOGİ Log dosyası bilgisi alma hatası: " + e.getMessage());
        }
    }
    
    public void clearCurrentLogFile() {
        try {
            if (logFileWriter != null) {
                logFileWriter.close();
            }

            if (currentLogFile != null && currentLogFile.exists()) {
                currentLogFile.delete();
                currentLogFile.createNewFile();
                currentLogFileSize = 0;

                logFileWriter = new FileWriter(currentLogFile, true);
                info(TAG, "DOGİ Mevcut log dosyası temizlendi");
            }
        } catch (Exception e) {
            error(TAG, "DOGİ Log dosyası temizleme hatası: " + e.getMessage());
        }
    }
    
    public void clearAllLogFiles() {
        try {
            // Mevcut dosyayı kapat
            if (logFileWriter != null) {
                logFileWriter.close();
            }

            // Log klasöründeki tüm dosyaları sil
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String logPath = rootPath + "/" + LOG_FOLDER;
            File logFolder = new File(logPath);

            if (logFolder.exists() && logFolder.isDirectory()) {
                File[] logFiles = logFolder.listFiles(
                        (dir, name) -> name.startsWith(LOG_FILE_PREFIX) && name.endsWith(LOG_FILE_EXTENSION));

                if (logFiles != null) {
                    for (File logFile : logFiles) {
                        boolean deleted = logFile.delete();
                        if (deleted) {
                            info(TAG, "DOGİ Log dosyası silindi: " + logFile.getName());
                        }
                    }
                }
            }

            // Yeni log dosyası oluştur
            createNewLogFile();
            info(TAG, "DOGİ Tüm log dosyaları temizlendi");

        } catch (Exception e) {
            error(TAG, "DOGİ Tüm log dosyaları temizleme hatası: " + e.getMessage());
        }
    }
    
    public void generateLogSystemReport() {
        try {
            info(TAG, "=== DOGİ Log Sistemi Detaylı Rapor ===");

            // Genel ayarlar
            info(TAG, "DOGİ Log Seviyesi: " + getLogLevelString(currentLogLevel));
            info(TAG, "DOGİ Dosya Loglama: " + (isFileLoggingEnabled ? "Açık" : "Kapalı"));
            info(TAG, "DOGİ Console Loglama: " + (isConsoleLoggingEnabled ? "Açık" : "Kapalı"));

            // Dosya bilgileri
            getLogFileInfo();

            // Sistem bilgileri
            info(TAG, "DOGİ Maksimum log dosyası boyutu: " + formatFileSize(MAX_LOG_FILE_SIZE));
            info(TAG, "DOGİ Maksimum log dosyası sayısı: " + MAX_LOG_FILES);

            info(TAG, "=== DOGİ Rapor Tamamlandı ===");

        } catch (Exception e) {
            error(TAG, "DOGİ Log sistemi rapor hatası: " + e.getMessage());
        }
    }
    
    public void logSystemEvent(String event, String details) {
        info("DOGİ_SYSTEM_EVENT", "Event: " + event + " | Details: " + details);
    }
    
    public void logUserAction(String user, String action, String details) {
        info("DOGİ_USER_ACTION", "User: " + user + " | Action: " + action + " | Details: " + details);
    }
    
    public void logPerformance(String operation, long durationMs) {
        info("DOGİ_PERFORMANCE", "Operation: " + operation + " | Duration: " + durationMs + "ms");
    }
    
    public void shutdown() {
        try {
            info(TAG, "DOGİ Log sistemi kapatılıyor...");

            // FileWriter'ı kapat
            if (logFileWriter != null) {
                logFileWriter.close();
            }

            info(TAG, "DOGİ Log sistemi kapatıldı");

        } catch (Exception e) {
            Log.e(TAG, "DOGİ Log sistemi kapatma hatası: " + e.getMessage());
        }
    }
    
    private String getLogLevelString(int level) {
        switch (level) {
            case LOG_LEVEL_VERBOSE: return "VERBOSE";
            case LOG_LEVEL_DEBUG: return "DEBUG";
            case LOG_LEVEL_INFO: return "INFO";
            case LOG_LEVEL_WARNING: return "WARNING";
            case LOG_LEVEL_ERROR: return "ERROR";
            case LOG_LEVEL_FATAL: return "FATAL";
            default: return "UNKNOWN";
        }
    }

    private String getStackTraceString(Throwable throwable) {
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            return "DOGİ Stack trace alınamadı: " + e.getMessage();
        }
    }

    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    // StringWriter sınıfı
    private static class StringWriter extends java.io.StringWriter {
        // Android'de mevcut olan StringWriter kullanılıyor
    }
}
