package com.dogus.otomat.icecdemo;

import java.util.HashMap;
import java.util.Map;

/**
 * Dondurma Makinesi Slot Sistemi
 * 16 slotlu sistem: Sade dondurma + 3 sos + 3 süsleme kombinasyonları
 */
public class IceCreamSlotSystem {
    
    // Slot numaraları ve açıklamaları
    private static final Map<Integer, String> SLOT_DESCRIPTIONS = new HashMap<>();
    
    // Slot fiyatları (SharedPreferences'dan yüklenecek)
    private static Map<Integer, Double> slotPrices = new HashMap<>();
    
    static {
        // Temel slot açıklamaları
        SLOT_DESCRIPTIONS.put(1, "Sade Dondurma");
        SLOT_DESCRIPTIONS.put(2, "Süsleme 1 + Dondurma");
        SLOT_DESCRIPTIONS.put(3, "Süsleme 2 + Dondurma");
        SLOT_DESCRIPTIONS.put(4, "Süsleme 3 + Dondurma");
        
        // Sos 1 kombinasyonları
        SLOT_DESCRIPTIONS.put(11, "Sos 1 + Dondurma");
        SLOT_DESCRIPTIONS.put(12, "Sos 1 + Süsleme 1 + Dondurma");
        SLOT_DESCRIPTIONS.put(13, "Sos 1 + Süsleme 2 + Dondurma");
        SLOT_DESCRIPTIONS.put(14, "Sos 1 + Süsleme 3 + Dondurma");
        
        // Sos 2 kombinasyonları
        SLOT_DESCRIPTIONS.put(21, "Sos 2 + Dondurma");
        SLOT_DESCRIPTIONS.put(22, "Sos 2 + Süsleme 1 + Dondurma");
        SLOT_DESCRIPTIONS.put(23, "Sos 2 + Süsleme 2 + Dondurma");
        SLOT_DESCRIPTIONS.put(24, "Sos 2 + Süsleme 3 + Dondurma");
        
        // Sos 3 kombinasyonları
        SLOT_DESCRIPTIONS.put(31, "Sos 3 + Dondurma");
        SLOT_DESCRIPTIONS.put(32, "Sos 3 + Süsleme 1 + Dondurma");
        SLOT_DESCRIPTIONS.put(33, "Sos 3 + Süsleme 2 + Dondurma");
        SLOT_DESCRIPTIONS.put(34, "Sos 3 + Süsleme 3 + Dondurma");
    }
    
    /**
     * Slot numarasına göre açıklama döndürür
     * @param slotNo Slot numarası
     * @return Slot açıklaması
     */
    public static String getSlotDescription(int slotNo) {
        String description = SLOT_DESCRIPTIONS.get(slotNo);
        if (description == null) {
            description = "Bilinmeyen Slot: " + slotNo;
        }
        return description;
    }
    
    /**
     * Seçilen sos ve süslemelere göre slot numarası hesaplar
     * @param selectedSauces Seçilen soslar (1, 2, 3)
     * @param selectedToppings Seçilen süslemeler (1, 2, 3)
     * @return Slot numarası
     */
    public static int calculateSlotNumber(int[] selectedSauces, int[] selectedToppings) {
        if (selectedSauces == null || selectedSauces.length == 0) {
            // Sade dondurma veya sadece süsleme
            if (selectedToppings == null || selectedToppings.length == 0) {
                return 1; // Sade dondurma
            } else if (selectedToppings.length == 1) {
                return selectedToppings[0] + 1; // Süsleme 1=2, 2=3, 3=4
            }
        } else if (selectedSauces.length == 1) {
            int sauceNo = selectedSauces[0];
            if (selectedToppings == null || selectedToppings.length == 0) {
                return sauceNo * 10 + 1; // Sos 1=11, 2=21, 3=31
            } else if (selectedToppings.length == 1) {
                return sauceNo * 10 + selectedToppings[0] + 1; // Sos 1 + Süsleme 1 = 12
            }
        }
        
        // Varsayılan olarak sade dondurma
        return 1;
    }
    
    /**
     * Slot numarasından sos ve süsleme bilgilerini çıkarır
     * @param slotNo Slot numarası
     * @return [sosNo, süslemeNo] dizisi (0 = yok)
     */
    public static int[] parseSlotNumber(int slotNo) {
        if (slotNo <= 4) {
            // Sade dondurma veya sadece süsleme
            if (slotNo == 1) {
                return new int[]{0, 0}; // Sade dondurma
            } else {
                return new int[]{0, slotNo - 1}; // Süsleme 1, 2, 3
            }
        } else {
            // Sos + süsleme kombinasyonları
            int sauceNo = slotNo / 10;
            int toppingNo = slotNo % 10;
            if (toppingNo == 1) {
                return new int[]{sauceNo, 0}; // Sadece sos
            } else {
                return new int[]{sauceNo, toppingNo - 1}; // Sos + süsleme
            }
        }
    }
    
    /**
     * Slot fiyatını hesaplar
     * @param slotNo Slot numarası
     * @param basePrice Temel dondurma fiyatı
     * @param saucePrices Sos fiyatları [sauce1, sauce2, sauce3]
     * @param toppingPrices Süsleme fiyatları [topping1, topping2, topping3]
     * @return Toplam fiyat
     */
    public static double calculateSlotPrice(int slotNo, double basePrice, 
                                         double[] saucePrices, double[] toppingPrices) {
        int[] components = parseSlotNumber(slotNo);
        double totalPrice = basePrice;
        
        // Sos fiyatı ekle
        if (components[0] > 0 && components[0] <= saucePrices.length) {
            totalPrice += saucePrices[components[0] - 1];
        }
        
        // Süsleme fiyatı ekle
        if (components[1] > 0 && components[1] <= toppingPrices.length) {
            totalPrice += toppingPrices[components[1] - 1];
        }
        
        return totalPrice;
    }
    
    /**
     * Tüm slot açıklamalarını döndürür
     * @return Slot açıklamaları map'i
     */
    public static Map<Integer, String> getAllSlotDescriptions() {
        return new HashMap<>(SLOT_DESCRIPTIONS);
    }
    
    /**
     * Slot numarasının geçerli olup olmadığını kontrol eder
     * @param slotNo Slot numarası
     * @return Geçerli mi?
     */
    public static boolean isValidSlot(int slotNo) {
        return SLOT_DESCRIPTIONS.containsKey(slotNo);
    }
    
    /**
     * Slot numarasının türünü döndürür
     * @param slotNo Slot numarası
     * @return Slot türü (BASIC, TOPPING_ONLY, SAUCE_ONLY, SAUCE_TOPPING)
     */
    public static SlotType getSlotType(int slotNo) {
        if (slotNo == 1) return SlotType.BASIC;
        if (slotNo <= 4) return SlotType.TOPPING_ONLY;
        if (slotNo % 10 == 1) return SlotType.SAUCE_ONLY;
        return SlotType.SAUCE_TOPPING;
    }
    
    /**
     * Slot türleri
     */
    public enum SlotType {
        BASIC("Sade Dondurma"),
        TOPPING_ONLY("Süsleme + Dondurma"),
        SAUCE_ONLY("Sos + Dondurma"),
        SAUCE_TOPPING("Sos + Süsleme + Dondurma");
        
        private final String description;
        
        SlotType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
