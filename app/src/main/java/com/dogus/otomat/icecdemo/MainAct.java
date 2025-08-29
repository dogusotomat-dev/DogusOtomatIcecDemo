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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tcn.icecboard.TcnService;
import com.tcn.icecboard.control.TcnVendIF;
import com.tcn.icecboard.control.TcnVendEventID;
import com.tcn.icecboard.control.TcnVendEventResultID;
import com.tcn.icecboard.control.VendEventInfo;
import com.tcn.icecboard.control.PayMethod;
import com.tcn.icecboard.control.Coil_info;
import com.tcn.icecboard.DriveControl.VendProtoControl;

import java.util.ArrayList;
import java.util.List;

public class MainAct extends AppCompatActivity implements TcnVendIF.VendEventListener {
    private static final String TAG = "MainAct";

    // UI Components
    private TextView tvStatus;
    private Button btnTestShip;
    private Button btnQueryStatus;
    private Button btnConnect;
    
    // TCN SDK
    private TcnVendIF tcnVendIF;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "MainAct onCreate started");
        
        initializeViews();
        initializeTCNSDK();
        setupClickListeners();
            
            Log.i(TAG, "MainAct onCreate completed successfully");
    }
    
    private void initializeViews() {
        tvStatus = findViewById(R.id.tv_status);
        btnTestShip = findViewById(R.id.btn_test_ship);
        btnQueryStatus = findViewById(R.id.btn_query_status);
        btnConnect = findViewById(R.id.btn_connect);
        
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

    private void setupClickListeners() {
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToMachine();
            }
        });
        
        btnTestShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testShipment();
            }
        });
        
        btnQueryStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryMachineStatus();
            }
        });
    }
    
    private void connectToMachine() {
        try {
            updateStatus("Makineye bağlanıyor...");
            
            // TCN SDK ile makineye bağlan
            VendProtoControl vendProtoControl = VendProtoControl.getInstance();
            vendProtoControl.initialize(
                "icec_3",  // board1 - ice cream machine
                "NONE",    // board2
                "NONE",    // board3
                "NONE",    // board4
                "1",       // group1
                "NONE",    // group2
                "NONE",    // group3
                "NONE",    // group4
                new Handler() {
            @Override
                    public void handleMessage(Message msg) {
                        handleVendMessage(msg);
                    }
                }
            );
            
            vendProtoControl.setUnlock(true);
            
            isConnected = true;
            updateStatus("Makineye başarıyla bağlandı");
            
            // Butonları aktif et
            btnTestShip.setEnabled(true);
            btnQueryStatus.setEnabled(true);
            
        } catch (Exception e) {
            Log.e(TAG, "Makine bağlantı hatası: " + e.getMessage(), e);
            updateStatus("Makine bağlantı hatası: " + e.getMessage());
            isConnected = false;
        }
    }
    
    private void testShipment() {
        if (!isConnected) {
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
                            break;
                        case TcnVendEventResultID.STATUS_BUSY:
                            status = "Meşgul";
                            break;
                        case TcnVendEventResultID.STATUS_FAULT:
                            status = "Arızalı";
                            break;
                        default:
                            status = "Bilinmeyen (" + eventInfo.GetlParam1() + ")";
                    break;
                }
                    updateStatus("Makine durumu: " + status);
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
