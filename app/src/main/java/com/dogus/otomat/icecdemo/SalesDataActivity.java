package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SalesDataActivity extends AppCompatActivity {

    private TextView tvSalesData;
    private Button btnBack;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_data);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        initializeViews();
        loadSalesData();
        setupClickListeners();
    }

    private void initializeViews() {
        tvSalesData = findViewById(R.id.tvSalesData);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadSalesData() {
        int totalSales = sharedPreferences.getInt("total_sales", 0);
        float totalRevenue = sharedPreferences.getFloat("total_revenue", 0.0f);
        long lastSaleTime = sharedPreferences.getLong("last_sale_time", 0);

        StringBuilder salesData = new StringBuilder();
        salesData.append("📊 SATIŞ VERİLERİ\n\n");
        salesData.append("🛒 Toplam Satış: ").append(totalSales).append(" adet\n");
        salesData.append("💰 Toplam Gelir: ").append(String.format("%.2f", totalRevenue)).append(" TL\n");

        if (lastSaleTime > 0) {
            salesData.append("🕒 Son Satış: ")
                    .append(new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
                            .format(new java.util.Date(lastSaleTime)))
                    .append("\n");
        }

        salesData.append("\n📈 GÜNLÜK İSTATİSTİKLER\n");
        salesData.append("Bugün: ").append(sharedPreferences.getInt("today_sales", 0)).append(" satış\n");
        salesData.append("Bu Hafta: ").append(sharedPreferences.getInt("week_sales", 0)).append(" satış\n");
        salesData.append("Bu Ay: ").append(sharedPreferences.getInt("month_sales", 0)).append(" satış\n");

        tvSalesData.setText(salesData.toString());
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }
}
