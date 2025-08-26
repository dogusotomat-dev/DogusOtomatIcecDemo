package com.tcn.icecboard.DriveControl;



import com.tcn.icecboard.DriveControl.icec.IceBean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 */
public class MsgToSend {
    private int m_iSerialPortType = -1;
    private int m_iCmdType = -1;
    private int m_iSlotNo = -1;
    private int m_iAddrNum = -1;
    private int m_iParam1 = -1;
    private int m_iParam2 = -1;
    private int m_iErrCode = -1;
    private int m_iCurrentCount = 0;
    private int m_iMaxCount = 0;
    private int m_iHeatTime = -1;
    private int m_iShipMode = -1;
    private int m_iShipData1 = -1;
    private int m_iShipData2 = -1;
    private int m_iShipData3 = -1;
    private int m_iShipData4 = -1;
    private int m_iShipData5 = -1;
    private int m_iShipData6 = -1;

    private int m_iRow = -1;
    private int m_iColumn = -1;
    private int m_iBack = 0;
    private int m_iBoardType = -1;
    private int m_iKeyNum = -1;
    private int m_iIndex = -1;
    private int m_iRemainCount = 0;
    private byte m_iBoardGrp = (byte) 0xFF;
    private long m_lCmdTime = 0;
    private long m_lCmdOverTimeSpan = 0;
    private boolean m_bUseLightCheck = false;
    private boolean m_bControl = false;
    private boolean m_bValue = false;

    private String m_strPayMethod = null;
    private String m_strValue = null;
    private String m_strTradeNo = null;
    private String m_strAmount = null;


    private IceBean m_IceBean = null;


    private int m_iData = -1;
    private byte[] m_bData = null;
    private List<MTShipInfo> shipInfoList = null;
    private List<ShipSlotInfo> shipSlotInfoList = null;
    private int m_iValue = -1;


    public MsgToSend() {

    }

    public MsgToSend(MsgToSend msgToSend) {
        m_iSerialPortType = msgToSend.getSerialType();
        m_iCmdType = msgToSend.getCmdType();
        m_iSlotNo = msgToSend.getSlotNo();
        m_iAddrNum = msgToSend.getAddrNum();
        m_iParam1 = msgToSend.getPram1();
        m_iParam2 = msgToSend.getPram2();
        m_iErrCode = msgToSend.getErrCode();
        m_iCurrentCount = msgToSend.getCurrentCount();
        m_iMaxCount = msgToSend.getMaxCount();
        m_iHeatTime = msgToSend.getHeatTime();
        m_iShipMode = msgToSend.getShipMode();
        m_iShipData1 = msgToSend.getShipData1();
        m_iShipData2 = msgToSend.getShipData2();
        m_iShipData3 = msgToSend.getShipData3();
        m_iShipData4 = msgToSend.getShipData4();
        m_iShipData5 = msgToSend.getShipData5();
        m_iShipData6 = msgToSend.getShipData6();
        m_iRow = msgToSend.getRow();
        m_iColumn = msgToSend.getColumn();
        m_iBack = msgToSend.getBack();
        m_iBoardType = msgToSend.getBoardType();
        m_iKeyNum = msgToSend.getKeyNumber();
        m_iIndex = msgToSend.getIndex();
        m_iRemainCount = msgToSend.getRemainCount();
        m_iBoardGrp = msgToSend.getBoardGrp();
        m_lCmdTime = msgToSend.getCmdTime();
        m_lCmdOverTimeSpan = msgToSend.getOverTimeSpan();
        m_bUseLightCheck = msgToSend.isUseLightCheck();
        m_bControl = msgToSend.isControl();
        m_bValue = msgToSend.getBValue();
        m_strPayMethod = msgToSend.getPayMethod();
        m_strValue = msgToSend.getValue();
        m_strTradeNo = msgToSend.getTradeNo();
        m_strAmount = msgToSend.getAmount();
        m_IceBean = msgToSend.getIceBean();
        m_iData = msgToSend.getDataInt();
        m_bData = msgToSend.getDataBytes();
        shipInfoList = msgToSend.getShipInfoList();
        shipSlotInfoList = msgToSend.getShipInfoMutiList();
        m_iValue = msgToSend.getValueInt();
    }

    public MsgToSend(int serialPortType, int cmdType, int slotNo, int addrNum, int currentCount, int maxCount, int errCode, boolean useLightCheck, byte grp, long cmdTime, long cmdOverTimeSpan,
                     String payMethod, String tradeNo)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
    }
    public MsgToSend(int serialPortType, int cmdType, int slotNo, int addrNum, int currentCount, int maxCount, int errCode, boolean useLightCheck, byte grp, long cmdTime, long cmdOverTimeSpan,
                     String payMethod, String tradeNo, List<MTShipInfo> data)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
        shipInfoList = data;
    }
  public MsgToSend(int serialPortType, int cmdType, int slotNo, int addrNum, int currentCount, int maxCount, int errCode, boolean useLightCheck, byte grp, long cmdTime, long cmdOverTimeSpan,
                   String payMethod, List<MTShipInfo> data)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        shipInfoList = data;
    }

    public List<MTShipInfo> getShipInfoList() {
        return shipInfoList;
    }

    public void setShipInfoList(List<MTShipInfo> shipInfoList) {
        this.shipInfoList = shipInfoList;
    }

    public MsgToSend(int serialPortType, int cmdType, int slotNo, int addrNum, int currentCount, int maxCount, int errCode, int key, byte grp, long cmdTime, long cmdOverTimeSpan,
                     String payMethod, String tradeNo)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_iKeyNum = key;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
    }

    public MsgToSend(int serialPortType, int cmdType, int slotNo, int addrNum, int currentCount, int maxCount, int errCode, boolean useLightCheck, byte grp, long cmdTime, long cmdOverTimeSpan,
                     String payMethod, String tradeNo, int heatTime, int row, int column, int back)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
        m_iHeatTime = heatTime;
        m_iRow = row;
        m_iColumn = column;
        m_iBack = back;
    }

    public MsgToSend(int serialPortType, int cmdType, int slotNo, int addrNum, int currentCount, int maxCount, int errCode, boolean useLightCheck, byte grp, long cmdTime, long cmdOverTimeSpan,
                     String payMethod, String tradeNo, int heatTime, int shipMode, int shipData1, int shipData2, int shipData3, int shipData4, int shipData5, int shipData6)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
        m_iHeatTime = heatTime;
        m_iShipMode = shipMode;
        m_iShipData1 = shipData1;
        m_iShipData2 = shipData2;
        m_iShipData3 = shipData3;
        m_iShipData4 = shipData4;
        m_iShipData5 = shipData5;
        m_iShipData6 = shipData6;
    }

    public void setDataInt(int dataInt) {
        this.m_iData = dataInt;
    }

    public MsgToSend(int serialPortType, int cmdType, int slotNo, int addrNum, int currentCount, int maxCount, int errCode, boolean useLightCheck, byte grp, long cmdTime, long cmdOverTimeSpan,
                     String payMethod)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
    }

    public MsgToSend(int serialPortType, int cmdType, int slotNo, int addrNum, int currentCount, int maxCount, int errCode, int keyNum, byte grp, long cmdTime, long cmdOverTimeSpan,
                     String payMethod)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_iKeyNum = keyNum;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
    }

    public MsgToSend(int serialPortType, int cmdType, int slotNo, int addrNum, int currentCount, int maxCount, int errCode, boolean useLightCheck, byte grp, long cmdTime, long cmdOverTimeSpan,
                     String payMethod, int heatTime, int row, int column, int back)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_iHeatTime = heatTime;
        m_iRow = row;
        m_iColumn = column;
        m_iBack = back;
    }

    public MsgToSend(int serialPortType, int cmdType, int currentCount, int maxCount, int pram1, boolean control, byte grp, long cmdTime, long cmdOverTimeSpan)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iParam1 = pram1;
        m_bControl = control;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
    }

    public MsgToSend(int serialPortType, int cmdType, int currentCount, int maxCount, int pram1, int pram2, byte grp, long cmdTime, long cmdOverTimeSpan)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iParam1 = pram1;
        m_iParam2 = pram2;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
    }

    public MsgToSend(int serialPortType, int cmdType, int slotNo, int currentCount, int maxCount, byte grp, long cmdTime, long cmdOverTimeSpan, int idata, String value, byte[] bdata)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_iData = idata;
        m_strValue = value;
        m_bData = bdata;
    }

    public MsgToSend(int serialPortType, int cmdType, int currentCount, int maxCount, byte grp, long cmdTime, long cmdOverTimeSpan, int idata, byte[] bdata)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_iData = idata;
        m_bData = bdata;
    }

    public MsgToSend(int serialPortType, int cmdType, int slotNo, int addrNum, int currentCount, int maxCount, int errCode, byte grp, long cmdTime, long cmdOverTimeSpan,
                     String payMethod, String tradeNo, IceBean iceBean)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
        m_IceBean = iceBean;
    }

    public MsgToSend(int serialPortType, int cmdType, int slotNo, int addrNum, int currentCount, int maxCount, int errCode, byte grp, long cmdTime, long cmdOverTimeSpan,
                     String payMethod, String tradeNo)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
    }

    public void setMsgToSend(int serialPortType,int cmdType,int slotNo,int addrNum,int currentCount,int maxCount,int errCode,boolean useLightCheck,byte grp,long cmdTime,long cmdOverTimeSpan,String payMethod)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
    }

    public void setMsgToSend(int serialPortType,int cmdType,int slotNo,int addrNum,int currentCount,int maxCount,int errCode,int key,byte grp,long cmdTime,long cmdOverTimeSpan,String payMethod)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_iKeyNum = key;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
    }

    public void setMsgToSend(int serialPortType,int cmdType,int slotNo,int addrNum,int currentCount,int maxCount,int errCode,boolean useLightCheck,byte grp,long cmdTime,long cmdOverTimeSpan,
                             String payMethod,int heatTime,int row,int column,int back)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_iHeatTime = heatTime;
        m_iRow = row;
        m_iColumn = column;
        m_iBack = back;
    }

    public void setMsgToSend(int serialPortType,int cmdType,int slotNo,int addrNum,int currentCount,int maxCount,int errCode,boolean useLightCheck,byte grp,long cmdTime,long cmdOverTimeSpan
            ,String payMethod,String tradeNo,int heatTime,int row,int column,int back)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
        m_iHeatTime = heatTime;
        m_iRow = row;
        m_iColumn = column;
        m_iBack = back;
    }

    public void setMsgToSend(int serialPortType,int cmdType,int slotNo,int addrNum,int currentCount,int maxCount,int errCode,boolean useLightCheck,byte grp,long cmdTime,long cmdOverTimeSpan
            ,String payMethod,String tradeNo,int heatTime,int shipMode, int shipData1,int shipData2,int shipData3,int shipData4,int shipData5,int shipData6)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
        m_iHeatTime = heatTime;
        m_iShipMode = shipMode;
        m_iShipData1 = shipData1;
        m_iShipData2 = shipData2;
        m_iShipData3 = shipData3;
        m_iShipData4 = shipData4;
        m_iShipData5 = shipData5;
        m_iShipData6 = shipData6;
    }

    public void setMsgToSend(int serialPortType,int cmdType,int slotNo,int addrNum,int currentCount,int maxCount,int errCode,boolean useLightCheck,byte grp,long cmdTime,long cmdOverTimeSpan
            ,String payMethod,String tradeNo)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_bUseLightCheck = useLightCheck;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
    }

    public void setMsgToSend(int serialPortType,int cmdType,int slotNo,int addrNum,int currentCount,int maxCount,int errCode,int key,byte grp,long cmdTime,long cmdOverTimeSpan
            ,String payMethod,String tradeNo)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_iKeyNum = key;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
    }

    public void setMsgToSend(int serialPortType,int cmdType,int currentCount,int maxCount,int pram1,boolean control,byte grp,long cmdTime,long cmdOverTimeSpan)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iParam1 = pram1;
        m_bControl = control;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
    }

    public void setMsgToSend(int serialPortType,int cmdType,int currentCount,int maxCount,int pram1,int pram2,byte grp,long cmdTime,long cmdOverTimeSpan)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iParam1 = pram1;
        m_iParam2 = pram2;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
    }

    public void setMsgToSend(int serialPortType,int cmdType,int slotNo,int addrNum,int currentCount,int maxCount,int errCode,byte grp,long cmdTime,long cmdOverTimeSpan,
                             String payMethod,String tradeNo, IceBean iceBean)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
        m_IceBean = iceBean;
    }

    public void setMsgToSend(int serialPortType,int cmdType,int slotNo,int addrNum,int currentCount,int maxCount,int errCode,byte grp,long cmdTime,long cmdOverTimeSpan,
                             String payMethod,String tradeNo)
    {
        m_iSerialPortType = serialPortType;
        m_iCmdType = cmdType;
        m_iSlotNo = slotNo;
        m_iAddrNum = addrNum;
        m_iCurrentCount = currentCount;
        m_iMaxCount = maxCount;
        m_iErrCode = errCode;
        m_iBoardGrp = grp;
        m_lCmdTime = cmdTime;
        m_lCmdOverTimeSpan = cmdOverTimeSpan;
        m_strPayMethod = payMethod;
        m_strTradeNo = tradeNo;
    }

    public void setSerialType(int serialType)
    {
        this.m_iSerialPortType = serialType;
    }

    public int getSerialType()
    {
        return this.m_iSerialPortType;
    }

    public void setCmdType(int type)
    {
        this.m_iCmdType = type;
    }

    public int getCmdType()
    {
        return this.m_iCmdType;
    }

    public void setSlotNo(int slotNo)
    {
        this.m_iSlotNo = slotNo;
    }

    public int getSlotNo()
    {
        return this.m_iSlotNo;
    }

    public void setAddrNum(int num)
    {
        this.m_iAddrNum = num;
    }

    public int getAddrNum()
    {
        return this.m_iAddrNum;
    }

    public void setPram1(int pram1)
    {
        this.m_iParam1 = pram1;
    }

    public int getPram1()
    {
        return this.m_iParam1;
    }

    public void setPram2(int pram2)
    {
        this.m_iParam2 = pram2;
    }

    public int getPram2()
    {
        return this.m_iParam2;
    }

    public void setCurrentCount(int count)
    {
        this.m_iCurrentCount = count;
    }

    public int getCurrentCount()
    {
        return this.m_iCurrentCount;
    }

    public void setErrCode(int errCode)
    {
        this.m_iErrCode = errCode;
    }

    public int getErrCode()
    {
        return this.m_iErrCode;
    }

    public int getMaxCount()
    {
        return this.m_iMaxCount;
    }

    public void setHeatTime(int heatTime)
    {
        this.m_iHeatTime = heatTime;
    }

    public int getHeatTime()
    {
        return this.m_iHeatTime;
    }

    public void setShipMode(int shipMode)
    {
        this.m_iShipMode = shipMode;
    }

    public int getShipMode()
    {
        return this.m_iShipMode;
    }

    public void setShipData1(int shipData1)
    {
        this.m_iShipData1 = shipData1;
    }

    public int getShipData1()
    {
        return this.m_iShipData1;
    }

    public void setShipData2(int shipData2)
    {
        this.m_iShipData2 = shipData2;
    }

    public int getShipData2()
    {
        return this.m_iShipData2;
    }

    public void setShipData3(int shipData3)
    {
        this.m_iShipData3 = shipData3;
    }

    public int getShipData3()
    {
        return this.m_iShipData3;
    }

    public void setShipData4(int shipData4)
    {
        this.m_iShipData4 = shipData4;
    }

    public int getShipData4()
    {
        return this.m_iShipData4;
    }

    public void setShipData5(int shipData5)
    {
        this.m_iShipData5 = shipData5;
    }

    public int getShipData5()
    {
        return this.m_iShipData5;
    }

    public void setShipData6(int shipData6)
    {
        this.m_iShipData6 = shipData6;
    }

    public int getShipData6()
    {
        return this.m_iShipData6;
    }

    public void setRow(int row)
    {
        this.m_iRow = row;
    }

    public int getRow()
    {
        return this.m_iRow;
    }

    public void setColumn(int column)
    {
        this.m_iColumn = column;
    }

    public int getColumn()
    {
        return this.m_iColumn;
    }

    public int getBack()
    {
        return this.m_iBack;
    }

    public int getKeyNumber()
    {
        return this.m_iKeyNum;
    }

    public void setIndex(int index)
    {
        this.m_iIndex = index;
    }

    public int getIndex()
    {
        return this.m_iIndex;
    }

    public void setRemainCount(int remain)
    {
        this.m_iRemainCount = remain;
    }

    public int getRemainCount()
    {
        return this.m_iRemainCount;
    }

    public void setBoardGrp(byte boardGrp)
    {
        this.m_iBoardGrp = boardGrp;
    }

    public byte getBoardGrp()
    {
        return this.m_iBoardGrp;
    }

    public long getCmdTime()
    {
        return this.m_lCmdTime;
    }

    public long getOverTimeSpan()
    {
        return this.m_lCmdOverTimeSpan;
    }

    public boolean isUseLightCheck()
    {
        return this.m_bUseLightCheck;
    }

    public void setControl(boolean control)
    {
        m_bControl = control;
    }

    public boolean isControl()
    {
        return this.m_bControl;
    }

    public void setPayMethod(String payMethod)
    {
        this.m_strPayMethod = payMethod;
    }

    public String getPayMethod()
    {
        return this.m_strPayMethod;
    }

    public void setTradeNo(String tradeNo)
    {
        this.m_strTradeNo = tradeNo;
    }

    public String getTradeNo()
    {
        return this.m_strTradeNo;
    }

    public void setAmount(String amount)
    {
        this.m_strAmount = amount;
    }

    public String getAmount()
    {
        return this.m_strAmount;
    }

    public void setBValue(boolean bValue)
    {
        this.m_bValue = bValue;
    }

    public boolean getBValue()
    {
        return this.m_bValue;
    }

    public void setValue(String value)
    {
        this.m_strValue = value;
    }

    public String getValue()
    {
        return this.m_strValue;
    }

    public void setValueInt(int value)
    {
        this.m_iValue = value;
    }

    public int getValueInt()
    {
        return this.m_iValue;
    }

    public void setBoardType(int boardType)
    {
        this.m_iBoardType = boardType;
    }

    public int getBoardType()
    {
        return this.m_iBoardType;
    }

    public int getDataInt()
    {
        return this.m_iData;
    }

    public void setDataBytes(byte[] datas)
    {
        this.m_bData = datas;
    }

    public byte[] getDataBytes()
    {
        return this.m_bData;
    }

    public List<ShipSlotInfo> getShipInfoMutiList() {
        return shipSlotInfoList;
    }

    public void setShipInfoMutiList(List<ShipSlotInfo> shipInfoList) {
        this.shipSlotInfoList = shipInfoList;
    }

    public IceBean getIceBean() {
        return m_IceBean;
    }
}
