package com.dogus.otomat.icecdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;
import java.util.List;

public class RecipeManagementActivity extends AppCompatActivity {

    private LinearLayout recipeContainer;
    private Button btnAddRecipe, btnBack;
    private SharedPreferences sharedPreferences;
    private List<Recipe> recipes = new ArrayList<>();
    private int nextSlotNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_management);

        sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        initializeViews();
        loadRecipes();
        setupClickListeners();
    }

    private void initializeViews() {
        recipeContainer = findViewById(R.id.recipeContainer);
        btnAddRecipe = findViewById(R.id.btnAddRecipe);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnAddRecipe.setOnClickListener(v -> showAddRecipeDialog());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadRecipes() {
        // SharedPreferences'dan tarifleri yükle
        int recipeCount = sharedPreferences.getInt("recipe_count", 0);
        for (int i = 0; i < recipeCount; i++) {
            String name = sharedPreferences.getString("recipe_" + i + "_name", "");
            float price = sharedPreferences.getFloat("recipe_" + i + "_price", 0);
            int slot = sharedPreferences.getInt("recipe_" + i + "_slot", 0);
            String ingredients = sharedPreferences.getString("recipe_" + i + "_ingredients", "");

            if (!name.isEmpty()) {
                Recipe recipe = new Recipe(name, price, slot, ingredients);
                recipes.add(recipe);
                if (slot >= nextSlotNumber) {
                    nextSlotNumber = slot + 1;
                }
            }
        }
        updateRecipeDisplay();
    }

    private void showAddRecipeDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_recipe, null);
        EditText etRecipeName = dialogView.findViewById(R.id.etRecipeName);
        EditText etRecipePrice = dialogView.findViewById(R.id.etRecipePrice);
        EditText etRecipeIngredients = dialogView.findViewById(R.id.etRecipeIngredients);

        new AlertDialog.Builder(this)
                .setTitle("Yeni Tarif Ekle")
                .setView(dialogView)
                .setPositiveButton("Ekle", (dialog, which) -> {
                    String name = etRecipeName.getText().toString();
                    String priceStr = etRecipePrice.getText().toString();
                    String ingredients = etRecipeIngredients.getText().toString();

                    if (name.isEmpty() || priceStr.isEmpty()) {
                        showToast("Tarif adı ve fiyatı gerekli!");
                        return;
                    }

                    try {
                        float price = Float.parseFloat(priceStr);
                        addRecipe(name, price, ingredients);
                    } catch (NumberFormatException e) {
                        showToast("Geçersiz fiyat formatı!");
                    }
                })
                .setNegativeButton("İptal", null)
                .show();
    }

    private void addRecipe(String name, float price, String ingredients) {
        Recipe recipe = new Recipe(name, price, nextSlotNumber, ingredients);
        recipes.add(recipe);
        nextSlotNumber++;

        saveRecipes();
        updateRecipeDisplay();
        showToast("Tarif eklendi: " + name);
    }

    private void deleteRecipe(Recipe recipe) {
        new AlertDialog.Builder(this)
                .setTitle("Tarifi Sil")
                .setMessage("'" + recipe.getName() + "' tarifini silmek istediğinizden emin misiniz?")
                .setPositiveButton("Evet, Sil", (dialog, which) -> {
                    recipes.remove(recipe);
                    saveRecipes();
                    updateRecipeDisplay();
                    showToast("Tarif silindi: " + recipe.getName());
                })
                .setNegativeButton("İptal", null)
                .show();
    }

    private void updateRecipeDisplay() {
        recipeContainer.removeAllViews();

        for (Recipe recipe : recipes) {
            CardView recipeCard = createRecipeCard(recipe);
            recipeContainer.addView(recipeCard);
        }
    }

    private CardView createRecipeCard(Recipe recipe) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);
        card.setRadius(8);
        card.setElevation(4);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(16, 16, 16, 16);

        // Tarif adı
        TextView tvName = new TextView(this);
        tvName.setText(recipe.getName());
        tvName.setTextSize(18);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        content.addView(tvName);

        // Fiyat
        TextView tvPrice = new TextView(this);
        tvPrice.setText("Fiyat: " + String.format("%.2f", recipe.getPrice()) + " ₺");
        tvPrice.setTextSize(16);
        tvPrice.setPadding(0, 8, 0, 0);
        content.addView(tvPrice);

        // Slot numarası
        TextView tvSlot = new TextView(this);
        tvSlot.setText("Slot: " + recipe.getSlot());
        tvSlot.setTextSize(16);
        tvSlot.setPadding(0, 4, 0, 0);
        content.addView(tvSlot);

        // İçerikler
        if (!recipe.getIngredients().isEmpty()) {
            TextView tvIngredients = new TextView(this);
            tvIngredients.setText("İçerik: " + recipe.getIngredients());
            tvIngredients.setTextSize(14);
            tvIngredients.setPadding(0, 4, 0, 0);
            content.addView(tvIngredients);
        }

        // Butonlar
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 16, 0, 0);

        Button btnTest = new Button(this);
        btnTest.setText("Test Et");
        btnTest.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        btnTest.setOnClickListener(v -> testRecipe(recipe));
        buttonLayout.addView(btnTest);

        Button btnDelete = new Button(this);
        btnDelete.setText("Sil");
        btnDelete.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        btnDelete.setOnClickListener(v -> deleteRecipe(recipe));
        buttonLayout.addView(btnDelete);

        content.addView(buttonLayout);
        card.addView(content);

        return card;
    }

    private void testRecipe(Recipe recipe) {
        showToast("Test başlatıldı: " + recipe.getName() + " (Slot " + recipe.getSlot() + ")");
        // Burada SDK ile test komutu gönderilebilir
    }

    private void saveRecipes() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("recipe_count", recipes.size());

        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            editor.putString("recipe_" + i + "_name", recipe.getName());
            editor.putFloat("recipe_" + i + "_price", recipe.getPrice());
            editor.putInt("recipe_" + i + "_slot", recipe.getSlot());
            editor.putString("recipe_" + i + "_ingredients", recipe.getIngredients());
        }
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static class Recipe {
        private String name;
        private float price;
        private int slot;
        private String ingredients;

        public Recipe(String name, float price, int slot, String ingredients) {
            this.name = name;
            this.price = price;
            this.slot = slot;
            this.ingredients = ingredients;
        }

        public String getName() {
            return name;
        }

        public float getPrice() {
            return price;
        }

        public int getSlot() {
            return slot;
        }

        public String getIngredients() {
            return ingredients;
        }
    }
}
