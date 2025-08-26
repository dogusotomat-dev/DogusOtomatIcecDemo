package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class PriceSettingsActivity extends AppCompatActivity {

    // Temel fiyat
    private EditText etBasePrice;

    // Sos fiyatları
    private EditText etSauce1Price, etSauce2Price, etSauce3Price;

    // Süsleme fiyatları
    private EditText etTopping1Price, etTopping2Price, etTopping3Price;

    // Esnek seçim sistemi
    private Switch swFlexibleSelection;

    private Button btnSavePrices, btnBack;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_settings);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        initializeViews();
        loadCurrentPrices();
        setupClickListeners();
    }

    private void initializeViews() {
        etBasePrice = findViewById(R.id.etBasePrice);

        // Sos fiyatları
        etSauce1Price = findViewById(R.id.etSauce1Price);
        etSauce2Price = findViewById(R.id.etSauce2Price);
        etSauce3Price = findViewById(R.id.etSauce3Price);

        // Süsleme fiyatları
        etTopping1Price = findViewById(R.id.etTopping1Price);
        etTopping2Price = findViewById(R.id.etTopping2Price);
        etTopping3Price = findViewById(R.id.etTopping3Price);

        btnSavePrices = findViewById(R.id.btnSavePrices);
        btnBack = findViewById(R.id.btnBack);

        // Esnek seçim sistemi
        swFlexibleSelection = findViewById(R.id.swFlexibleSelection);
    }

    private void loadCurrentPrices() {
        // Temel fiyat
        float basePrice = sharedPreferences.getFloat("base_price", 8.0f);
        etBasePrice.setText(String.valueOf(basePrice));

        // Sos fiyatları
        float sauce1Price = sharedPreferences.getFloat("sauce1_price", 2.0f);
        float sauce2Price = sharedPreferences.getFloat("sauce2_price", 2.5f);
        float sauce3Price = sharedPreferences.getFloat("sauce3_price", 2.0f);
        etSauce1Price.setText(String.valueOf(sauce1Price));
        etSauce2Price.setText(String.valueOf(sauce2Price));
        etSauce3Price.setText(String.valueOf(sauce3Price));

        // Süsleme fiyatları
        float topping1Price = sharedPreferences.getFloat("topping1_price", 1.5f);
        float topping2Price = sharedPreferences.getFloat("topping2_price", 1.0f);
        float topping3Price = sharedPreferences.getFloat("topping3_price", 1.5f);
        etTopping1Price.setText(String.valueOf(topping1Price));
        etTopping2Price.setText(String.valueOf(topping2Price));
        etTopping3Price.setText(String.valueOf(topping3Price));

        // Esnek seçim sistemi durumu
        boolean flexibleSelection = sharedPreferences.getBoolean("flexible_selection", true);
        swFlexibleSelection.setChecked(flexibleSelection);
    }

    private void setupClickListeners() {
        btnSavePrices.setOnClickListener(v -> savePrices());
        btnBack.setOnClickListener(v -> finish());
    }

    private void savePrices() {
        try {
            // Temel fiyat
            float basePrice = Float.parseFloat(etBasePrice.getText().toString());

            // Sos fiyatları
            float sauce1Price = Float.parseFloat(etSauce1Price.getText().toString());
            float sauce2Price = Float.parseFloat(etSauce2Price.getText().toString());
            float sauce3Price = Float.parseFloat(etSauce3Price.getText().toString());

            // Süsleme fiyatları
            float topping1Price = Float.parseFloat(etTopping1Price.getText().toString());
            float topping2Price = Float.parseFloat(etTopping2Price.getText().toString());
            float topping3Price = Float.parseFloat(etTopping3Price.getText().toString());

            // Fiyat kontrolü
            if (basePrice < 0 || sauce1Price < 0 || sauce2Price < 0 || sauce3Price < 0 ||
                    topping1Price < 0 || topping2Price < 0 || topping3Price < 0) {
                showToast("Fiyatlar negatif olamaz!");
                return;
            }

            // Fiyatları kaydet
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("base_price", basePrice);
            editor.putFloat("sauce1_price", sauce1Price);
            editor.putFloat("sauce2_price", sauce2Price);
            editor.putFloat("sauce3_price", sauce3Price);
            editor.putFloat("topping1_price", topping1Price);
            editor.putFloat("topping2_price", topping2Price);
            editor.putFloat("topping3_price", topping3Price);

            // Esnek seçim sistemi durumu
            editor.putBoolean("flexible_selection", swFlexibleSelection.isChecked());
            editor.apply();

            showToast("Tüm fiyatlar kaydedildi!");
        } catch (NumberFormatException e) {
            showToast("Geçersiz fiyat formatı!");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
