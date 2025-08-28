package com.dogus.otomat.icecdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Gelişmiş loglama aktivitesi
 * Advanced logging activity
 */
public class AdvancedLoggingActivity extends AppCompatActivity {
    private static final String TAG = "AdvancedLogging";

    private Spinner spinnerLogLevel;
    private Spinner spinnerLogCategory;
    private Spinner spinnerLogTime;
    private TextView tvLogContent;
    private Button btnRefreshLogs;
    private Button btnExportLogs;
    private Button btnClearLogs;
    private Button btnTestLogging;
    private Button btnViewSystemLogs;

    private SharedPreferences sharedPreferences;
    private String selectedLogLevel;
    private String selectedLogCategory;
    private String selectedLogTime;
    private List<String> logEntries;
    private SimpleDateFormat dateFormat;

    // Log seviyeleri
    private static final String[] LOG_LEVELS = { "Tümü", "DEBUG", "INFO", "WARNING", "ERROR", "CRITICAL" };
    private static final String[] LOG_CATEGORIES = { "Tümü", "Sistem", "Ürün", "Ödeme", "Board", "MDB", "TCN",
            "Kullanıcı" };
    private static final String[] LOG_TIMES = { "Son 1 Saat", "Son 6 Saat", "Son 24 Saat", "Son 7 Gün", "Tümü" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_logging);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        logEntries = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        initViews();
        setupSpinners();
        setupListeners();
        loadLogs();
    }

    private void initViews() {
        spinnerLogLevel = findViewById(R.id.spinner_log_level);
        spinnerLogCategory = findViewById(R.id.spinner_log_category);
        spinnerLogTime = findViewById(R.id.spinner_log_time);
        tvLogContent = findViewById(R.id.tv_log_content);
        btnRefreshLogs = findViewById(R.id.btn_refresh_logs);
        btnExportLogs = findViewById(R.id.btn_export_logs);
        btnClearLogs = findViewById(R.id.btn_clear_logs);
        btnTestLogging = findViewById(R.id.btn_test_logging);
        btnViewSystemLogs = findViewById(R.id.btn_view_system_logs);
    }

    private void setupSpinners() {
        try {
            // Log seviyesi spinner'ı
            ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, LOG_LEVELS);
            levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLogLevel.setAdapter(levelAdapter);

            // Log kategorisi spinner'ı
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, LOG_CATEGORIES);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLogCategory.setAdapter(categoryAdapter);

            // Log zamanı spinner'ı
            ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, LOG_TIMES);
            timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLogTime.setAdapter(timeAdapter);

            // İlk seçimleri yap
            spinnerLogLevel.setSelection(0);
            spinnerLogCategory.setSelection(0);
            spinnerLogTime.setSelection(0);

            // Spinner listener'ları
            spinnerLogLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedLogLevel = LOG_LEVELS[position];
                    filterAndDisplayLogs();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Gerekli değil
                }
            });

            spinnerLogCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedLogCategory = LOG_CATEGORIES[position];
                    filterAndDisplayLogs();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Gerekli değil
                }
            });

            spinnerLogTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedLogTime = LOG_TIMES[position];
                    filterAndDisplayLogs();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Gerekli değil
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Spinner kurulum hatası: " + e.getMessage());
        }
    }

    private void setupListeners() {
        btnRefreshLogs.setOnClickListener(v -> refreshLogs());
        btnExportLogs.setOnClickListener(v -> exportLogs());
        btnClearLogs.setOnClickListener(v -> clearLogs());
        btnTestLogging.setOnClickListener(v -> testLogging());
        btnViewSystemLogs.setOnClickListener(v -> viewSystemLogs());
    }

    private void loadLogs() {
        try {
            // Log dosyalarını oku
            readLogFiles();

            // Filtreleme ve gösterme
            filterAndDisplayLogs();

        } catch (Exception e) {
            Log.e(TAG, "Log yükleme hatası: " + e.getMessage());
            Toast.makeText(this, "Loglar yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void readLogFiles() {
        try {
            logEntries.clear();

            // Ana log dosyası
            File logFile = new File(getFilesDir(), "ice_cream_machine.log");
            if (logFile.exists()) {
                readLogFile(logFile);
            }

            // Board log dosyaları
            File logsDir = new File(getFilesDir(), "board_logs");
            if (logsDir.exists() && logsDir.isDirectory()) {
                File[] logFiles = logsDir.listFiles();
                if (logFiles != null) {
                    for (File file : logFiles) {
                        if (file.getName().endsWith(".log")) {
                            readLogFile(file);
                        }
                    }
                }
            }

            // MDB log dosyası
            File mdbLogFile = new File(getFilesDir(), "mdb_payment.log");
            if (mdbLogFile.exists()) {
                readLogFile(mdbLogFile);
            }

            // TCN log dosyası
            File tcnLogFile = new File(getFilesDir(), "tcn_integration.log");
            if (tcnLogFile.exists()) {
                readLogFile(tcnLogFile);
            }

            Log.i(TAG, "Toplam " + logEntries.size() + " log girişi okundu");

        } catch (Exception e) {
            Log.e(TAG, "Log dosyaları okuma hatası: " + e.getMessage());
        }
    }

    private void readLogFile(File logFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    logEntries.add(line);
                }
            }
            reader.close();

        } catch (IOException e) {
            Log.e(TAG, "Log dosyası okuma hatası (" + logFile.getName() + "): " + e.getMessage());
        }
    }

    private void filterAndDisplayLogs() {
        try {
            List<String> filteredLogs = new ArrayList<>();

            for (String logEntry : logEntries) {
                if (shouldIncludeLog(logEntry)) {
                    filteredLogs.add(logEntry);
                }
            }

            // Logları göster
            displayLogs(filteredLogs);

            Log.i(TAG, "Loglar filtrelendi: " + filteredLogs.size() + " giriş gösteriliyor");

        } catch (Exception e) {
            Log.e(TAG, "Log filtreleme hatası: " + e.getMessage());
        }
    }

    private boolean shouldIncludeLog(String logEntry) {
        try {
            // Log seviyesi kontrolü
            if (!selectedLogLevel.equals("Tümü")) {
                if (!logEntry.contains("[" + selectedLogLevel + "]")) {
                    return false;
                }
            }

            // Log kategorisi kontrolü
            if (!selectedLogCategory.equals("Tümü")) {
                String category = getLogCategory(logEntry);
                if (!category.equals(selectedLogCategory)) {
                    return false;
                }
            }

            // Zaman kontrolü
            if (!selectedLogTime.equals("Tümü")) {
                if (!isLogInTimeRange(logEntry)) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            Log.e(TAG, "Log filtreleme kontrol hatası: " + e.getMessage());
            return true; // Hata durumunda göster
        }
    }

    private String getLogCategory(String logEntry) {
        try {
            if (logEntry.contains("[BOARD]"))
                return "Board";
            if (logEntry.contains("[MDB]"))
                return "MDB";
            if (logEntry.contains("[TCN]"))
                return "TCN";
            if (logEntry.contains("[PAYMENT]"))
                return "Ödeme";
            if (logEntry.contains("[PRODUCT]"))
                return "Ürün";
            if (logEntry.contains("[USER]"))
                return "Kullanıcı";
            if (logEntry.contains("[SYSTEM]"))
                return "Sistem";
            return "Sistem";

        } catch (Exception e) {
            return "Sistem";
        }
    }

    private boolean isLogInTimeRange(String logEntry) {
        try {
            // Log girişindeki zamanı parse et
            String timeStr = extractTimeFromLog(logEntry);
            if (timeStr == null)
                return true; // Zaman bulunamazsa göster

            Date logTime = dateFormat.parse(timeStr);
            if (logTime == null)
                return true;

            long currentTime = System.currentTimeMillis();
            long logTimeMs = logTime.getTime();
            long timeDiff = currentTime - logTimeMs;

            switch (selectedLogTime) {
                case "Son 1 Saat":
                    return timeDiff <= 3600000; // 1 saat = 3600000 ms
                case "Son 6 Saat":
                    return timeDiff <= 21600000; // 6 saat = 21600000 ms
                case "Son 24 Saat":
                    return timeDiff <= 86400000; // 24 saat = 86400000 ms
                case "Son 7 Gün":
                    return timeDiff <= 604800000; // 7 gün = 604800000 ms
                default:
                    return true;
            }

        } catch (Exception e) {
            Log.e(TAG, "Zaman aralığı kontrol hatası: " + e.getMessage());
            return true; // Hata durumunda göster
        }
    }

    private String extractTimeFromLog(String logEntry) {
        try {
            // Log formatı: [2024-01-15 14:30:25] [INFO] [SYSTEM] Mesaj
            int startIndex = logEntry.indexOf('[');
            if (startIndex != -1) {
                int endIndex = logEntry.indexOf(']', startIndex);
                if (endIndex != -1) {
                    return logEntry.substring(startIndex + 1, endIndex);
                }
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }

    private void displayLogs(List<String> logs) {
        try {
            if (logs.isEmpty()) {
                tvLogContent.setText("Filtrelenen kriterlere uygun log bulunamadı.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (String log : logs) {
                sb.append(log).append("\n\n");
            }

            tvLogContent.setText(sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "Log gösterme hatası: " + e.getMessage());
            tvLogContent.setText("Loglar gösterilemedi: " + e.getMessage());
        }
    }

    private void refreshLogs() {
        try {
            // Logları yeniden yükle
            loadLogs();

            Toast.makeText(this, "Loglar yenilendi", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Log yenileme hatası: " + e.getMessage());
            Toast.makeText(this, "Loglar yenilenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void exportLogs() {
        try {
            // Dışa aktarılacak logları al
            List<String> exportLogs = new ArrayList<>();
            for (String logEntry : logEntries) {
                if (shouldIncludeLog(logEntry)) {
                    exportLogs.add(logEntry);
                }
            }

            if (exportLogs.isEmpty()) {
                Toast.makeText(this, "Dışa aktarılacak log yok", Toast.LENGTH_SHORT).show();
                return;
            }

            // Export dosyası oluştur
            String fileName = "ice_cream_logs_" + dateFormat.format(new Date()).replace(":", "-") + ".txt";
            File exportFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName);

            FileWriter writer = new FileWriter(exportFile);
            for (String log : exportLogs) {
                writer.write(log + "\n");
            }
            writer.close();

            Toast.makeText(this, "Loglar dışa aktarıldı: " + fileName, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Loglar dışa aktarıldı: " + exportFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e(TAG, "Log dışa aktarma hatası: " + e.getMessage());
            Toast.makeText(this, "Loglar dışa aktarılamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearLogs() {
        try {
            // Log dosyalarını temizle
            clearLogFiles();

            // Listeyi temizle
            logEntries.clear();

            // UI'ı güncelle
            tvLogContent.setText("Loglar temizlendi.");

            Toast.makeText(this, "Loglar temizlendi", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Tüm loglar temizlendi");

        } catch (Exception e) {
            Log.e(TAG, "Log temizleme hatası: " + e.getMessage());
            Toast.makeText(this, "Loglar temizlenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearLogFiles() {
        try {
            // Ana log dosyası
            File logFile = new File(getFilesDir(), "ice_cream_machine.log");
            if (logFile.exists()) {
                logFile.delete();
            }

            // Board log dosyaları
            File logsDir = new File(getFilesDir(), "board_logs");
            if (logsDir.exists() && logsDir.isDirectory()) {
                File[] logFiles = logsDir.listFiles();
                if (logFiles != null) {
                    for (File file : logFiles) {
                        if (file.getName().endsWith(".log")) {
                            file.delete();
                        }
                    }
                }
            }

            // MDB log dosyası
            File mdbLogFile = new File(getFilesDir(), "mdb_payment.log");
            if (mdbLogFile.exists()) {
                mdbLogFile.delete();
            }

            // TCN log dosyası
            File tcnLogFile = new File(getFilesDir(), "tcn_integration.log");
            if (tcnLogFile.exists()) {
                tcnLogFile.delete();
            }

        } catch (Exception e) {
            Log.e(TAG, "Log dosyaları temizleme hatası: " + e.getMessage());
        }
    }

    private void testLogging() {
        try {
            // Test logları oluştur
            createTestLogs();

            // Logları yeniden yükle
            loadLogs();

            Toast.makeText(this, "Test logları oluşturuldu", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Test loglama hatası: " + e.getMessage());
            Toast.makeText(this, "Test logları oluşturulamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createTestLogs() {
        try {
            // Test logları ekle
            logEntries.add("[" + dateFormat.format(new Date()) + "] [INFO] [SYSTEM] Test log girişi oluşturuldu");
            logEntries.add("[" + dateFormat.format(new Date()) + "] [DEBUG] [BOARD] Board test log girişi");
            logEntries.add("[" + dateFormat.format(new Date()) + "] [WARNING] [MDB] MDB test uyarı log girişi");
            logEntries.add("[" + dateFormat.format(new Date()) + "] [ERROR] [TCN] TCN test hata log girişi");
            logEntries.add("[" + dateFormat.format(new Date()) + "] [INFO] [PAYMENT] Ödeme test log girişi");

        } catch (Exception e) {
            Log.e(TAG, "Test log oluşturma hatası: " + e.getMessage());
        }
    }

    private void viewSystemLogs() {
        try {
            // Sistem loglarını göster (Android logcat)
            Intent intent = new Intent(this, SystemLogsActivity.class);
            startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Sistem logları görüntüleme hatası: " + e.getMessage());
            Toast.makeText(this, "Sistem logları görüntülenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logları yeniden yükle
        loadLogs();
    }
}
