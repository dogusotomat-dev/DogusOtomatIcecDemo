package com.dogus.otomat.icecdemo;

/**
 * Sepet öğesi sınıfı
 * Dondurma otomatında seçilen ürünleri temsil eder
 */
public class CartItem {
    private String name;
    private double price;
    private int quantity;
    private String type; // "sauce", "topping", "base"

    public CartItem(String name, double price) {
        this.name = name;
        this.price = price;
        this.quantity = 1;
        this.type = "base";
    }

    public CartItem(String name, double price, String type) {
        this.name = name;
        this.price = price;
        this.quantity = 1;
        this.type = type;
    }

    // Getter ve Setter metodları
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Miktarı artırır
     */
    public void incrementQuantity() {
        this.quantity++;
    }

    /**
     * Miktarı azaltır
     */
    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    /**
     * Toplam fiyatı hesaplar
     */
    public double getTotalPrice() {
        return price * quantity;
    }

    /**
     * Öğe bilgilerini string olarak döndürür
     */
    @Override
    public String toString() {
        String safeName = (name != null) ? name : "Unknown";
        return safeName + " x" + quantity + " - " + String.format("%.2f", getTotalPrice()) + " TL";
    }

    /**
     * İki öğenin aynı olup olmadığını kontrol eder
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        CartItem cartItem = (CartItem) obj;
        
        // Null-safe karşılaştırma
        if (name == null && cartItem.name != null) return false;
        if (name != null && !name.equals(cartItem.name)) return false;
        if (type == null && cartItem.type != null) return false;
        if (type != null && !type.equals(cartItem.type)) return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        // Null-safe hashCode hesaplama
        int nameHash = (name != null) ? name.hashCode() : 0;
        int typeHash = (type != null) ? type.hashCode() : 0;
        return nameHash + typeHash;
    }
}
