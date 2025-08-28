package com.dogus.otomat.icecdemo;

import java.util.HashMap;
import java.util.Map;

/**
 * Board bilgilerini tutan sınıf
 * Ice cream machine board information class
 */
public class BoardInfo {
    private int boardId;
    private int boardType;
    private String boardName;
    private int portNumber;
    private String status;
    private int workMode;
    private int slotCount;
    private String productImagePath;
    private long lastSeen;
    private boolean isActive;
    private String firmwareVersion;
    private String hardwareVersion;
    private Map<String, Object> customProperties;

    public BoardInfo() {
        this.customProperties = new HashMap<>();
        this.lastSeen = System.currentTimeMillis();
        this.isActive = true;
    }

    // Getters and Setters
    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public int getBoardType() {
        return boardType;
    }

    public void setBoardType(int boardType) {
        this.boardType = boardType;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getWorkMode() {
        return workMode;
    }

    public void setWorkMode(int workMode) {
        this.workMode = workMode;
    }

    public int getSlotCount() {
        return slotCount;
    }

    public void setSlotCount(int slotCount) {
        this.slotCount = slotCount;
    }

    public String getProductImagePath() {
        return productImagePath;
    }

    public void setProductImagePath(String productImagePath) {
        this.productImagePath = productImagePath;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public Map<String, Object> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, Object> customProperties) {
        this.customProperties = customProperties;
    }

    /**
     * Özel özellik ekler
     */
    public void addCustomProperty(String key, Object value) {
        if (customProperties == null) {
            customProperties = new HashMap<>();
        }
        customProperties.put(key, value);
    }

    /**
     * Özel özellik alır
     */
    public Object getCustomProperty(String key) {
        if (customProperties != null) {
            return customProperties.get(key);
        }
        return null;
    }

    /**
     * Board tipinin açıklamasını döndürür
     */
    public String getBoardTypeDescription() {
        switch (boardType) {
            case 20:
                return "Dondurma Board";
            case 5:
                return "Yay Board";
            case 6:
                return "Izgara Board";
            default:
                return "Bilinmeyen Board";
        }
    }

    /**
     * Çalışma modunun açıklamasını döndürür
     */
    public String getWorkModeDescription() {
        switch (workMode) {
            case 0:
                return "Acil Durdur";
            case 1:
                return "Bekleme";
            case 2:
                return "Test Modu";
            case 3:
                return "Bakım Modu";
            case 4:
                return "Hata Modu";
            case 5:
                return "Dondurma Yapımı";
            default:
                return "Bilinmeyen Mod";
        }
    }

    /**
     * Board'ın aktif olup olmadığını kontrol eder
     */
    public boolean isBoardAlive() {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastSeen;

        // 5 dakika içinde sinyal alınmışsa aktif kabul et
        return timeDiff < 300000 && isActive;
    }

    /**
     * Board bilgilerini string olarak döndürür
     */
    @Override
    public String toString() {
        return "BoardInfo{" +
                "boardId=" + boardId +
                ", boardType=" + boardType + " (" + getBoardTypeDescription() + ")" +
                ", boardName='" + boardName + '\'' +
                ", portNumber=" + portNumber +
                ", status='" + status + '\'' +
                ", workMode=" + workMode + " (" + getWorkModeDescription() + ")" +
                ", slotCount=" + slotCount +
                ", productImagePath='" + productImagePath + '\'' +
                ", lastSeen=" + new java.util.Date(lastSeen) +
                ", isActive=" + isActive +
                ", isAlive=" + isBoardAlive() +
                '}';
    }

    /**
     * Board'ı JSON formatında döndürür
     */
    public String toJson() {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"boardId\":").append(boardId).append(",");
            json.append("\"boardType\":").append(boardType).append(",");
            json.append("\"boardName\":\"").append(boardName).append("\",");
            json.append("\"portNumber\":").append(portNumber).append(",");
            json.append("\"status\":\"").append(status).append("\",");
            json.append("\"workMode\":").append(workMode).append(",");
            json.append("\"slotCount\":").append(slotCount).append(",");
            json.append("\"productImagePath\":\"").append(productImagePath != null ? productImagePath : "")
                    .append("\",");
            json.append("\"lastSeen\":").append(lastSeen).append(",");
            json.append("\"isActive\":").append(isActive).append(",");
            json.append("\"isAlive\":").append(isBoardAlive());
            json.append("}");
            return json.toString();
        } catch (Exception e) {
            return "{}";
        }
    }
}
