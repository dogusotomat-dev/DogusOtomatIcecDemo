package com.dogus.otomat.icecdemo;

public class ToppingItem {
    private String name;
    private double price;
    private boolean available;
    private boolean selected;

    public ToppingItem(String name, double price, boolean available) {
        this.name = name;
        this.price = price;
        this.available = available;
        this.selected = false;
    }

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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ToppingItem that = (ToppingItem) obj;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name + " - ₺" + String.format("%.2f", price);
    }
}
