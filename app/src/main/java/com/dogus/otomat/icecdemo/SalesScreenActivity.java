package com.dogus.otomat.icecdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.tcn.icecboard.DriveControl.VendProtoControl;
import com.tcn.icecboard.control.PayMethod;

public class SalesScreenActivity extends AppCompatActivity {
    private static final String TAG = "SalesScreen";

    // UI Components
    private TextView tvTotal;
    private Button btnVanilla, btnChocolate;
    private Button btnSauce1, btnSauce2, btnSauce3;
    private Button btnTopping1, btnTopping2, btnTopping3;
    private Button btnPaymentCash, btnPaymentCard;
    private Button btnBackToMain;

    // Selected items
    private int selectedIceCream = 1; // 1=Vanilya, 2=Çikolata
    private boolean[] selectedSauces = new boolean[3]; // 0=Çikolata, 1=Karamel, 2=Çilek
    private boolean[] selectedToppings = new boolean[3]; // 0=Fındık, 1=Renkli Şeker, 2=Krem Şanti

    // Prices
    private double iceCreamPrice = 8.0;
    private double[] saucePrices = {2.0, 2.5, 2.0}; // Çikolata, Karamel, Çilek
    private double[] toppingPrices = {1.5, 1.0, 1.5}; // Fındık, Renkli Şeker, Krem Şanti

    // Payment manager
    private MDBPaymentManager paymentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_screen);

        Log.i(TAG, "SalesScreenActivity onCreate started");

        initializeViews();
        initializeManagers();
        setupClickListeners();
        updateTotal();

        Log.i(TAG, "SalesScreenActivity onCreate completed successfully");
    }

    private void initializeViews() {
        tvTotal = findViewById(R.id.tv_total);
        btnVanilla = findViewById(R.id.btn_vanilla);
        btnChocolate = findViewById(R.id.btn_chocolate);
        btnSauce1 = findViewById(R.id.btn_sauce1);
        btnSauce2 = findViewById(R.id.btn_sauce2);
        btnSauce3 = findViewById(R.id.btn_sauce3);
        btnTopping1 = findViewById(R.id.btn_topping1);
        btnTopping2 = findViewById(R.id.btn_topping2);
        btnTopping3 = findViewById(R.id.btn_topping3);
        btnPaymentCash = findViewById(R.id.btn_payment_cash);
        btnPaymentCard = findViewById(R.id.btn_payment_card);
        btnBackToMain = findViewById(R.id.btn_back_to_main);

        // Initially select vanilla
        btnVanilla.setSelected(true);
        btnVanilla.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
    }

    private void initializeManagers() {
        try {
            paymentManager = MDBPaymentManager.getInstance(this);
            paymentManager.setPaymentListener(new MDBPaymentManager.OnPaymentListener() {
                @Override
                public void onPaymentStarted(double amount, String paymentMethod) {
                    runOnUiThread(() -> {
                        showToast("Ödeme başlatıldı: " + amount + " TL (" + paymentMethod + ")");
                    });
                }

                @Override
                public void onPaymentCompleted(double amount, String paymentMethod, String transactionId) {
                    runOnUiThread(() -> {
                        showToast("Ödeme başarılı!");
                        dispenseProduct();
                    });
                }

                @Override
                public void onPaymentFailed(String error) {
                    runOnUiThread(() -> {
                        showToast("Ödeme başarısız: " + error);
                    });
                }

                @Override
                public void onPaymentCancelled() {
                    runOnUiThread(() -> {
                        showToast("Ödeme iptal edildi");
                    });
                }
            });

            Log.i(TAG, "Payment manager initialized");

        } catch (Exception e) {
            Log.e(TAG, "Payment manager initialization error: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        // Ice cream selection
        btnVanilla.setOnClickListener(v -> {
            selectedIceCream = 1;
            btnVanilla.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            btnChocolate.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            updateTotal();
        });

        btnChocolate.setOnClickListener(v -> {
            selectedIceCream = 2;
            btnChocolate.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            btnVanilla.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            updateTotal();
        });

        // Sauce selection
        btnSauce1.setOnClickListener(v -> {
            selectedSauces[0] = !selectedSauces[0];
            updateSauceButton(btnSauce1, selectedSauces[0]);
            updateTotal();
        });

        btnSauce2.setOnClickListener(v -> {
            selectedSauces[1] = !selectedSauces[1];
            updateSauceButton(btnSauce2, selectedSauces[1]);
            updateTotal();
        });

        btnSauce3.setOnClickListener(v -> {
            selectedSauces[2] = !selectedSauces[2];
            updateSauceButton(btnSauce3, selectedSauces[2]);
            updateTotal();
        });

        // Topping selection
        btnTopping1.setOnClickListener(v -> {
            selectedToppings[0] = !selectedToppings[0];
            updateToppingButton(btnTopping1, selectedToppings[0]);
            updateTotal();
        });

        btnTopping2.setOnClickListener(v -> {
            selectedToppings[1] = !selectedToppings[1];
            updateToppingButton(btnTopping2, selectedToppings[1]);
            updateTotal();
        });

        btnTopping3.setOnClickListener(v -> {
            selectedToppings[2] = !selectedToppings[2];
            updateToppingButton(btnTopping3, selectedToppings[2]);
            updateTotal();
        });

        // Payment buttons
        btnPaymentCash.setOnClickListener(v -> processPayment("Nakit"));
        btnPaymentCard.setOnClickListener(v -> processPayment("Kart"));

        // Back button
        btnBackToMain.setOnClickListener(v -> finish());
    }

    private void updateSauceButton(Button button, boolean selected) {
        if (selected) {
            button.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        } else {
            button.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private void updateToppingButton(Button button, boolean selected) {
        if (selected) {
            button.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        } else {
            button.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private void updateTotal() {
        double total = iceCreamPrice;

        // Add sauce prices
        for (int i = 0; i < selectedSauces.length; i++) {
            if (selectedSauces[i]) {
                total += saucePrices[i];
            }
        }

        // Add topping prices
        for (int i = 0; i < selectedToppings.length; i++) {
            if (selectedToppings[i]) {
                total += toppingPrices[i];
            }
        }

        tvTotal.setText(String.format("%.2f TL", total));
    }

    private void processPayment(String paymentMethod) {
        double total = calculateTotal();
        if (total <= 0) {
            showToast("Lütfen en az bir ürün seçin");
            return;
        }

        try {
            showToast("Ödeme işlemi başlatılıyor: " + total + " TL (" + paymentMethod + ")");
            boolean paymentStarted = paymentManager.startPayment(total, paymentMethod);

            if (!paymentStarted) {
                showToast("Ödeme başlatılamadı");
            }

        } catch (Exception e) {
            Log.e(TAG, "Payment processing error: " + e.getMessage(), e);
            showToast("Ödeme hatası: " + e.getMessage());
        }
    }

    private double calculateTotal() {
        double total = iceCreamPrice;

        // Add sauce prices
        for (int i = 0; i < selectedSauces.length; i++) {
            if (selectedSauces[i]) {
                total += saucePrices[i];
            }
        }

        // Add topping prices
        for (int i = 0; i < selectedToppings.length; i++) {
            if (selectedToppings[i]) {
                total += toppingPrices[i];
            }
        }

        return total;
    }

    private void dispenseProduct() {
        try {
            VendProtoControl vendProtoControl = VendProtoControl.getInstance();
            
            // Calculate slot number based on selections
            int slotNumber = calculateSlotNumber();
            
            // Prepare shipment parameters
            String tradeNumber = "SALE_" + System.currentTimeMillis();
            String amount = String.format("%.2f", calculateTotal());
            
            // Sauce selections
            String zhuliao = selectedSauces[0] ? "1" : "0"; // Chocolate sauce
            String dingliao = selectedSauces[1] ? "1" : "0"; // Caramel sauce
            String guojiang = selectedSauces[2] ? "1" : "0"; // Strawberry sauce
            
            // Topping selections
            String topping1 = selectedToppings[0] ? "1" : "0"; // Nuts
            String topping2 = selectedToppings[1] ? "1" : "0"; // Sprinkles
            String topping3 = selectedToppings[2] ? "1" : "0"; // Whipped cream
            
            // Quantities (in ml/grams)
            String zhuliaoQty = selectedSauces[0] ? "20" : "0";
            String dingliaoQty = selectedSauces[1] ? "25" : "0";
            String guojiangQty = selectedSauces[2] ? "20" : "0";
            String topping1Qty = selectedToppings[0] ? "15" : "0";
            String topping2Qty = selectedToppings[1] ? "10" : "0";
            String topping3Qty = selectedToppings[2] ? "15" : "0";

            // Send shipment command
            vendProtoControl.ship(
                slotNumber,
                PayMethod.PAYMETHED_CASH, // This will be overridden by actual payment
                tradeNumber,
                30, // Heat time
                amount,
                zhuliao,
                dingliao,
                guojiang,
                zhuliaoQty,
                dingliaoQty,
                guojiangQty
            );

            showToast("Ürün çıkarılıyor...");

        } catch (Exception e) {
            Log.e(TAG, "Product dispensing error: " + e.getMessage(), e);
            showToast("Ürün çıkarma hatası: " + e.getMessage());
        }
    }

    private int calculateSlotNumber() {
        // Simple slot calculation - in a real implementation this would be more complex
        // For now, we'll use a basic mapping
        if (selectedSauces[0] && selectedToppings[0]) return 12; // Chocolate sauce + Nuts
        if (selectedSauces[1] && selectedToppings[1]) return 22; // Caramel sauce + Sprinkles
        if (selectedSauces[2] && selectedToppings[2]) return 32; // Strawberry sauce + Whipped cream
        if (selectedSauces[0]) return 11; // Chocolate sauce only
        if (selectedSauces[1]) return 21; // Caramel sauce only
        if (selectedSauces[2]) return 31; // Strawberry sauce only
        if (selectedToppings[0]) return 2; // Nuts only
        if (selectedToppings[1]) return 3; // Sprinkles only
        if (selectedToppings[2]) return 4; // Whipped cream only
        return 1; // Plain ice cream
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "SalesScreenActivity onDestroy");
    }
}