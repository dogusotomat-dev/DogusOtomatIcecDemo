package com.tcn.icecboard.DriveControl.icec;

// 参数
public class IcecParameter {
    private int WorkMode = -1;
    private int NewsletterState = -1;
    private int PuffingGear = -1;
    private int RefrigerationGear=-1;
    private int TroughTemperature = -1;
    private int TankTemperature = -1;
    private int SetPreservationTemperature = -1;
    private int IceCreamMakingStatus = -1;
    private int StatusCode = -1;
    private int MachineCode= -1;
    private int MilkCupStockCondition = -1;
    private int JamMaterialConditions =-1;
    private int TopMaterialCondition = -1;

    public IcecParameter(int workMode, int newsletterState, int puffingGear, int refrigerationGear, int troughTemperature, int tankTemperature, int setPreservationTemperature, int iceCreamMakingStatus, int statusCode, int machineCode, int milkCupStockCondition, int jamMaterialConditions, int topMaterialCondition) {
        WorkMode = workMode;
        NewsletterState = newsletterState;
        PuffingGear = puffingGear;
        RefrigerationGear = refrigerationGear;
        TroughTemperature = troughTemperature;
        TankTemperature = tankTemperature;
        SetPreservationTemperature = setPreservationTemperature;
        IceCreamMakingStatus = iceCreamMakingStatus;
        StatusCode = statusCode;
        MachineCode = machineCode;
        MilkCupStockCondition = milkCupStockCondition;
        JamMaterialConditions = jamMaterialConditions;
        TopMaterialCondition = topMaterialCondition;
    }

    public int getNewsletterState() {
        return NewsletterState;
    }

    public void setNewsletterState(int newsletterState) {
        NewsletterState = newsletterState;
    }

    public int getWorkMode() {
        return WorkMode;
    }

    public void setWorkMode(int workMode) {
        WorkMode = workMode;
    }

    public int getPuffingGear() {
        return PuffingGear;
    }

    public void setPuffingGear(int puffingGear) {
        PuffingGear = puffingGear;
    }

    public int getTroughTemperature() {
        return TroughTemperature;
    }

    public void setTroughTemperature(int troughTemperature) {
        TroughTemperature = troughTemperature;
    }

    public int getTankTemperature() {
        return TankTemperature;
    }

    public int getRefrigerationGear() {
        return RefrigerationGear;
    }

    public void setRefrigerationGear(int refrigerationGear) {
        RefrigerationGear = refrigerationGear;
    }

    public void setTankTemperature(int tankTemperature) {
        TankTemperature = tankTemperature;
    }

    public int getSetPreservationTemperature() {
        return SetPreservationTemperature;
    }

    public void setSetPreservationTemperature(int setPreservationTemperature) {
        SetPreservationTemperature = setPreservationTemperature;
    }

    public int getIceCreamMakingStatus() {
        return IceCreamMakingStatus;
    }

    public void setIceCreamMakingStatus(int iceCreamMakingStatus) {
        IceCreamMakingStatus = iceCreamMakingStatus;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }

    public int getMachineCode() {
        return MachineCode;
    }

    public void setMachineCode(int machineCode) {
        MachineCode = machineCode;
    }

    public int getMilkCupStockCondition() {
        return MilkCupStockCondition;
    }

    public void setMilkCupStockCondition(int milkCupStockCondition) {
        MilkCupStockCondition = milkCupStockCondition;
    }

    public int getJamMaterialConditions() {
        return JamMaterialConditions;
    }

    public void setJamMaterialConditions(int jamMaterialConditions) {
        JamMaterialConditions = jamMaterialConditions;
    }

    public int getTopMaterialCondition() {
        return TopMaterialCondition;
    }

    public void setTopMaterialCondition(int topMaterialCondition) {
        TopMaterialCondition = topMaterialCondition;
    }

    @Override
    public String toString() {
        return "IcecParameter{" +
                "WorkMode=" + WorkMode +
                ", NewsletterState=" + NewsletterState +
                ", PuffingGear=" + PuffingGear +
                ", RefrigerationGear=" + RefrigerationGear +
                ", TroughTemperature=" + TroughTemperature +
                ", TankTemperature=" + TankTemperature +
                ", SetPreservationTemperature=" + SetPreservationTemperature +
                ", IceCreamMakingStatus=" + IceCreamMakingStatus +
                ", StatusCode=" + StatusCode +
                ", MachineCode=" + MachineCode +
                ", MilkCupStockCondition=" + MilkCupStockCondition +
                ", JamMaterialConditions=" + JamMaterialConditions +
                ", TopMaterialCondition=" + TopMaterialCondition +
                '}';
    }
}
