package com.tcn.icecboard.DriveControl.icec;

public class MachineStateBean {
    private int FallingCupLight = -1;// 杯光检状态
    private int AntiPinchLight = -1;// 防夹手光检
    private int JamLight = -1;// 果酱光检
    private int StartSwitchX = -1;// X轴起始开关
    private int StartSwitchY = -1;//Y轴起始开关
    private int IcecSwitch = -1;// 冰淇淋开关
    private int CupDropperSwitch = -1;// 落杯器开关
    private int MotorCoordinateX = -1;// X轴电机状态
    private int MotorCoordinateY =-1;//Y轴电机状态
    private int CupDropperMotor = -1;// 落杯器电机状态
    private int JamMotor = -1;// 果酱电机状态
    private int JamControlMotor = -1;// 果酱控制电机
    private int ToppingMotor1 = -1;// 顶料电机1状态
    private int ToppingMotor2 = -1;// 顶料电机2状态
    private int ToppingMotor3 = -1;// 顶料电机3状态
    private int MotorX = -1;// x电机坐标值
    private int MotorY =  -1;//y电机坐标值
    private int CupWeight = -1;// 杯重量值


    public MachineStateBean(int fallingCupLight, int antiPinchLight, int jamLight, int startSwitchX, int startSwitchY, int icecSwitch, int cupDropperSwitch, int motorCoordinateX, int motorCoordinateY, int cupDropperMotor, int jamMotor, int jamControlMotor, int toppingMotor1, int toppingMotor2, int toppingMotor3, int motorX, int motorY, int cupWeight) {
        FallingCupLight = fallingCupLight;
        AntiPinchLight = antiPinchLight;
        JamLight = jamLight;
        StartSwitchX = startSwitchX;
        StartSwitchY = startSwitchY;
        IcecSwitch = icecSwitch;
        CupDropperSwitch = cupDropperSwitch;
        MotorCoordinateX = motorCoordinateX;
        MotorCoordinateY = motorCoordinateY;
        CupDropperMotor = cupDropperMotor;
        JamMotor = jamMotor;
        JamControlMotor = jamControlMotor;
        ToppingMotor1 = toppingMotor1;
        ToppingMotor2 = toppingMotor2;
        ToppingMotor3 = toppingMotor3;
        MotorX = motorX;
        MotorY = motorY;
        CupWeight = cupWeight;
    }

    public int getFallingCupLight() {
        return FallingCupLight;
    }

    public void setFallingCupLight(int fallingCupLight) {
        FallingCupLight = fallingCupLight;
    }

    public int getAntiPinchLight() {
        return AntiPinchLight;
    }

    public void setAntiPinchLight(int antiPinchLight) {
        AntiPinchLight = antiPinchLight;
    }

    public int getJamLight() {
        return JamLight;
    }

    public void setJamLight(int jamLight) {
        JamLight = jamLight;
    }

    public int getStartSwitchX() {
        return StartSwitchX;
    }

    public void setStartSwitchX(int startSwitchX) {
        StartSwitchX = startSwitchX;
    }

    public int getStartSwitchY() {
        return StartSwitchY;
    }

    public void setStartSwitchY(int startSwitchY) {
        StartSwitchY = startSwitchY;
    }

    public int getIcecSwitch() {
        return IcecSwitch;
    }

    public void setIcecSwitch(int icecSwitch) {
        IcecSwitch = icecSwitch;
    }

    public int getCupDropperSwitch() {
        return CupDropperSwitch;
    }

    public void setCupDropperSwitch(int cupDropperSwitch) {
        CupDropperSwitch = cupDropperSwitch;
    }

    public int getMotorCoordinateX() {
        return MotorCoordinateX;
    }

    public void setMotorCoordinateX(int motorCoordinateX) {
        MotorCoordinateX = motorCoordinateX;
    }

    public int getMotorCoordinateY() {
        return MotorCoordinateY;
    }

    public void setMotorCoordinateY(int motorCoordinateY) {
        MotorCoordinateY = motorCoordinateY;
    }

    public int getCupDropperMotor() {
        return CupDropperMotor;
    }

    public void setCupDropperMotor(int cupDropperMotor) {
        CupDropperMotor = cupDropperMotor;
    }

    public int getJamMotor() {
        return JamMotor;
    }

    public void setJamMotor(int jamMotor) {
        JamMotor = jamMotor;
    }

    public int getJamControlMotor() {
        return JamControlMotor;
    }

    public void setJamControlMotor(int jamControlMotor) {
        JamControlMotor = jamControlMotor;
    }

    public int getToppingMotor1() {
        return ToppingMotor1;
    }

    public void setToppingMotor1(int toppingMotor1) {
        ToppingMotor1 = toppingMotor1;
    }

    public int getToppingMotor2() {
        return ToppingMotor2;
    }

    public void setToppingMotor2(int toppingMotor2) {
        ToppingMotor2 = toppingMotor2;
    }

    public int getToppingMotor3() {
        return ToppingMotor3;
    }

    public void setToppingMotor3(int toppingMotor3) {
        ToppingMotor3 = toppingMotor3;
    }

    public int getMotorX() {
        return MotorX;
    }

    public void setMotorX(int motorX) {
        MotorX = motorX;
    }

    public int getMotorY() {
        return MotorY;
    }

    public void setMotorY(int motorY) {
        MotorY = motorY;
    }

    public int getCupWeight() {
        return CupWeight;
    }

    public void setCupWeight(int cupWeight) {
        CupWeight = cupWeight;
    }

    @Override
    public String toString() {
        return "MachineStateBean{" +
                "FallingCupLight=" + FallingCupLight +
                ", AntiPinchLight=" + AntiPinchLight +
                ", JamLight=" + JamLight +
                ", StartSwitchX=" + StartSwitchX +
                ", StartSwitchY=" + StartSwitchY +
                ", IcecSwitch=" + IcecSwitch +
                ", CupDropperSwitch=" + CupDropperSwitch +
                ", MotorCoordinateX=" + MotorCoordinateX +
                ", MotorCoordinateY=" + MotorCoordinateY +
                ", CupDropperMotor=" + CupDropperMotor +
                ", JamMotor=" + JamMotor +
                ", JamControlMotor=" + JamControlMotor +
                ", ToppingMotor1=" + ToppingMotor1 +
                ", ToppingMotor2=" + ToppingMotor2 +
                ", ToppingMotor3=" + ToppingMotor3 +
                ", MotorX=" + MotorX +
                ", MotorY=" + MotorY +
                ", CupWeight=" + CupWeight +
                '}';
    }
}
