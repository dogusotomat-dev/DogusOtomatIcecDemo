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
    private TextView tvBalance;
    private TextView tvTemperature;
    private TextView tvSelectedToppings;
    private TextView tvTotalPrice;
    private Button btnAdmin;
    private Button btnSettings;
    private Button btnConfirmOrder;
    private Button btnClearSelection;
    private RecyclerView rvToppings;
    private RecyclerView rvSauces;
    
    private ToppingAdapter toppingAdapter;
    private SauceAdapter sauceAdapter;
    
    // TCN SDK
    private TcnVendIF tcnVendIF;
    private VendProtoControl vendProtoControl;
    private boolean isConnected = false;
    
    // Seçilen ürünler
    private List<ToppingItem> selectedToppings = new ArrayList<>();
    private List<SauceItem> selectedSauces = new ArrayList<>();
    private double basePrice = 5.00; // Temel dondurma fiyatı
    
    private Handler mainHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            handleVendMessage(msg);
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "MainAct onCreate started");

        initializeViews();
        initializeTCNSDK();
        setupClickListeners();
        loadToppingsAndSauces();

        Log.i(TAG, "MainAct onCreate completed successfully");
    }

    private void initializeViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvBalance = findViewById(R.id.tv_balance);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvSelectedToppings = findViewById(R.id.tv_selected_toppings);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnAdmin = findViewById(R.id.btn_admin);
        btnSettings = findViewById(R.id.btn_settings);
        btnConfirmOrder = findViewById(R.id.btn_confirm_order);
        btnClearSelection = findViewById(R.id.btn_clear_selection);
        rvToppings = findViewById(R.id.rv_toppings);
        rvSauces = findViewById(R.id.rv_sauces);

        // Toppings RecyclerView
        rvToppings.setLayoutManager(new GridLayoutManager(this, 3));
        toppingAdapter = new ToppingAdapter(new ArrayList<>(), new ToppingAdapter.OnToppingClickListener() {
            @Override
            public void onToppingClick(ToppingItem topping) {
                handleToppingSelection(topping);
            }
        });
        rvToppings.setAdapter(toppingAdapter);

        // Sauces RecyclerView
        rvSauces.setLayoutManager(new GridLayoutManager(this, 3));
        sauceAdapter = new SauceAdapter(new ArrayList<>(), new SauceAdapter.OnSauceClickListener() {
            @Override
            public void onSauceClick(SauceItem sauce) {
                handleSauceSelection(sauce);
            }
        });
        rvSauces.setAdapter(sauceAdapter);

        updateStatus("Sistem başlatılıyor...");
        updateTotalPrice();
    }

    private void initializeTCNSDK() {
        try {
            tcnVendIF = TcnVendIF.getInstance();
            tcnVendIF.init(this);
            tcnVendIF.registerListener(this);
            
            // TCN Service başlat
            Intent serviceIntent = new Intent(this, TcnService.class);
            startService(serviceIntent);
            
            // VendProtoControl başlat
            vendProtoControl = VendProtoControl.getInstance();
            vendProtoControl.initialize(
                "icec_3", "NONE", "NONE", "NONE",
                "1", "NONE", "NONE", "NONE",
                mainHandler
            );
            
            updateStatus("TCN SDK başlatıldı");
        } catch (Exception e) {
            Log.e(TAG, "TCN SDK başlatma hatası: " + e.getMessage(), e);
            updateStatus("TCN SDK başlatma hatası: " + e.getMessage());
        }
    }

    private void setupClickListeners() {
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainAct.this, AdminLoginActivity.class);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainAct.this, MachineSettingsActivity.class);
                startActivity(intent);
            }
        });

        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });

        btnClearSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelection();
            }
        });
    }

    private void loadToppingsAndSauces() {
        // Ürün ayarlarından sos ve süsleme bilgilerini yükle
        SharedPreferences prefs = getSharedPreferences("ProductPrefs", MODE_PRIVATE);
        
        // Süslemeler
        List<ToppingItem> toppings = new ArrayList<>();
        toppings.add(new ToppingItem("Çikolata Parçaları", 1.50, prefs.getBoolean("topping_chocolate", true)));
        toppings.add(new ToppingItem("Fındık", 2.00, prefs.getBoolean("topping_hazelnut", true)));
        toppings.add(new ToppingItem("Meyve", 1.75, prefs.getBoolean("topping_fruit", true)));
        toppingAdapter.updateToppings(toppings);
        
        // Soslar
        List<SauceItem> sauces = new ArrayList<>();
        sauces.add(new SauceItem("Çikolata Sosu", 1.00, prefs.getBoolean("sauce_chocolate", true)));
        sauces.add(new SauceItem("Karamel Sosu", 1.25, prefs.getBoolean("sauce_caramel", true)));
        sauces.add(new SauceItem("Meyve Sosu", 1.50, prefs.getBoolean("sauce_fruit", true)));
        sauceAdapter.updateSauces(sauces);
        
        updateStatus("Ürün listesi yüklendi");
    }

    private void handleToppingSelection(ToppingItem topping) {
        if (topping.isAvailable()) {
            if (selectedToppings.contains(topping)) {
                selectedToppings.remove(topping);
                showToast("Süsleme kaldırıldı: " + topping.getName());
            } else {
                selectedToppings.add(topping);
                showToast("Süsleme eklendi: " + topping.getName());
            }
            updateSelectedItems();
            updateTotalPrice();
        } else {
            showToast("Bu süsleme şu anda mevcut değil");
        }
    }

    private void handleSauceSelection(SauceItem sauce) {
        if (sauce.isAvailable()) {
            if (selectedSauces.contains(sauce)) {
                selectedSauces.remove(sauce);
                showToast("Sos kaldırıldı: " + sauce.getName());
            } else {
                selectedSauces.add(sauce);
                showToast("Sos eklendi: " + sauce.getName());
            }
            updateSelectedItems();
            updateTotalPrice();
        } else {
            showToast("Bu sos şu anda mevcut değil");
        }
    }

    private void updateSelectedItems() {
        StringBuilder toppingsText = new StringBuilder("Seçilen Süslemeler: ");
        if (selectedToppings.isEmpty()) {
            toppingsText.append("Yok");
        } else {
            for (ToppingItem topping : selectedToppings) {
                toppingsText.append(topping.getName()).append(", ");
            }
            toppingsText.setLength(toppingsText.length() - 2); // Son virgülü kaldır
        }
        tvSelectedToppings.setText(toppingsText.toString());
    }

    private void updateTotalPrice() {
        double total = basePrice;
        
        for (ToppingItem topping : selectedToppings) {
            total += topping.getPrice();
        }
        
        for (SauceItem sauce : selectedSauces) {
            total += sauce.getPrice();
        }
        
        tvTotalPrice.setText(String.format("Toplam: ₺%.2f", total));
    }

    private void confirmOrder() {
        if (!isConnected) {
            showToast("Önce makineye bağlanın");
            return;
        }
        
        if (selectedToppings.isEmpty() && selectedSauces.isEmpty()) {
            showToast("Lütfen en az bir süsleme veya sos seçin");
            return;
        }
        
        try {
            updateStatus("Sipariş hazırlanıyor...");
            
            // TCN SDK ile sipariş gönder
            String tradeNo = "TRADE_" + System.currentTimeMillis();
            
            // Süsleme ve sos bilgilerini TCN formatında hazırla
            int toppingType = 0, sauceType = 0;
            int toppingQty = 0, sauceQty = 0;
            
            if (!selectedToppings.isEmpty()) {
                toppingType = 1; // Süsleme var
                toppingQty = selectedToppings.size();
            }
            
            if (!selectedSauces.isEmpty()) {
                sauceType = 1; // Sos var
                sauceQty = selectedSauces.size();
            }
            
            vendProtoControl.ship(
                1, // Slot numarası
                PayMethod.PAYMETHED_CASH,
                tradeNo,
                30, // Heat time
                String.valueOf(getTotalPrice()),
                "1", // Ana malzeme (dondurma)
                String.valueOf(toppingType), // Süsleme tipi
                String.valueOf(sauceType), // Sos tipi
                "50", // Ana malzeme miktarı
                String.valueOf(toppingQty * 10), // Süsleme miktarı
                String.valueOf(sauceQty * 15) // Sos miktarı
            );
            
            showToast("Sipariş gönderildi!");
            updateStatus("Sipariş hazırlanıyor...");
            
        } catch (Exception e) {
            Log.e(TAG, "Sipariş hatası: " + e.getMessage(), e);
            updateStatus("Sipariş hatası: " + e.getMessage());
            showToast("Sipariş gönderilemedi");
        }
    }

    private void clearSelection() {
        selectedToppings.clear();
        selectedSauces.clear();
        updateSelectedItems();
        updateTotalPrice();
        showToast("Seçimler temizlendi");
    }

    private double getTotalPrice() {
        double total = basePrice;
        for (ToppingItem topping : selectedToppings) {
            total += topping.getPrice();
        }
        for (SauceItem sauce : selectedSauces) {
            total += sauce.getPrice();
        }
        return total;
    }

    private void handleVendMessage(Message msg) {
        switch (msg.what) {
            case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:
                runOnUiThread(() -> {
                    updateStatus("Dondurma başarıyla hazırlandı!");
                    showToast("Siparişiniz hazır!");
                    clearSelection(); // Seçimleri temizle
                });
                break;
            case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:
                runOnUiThread(() -> {
                    updateStatus("Dondurma hazırlanamadı!");
                    showToast("Sipariş hazırlanamadı");
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
                    tvTemperature.setText("Sıcaklık: " + msg.arg1 + "°C");
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
                    updateStatus("Dondurma başarıyla hazırlandı!");
                    showToast("Siparişiniz hazır!");
                    clearSelection();
                });
                break;
            case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:
                runOnUiThread(() -> {
                    updateStatus("Dondurma hazırlanamadı! Hata kodu: " + eventInfo.GetlParam1());
                    showToast("Sipariş hazırlanamadı");
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
                    tvTemperature.setText("Sıcaklık: " + eventInfo.GetlParam1() + "°C");
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

    @Override
    protected void onResume() {
        super.onResume();
        // Ürün ayarlarından güncel bilgileri yükle
        loadToppingsAndSauces();
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
            if (tcnVendIF != null) {
                tcnVendIF.unregisterListener(this);
                tcnVendIF.stopWorkThread();
            }
            Intent serviceIntent = new Intent(this, TcnService.class);
            stopService(serviceIntent);
        } catch (Exception e) {
            Log.e(TAG, "Cleanup hatası: " + e.getMessage(), e);
        }
    }
}
