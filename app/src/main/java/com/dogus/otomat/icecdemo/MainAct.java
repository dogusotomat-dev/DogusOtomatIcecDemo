package com.dogus.otomat.icecdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.tcn.icecboard.TcnService;
import com.tcn.icecboard.control.TcnVendIF;
import com.tcn.icecboard.control.TcnVendEventID;
import com.tcn.icecboard.control.TcnVendEventResultID;
import com.tcn.icecboard.control.VendEventInfo;
import com.tcn.icecboard.control.PayMethod;
import com.tcn.icecboard.DriveControl.VendProtoControl;

public class MainAct extends AppCompatActivity implements TcnVendIF.VendEventListener {
    private static final String TAG = "MainAct";

    // UI Components
    private TextView tvStatus;
    private Button btnAdmin;
    
    // TCN SDK
    private TcnVendIF tcnVendIF;
    private boolean isConnected = false;
    private boolean isMachineReady = false;

    // Payment manager
    private MDBPaymentManager paymentManager;
    
    // Advertisement manager
    private AdvertisementManager advertisementManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "MainAct onCreate started");
        
        initializeViews();
        initializeTCNSDK();
        initializeManagers();
        setupClickListeners();
            
        Log.i(TAG, "MainAct onCreate completed successfully");
        
        // Uygulama başlatıldığında doğrudan satış ekranını aç
        openSalesScreen();
    }
    
    private void initializeViews() {
        tvStatus = findViewById(R.id.tv_status);
        btnAdmin = findViewById(R.id.btn_admin);
        
        updateStatus("Sistem başlatılıyor...");
    }
    
    private void initializeTCNSDK() {
        try {
            // TCN SDK'yı initialize et
            tcnVendIF = TcnVendIF.getInstance();
            tcnVendIF.init(this);
            
            // Event listener'ı kaydet
            tcnVendIF.registerListener(this);
            
            // TCN Service'i başlat
            Intent serviceIntent = new Intent(this, TcnService.class);
            startService(serviceIntent);
            
            updateStatus("TCN SDK başlatıldı");
            
        } catch (Exception e) {
            Log.e(TAG, "TCN SDK başlatma hatası: " + e.getMessage(), e);
            updateStatus("TCN SDK başlatma hatası: " + e.getMessage());
        }
    }

    private void initializeManagers() {
        try {
            // Payment manager'ı başlat
            paymentManager = MDBPaymentManager.getInstance(this);
            paymentManager.setPaymentListener(new MDBPaymentManager.OnPaymentListener() {
                @Override
                public void onPaymentStarted(double amount, String paymentMethod) {
                    runOnUiThread(() -> {
                        updateStatus("Ödeme başlatıldı: " + amount + " TL (" + paymentMethod + ")");
                        showToast("Ödeme başlatıldı");
                    });
                }

                @Override
                public void onPaymentCompleted(double amount, String paymentMethod, String transactionId) {
                    runOnUiThread(() -> {
                        updateStatus("Ödeme tamamlandı: " + amount + " TL");
                        showToast("Ödeme başarılı");
                        
                        // Ödeme tamamlandıktan sonra ürün çıkar
                        testShipment();
                    });
                }

                @Override
                public void onPaymentFailed(String error) {
                    runOnUiThread(() -> {
                        updateStatus("Ödeme başarısız: " + error);
                        showToast("Ödeme başarısız: " + error);
                    });
                }

                @Override
                public void onPaymentCancelled() {
                    runOnUiThread(() -> {
                        updateStatus("Ödeme iptal edildi");
                        showToast("Ödeme iptal edildi");
                    });
                }
            });
            
            // Advertisement manager'ı başlat
            advertisementManager = AdvertisementManager.getInstance(this);
            
            Log.i(TAG, "Yöneticiler başlatıldı");
            
        } catch (Exception e) {
            Log.e(TAG, "Yönetici başlatma hatası: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Admin girişi için AdminLoginActivity'yi aç
                Intent intent = new Intent(MainAct.this, AdminLoginActivity.class);
                startActivity(intent);
            }
        });
    }
    
    private void openSalesScreen() {
        // Doğrudan satış ekranını aç
        Intent intent = new Intent(MainAct.this, SalesScreenActivity.class);
        startActivity(intent);
    }
    
    private void testShipment() {
        if (!isConnected || !isMachineReady) {
            showToast("Önce makineye bağlanın");
            return;
        }
        
        try {
            updateStatus("Test çıkarma başlatılıyor...");
            
            VendProtoControl vendProtoControl = VendProtoControl.getInstance();
            
            // Test ice cream shipment
            vendProtoControl.ship(
                1,                              // slot number
                PayMethod.PAYMETHED_CASH,       // payment method
                "TEST_" + System.currentTimeMillis(), // trade number
                30,                             // heat time
                "1.00",                         // amount
                "1",                            // zhuliao (main material)
                "1",                            // dingliao (topping)
                "1",                            // guojiang (sauce)
                "50",                           // zhuliao quantity
                "30",                           // dingliao quantity
                "20"                            // guojiang quantity
            );
            
            updateStatus("Test çıkarma komutu gönderildi");
            
        } catch (Exception e) {
            Log.e(TAG, "Test çıkarma hatası: " + e.getMessage(), e);
            updateStatus("Test çıkarma hatası: " + e.getMessage());
        }
    }
    
    private void queryMachineStatus() {
        if (!isConnected) {
            showToast("Önce makineye bağlanın");
            return;
        }
        
        try {
            updateStatus("Makine durumu sorgulanıyor...");
            
            VendProtoControl vendProtoControl = VendProtoControl.getInstance();
            vendProtoControl.reqQueryStatus(1); // Query status for group 1
            
        } catch (Exception e) {
            Log.e(TAG, "Durum sorgulama hatası: " + e.getMessage(), e);
            updateStatus("Durum sorgulama hatası: " + e.getMessage());
        }
    }
    
    private void queryMachineParameters() {
        if (!isConnected) {
            return;
        }
        
        try {
            updateStatus("Makine parametreleri sorgulanıyor...");
            
            VendProtoControl vendProtoControl = VendProtoControl.getInstance();
            vendProtoControl.reqQueryParamIceMake(); // Query ice cream machine parameters
            
        } catch (Exception e) {
            Log.e(TAG, "Parametre sorgulama hatası: " + e.getMessage(), e);
        }
    }
    
    private void handleVendMessage(Message msg) {
        switch (msg.what) {
            case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:
                runOnUiThread(() -> {
                    updateStatus("Ürün başarıyla çıkarıldı!");
                    showToast("Ürün çıkarma başarılı");
                });
                break;
                
            case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:
                runOnUiThread(() -> {
                    updateStatus("Ürün çıkarma başarısız!");
                    showToast("Ürün çıkarma başarısız");
                });
                break;
                
            case TcnVendEventID.COMMAND_SYSTEM_BUSY:
                runOnUiThread(() -> {
                    updateStatus("Sistem meşgul");
                    showToast("Sistem meşgul, lütfen bekleyin");
                });
                break;
                
            case TcnVendEventID.TEMPERATURE_INFO:
                runOnUiThread(() -> {
                    updateStatus("Sıcaklık bilgisi alındı: " + msg.arg1 + "°C");
                });
                break;
                
            default:
                Log.d(TAG, "Bilinmeyen mesaj: " + msg.what);
                break;
        }
    }
    
    @Override
    public void VendEvent(VendEventInfo eventInfo) {
        Log.d(TAG, "VendEvent alındı: " + eventInfo.GetEventID());
        
        switch (eventInfo.GetEventID()) {
            case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:
                runOnUiThread(() -> {
                    updateStatus("Ürün başarıyla çıkarıldı!");
                    showToast("Ürün çıkarma başarılı");
                });
                break;
                
            case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:
                runOnUiThread(() -> {
                    updateStatus("Ürün çıkarma başarısız! Hata kodu: " + eventInfo.GetlParam1());
                    showToast("Ürün çıkarma başarısız");
                });
                break;
                
            case TcnVendEventID.COMMAND_SYSTEM_BUSY:
                runOnUiThread(() -> {
                    updateStatus("Sistem meşgul");
                    showToast("Sistem meşgul");
                });
                break;
                
            case TcnVendEventID.TEMPERATURE_INFO:
                runOnUiThread(() -> {
                    updateStatus("Sıcaklık: " + eventInfo.GetlParam1() + "°C");
                });
                break;
                
            case TcnVendEventID.CMD_QUERY_STATUS_ICEC:
                runOnUiThread(() -> {
                    String status = "";
                    switch (eventInfo.GetlParam1()) {
                        case TcnVendEventResultID.STATUS_FREE:
                            status = "Boş";
                            isMachineReady = true;
                            break;
                        case TcnVendEventResultID.STATUS_BUSY:
                            status = "Meşgul";
                            isMachineReady = false;
                            break;
                        case TcnVendEventResultID.STATUS_FAULT:
                            status = "Arızalı";
                            isMachineReady = false;
                            break;
                        default:
                            status = "Bilinmeyen (" + eventInfo.GetlParam1() + ")";
                            isMachineReady = false;
                    }
                    updateStatus("Makine durumu: " + status);
                });
                break;
                
            case 0x1001: // RET_QUERY_PARAM_ICE_MAKE - assuming this value
                runOnUiThread(() -> {
                    updateStatus("Makine parametreleri alındı");
                    // Burada parametreleri işleyebiliriz
                });
                break;
                
            default:
                Log.d(TAG, "İşlenmeyen event: " + eventInfo.GetEventID());
                break;
        }
    }
    
    private void updateStatus(String status) {
        if (tvStatus != null) {
            tvStatus.setText(status);
        }
        Log.i(TAG, "Status: " + status);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        try {
            // TCN SDK temizleme
            if (tcnVendIF != null) {
                tcnVendIF.unregisterListener(this);
                tcnVendIF.stopWorkThread();
            }
            
            // Service'i durdur
            Intent serviceIntent = new Intent(this, TcnService.class);
            stopService(serviceIntent);
            
        } catch (Exception e) {
            Log.e(TAG, "Cleanup hatası: " + e.getMessage(), e);
        }
    }
}