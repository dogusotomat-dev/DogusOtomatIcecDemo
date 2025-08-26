package com.dogus.otomat.icecdemo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Dondurma Otomatı Satış Ekranı
 */
public class SalesScreenActivity extends AppCompatActivity {

	private static final String TAG = "SalesScreenActivity";

	// UI bileşenleri
	private TextView tvTotalPrice;
	private EditText etQuantity;
	private Button btnVanilla, btnChocolate, btnStrawberry, btnCustom;
	private TextView btnAddToCart;
	private Button btnCheckout, btnBack;

	// Sepet bilgileri
	private double totalPrice = 0.0;
	private int totalQuantity = 0;

	// Fiyatlar
	private static final double VANILLA_PRICE = 3.50;
	private static final double CHOCOLATE_PRICE = 4.00;
	private static final double STRAWBERRY_PRICE = 3.75;
	private static final double CUSTOM_PRICE = 5.00;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_screen);

		initView();
		setupClickListeners();
	}

	private void initView() {
		tvTotalPrice = findViewById(R.id.tv_total_price);
		etQuantity = findViewById(R.id.et_custom_flavor);

		btnVanilla = findViewById(R.id.btn_buy_vanilla);
		btnChocolate = findViewById(R.id.btn_buy_chocolate);
		btnStrawberry = findViewById(R.id.btn_buy_strawberry);
		btnCustom = findViewById(R.id.btn_custom_ice_cream);

		btnAddToCart = findViewById(R.id.tv_cart_items);
		btnCheckout = findViewById(R.id.btn_checkout);
		btnBack = findViewById(R.id.btn_back_to_menu);

		// Başlangıç değerleri
		etQuantity.setText("1");
		updateTotalDisplay();
	}

	private void setupClickListeners() {
		btnVanilla.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectIceCream("Vanilya", VANILLA_PRICE);
			}
		});

		btnChocolate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectIceCream("Çikolata", CHOCOLATE_PRICE);
			}
		});

		btnStrawberry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectIceCream("Çilek", STRAWBERRY_PRICE);
			}
		});

		btnCustom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectIceCream("Özel", CUSTOM_PRICE);
			}
		});

		// btnAddToCart artık TextView olduğu için click listener yok

		btnCheckout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkout();
			}
		});

		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void selectIceCream(String flavor, double price) {
		try {
			String quantityText = etQuantity.getText().toString();
			if (quantityText.isEmpty()) {
				showMessage("Uyarı", "Lütfen miktar girin");
				return;
			}

			int quantity = Integer.parseInt(quantityText);
			if (quantity <= 0) {
				showMessage("Uyarı", "Miktar 0'dan büyük olmalı");
				return;
			}

			double itemTotal = price * quantity;
			totalPrice += itemTotal;
			totalQuantity += quantity;

			updateTotalDisplay();

			// Telemetri verisi gönder
			TelemetryManager telemetryManager = TelemetryManager.getInstance(this);
			if (telemetryManager != null) {
				telemetryManager.sendSalesData(1, flavor + " Dondurma", itemTotal, "cash", true);
			}

			showMessage("Başarılı", quantity + " adet " + flavor + " dondurma sepete eklendi!");
		} catch (NumberFormatException e) {
			showMessage("Hata", "Geçersiz miktar formatı");
		}
	}

	private void addToCart() {
		if (totalQuantity == 0) {
			showMessage("Uyarı", "Sepette ürün bulunmuyor");
			return;
		}

		showMessage("Sepet", "Toplam: " + totalQuantity + " adet\nFiyat: " + String.format("%.2f", totalPrice) + " TL");
	}

	private void checkout() {
		if (totalQuantity == 0) {
			showMessage("Uyarı", "Sepette ürün bulunmuyor");
			return;
		}

		// MDB ödeme sistemi ile ödeme
		MDBPaymentManager mdbPaymentManager = MDBPaymentManager.getInstance(this);
		if (mdbPaymentManager != null) {
			boolean paymentStarted = mdbPaymentManager.startPayment(totalPrice, "cash");

			if (paymentStarted) {
				boolean paymentApproved = mdbPaymentManager.approvePayment();

				if (paymentApproved) {
					// Başarılı ödeme
					showMessage("Başarılı", "Ödeme tamamlandı!\nDondurmanız hazırlanıyor...");

					// Sepeti temizle
					clearCart();

					// Telemetri verisi gönder
					TelemetryManager telemetryManager = TelemetryManager.getInstance(this);
					if (telemetryManager != null) {
						telemetryManager.sendSalesData(1, "Sepet Ödemesi", totalPrice, "cash", true);
					}
				} else {
					showMessage("Hata", "Ödeme reddedildi!");
				}
			} else {
				showMessage("Hata", "Ödeme sistemi hazır değil!");
			}
		} else {
			showMessage("Hata", "Ödeme sistemi bulunamadı!");
		}
	}

	private void clearCart() {
		totalPrice = 0.0;
		totalQuantity = 0;
		updateTotalDisplay();
	}

	private void updateTotalDisplay() {
		tvTotalPrice.setText("Toplam: " + String.format("%.2f", totalPrice) + " TL (" + totalQuantity + " adet)");
	}

	private void showMessage(String title, String message) {
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton("Tamam", null)
				.show();
	}
}
