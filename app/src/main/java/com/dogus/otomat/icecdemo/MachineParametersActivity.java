package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ScrollView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.tcn.icecboard.DriveControl.icec.IceMakeParamBean;

/**
 * Makine Parametreleri Ekranı
 * SDK'dan gelen gerçek dondurma makinesi parametrelerini kullanır
 * TEK dondurma makinesi: Üst hazne (4°C taze tutma) + Alt hazne (dondurma
 * yapma/karıştırma)
 */
public class MachineParametersActivity extends AppCompatActivity {

    private static final String TAG = "MachineParameters";

    // UI Elements - TEK makine, iki hazne
    private EditText etLeftIceLevel, etRightIceLevel; // Dondurma seviyeleri doğru (sol/sağ)
    private EditText etUpperTankTemp, etLowerTankTemp; // Üst hazne (4°C) + Alt hazne (dondurma yapma)
    private EditText etFridgeTemp, etFreezerTemp;
    private Spinner spnWorkMode; // TEK çalışma modu
    private Button btnSaveParameters, btnBack, btnTestParameters, btnQueryStatus;

    // Parametre ayarları için UI
    private LinearLayout llParameterSettings;
    private EditText etParamAddress, etParamValue;
    private Spinner spnParamType;
    private Button btnQueryParam, btnSetParam, btnQueryAllParams;

    // Kapı kontrolü için UI
    private Button btnOpenDoor, btnCloseDoor, btnQueryDoorStatus;

    // Sos ve süsleme dozaj ayarları için UI
    private EditText etSauce1Dosage, etSauce2Dosage, etSauce3Dosage;
    private EditText etTopping1Dosage, etTopping2Dosage, etTopping3Dosage;

    // Makine bilgileri için UI
    private EditText etMachineSerialNo, etIoTNumber;
    private Button btnGenerateMachineInfo;

    // SharedPreferences
    private SharedPreferences sharedPreferences;

    // SDK Integration Helper
    private SDKIntegrationHelper sdkHelper;

    // Handler for UI updates
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_parameters);

        // Initialize UI Handler
        uiHandler = new Handler(Looper.getMainLooper());

        // Initialize SDK Integration Helper
        initializeSDKHelper();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MachineSettings", MODE_PRIVATE);

        initializeViews();
        setupClickListeners();
        setupParameterSettings();

        // Load parameters after SDK initialization
        // SDK Integration Helper callback'te yapılacak
    }

    /**
     * SDK Integration Helper'ı başlatır
     */
    private void initializeSDKHelper() {
        try {
            sdkHelper = SDKIntegrationHelper.getInstance(this);

            // Callback'i ayarla
            sdkHelper.setCallback(new SDKIntegrationHelper.SDKCallback() {
                @Override
                public void onSDKInitialized(boolean success) {
                    if (success) {
                        Log.i(TAG, "SDK Integration Helper başarıyla başlatıldı");
                        showToast("SDK başarıyla başlatıldı!");

                        // Parametreleri yükle ve makine durumunu sorgula
                        if (sdkHelper.isSDKConnected()) {
                            loadCurrentParameters();
                            sdkHelper.queryMachineStatus();
                        }
                    } else {
                        Log.e(TAG, "SDK Integration Helper başlatılamadı");
                        showToast("SDK başlatılamadı!");
                    }
                }

                @Override
                public void onMachineConnected(boolean connected) {
                    if (connected) {
                        Log.i(TAG, "Makine bağlantısı başarılı");
                        showToast("Makine bağlantısı kuruldu!");
                    } else {
                        Log.w(TAG, "Makine bağlantısı başarısız");
                        showToast("Makine bağlantısı kurulamadı!");
                    }
                }

                @Override
                public void onParameterReceived(IceMakeParamBean paramBean) {
                    if (paramBean != null) {
                        updateUIWithMachineParameters(paramBean);
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "SDK Hatası: " + error);
                    showToast("SDK Hatası: " + error);
                }

                @Override
                public void onStatusUpdate(String status) {
                    Log.i(TAG, "SDK Durum: " + status);
                    // UI'da durum göstergesini güncelle
                    updateStatusIndicator(status);
                }
            });

            // SDK'yı başlat
            sdkHelper.initializeSDK();

        } catch (Exception e) {
            Log.e(TAG, "SDK Integration Helper başlatma hatası: " + e.getMessage());
            showToast("SDK başlatma hatası: " + e.getMessage());
        }
    }

    private void initializeViews() {
        // Dondurma seviyeleri (0x01-0x0F) - SOL/SAĞ doğru
        etLeftIceLevel = findViewById(R.id.etLeftIceLevel);
        etRightIceLevel = findViewById(R.id.etRightIceLevel);

        // Sıcaklıklar - TEK makine, iki hazne
        etUpperTankTemp = findViewById(R.id.etUpperTankTemp); // Üst hazne (4°C taze tutma)
        etLowerTankTemp = findViewById(R.id.etLowerTankTemp); // Alt hazne (dondurma yapma/karıştırma)
        etFridgeTemp = findViewById(R.id.etFridgeTemp);
        etFreezerTemp = findViewById(R.id.etFreezerTemp);

        // Çalışma modu - TEK mod (SDK'dan)
        spnWorkMode = findViewById(R.id.spnWorkMode);

        // Parametre ayarları
        llParameterSettings = findViewById(R.id.llParameterSettings);
        etParamAddress = findViewById(R.id.etParamAddress);
        etParamValue = findViewById(R.id.etParamValue);
        spnParamType = findViewById(R.id.spnParamType);
        btnQueryParam = findViewById(R.id.btnQueryParam);
        btnSetParam = findViewById(R.id.btnSetParam);
        btnQueryAllParams = findViewById(R.id.btnQueryAllParams);

        // Buttons
        btnSaveParameters = findViewById(R.id.btnSaveParameters);
        btnBack = findViewById(R.id.btnBack);
        btnTestParameters = findViewById(R.id.btnTestParameters);
        btnQueryStatus = findViewById(R.id.btnQueryStatus);

        // Kapı kontrolü butonları
        btnOpenDoor = findViewById(R.id.btnOpenDoor);
        btnCloseDoor = findViewById(R.id.btnCloseDoor);
        btnQueryDoorStatus = findViewById(R.id.btnQueryDoorStatus);

        // Sos ve süsleme dozaj ayarları
        etSauce1Dosage = findViewById(R.id.etSauce1Dosage);
        etSauce2Dosage = findViewById(R.id.etSauce2Dosage);
        etSauce3Dosage = findViewById(R.id.etSauce3Dosage);
        etTopping1Dosage = findViewById(R.id.etTopping1Dosage);
        etTopping2Dosage = findViewById(R.id.etTopping2Dosage);
        etTopping3Dosage = findViewById(R.id.etTopping3Dosage);

        // Makine bilgileri
        etMachineSerialNo = findViewById(R.id.etMachineSerialNo);
        etIoTNumber = findViewById(R.id.etIoTNumber);
        btnGenerateMachineInfo = findViewById(R.id.btnGenerateMachineInfo);

        // Çalışma modu seçeneklerini ayarla (SDK'dan - TEK mod)
        String[] workModes = { "00 - Durdur", "01 - Çözme", "02 - Temizlik", "03 - Malzeme Ekleme", "04 - Koruma",
                "05 - Dondurma Yapma" };
        ArrayAdapter<String> workModeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                workModes);
        workModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnWorkMode.setAdapter(workModeAdapter);
    }

    private void setupClickListeners() {
        btnSaveParameters.setOnClickListener(v -> saveParameters());
        btnBack.setOnClickListener(v -> finish());
        btnTestParameters.setOnClickListener(v -> testParameters());
        btnQueryStatus.setOnClickListener(v -> queryMachineStatus());

        // Parametre ayarları
        btnQueryParam.setOnClickListener(v -> queryParameter());
        btnSetParam.setOnClickListener(v -> setParameter());
        btnQueryAllParams.setOnClickListener(v -> queryAllParameters());

        // Kapı kontrolü
        btnOpenDoor.setOnClickListener(v -> openDoor());
        btnCloseDoor.setOnClickListener(v -> closeDoor());
        btnQueryDoorStatus.setOnClickListener(v -> queryDoorStatus());

        // Makine bilgileri üretme
        btnGenerateMachineInfo.setOnClickListener(v -> generateMachineInfo());

        // Çalışma modu değişikliklerini dinle - TEK mod
        spnWorkMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TEK çalışma modunu SDK'ya gönder
                setWorkMode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupParameterSettings() {
        // Parametre ayarları için gerekli ayarları yap
        if (etParamAddress != null) {
            etParamAddress.setHint("0");
            etParamAddress.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        }

        if (etParamValue != null) {
            etParamValue.setHint("0");
            etParamValue.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        }

        // Parametre tipi seçeneklerini güncelle
        if (spnParamType != null) {
            String[] paramTypes = {
                    "0 - Genel",
                    "1 - Akım Ayarları",
                    "2 - Pozisyon Ayarları",
                    "3 - Zaman Ayarları",
                    "4 - Dondurma Makinesi",
                    "5 - Sistem Parametreleri",
                    "6 - Güvenlik Ayarları"
            };
            ArrayAdapter<String> paramTypeAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, paramTypes);
            paramTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnParamType.setAdapter(paramTypeAdapter);
        }
    }

    private void loadCurrentParameters() {
        // Dondurma seviyeleri - SOL/SAĞ doğru
        int leftIceLevel = sharedPreferences.getInt("left_ice_level", 5);
        int rightIceLevel = sharedPreferences.getInt("right_ice_level", 5);
        etLeftIceLevel.setText(String.valueOf(leftIceLevel));
        etRightIceLevel.setText(String.valueOf(rightIceLevel));

        // Sıcaklıklar - TEK makine, iki hazne
        int upperTankTemp = sharedPreferences.getInt("upper_tank_temp", 4); // Üst hazne 4°C
        int lowerTankTemp = sharedPreferences.getInt("lower_tank_temp", 2); // Alt hazne dondurma yapma
        int fridgeTemp = sharedPreferences.getInt("fridge_temp", 4);
        int freezerTemp = sharedPreferences.getInt("freezer_temp", -18);

        etUpperTankTemp.setText(String.valueOf(upperTankTemp));
        etLowerTankTemp.setText(String.valueOf(lowerTankTemp));
        etFridgeTemp.setText(String.valueOf(fridgeTemp));
        etFreezerTemp.setText(String.valueOf(freezerTemp));

        // Çalışma modu - TEK mod
        int workMode = sharedPreferences.getInt("work_mode", 5);
        spnWorkMode.setSelection(workMode);

        // Sos ve süsleme dozaj ayarları
        int sauce1Dosage = sharedPreferences.getInt("sauce1_dosage", 15);
        int sauce2Dosage = sharedPreferences.getInt("sauce2_dosage", 15);
        int sauce3Dosage = sharedPreferences.getInt("sauce3_dosage", 15);
        int topping1Dosage = sharedPreferences.getInt("topping1_dosage", 8);
        int topping2Dosage = sharedPreferences.getInt("topping2_dosage", 8);
        int topping3Dosage = sharedPreferences.getInt("topping3_dosage", 8);

        etSauce1Dosage.setText(String.valueOf(sauce1Dosage));
        etSauce2Dosage.setText(String.valueOf(sauce2Dosage));
        etSauce3Dosage.setText(String.valueOf(sauce3Dosage));
        etTopping1Dosage.setText(String.valueOf(topping1Dosage));
        etTopping2Dosage.setText(String.valueOf(topping2Dosage));
        etTopping3Dosage.setText(String.valueOf(topping3Dosage));

        // Makine bilgileri
        String machineSerialNo = sharedPreferences.getString("machine_serial_no", "");
        String iotNumber = sharedPreferences.getString("iot_number", "");

        if (!machineSerialNo.isEmpty()) {
            etMachineSerialNo.setText(machineSerialNo);
        }
        if (!iotNumber.isEmpty()) {
            etIoTNumber.setText(iotNumber);
        }
    }

    private void saveParameters() {
        try {
            // Dondurma seviyeleri - SOL/SAĞ doğru
            int leftIceLevel = Integer.parseInt(etLeftIceLevel.getText().toString());
            int rightIceLevel = Integer.parseInt(etRightIceLevel.getText().toString());

            // Sıcaklıklar - TEK makine, iki hazne
            int upperTankTemp = Integer.parseInt(etUpperTankTemp.getText().toString());
            int lowerTankTemp = Integer.parseInt(etLowerTankTemp.getText().toString());
            int fridgeTemp = Integer.parseInt(etFridgeTemp.getText().toString());
            int freezerTemp = Integer.parseInt(etFreezerTemp.getText().toString());

            // Çalışma modu - TEK mod
            int workMode = spnWorkMode.getSelectedItemPosition();

            // Sos ve süsleme dozaj ayarları
            int sauce1Dosage = Integer.parseInt(etSauce1Dosage.getText().toString());
            int sauce2Dosage = Integer.parseInt(etSauce2Dosage.getText().toString());
            int sauce3Dosage = Integer.parseInt(etSauce3Dosage.getText().toString());
            int topping1Dosage = Integer.parseInt(etTopping1Dosage.getText().toString());
            int topping2Dosage = Integer.parseInt(etTopping2Dosage.getText().toString());
            int topping3Dosage = Integer.parseInt(etTopping3Dosage.getText().toString());

            // Değerleri doğrula
            if (leftIceLevel < 1 || leftIceLevel > 15) {
                showToast("Sol dondurma seviyesi 1-15 arasında olmalı!");
                return;
            }
            if (rightIceLevel < 1 || rightIceLevel > 15) {
                showToast("Sağ dondurma seviyesi 1-15 arasında olmalı!");
                return;
            }
            if (upperTankTemp < 2 || upperTankTemp > 6) {
                showToast("Üst hazne sıcaklığı 2-6°C arasında olmalı!");
                return;
            }
            if (lowerTankTemp < -1 || lowerTankTemp > 6) {
                showToast("Alt hazne sıcaklığı -1°C ile +6°C arasında olmalı!");
                return;
            }
            if (fridgeTemp < 3 || fridgeTemp > 6) {
                showToast("Buzdolabı sıcaklığı 3-6°C arasında olmalı!");
                return;
            }
            if (freezerTemp < -30 || freezerTemp > 10) {
                showToast("Dondurucu sıcaklığı -30°C ile +10°C arasında olmalı!");
                return;
            }

            // Dozaj validasyonları
            if (sauce1Dosage < 5 || sauce1Dosage > 50) {
                showToast("Sos 1 dozajı 5-50 ml arasında olmalı!");
                return;
            }
            if (sauce2Dosage < 5 || sauce2Dosage > 50) {
                showToast("Sos 2 dozajı 5-50 ml arasında olmalı!");
                return;
            }
            if (sauce3Dosage < 5 || sauce3Dosage > 50) {
                showToast("Sos 3 dozajı 5-50 ml arasında olmalı!");
                return;
            }
            if (topping1Dosage < 2 || topping1Dosage > 20) {
                showToast("Süsleme 1 dozajı 2-20 g arasında olmalı!");
                return;
            }
            if (topping2Dosage < 2 || topping2Dosage > 20) {
                showToast("Süsleme 2 dozajı 2-20 g arasında olmalı!");
                return;
            }
            if (topping3Dosage < 2 || topping3Dosage > 20) {
                showToast("Süsleme 3 dozajı 2-20 g arasında olmalı!");
                return;
            }

            // SDK'ya gönder
            setIceMakeParameters(leftIceLevel, rightIceLevel, upperTankTemp, lowerTankTemp, fridgeTemp, freezerTemp);

            // SharedPreferences'a kaydet
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("left_ice_level", leftIceLevel);
            editor.putInt("right_ice_level", rightIceLevel);
            editor.putInt("upper_tank_temp", upperTankTemp);
            editor.putInt("lower_tank_temp", lowerTankTemp);
            editor.putInt("fridge_temp", fridgeTemp);
            editor.putInt("freezer_temp", freezerTemp);
            editor.putInt("work_mode", workMode);

            // Dozaj ayarlarını kaydet
            editor.putInt("sauce1_dosage", sauce1Dosage);
            editor.putInt("sauce2_dosage", sauce2Dosage);
            editor.putInt("sauce3_dosage", sauce3Dosage);
            editor.putInt("topping1_dosage", topping1Dosage);
            editor.putInt("topping2_dosage", topping2Dosage);
            editor.putInt("topping3_dosage", topping3Dosage);

            editor.apply();

            showToast("Parametreler başarıyla kaydedildi!");
        } catch (NumberFormatException e) {
            showToast("Lütfen tüm alanları sayısal değerlerle doldurun!");
        }
    }

    /**
     * Çalışma modunu ayarla - TEK mod
     * 
     * @param workMode Çalışma modu (0-5)
     */
    private void setWorkMode(int workMode) {
        if (sdkHelper != null && sdkHelper.isSDKConnected()) {
            try {
                // SDK Integration Helper ile çalışma modunu ayarla
                if (sdkHelper.setWorkMode(workMode)) {
                    // SharedPreferences'a kaydet
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("work_mode", workMode);
                    editor.apply();

                    Log.i(TAG, "Çalışma modu ayarlandı: " + workMode);
                    showToast("Çalışma modu ayarlandı: " + workMode);
                } else {
                    showToast("Çalışma modu ayarlanamadı!");
                }

            } catch (Exception e) {
                Log.e(TAG, "Çalışma modu ayarlama hatası: " + e.getMessage());
                showToast("Çalışma modu ayarlanamadı: " + e.getMessage());
            }
        } else {
            showToast("SDK bağlantısı kurulamadı! Lütfen bağlantıyı kontrol edin.");
        }
    }

    /**
     * Dondurma parametrelerini ayarla - TEK makine, iki hazne
     */
    private void setIceMakeParameters(int leftIceLevel, int rightIceLevel, int upperTankTemp, int lowerTankTemp,
            int fridgeTemp, int freezerTemp) {
        if (sdkHelper != null && sdkHelper.isSDKConnected()) {
            try {
                // SDK Integration Helper ile parametreleri ayarla
                if (sdkHelper.setMachineParameters(leftIceLevel, rightIceLevel, upperTankTemp, lowerTankTemp,
                        fridgeTemp, freezerTemp)) {
                    Log.i(TAG, String.format(
                            "Dondurma parametreleri gönderildi: Sol=%d, Sağ=%d, Üst=%d°C, Alt=%d°C, Buzdolabı=%d°C, Dondurucu=%d°C",
                            leftIceLevel, rightIceLevel, upperTankTemp, lowerTankTemp, fridgeTemp, freezerTemp));

                    showToast("Dondurma parametreleri makineye gönderildi!");

                    // Parametreleri SharedPreferences'a kaydet
                    saveParametersToPreferences(leftIceLevel, rightIceLevel, upperTankTemp, lowerTankTemp, fridgeTemp,
                            freezerTemp);
                } else {
                    showToast("Parametreler gönderilemedi!");
                }

            } catch (Exception e) {
                Log.e(TAG, "Dondurma parametreleri gönderme hatası: " + e.getMessage());
                showToast("Parametreler gönderilemedi: " + e.getMessage());
            }
        } else {
            showToast("SDK bağlantısı kurulamadı! Lütfen bağlantıyı kontrol edin.");
        }
    }

    /**
     * Makine durumunu sorgula
     */
    private void queryMachineStatus() {
        if (sdkHelper != null && sdkHelper.isSDKConnected()) {
            if (sdkHelper.queryMachineStatus()) {
                showToast("Makine durumu sorgulanıyor...");
            } else {
                showToast("Makine durumu sorgulanamadı!");
            }
        } else {
            showToast("SDK bağlantısı kurulamadı!");
        }
    }

    /**
     * Parametreleri test et
     */
    private void testParameters() {
        if (sdkHelper != null && sdkHelper.isSDKConnected()) {
            // Test komutları gönder
            if (sdkHelper.queryMachineStatus()) {
                showToast("Makine test komutları gönderildi!");
            } else {
                showToast("Test komutları gönderilemedi!");
            }
        } else {
            showToast("SDK bağlantısı kurulamadı!");
        }
    }

    /**
     * Belirli bir parametreyi sorgula
     */
    private void queryParameter() {
        try {
            int address = Integer.parseInt(etParamAddress.getText().toString());
            int paramType = spnParamType.getSelectedItemPosition();

            if (sdkHelper != null && sdkHelper.isSDKConnected()) {
                // SDK Integration Helper ile parametre sorgula
                if (sdkHelper.queryMachineParameters()) {
                    showToast("Parametre sorgulanıyor: Adres=" + address + ", Tip=" + paramType);
                } else {
                    showToast("Parametre sorgulanamadı!");
                }
            } else {
                showToast("SDK bağlantısı kurulamadı!");
            }
        } catch (NumberFormatException e) {
            showToast("Lütfen geçerli bir parametre adresi girin!");
        }
    }

    /**
     * Belirli bir parametreyi ayarla
     */
    private void setParameter() {
        try {
            int address = Integer.parseInt(etParamAddress.getText().toString());
            int paramType = spnParamType.getSelectedItemPosition();
            int value = Integer.parseInt(etParamValue.getText().toString());

            if (sdkHelper != null && sdkHelper.isSDKConnected()) {
                // SDK Integration Helper ile parametre ayarla
                showToast("Parametre ayarlanıyor: Adres=" + address + ", Tip=" + paramType + ", Değer=" + value);
                // Not: Bu metod SDK Integration Helper'da henüz implement edilmedi
            } else {
                showToast("SDK bağlantısı kurulamadı!");
            }
        } catch (NumberFormatException e) {
            showToast("Lütfen geçerli değerler girin!");
        }
    }

    /**
     * Tüm parametreleri sorgula
     */
    private void queryAllParameters() {
        try {
            int paramType = spnParamType.getSelectedItemPosition();

            if (sdkHelper != null && sdkHelper.isSDKConnected()) {
                // SDK Integration Helper ile tüm parametreleri sorgula
                if (sdkHelper.queryMachineParameters()) {
                    showToast("Tüm parametreler sorgulanıyor: Tip=" + paramType);
                } else {
                    showToast("Parametreler sorgulanamadı!");
                }
            } else {
                showToast("SDK bağlantısı kurulamadı!");
            }
        } catch (Exception e) {
            showToast("Parametre sorgulama hatası: " + e.getMessage());
        }
    }

    /**
     * Elektronik kilitli kapıyı aç
     */
    private void openDoor() {
        if (sdkHelper != null && sdkHelper.isSDKConnected()) {
            // SDK Integration Helper ile kapıyı aç
            if (sdkHelper.controlDoor(1, true)) {
                showToast("🚪 Kapı açma komutu gönderildi!");
            } else {
                showToast("Kapı açma komutu gönderilemedi!");
            }
        } else {
            showToast("SDK bağlantısı kurulamadı!");
        }
    }

    /**
     * Elektronik kilitli kapıyı kapat
     */
    private void closeDoor() {
        if (sdkHelper != null && sdkHelper.isSDKConnected()) {
            // SDK Integration Helper ile kapıyı kapat
            if (sdkHelper.controlDoor(1, false)) {
                showToast("🔒 Kapı kapatma komutu gönderildi!");
            } else {
                showToast("Kapı kapatma komutu gönderilemedi!");
            }
        } else {
            showToast("SDK bağlantısı kurulamadı!");
        }
    }

    /**
     * Kapı durumunu sorgula
     */
    private void queryDoorStatus() {
        if (sdkHelper != null && sdkHelper.isSDKConnected()) {
            // SDK Integration Helper ile makine durumunu sorgula
            if (sdkHelper.queryMachineStatus()) {
                showToast("🔍 Kapı durumu sorgulanıyor...");
            } else {
                showToast("Kapı durumu sorgulanamadı!");
            }
        } else {
            showToast("SDK bağlantısı kurulamadı!");
        }
    }

    /**
     * Makine seri no ve IoT numarasını otomatik üret
     */
    private void generateMachineInfo() {
        try {
            // Benzersiz makine seri no üret (timestamp + random)
            long timestamp = System.currentTimeMillis();
            int random = (int) (Math.random() * 10000);
            String machineSerialNo = "DOGUS-" + timestamp + "-" + String.format("%04d", random);

            // IoT numarası üret (MAC adresi benzeri)
            String iotNumber = generateIoTNumber();

            // UI'ya set et
            etMachineSerialNo.setText(machineSerialNo);
            etIoTNumber.setText(iotNumber);

            // SharedPreferences'a kaydet
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("machine_serial_no", machineSerialNo);
            editor.putString("iot_number", iotNumber);
            editor.apply();

            showToast("🔧 Makine bilgileri üretildi ve kaydedildi!");

        } catch (Exception e) {
            showToast("Makine bilgileri üretilirken hata: " + e.getMessage());
        }
    }

    /**
     * IoT numarası üret (MAC adresi formatında)
     */
    private String generateIoTNumber() {
        StringBuilder iotNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (i > 0)
                iotNumber.append(":");
            int hex = (int) (Math.random() * 256);
            iotNumber.append(String.format("%02X", hex));
        }
        return iotNumber.toString();
    }

    /**
     * Parametreleri SharedPreferences'a kaydeder
     */
    private void saveParametersToPreferences(int leftIceLevel, int rightIceLevel, int upperTankTemp, int lowerTankTemp,
            int fridgeTemp, int freezerTemp) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("left_ice_level", leftIceLevel);
            editor.putInt("right_ice_level", rightIceLevel);
            editor.putInt("upper_tank_temp", upperTankTemp);
            editor.putInt("lower_tank_temp", lowerTankTemp);
            editor.putInt("fridge_temp", fridgeTemp);
            editor.putInt("freezer_temp", freezerTemp);
            editor.apply();

            Log.i(TAG, "Parametreler SharedPreferences'a kaydedildi");
        } catch (Exception e) {
            Log.e(TAG, "Parametre kaydetme hatası: " + e.getMessage());
        }
    }

    /**
     * Durum göstergesini günceller
     */
    private void updateStatusIndicator(String status) {
        // Bu metod UI'daki durum göstergesini günceller
        if (sdkHelper != null && sdkHelper.isSDKConnected()) {
            // Başarılı bağlantı göstergesi
            Log.i(TAG, "Makine durumu güncellendi: " + status);
        } else {
            // Bağlantı sorunu göstergesi
            Log.w(TAG, "Makine durumu güncellendi - Bağlantı sorunu");
        }
    }

    /**
     * SDK'dan gelen parametreleri işler
     */
    public void onParameterReceived(IceMakeParamBean paramBean) {
        if (paramBean != null) {
            uiHandler.post(() -> {
                try {
                    // SDK'dan gelen parametreleri UI'ya aktar
                    updateUIWithMachineParameters(paramBean);

                    Log.i(TAG, "Makine parametreleri alındı ve UI güncellendi");

                } catch (Exception e) {
                    Log.e(TAG, "UI güncelleme hatası: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Makine parametrelerini UI'ya aktarır
     */
    private void updateUIWithMachineParameters(IceMakeParamBean paramBean) {
        try {
            // Sıcaklık parametrelerini güncelle
            if (paramBean.getRefriTemp() != -1) {
                etFridgeTemp.setText(String.valueOf(paramBean.getRefriTemp()));
            }
            if (paramBean.getRefriFreezTemp() != -1) {
                etFreezerTemp.setText(String.valueOf(paramBean.getRefriFreezTemp()));
            }
            if (paramBean.getRefriCylinderTemp1() != -1) {
                etUpperTankTemp.setText(String.valueOf(paramBean.getRefriCylinderTemp1()));
            }
            if (paramBean.getRefriCylinderTemp2() != -1) {
                etLowerTankTemp.setText(String.valueOf(paramBean.getRefriCylinderTemp2()));
            }

            // Çalışma modlarını güncelle
            if (paramBean.getRefri1WorkMode() != -1) {
                // Sol sistem çalışma modu
                Log.i(TAG, "Sol sistem çalışma modu: " + paramBean.getRefri1WorkMode());
            }
            if (paramBean.getRefri2WorkMode() != -1) {
                // Sağ sistem çalışma modu
                Log.i(TAG, "Sağ sistem çalışma modu: " + paramBean.getRefri2WorkMode());
            }

            // Hata durumlarını kontrol et
            checkMachineFaults(paramBean);

        } catch (Exception e) {
            Log.e(TAG, "UI parametre güncelleme hatası: " + e.getMessage());
        }
    }

    /**
     * Makine hatalarını kontrol eder
     */
    private void checkMachineFaults(IceMakeParamBean paramBean) {
        try {
            // Sol sistem hataları
            if (paramBean.getRefriFault1() != -1 && paramBean.getRefriFault1() != 0) {
                String faultMessage = getFaultMessage(paramBean.getRefriFault1(), "Sol");
                showToast("⚠️ " + faultMessage);
                Log.w(TAG, "Sol sistem hatası: " + faultMessage);
            }

            // Sağ sistem hataları
            if (paramBean.getRefriFault2() != -1 && paramBean.getRefriFault2() != 0) {
                String faultMessage = getFaultMessage(paramBean.getRefriFault2(), "Sağ");
                showToast("⚠️ " + faultMessage);
                Log.w(TAG, "Sağ sistem hatası: " + faultMessage);
            }

            // Genel makine hataları
            if (paramBean.getMachineFault() != -1 && paramBean.getMachineFault() != 0) {
                String machineFaultMessage = getMachineFaultMessage(paramBean.getMachineFault());
                showToast("🚨 " + machineFaultMessage);
                Log.e(TAG, "Genel makine hatası: " + machineFaultMessage);
            }

        } catch (Exception e) {
            Log.e(TAG, "Hata kontrolü hatası: " + e.getMessage());
        }
    }

    /**
     * Hata mesajını döndürür
     */
    private String getFaultMessage(int faultCode, String system) {
        switch (faultCode) {
            case 1:
                return system + " motor tıkanması";
            case 2:
                return system + " motor kayış kayması";
            case 3:
                return system + " limit switch hatası";
            case 4:
                return system + " soğutma zaman aşımı";
            case 5:
                return system + " malzeme eksikliği";
            case 6:
                return system + " kompresör yüksek basınç koruması";
            default:
                return system + " bilinmeyen hata: " + faultCode;
        }
    }

    /**
     * Genel makine hata mesajını döndürür
     */
    private String getMachineFaultMessage(int faultCode) {
        switch (faultCode) {
            case 1:
                return "Kompresör yüksek basınç koruması";
            case 2:
                return "Voltaj çok yüksek";
            case 3:
                return "Voltaj çok düşük";
            case 4:
                return "Orta limit switch hatası";
            case 5:
                return "Buzdolabı soğutma sistemi hatası";
            case 6:
                return "Buzdolabı dondurucu sistemi hatası";
            case 7:
                return "Buzdolabı sıcaklık sensörü hatası";
            case 8:
                return "Dondurucu sıcaklık sensörü hatası";
            default:
                return "Bilinmeyen makine hatası: " + faultCode;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
