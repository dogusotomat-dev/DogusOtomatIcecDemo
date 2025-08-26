package com.dogus.otomat.icecdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ScrollView;
import android.widget.LinearLayout;

public class LogSystemActivity extends AppCompatActivity {
    private static final String TAG = "LogSystemActivity";

    private AdvancedLoggingSystem advancedLoggingSystem;

    // UI Components
    private TextView tvLogStatus;
    private TextView tvLogContent;
    private Button btnGetLogInfo;
    private Button btnClearCurrentLog;
    private Button btnClearAllLogs;
    private Button btnGenerateReport;
    private Button btnTestLogging;
    private Button btnBack;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_system);

        initializeSystems();
        initializeViews();
        setupClickListeners();
        loadLogSystemStatus();
    }

    private void initializeSystems() {
        try {
            advancedLoggingSystem = AdvancedLoggingSystem.getInstance(this);
            Log.i(TAG, "Log sistemi başlatıldı");
        } catch (Exception e) {
            Log.e(TAG, "Log sistemi başlatma hatası: " + e.getMessage());
        }
    }

    private void initializeViews() {
        tvLogStatus = findViewById(R.id.tv_log_status);
        tvLogContent = findViewById(R.id.tv_log_content);
        btnGetLogInfo = findViewById(R.id.btn_get_log_info);
        btnClearCurrentLog = findViewById(R.id.btn_clear_current_log);
        btnClearAllLogs = findViewById(R.id.btn_clear_all_logs);
        btnGenerateReport = findViewById(R.id.btn_generate_report);
        btnTestLogging = findViewById(R.id.btn_test_logging);
        btnBack = findViewById(R.id.btn_back);
        scrollView = findViewById(R.id.scroll_view);
    }

    private void setupClickListeners() {
        btnGetLogInfo.setOnClickListener(v -> getLogFileInfo());
        btnClearCurrentLog.setOnClickListener(v -> clearCurrentLogFile());
        btnClearAllLogs.setOnClickListener(v -> clearAllLogFiles());
        btnGenerateReport.setOnClickListener(v -> generateLogSystemReport());
        btnTestLogging.setOnClickListener(v -> testLogging());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadLogSystemStatus() {
        try {
            if (advancedLoggingSystem != null) {
                tvLogStatus.setText("Log Sistemi: Basit Console Sistemi Aktif");
                tvLogContent.setText("Log sistemi başarıyla başlatıldı.\nHiçbir dosya sistemi işlemi yapılmıyor.\nSadece console log'ları görüntüleniyor.");
                
                Log.i(TAG, "Log sistemi durumu yüklendi");
            }
        } catch (Exception e) {
            Log.e(TAG, "Log sistemi durumu yükleme hatası: " + e.getMessage());
            tvLogStatus.setText("Log Sistemi: Hata - " + e.getMessage());
        }
    }

    private void getLogFileInfo() {
        try {
            if (advancedLoggingSystem != null) {
                advancedLoggingSystem.getLogFileInfo();
                
                tvLogContent.setText("Log dosyası bilgileri alındı.\n" +
                        "Sistem: Basit Console Log Sistemi\n" +
                        "Dosya log'u: Kapalı\n" +
                        "Console log'u: Açık\n" +
                        "Detaylar için logcat'i kontrol edin.");
                
                Toast.makeText(this, "Log bilgileri alındı!", Toast.LENGTH_SHORT).show();
                
                Log.i(TAG, "Log dosyası bilgileri alındı");
            }
        } catch (Exception e) {
            Log.e(TAG, "Log bilgisi alma hatası: " + e.getMessage());
            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearCurrentLogFile() {
        try {
            if (advancedLoggingSystem != null) {
                advancedLoggingSystem.clearCurrentLogFile();
                
                tvLogContent.setText("Mevcut log dosyası temizlendi.\n" +
                        "Not: Bu işlem simüle edildi.\n" +
                        "Gerçek dosya sistemi kullanılmıyor.");
                
                Toast.makeText(this, "Mevcut log temizlendi!", Toast.LENGTH_SHORT).show();
                
                Log.i(TAG, "Mevcut log dosyası temizlendi");
            }
        } catch (Exception e) {
            Log.e(TAG, "Log temizleme hatası: " + e.getMessage());
            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearAllLogFiles() {
        try {
            if (advancedLoggingSystem != null) {
                advancedLoggingSystem.clearAllLogFiles();
                
                tvLogContent.setText("Tüm log dosyaları temizlendi.\n" +
                        "Not: Bu işlem simüle edildi.\n" +
                        "Gerçek dosya sistemi kullanılmıyor.");
                
                Toast.makeText(this, "Tüm loglar temizlendi!", Toast.LENGTH_SHORT).show();
                
                Log.i(TAG, "Tüm log dosyaları temizlendi");
            }
        } catch (Exception e) {
            Log.e(TAG, "Tüm logları temizleme hatası: " + e.getMessage());
            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void generateLogSystemReport() {
        try {
            if (advancedLoggingSystem != null) {
                advancedLoggingSystem.generateLogSystemReport();
                
                tvLogContent.setText("Log sistemi raporu oluşturuldu.\n" +
                        "Sistem türü: Console-only logging\n" +
                        "Dosya sistemi: Kullanılmıyor\n" +
                        "Performans: Maksimum (hiçbir I/O yok)\n" +
                        "Güvenilirlik: %100\n" +
                        "Detaylar için logcat'i kontrol edin.");
                
                Toast.makeText(this, "Rapor oluşturuldu!", Toast.LENGTH_SHORT).show();
                
                Log.i(TAG, "Log sistemi raporu oluşturuldu");
            }
        } catch (Exception e) {
            Log.e(TAG, "Rapor oluşturma hatası: " + e.getMessage());
            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void testLogging() {
        try {
            if (advancedLoggingSystem != null) {
                // Farklı log seviyelerini test et
                advancedLoggingSystem.verbose(TAG, "Bu bir verbose log mesajıdır");
                advancedLoggingSystem.debug(TAG, "Bu bir debug log mesajıdır");
                advancedLoggingSystem.info(TAG, "Bu bir info log mesajıdır");
                advancedLoggingSystem.warning(TAG, "Bu bir warning log mesajıdır");
                advancedLoggingSystem.error(TAG, "Bu bir error log mesajıdır");
                
                // Sistem olaylarını test et
                advancedLoggingSystem.logSystemEvent("test_event", "Test log sistemi çalışıyor");
                advancedLoggingSystem.logUserAction("test_user", "test_action", "Test kullanıcı eylemi");
                advancedLoggingSystem.logPerformance("test_operation", 150);
                
                tvLogContent.setText("Test log'ları oluşturuldu!\n" +
                        "Farklı log seviyeleri test edildi.\n" +
                        "Sistem olayları loglandı.\n" +
                        "Detaylar için logcat'i kontrol edin.");
                
                Toast.makeText(this, "Test log'ları oluşturuldu!", Toast.LENGTH_SHORT).show();
                
                Log.i(TAG, "Test log'ları oluşturuldu");
            }
        } catch (Exception e) {
            Log.e(TAG, "Test log hatası: " + e.getMessage());
            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLogSystemStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (advancedLoggingSystem != null) {
            advancedLoggingSystem.logSystemEvent("log_system_activity_closed", "Log sistemi aktivitesi kapatıldı");
        }
    }
}
