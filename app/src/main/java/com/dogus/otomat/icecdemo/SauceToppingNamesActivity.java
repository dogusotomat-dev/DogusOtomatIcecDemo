package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SauceToppingNamesActivity extends AppCompatActivity {

    private EditText etSauce1Name, etSauce2Name, etSauce3Name;
    private EditText etTopping1Name, etTopping2Name, etTopping3Name;
    private Button btnSaveNames, btnBack;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sauce_topping_names);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        initializeViews();
        loadCurrentNames();
        setupClickListeners();
    }

    private void initializeViews() {
        etSauce1Name = findViewById(R.id.etSauce1Name);
        etSauce2Name = findViewById(R.id.etSauce2Name);
        etSauce3Name = findViewById(R.id.etSauce3Name);
        etTopping1Name = findViewById(R.id.etTopping1Name);
        etTopping2Name = findViewById(R.id.etTopping2Name);
        etTopping3Name = findViewById(R.id.etTopping3Name);
        btnSaveNames = findViewById(R.id.btnSaveNames);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadCurrentNames() {
        // Sos isimleri - emoji anahtarları ile
        String sauce1Name = sharedPreferences.getString("sauce_name_🍫 Çikolata Sos", "Çikolata Sos");
        String sauce2Name = sharedPreferences.getString("sauce_name_🍯 Karamel Sos", "Karamel Sos");
        String sauce3Name = sharedPreferences.getString("sauce_name_🍓 Çilek Sos", "Çilek Sos");
        
        // Süsleme isimleri - emoji anahtarları ile
        String topping1Name = sharedPreferences.getString("topping_name_🥜 Fındık", "Fındık");
        String topping2Name = sharedPreferences.getString("topping_name_🌈 Renkli Şeker", "Renkli Şeker");
        String topping3Name = sharedPreferences.getString("topping_name_🍰 Krem Şanti", "Krem Şanti");

        etSauce1Name.setText(sauce1Name);
        etSauce2Name.setText(sauce2Name);
        etSauce3Name.setText(sauce3Name);
        etTopping1Name.setText(topping1Name);
        etTopping2Name.setText(topping2Name);
        etTopping3Name.setText(topping3Name);
    }

    private void setupClickListeners() {
        btnSaveNames.setOnClickListener(v -> saveNames());
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveNames() {
        String sauce1Name = etSauce1Name.getText().toString();
        String sauce2Name = etSauce2Name.getText().toString();
        String sauce3Name = etSauce3Name.getText().toString();
        String topping1Name = etTopping1Name.getText().toString();
        String topping2Name = etTopping2Name.getText().toString();
        String topping3Name = etTopping3Name.getText().toString();

        if (sauce1Name.isEmpty() || sauce2Name.isEmpty() || sauce3Name.isEmpty() ||
                topping1Name.isEmpty() || topping2Name.isEmpty() || topping3Name.isEmpty()) {
            showToast("Tüm sos ve süsleme isimleri doldurulmalı!");
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Sos isimleri - emoji anahtarları ile kaydet
        editor.putString("sauce_name_🍫 Çikolata Sos", sauce1Name);
        editor.putString("sauce_name_🍯 Karamel Sos", sauce2Name);
        editor.putString("sauce_name_🍓 Çilek Sos", sauce3Name);
        
        // Süsleme isimleri - emoji anahtarları ile kaydet
        editor.putString("topping_name_🥜 Fındık", topping1Name);
        editor.putString("topping_name_🌈 Renkli Şeker", topping2Name);
        editor.putString("topping_name_🍰 Krem Şanti", topping3Name);
        editor.apply();

        showToast("Sos ve süsleme isimleri kaydedildi!");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
