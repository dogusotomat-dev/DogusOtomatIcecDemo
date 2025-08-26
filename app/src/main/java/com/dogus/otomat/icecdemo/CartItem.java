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
        return name + " x" + quantity + " - " + String.format("%.2f", getTotalPrice()) + " TL";
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
        return name.equals(cartItem.name) && type.equals(cartItem.type);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode();
    }
}
