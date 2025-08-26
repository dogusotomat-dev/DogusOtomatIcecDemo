package com.tcn.icecboard.vend;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tcn.icecboard.DriveControl.MsgToSend;
import com.tcn.icecboard.DriveControl.Utility;
import com.tcn.icecboard.DriveControl.VendProtoControl;
import com.tcn.icecboard.DriveControl.icec.DriveIcec;
import com.tcn.icecboard.R;
import com.tcn.icecboard.TcnUtility;
import com.tcn.icecboard.control.MsgTrade;
import com.tcn.icecboard.control.TcnShareUseData;
import com.tcn.icecboard.control.TcnVendEventID;
import com.tcn.icecboard.control.TcnVendEventResultID;
import com.tcn.icecboard.control.TcnVendIF;
import com.tcn.icecboard.def.TcnProtoDef;
import com.tcn.icecboard.def.TcnProtoResultDef;
import com.tcn.icecboard.def.TcnVendCMDDef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import android_serialport_api.SerialPortController;

/**
 * 描述：
 * 作者：Jiancheng,Song on 2016/5/20 20:30
 * 邮箱：m68013@qq.com
 */
public class VendControl extends HandlerThread {
    private static final String TAG = "VendControl";
    private static final String TCN_FOLDER_KEY = "TcnKey";

    private static VendHandler m_vendHandler = null;
    private static TCNCommunicationHandler m_cmunicatHandler = null;

    private volatile String m_strTemp = "";
    private volatile String m_strTotalTemp = "";
    private volatile int m_EffectiveTime = 90;

    private volatile boolean m_bIsUnlock = false;

    private volatile int m_iTempControl = -1;
    private volatile int m_iTempControlTemp = -100;
    private volatile int m_iTempControlStartTime = -1;
    private volatile int m_iTempControlEndTime = -1;
    private volatile String m_shipTradeNo = null;
    private volatile String m_deviceUuid = null;
    private Context m_context = null;

    private Timer m_UpdatePayTimer = null;
    private TimerTask m_UpdatePayTimerTask = null;


    public VendControl(Context context, String name) {
        super(name);
        m_context = context;
    }

    public VendControl(String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {
        TcnLog.getInstance().LoggerInfoForce(TAG, "onLooperPrepared()");
        initialize();
        super.onLooperPrepared();
    }

    @Override
    public void run() {
        TcnLog.getInstance().LoggerInfoForce(TAG, "run()");
        super.run();
    }

    @Override
    public boolean quit() {
        TcnLog.getInstance().LoggerInfoForce(TAG, "quit()");
        deInitialize();
        return super.quit();
    }

    private void initialize() {

        m_vendHandler = new VendHandler();
        m_cmunicatHandler = new TCNCommunicationHandler();
        saveDeviceID();
        if (TextUtils.isEmpty(m_deviceUuid)) {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            saveDeviceID();

            if (TextUtils.isEmpty(m_deviceUuid)) {
                try {
                    sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                saveDeviceID();

                if (TextUtils.isEmpty(m_deviceUuid)) {
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    saveDeviceID();
                }
            }

        }
        m_bIsUnlock = true;//isUnlock();
//        String deiD = getDeviceID(m_context);
//        TcnLog.getInstance().LoggerInfoForce(TAG, "initialize key:"+getKeyString("dbdc25864fa59cd8c662dc1b2c9bb950"));
        VendProtoControl.getInstance().setUnlock(m_bIsUnlock);
        VendProtoControl.getInstance().initialize(TcnShareUseData.getInstance().getBoardType(), TcnShareUseData.getInstance().getBoardTypeSecond(), TcnShareUseData.getInstance().getBoardTypeThird(),
                TcnShareUseData.getInstance().getBoardTypeFourth(), TcnShareUseData.getInstance().getSerPortGroupMapFirst(), TcnShareUseData.getInstance().getSerPortGroupMapSecond(),
                TcnShareUseData.getInstance().getSerPortGroupMapThird(), TcnShareUseData.getInstance().getSerPortGroupMapFourth(), m_cmunicatHandler);

        m_vendHandler.removeMessages(TcnVendCMDDef.HANDLER_THREAD_VEND_STARED);
        m_vendHandler.sendEmptyMessage(TcnVendCMDDef.HANDLER_THREAD_VEND_STARED);
        m_vendHandler.removeMessages(TcnVendCMDDef.HANDLER_THREAD_VEND_STARED_DELAY);
        m_vendHandler.sendEmptyMessageDelayed(TcnVendCMDDef.HANDLER_THREAD_VEND_STARED_DELAY, 10000);

        reqVendLoopDelay(-1,-1,60000);

        if (!TcnVendIF.getInstance().isHasPermission()) {
            m_vendHandler.sendEmptyMessageDelayed(TcnVendCMDDef.CMD_REQ_PERMISSION, 10000);
        }
        TcnVendIF.getInstance().logFileCheck();
    }

    public void deInitialize() {
        if (m_vendHandler != null) {
            m_vendHandler.removeCallbacksAndMessages(null);
            m_vendHandler = null;
        }

        if (m_cmunicatHandler != null) {
            m_cmunicatHandler.removeCallbacksAndMessages(null);
            m_cmunicatHandler = null;
        }

        m_context = null;


        stopUpdatePayTimer();

        closeSerialPort();

        VendProtoControl.getInstance().deInit();
    }

    public Handler getCmunicatHandler() {
        return m_cmunicatHandler;
    }

    public int getTempControl() {
        return m_iTempControl;
    }

    public int getTempControlTemp() {
        return m_iTempControlTemp;
    }

    public int getTempControlStartTime() {
        return m_iTempControlStartTime;
    }

    public int getTempControlEndTime() {
        return m_iTempControlEndTime;
    }

    public void setTemperature(String temp) {
        m_strTemp = temp;
        m_strTotalTemp = temp;
    }

    public String getTemp() {
        if (m_strTotalTemp.contains("|")) {
            return m_strTotalTemp;
        }
        return m_strTemp;
    }

    public boolean isDoorOpen() {
        return VendProtoControl.getInstance().isDoorOpen();
    }

    public void setDoorOpen(boolean open) {
        VendProtoControl.getInstance().setDoorOpen(open);
    }


    private void closeSerialPort() {
        SerialPortController.getInstance().closeSerialPort();
        SerialPortController.getInstance().closeSerialPortNew();
        SerialPortController.getInstance().closeSerialPortThird();
        SerialPortController.getInstance().closeSerialPortFourth();
    }

    public void reqVendLoopDelay(int arg1, int arg2, long delayTime) {
        TcnUtility.removeMessages(m_vendHandler,TcnVendCMDDef.VEND_LOOP_MSG);
        TcnUtility.sendMsgDelayed(m_vendHandler, TcnVendCMDDef.VEND_LOOP_MSG, arg1, arg2, delayTime,null);
    }

    public void reqReadMeText() {
        TcnUtility.removeMessages(m_vendHandler, TcnVendCMDDef.QUERY_README_TEXT);
        TcnUtility.sendMsg(m_vendHandler, TcnVendCMDDef.QUERY_README_TEXT, -1, -1, null);
    }

    public void reqCopyLog() {
        TcnUtility.removeMessages(m_vendHandler, TcnVendCMDDef.REQ_COPY_LOG);
        TcnUtility.sendMsg(m_vendHandler, TcnVendCMDDef.REQ_COPY_LOG, -1, -1, null);
    }

    public void reqShip(int slotNo, int zhuliao, int dingliao, int guojiang, int zhuQ, int dingQ, int guoQ, String shipMethod, String amount, String tradeNo) {
        String methodAmountTradeNo = shipMethod + "|" + amount + "|" + tradeNo+"|"+zhuliao+"|"+dingliao+"|"+guojiang+"|"+zhuQ+"|"+dingQ+"|"+guoQ;
        TcnUtility.sendMsg(m_vendHandler, TcnVendCMDDef.REQ_SHIP, slotNo, zhuliao, methodAmountTradeNo);
    }

    private void ship(int slotNo, int heatSeconds, String shipMethod, String amount, String tradeNo,String zhuliao,String dingliao,String guojiang,String zhuQ,String dingQ,String guoQ) {
        if (!m_bIsUnlock) {
            return;
        }
        TcnLog.getInstance().LoggerInfoForce(TAG, "ship slotNo: " + slotNo + " shipMethod: " + shipMethod + " amount: " + amount + " tradeNo: " + tradeNo+" heatSeconds: "+heatSeconds);
        if (TcnVendIF.getInstance().isMachineLocked()) {
            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_MACHINE_LOCKED, -1, -1, -1, m_context.getString(R.string.board_tip_machine_locked));
            return;
        }
        if (slotNo < 1) {
            return;
        }

        if ((null == tradeNo) || (tradeNo.length() < 1) || (tradeNo.equalsIgnoreCase("null"))) {
            tradeNo = TcnVendIF.getInstance().getTradeNoNew(slotNo);
        }

        if (tradeNo.equals(m_shipTradeNo)) {
            TcnLog.getInstance().LoggerInfoForce(TAG, "ship 订单号已存在!");
            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.PROMPT_INFO, -1, -1, -1, m_context.getString(R.string.board_tip_order_used));
            return;
        }
        shipTcn(slotNo,heatSeconds, shipMethod, amount, tradeNo,zhuliao,dingliao,guojiang,zhuQ,dingQ,guoQ);
    }

    private void shipTcn(int slotNo, int heatSeconds, String shipMethod, String amount, String tradeNo,String zhuliao,String dingliao,String guojiang,String zhuQ,String dingQ,String guoQ) {

        if (slotNo < 1) {
            TcnLog.getInstance().LoggerErrorForce(TAG, "shipTcn shipMethod: " + shipMethod + " amount: " + amount + " tradeNo: " + tradeNo);
            return;
        }

        if (TextUtils.isEmpty(tradeNo)) {
            TcnLog.getInstance().LoggerErrorForce(TAG, "shipTcn tradeNo: " + tradeNo);
            return;
        }

        if (tradeNo.equals(m_shipTradeNo)) {
            TcnLog.getInstance().LoggerInfoForce(TAG, "ship 订单号已存在!");
            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.PROMPT_INFO, -1, -1, -1, m_context.getString(R.string.board_tip_order_used));
            return;
        }
        m_shipTradeNo = tradeNo;

        if (!m_bIsUnlock) {
            return;
        }

        VendProtoControl.getInstance().ship(slotNo, shipMethod,tradeNo,heatSeconds, amount,zhuliao,dingliao,guojiang,zhuQ,dingQ,guoQ);
    }

    public void closeTrade(boolean canRefund) {
        TcnUtility.removeMessages(m_vendHandler, TcnVendCMDDef.CLOSE_TRADE);
        TcnUtility.sendMsg(m_vendHandler, TcnVendCMDDef.CLOSE_TRADE, -1, -1, canRefund);
    }

    public void reqSelectGoods(int position) {
        TcnUtility.removeMessages(m_vendHandler, TcnVendCMDDef.SELECT_GOODS_REQ);
        TcnUtility.sendMsg(m_vendHandler, TcnVendCMDDef.SELECT_GOODS_REQ, position, -1, null);
    }

    public void reqEndEffectiveTime() {
        TcnUtility.removeMessages(m_vendHandler, TcnVendCMDDef.END_EFFECTIVETIME);
        TcnUtility.sendMsg(m_vendHandler, TcnVendCMDDef.END_EFFECTIVETIME, -1, -1, null);
    }

    public void reqTestSlotNo(int zhuliao,int dingliao,int guojiang,int zhuQ,int dingQ,int guoQ) {
        String parm = zhuliao+"|"+dingliao+"|"+guojiang+"|"+zhuQ+"|"+dingQ+"|"+guoQ;
        TcnUtility.sendMsg(m_vendHandler, TcnVendCMDDef.WRITE_DATA_SHIP_TEST, -1,-1,parm);
    }

    private void reqShipTest(int slotNo,int heatSeconds, CopyOnWriteArrayList<Integer> slotNoList) {
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_TEST_SLOT, slotNo, heatSeconds, slotNoList);
    }

    public void reqSelectSlotNo(int slotNo) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnVendCMDDef.SELECT_SLOTNO);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnVendCMDDef.SELECT_SLOTNO, slotNo, -1, null);
    }

    public void reqQuerySlotStatus(int slotNo) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_QUERY_SLOT_STATUS);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_QUERY_SLOT_STATUS, slotNo, -1, null);
    }

    public void reqSelfCheck(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_SELF_CHECK);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_SELF_CHECK, grpId, -1, null);
    }

    public void reqReset(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_RESET);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_RESET, grpId, -1, null);
    }

    public void reqSetSpringSlot(int slotNo) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_SPRING);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_SPRING, slotNo, -1, null);
    }

    public void reqSetBeltsSlot(int slotNo) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_BELTS);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_BELTS, slotNo, -1, null);
    }


    public void reqSpringAllSlot(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_ALL_SPRING);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_ALL_SPRING, grpId, -1, null);
    }

    public void reqBeltsAllSlot(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_ALL_BELT);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_ALL_BELT, grpId, -1, null);
    }

    public void reqSingleSlot(int slotNo) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_SINGLE);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_SINGLE, slotNo, -1, null);
    }

    public void reqDoubleSlot(int slotNo) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_DOUBLE);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_DOUBLE, slotNo, -1, null);
    }

    public void reqSingleAllSlot(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_ALL_SINGLE);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_SET_SLOTNO_ALL_SINGLE, grpId, -1, null);
    }

    public void reqTestMode(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_SET_TEST_MODE);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_SET_TEST_MODE, grpId, -1, null);
    }

    public void reqOpenCoolSpring(int grpId, int temp) {
        TcnLog.getInstance().LoggerInfoForce(TAG, "reqOpenCoolSpring temp: " + temp);
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_COOL);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_COOL, grpId, temp, null);
    }

    public void reqQueryStatus(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_QUERY_STATUS);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_QUERY_STATUS, grpId, -1, null);
    }

    public void reqHeatSpring(int grpId, int temp) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_HEAT);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_HEAT, grpId, temp, null);
    }

    public void reqCloseCoolHeatSpring(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_COOL_HEAT_CLOSE);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_COOL_HEAT_CLOSE, grpId, -1, null);
    }

    public void reqCloseCoolHeat(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_CLOSE_COOL_HEAT);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_CLOSE_COOL_HEAT, grpId, -1, null);
    }

    public void reqSetTemp(int grpId, int temp) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_TEMP);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_TEMP, grpId, temp, null);
    }

    public void reqSetGlassHeatEnable(int grpId, boolean enable) {
        if (enable) {
            TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_GLASS_HEAT_OPEN);
            TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_GLASS_HEAT_OPEN, grpId, -1, enable);
        } else {
            TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_GLASS_HEAT_CLOSE);
            TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_GLASS_HEAT_CLOSE, grpId, -1, enable);
        }
    }

    public void reqSetLedOpen(int grpId, boolean open) {
        if (open) {
            TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_LIGHT_OPEN);
            TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_LIGHT_OPEN, grpId, -1, null);
        } else {
            TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_LIGHT_CLOSE);
            TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_LIGHT_CLOSE, grpId, -1, null);
        }
    }

    public void reqSetBuzzerOpen(int grpId, boolean open) {
        if (open) {
            TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_BUZZER_OPEN);
            TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_BUZZER_OPEN, grpId, -1, null);
        } else {
            TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_BUZZER_CLOSE);
            TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_BUZZER_CLOSE, grpId, -1, null);
        }
    }


    public void reqCleanDriveFaults(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_CLEAN_FAULTS);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_CLEAN_FAULTS, grpId, -1, null);
    }

    public void reqQueryParameters(int grpId, int address) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_QUERY_PARAMETERS);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_QUERY_PARAMETERS, grpId, address, null);
    }

    public void reqSetSwitchOutPutStatus(int grpId,int number, int status) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_SWITCH_OUTPUT_STATUS);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_SWITCH_OUTPUT_STATUS, grpId, number, status);
    }

    public void reqSetId(int grpId, int id) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_ID);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_ID, grpId, id, null);
    }

    public void reqSetLightOutSteps(int grpId, int steps) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_LIGHT_OUT_STEP);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_LIGHT_OUT_STEP, grpId, steps, null);
    }

    public void reqSwitchInPutDetect(int grpId,int number) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_DETECT_SWITCH_INPUT);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_DETECT_SWITCH_INPUT, grpId, number, null);
    }

    public void reqSetParameters(int grpId, int address, String value) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_PARAMETERS);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SET_PARAMETERS, grpId, address, value);
    }

    public void reqFactoryReset(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_FACTORY_RESET);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_FACTORY_RESET, grpId, -1, null);
    }

    public void reqDetectLight(int grpId, String direction) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_DETECT_LIGHT);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_DETECT_LIGHT, grpId, -1, direction);
    }

    public void reqDetectShip(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_DETECT_SHIP);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_DETECT_SHIP, grpId, -1, null);
    }


    public void reqTakeGoodsDoorControl(int grpId, boolean bOpen) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_TAKE_GOODS_DOOR);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_TAKE_GOODS_DOOR, grpId, -1, bOpen);
    }

    public void reqLifterUp(int grpId, int floor) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_LIFTER_UP);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_LIFTER_UP, grpId, floor, null);
    }
    public void reqBackHome(int grpId) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_LIFTER_BACK_HOME);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_LIFTER_BACK_HOME, grpId, -1, null);
    }

    public void reqClapboardSwitch(int grpId, boolean bOpen) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_CLAPBOARD_SWITCH);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_CLAPBOARD_SWITCH, grpId, -1, bOpen);
    }



    //单位秒
    public void resetPayTimer(int payTime) {
        m_EffectiveTime = payTime;
    }


    private int getSlotNoForOnekeyMapFloorSlot(int key) {
        int slotNo = -1;
        if (1 == key) {
            slotNo = 1;
        } else if (2 == key) {
            slotNo = 11;
        } else if (3 == key) {
            slotNo = 21;
        } else if (4 == key) {
            slotNo = 31;
        } else if (5 == key) {
            slotNo = 41;
        } else if (6 == key) {
            slotNo = 51;
        } else {

        }

        return slotNo;
    }

    private void startUpdatePayTimer() {

        m_EffectiveTime = TcnShareUseData.getInstance().getPayTime();

        if (null == m_UpdatePayTimer) {
            m_UpdatePayTimer = new Timer("startUpdatePayTimer");
        }
        if (m_UpdatePayTimerTask != null) {
            m_UpdatePayTimerTask.cancel();
            m_UpdatePayTimerTask = null;
        }
        m_UpdatePayTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (m_EffectiveTime >= 0) {
                    m_EffectiveTime--;
                    TcnUtility.removeMessages(m_vendHandler, TcnVendCMDDef.UPDATE_PAY_TIME);
                    TcnUtility.sendMsg(m_vendHandler, TcnVendCMDDef.UPDATE_PAY_TIME, m_EffectiveTime, -1, null);
                }
            }
        };
        m_UpdatePayTimer.schedule(m_UpdatePayTimerTask, 0, 1000);
    }

    private void stopUpdatePayTimer() {
        if (m_UpdatePayTimer != null) {
            m_UpdatePayTimer.cancel();
            m_UpdatePayTimer.purge();
            m_UpdatePayTimer = null;
        }

        if (m_UpdatePayTimerTask != null) {
            m_UpdatePayTimerTask.cancel();
            m_UpdatePayTimerTask = null;
        }
    }

    private void OnSystemBusy() {
        TcnLog.getInstance().LoggerInfoForce(TAG, "OnSystemBusy");
        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.COMMAND_SYSTEM_BUSY, -1, -1, -1, m_context.getString(R.string.board_notify_sys_busy));
    }

    private void OnInvalidSlotNo() {
        TcnLog.getInstance().LoggerInfoForce(TAG, "OnInvalidSlotNo");
        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.COMMAND_INVALID_SLOTNO, -1, -1, -1, m_context.getString(R.string.board_notify_invalid_slot));
    }

    private void OnInvalidSlotNo(int slotNo) {
        TcnLog.getInstance().LoggerInfoForce(TAG, "OnInvalidSlotNo slotNo: " + slotNo);
        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.COMMAND_INVALID_SLOTNO, slotNo, -1, -1, m_context.getString(R.string.board_notify_invalid_slot));
    }

    private void OnFaultSlotNo(int slotNoOrKey) {
        TcnLog.getInstance().LoggerInfoForce(TAG, "OnFaultSlotNo slotNoOrKey: " + slotNoOrKey);
        String speakText = m_context.getString(R.string.board_notify_slot_faultSlot) + String.valueOf(slotNoOrKey);
        ;
        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.COMMAND_FAULT_SLOTNO, slotNoOrKey, -1, -1, speakText);
    }

    private void OnFaultSlotNo(int slotNo, int errCode) {
        TcnLog.getInstance().LoggerInfoForce(TAG, "OnFaultSlotNo slotNo: " + slotNo + " errCode: " + errCode);
        String speakText = null;
        if (slotNo > 0) {
            if (errCode > 0) {
                speakText = m_context.getString(R.string.board_notify_slot_fault) + String.valueOf(slotNo) + "\n" + m_context.getString(R.string.board_notify_slot_code) + String.valueOf(errCode);
            } else {
                speakText = m_context.getString(R.string.board_notify_slot_fault) + String.valueOf(slotNo);
            }
        } else {
            speakText = m_context.getString(R.string.board_notify_slot_faultSlot);
        }
        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.COMMAND_FAULT_SLOTNO, slotNo, errCode, -1, speakText);
    }

    private void OnSelectFail(int slotNo, int errCode, int boardType) {
        TcnLog.getInstance().LoggerInfoForce(TAG, "OnSelectFail slotNo: " + slotNo + " errCode: " + errCode + " boardType: " + boardType);
        String strMsg = getErrCodeMessageSpring(false, errCode);
        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.COMMAND_SELECT_FAIL, slotNo, errCode, -1, strMsg);
    }

    private void OnSelectSlotNo(int slotNo) {
        if (!m_bIsUnlock) {
            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.PROMPT_INFO, -1, -1, -1, "请先授权！");
            return;
        }
        if (TcnVendIF.getInstance().isMachineLocked()) {
            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_MACHINE_LOCKED, slotNo, -1, -1, m_context.getString(R.string.board_tip_machine_locked));
            return;
        }

        VendProtoControl.getInstance().reqSelectSlotNo(slotNo);
    }

    private void OnSelectedSlotNo(int slotNo) {

        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.COMMAND_SELECT_GOODS, slotNo, -1, -1, null);
    }

    private void OnShipForTestSlot(int slotNo, int errCode, int shipStatus) {
        TcnLog.getInstance().LoggerInfoForce(TAG, "OnShipForTestSlot slotNo: " + slotNo + " errCode: " + errCode + " shipStatus: " + shipStatus);
        if (TcnProtoResultDef.SHIP_SHIPING == shipStatus) {
            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_TEST_SLOT, slotNo, errCode, TcnVendEventResultID.SHIP_SHIPING, null);
        } else if (TcnProtoResultDef.SHIP_SUCCESS == shipStatus) {
            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_TEST_SLOT, slotNo, errCode, TcnVendEventResultID.SHIP_SUCCESS, null);
        } else if (TcnProtoResultDef.SHIP_FAIL == shipStatus) {
            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_TEST_SLOT, slotNo, errCode, TcnVendEventResultID.SHIP_FAIL, null);
        } else {

        }
    }

    private void OnShipMessage(int slotNo, int shipStatus,String tradeNo) {
        if (TcnProtoResultDef.SHIP_SHIPING == shipStatus) {
            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.COMMAND_SHIPPING, slotNo, -1, -1, m_context.getString(R.string.board_notify_shipping));
        } else if (TcnProtoResultDef.SHIP_SUCCESS == shipStatus) {
            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.COMMAND_SHIPMENT_SUCCESS, slotNo, -1, -1, m_context.getString(R.string.board_notify_shipsuc_rec_notify));
        } else if (TcnProtoResultDef.SHIP_FAIL == shipStatus) {
            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.COMMAND_SHIPMENT_FAILURE, slotNo, -1, -1, m_context.getString(R.string.board_notify_fail_contact));
        } else {

        }
    }

    private void OnShipWithMethod(int shipMethod, int slotNo, int shipStatus, MsgToSend msgToSend) {

        if (null == msgToSend) {
            TcnLog.getInstance().LoggerErrorForce(TAG, "OnShipWithMethod slotNo: " + slotNo + " shipStatus: " + shipStatus + " shipMethod: " + shipMethod);
            return;
        }
        TcnLog.getInstance().LoggerInfoForce(TAG, "OnShip slotNo: " + slotNo + " shipStatus: " + shipStatus + " errCode: " + msgToSend.getErrCode()
                + " shipMethod: " + shipMethod + " PayMethod: " + msgToSend.getPayMethod() + " TradeNo: " + msgToSend.getTradeNo());

        stopUpdatePayTimer();
        if (TcnProtoResultDef.SHIP_SHIPING == shipStatus) {
            //
        } else if (TcnProtoResultDef.SHIP_SUCCESS == shipStatus) {


        } else {    //出货失败

        }

        OnShipMessage(slotNo, shipStatus,msgToSend.getTradeNo());
    }

    private void OnEndEffectiveTime() {
	    TcnLog.getInstance().LoggerInfoForce(TAG, "OnEndEffectiveTime()");
        stopUpdatePayTimer();
	    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.BACK_TO_SHOPPING, -1, -1, -1, null);
    }

    private void OnTestSlotNo(int start, int end,int heatSeconds) {
        TcnLog.getInstance().LoggerInfoForce(TAG, "OnTestSlotNo start: " + start + " end: " + end+" heatSeconds: "+heatSeconds);
        if (start < 1) {
            return;
        }
        if (TcnVendIF.getInstance().handleShipTest(start, end)) {
            return;
        }
        if ((start == end) || (end < 1)) {
            reqShipTest(start, heatSeconds,null);
            return;
        }
        CopyOnWriteArrayList<Integer> slotNoList = new CopyOnWriteArrayList<Integer>();
        for (int i = start; i <= end; i++) {
            slotNoList.add(i);
        }
        reqShipTest(start, heatSeconds,slotNoList);
    }

    //升降机故障代码
    private String getErrCodeMessageSpring(boolean isSet, int errCode) {
        TcnLog.getInstance().LoggerInfoForce(TAG, "getErrCodeMessageSpring() errCode: " + errCode);
        StringBuffer errMsg = new StringBuffer();
        if (errCode == VendProtoControl.ERROR_SPRING_CODE_0) {
            if (isSet) {
                errMsg.append(m_context.getString(R.string.board_drive_success));
            } else {
                errMsg.append(m_context.getString(R.string.board_drive_errcode_normal));
            }
            return errMsg.toString();
        }
        errMsg.append(m_context.getString(R.string.board_drive_errcode));
        errMsg.append(errCode);
        errMsg.append(" ");
        if (errCode == VendProtoControl.ERROR_SPRING_CODE_1) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_1));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_2) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_2));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_3) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_3));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_4) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_4));
        } else if (errCode == VendProtoControl.ERROR_CODE_22) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_16));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_23) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_17));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_24) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_18));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_25) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_19));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_50) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_32));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_51) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_33));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_52) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_34));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_53) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_35));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_72) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_48));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_73) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_49));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_80) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_50));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_81) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_51));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_100) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_64));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_101) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_65));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_102) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_66));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_103) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_67));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_80) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_80));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_81) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_81));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_130) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_82));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_131) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_83));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_132) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_84));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_134) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_86));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_135) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_87));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_144) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_90));
        } else if (errCode == VendProtoControl.ERROR_SPRING_CODE_145) {
            errMsg.append(m_context.getString(R.string.board_spring_errcode_91));
        } else if (errCode == VendProtoControl.ERROR_CODE_255) {
            errMsg.append(m_context.getString(R.string.board_drive_errcode_255));
        } else {

        }
        return errMsg.toString();
    }
	private int getStatusLifter(int status) {
		int iRetStatus = TcnVendEventResultID.STATUS_INVALID;
		if (TcnProtoResultDef.STATUS_FREE == status) {
			iRetStatus = TcnVendEventResultID.STATUS_FREE;
		} else if (TcnProtoResultDef.STATUS_BUSY == status) {
			iRetStatus = TcnVendEventResultID.STATUS_BUSY;
		} else if (TcnProtoResultDef.STATUS_WAIT_TAKE_GOODS == status) {
			iRetStatus = TcnVendEventResultID.STATUS_WAIT_TAKE_GOODS;
		} else if (TcnProtoResultDef.STATUS_HEATING == status) {
			iRetStatus = TcnVendEventResultID.STATUS_HEATING;
		} else if (TcnProtoResultDef.STATUS_HEATING_START == status) {
			iRetStatus = TcnVendEventResultID.STATUS_HEATING_START;
		} else if (TcnProtoResultDef.STATUS_HEATING_END == status) {
			iRetStatus = TcnVendEventResultID.STATUS_HEATING_END;
		} else if (TcnProtoResultDef.CMD_NO_DATA_RECIVE == status) {
			iRetStatus = TcnVendEventResultID.CMD_NO_DATA_RECIVE;
		} else {

		}
		return iRetStatus;
	}

	private int getStatusStartEndLifter(int status) {
		int iRetStatus = TcnVendEventResultID.DO_NONE;
		if (TcnProtoResultDef.DO_START == status) {
			iRetStatus = TcnVendEventResultID.DO_START;
		} else if (TcnProtoResultDef.DO_END == status) {
			iRetStatus = TcnVendEventResultID.DO_END;
		} else {

		}
		return iRetStatus;
	}

    //升降机故障代码
    private String getErrCodeMessageLifter(boolean isSet, int boardType, int errCode) {
        TcnVendIF.getInstance().LoggerInfoForce(TAG, "getErrCodeMessageLifter() errCode: " + errCode+" boardType: "+boardType);
        StringBuffer errMsg = new StringBuffer();
        if (errCode == VendProtoControl.ERROR_CODE_BUSY) {
            errMsg.append(m_context.getString(R.string.board_notify_sys_busy));
            return errMsg.toString();
        }
        if (errCode == VendProtoControl.ERROR_LIFT_CODE_WAIT_TAKE_GOODS) {
            errMsg.append(m_context.getString(R.string.board_notify_receive_goods));
            return errMsg.toString();
        }
        if (errCode == VendProtoControl.ERROR_LIFT_CODE_0) {
            if (isSet) {
                errMsg.append(m_context.getString(R.string.board_drive_success));
            } else {
                errMsg.append(m_context.getString(R.string.board_drive_errcode_normal));
            }
            return errMsg.toString();
        }
        errMsg.append(m_context.getString(R.string.board_drive_errcode));
        errMsg.append(errCode);
        errMsg.append(" ");

        switch (boardType) {
//            case VendProtoControl.BOARD_LIFT_HEFAN_ZP:
//                getErrCodeMessageHefanZp(errMsg,errCode);
//                break;
            default:
                break;
        }

        return errMsg.toString();
    }

    private String getErrCodeMessageHefanZp(StringBuffer errMsg, int errCode) {
        switch (errCode) {
            case VendProtoControl.ERROR_LIFT_CODE_1:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_1_hefan));
                break;
            case VendProtoControl.ERROR_LIFT_CODE_2:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_2_hefan));
                break;
            case VendProtoControl.ERROR_LIFT_CODE_3:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_3_hefan));
                break;
            case VendProtoControl.ERROR_LIFT_CODE_4:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_4_hefan));
                break;
            case VendProtoControl.ERROR_LIFT_CODE_5:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_5_hefan));
                break;
            case VendProtoControl.ERROR_LIFT_CODE_6:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_6_hefan));
                break;
            case VendProtoControl.ERROR_LIFT_CODE_7:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_7_hefan));
                break;
            case VendProtoControl.ERROR_LIFT_CODE_8:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_8_hefan));
                break;
            case VendProtoControl.ERROR_LIFT_CODE_9:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_9_hefan));
                break;
            case VendProtoControl.ERROR_LIFT_CODE_10:
                errMsg.append("移动平台有商品");
                break;
            case 11:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_11_hefan));
                break;
            case 12:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_12_hefan));
                break;
            case 13:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_13_hefan));
                break;
            case 14:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_14_hefan));
                break;
            case 15:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_15_hefan));
                break;
            case 16:
                errMsg.append("升降电机超出最大步数");
                break;
            case 17:
                errMsg.append("平移电机超出最大步数");
                break;
            case 18:
                errMsg.append("加热平台升降电机断线");
                break;
            case 19:
                errMsg.append("加热平台升降电机过流");
                break;
            case 20:
                errMsg.append("加热平台升降电机超时");
                break;
            case 21:
                errMsg.append("磁控管1完全没有工作");
                break;
            case 22:
                errMsg.append("磁控管2完全没有工作");
                break;
            case 23:
                errMsg.append("磁控管1和磁控管2完全没有工作");
                break;
            case 24:
                errMsg.append("磁控管3完全没有工作");
                break;
            case 25:
                errMsg.append("磁控管1和磁控管3完全没有工作");
                break;
            case 26:
                errMsg.append("磁控管2和磁控管3完全没有工作");
                break;
            case 27:
                errMsg.append("磁控管1磁控管2和磁控管3完全没有工作");
                break;
            case 28:
                errMsg.append("磁控管1工作一定时间后停止工作（可能过热保护/电池保护）");
                break;
            case 29:
                errMsg.append("磁控管2工作一定时间后停止工作（可能过热保护）");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_30:
                errMsg.append("磁控管1和磁控管2工作一定时间后停止工作（可能过热保护）");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_31:
                errMsg.append("磁控管3工作一定时间后停止工作（可能过热保护）");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_32:
                errMsg.append("磁控管1和磁控管3工作一定时间后停止工作（可能过热保护）");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_33:
                errMsg.append("磁控管2和磁控管3工作一定时间后停止工作（可能过热保护）");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_34:
                errMsg.append("磁控管1磁控管2和磁控管3工作一定时间后停止工作（可能过热保护）");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_35:
                errMsg.append("磁控管1控制继电器粘连");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_36:
                errMsg.append("磁控管2控制继电器粘连");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_37:
                errMsg.append("磁控管3控制继电器粘连");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_40:
                errMsg.append("货道故障/没有找到对应货道层");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_51:
                errMsg.append("取货门电机断线");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_52:
                errMsg.append("取货门电机过流");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_53:
                errMsg.append("取货门电机超时");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_54:
                errMsg.append("防夹手光检被挡住");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_55:
                errMsg.append("取货口推货电机断线");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_56:
                errMsg.append("取货口推货电机过流");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_57:
                errMsg.append("取货口推货电机超时");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_58:
                errMsg.append("取货口推货电机堵转");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_59:
                errMsg.append("出货皮带电机断线");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_60:
                errMsg.append("出货皮带电机过流");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_61:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_61));
                break;
            case VendProtoControl.ERROR_LIFT_CODE_64:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_64));
                break;
            case 71:
                errMsg.append("转盘电机断线");
                break;
            case 72:
                errMsg.append("转盘电机过流");
                break;
            case 73:
                errMsg.append("转盘电机超时");
                break;
            case 74:
                errMsg.append("转盘电机堵转");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_80:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_80));
                break;
            case 101:
                errMsg.append("取货口有商品");
                break;
            case 102:
                errMsg.append("加热平台没有检测到商品（可能卡在侧门）");
                break;
            case VendProtoControl.ERROR_LIFT_CODE_127:
                errMsg.append(m_context.getString(R.string.board_lift_errcode_127));
                break;
            case 240:
                errMsg.append("存储芯片故障");
                break;
            case 255:
                errMsg.append("通信故障");
                break;
            default:
                break;
        }

        return errMsg.toString();
    }


    /***********************  冰淇淋 start *******************************************/

    public void reqSetWorkMode(int workModeLeft, int workModeRight) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_ICE_SET_WORK_MODE);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_ICE_SET_WORK_MODE, workModeLeft, workModeRight, null);
    }

    public void reqSetParamIceMake(int positionCoolLeft, int positionCoolRight, int coolTempLeft, int coolTempRight, int coolStorage, int coolFree) {
        MsgSend mMsgSend = new MsgSend();
        mMsgSend.setData1(coolTempLeft);
        mMsgSend.setData2(coolTempRight);
        mMsgSend.setData3(coolStorage);
        mMsgSend.setData4(coolFree);
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_PARAM_ICE_MAKE_SET);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_PARAM_ICE_MAKE_SET, positionCoolLeft, positionCoolRight, mMsgSend);
    }

    public void reqQueryParamIceMake() {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_PARAM_ICE_MAKE_QUERY);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_PARAM_ICE_MAKE_QUERY, -1, -1, null);
    }

    public void reqQueryParam(int operaItem, int operaPosition) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_PARAM_QUERY);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_PARAM_QUERY, operaItem, operaPosition, null);
    }

    public void reqQueryParamAll(int operaItem, int operaPosition) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_PARAM_AUERY_ALL);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_PARAM_AUERY_ALL, operaItem, operaPosition, null);
    }

    public void reqParamSet(int operaItem, int operaPosition, int data) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_PARAM_SET);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_PARAM_SET, operaItem, operaPosition, data);
    }

//    public void reqIceLogout(int operaItem) {
//        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_QUERY_LOGOUT);
//        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_QUERY_LOGOUT, operaItem, -1, null);
//    }

    public void reqMove(int operaPosition, int data) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_POSITION_MOVE);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_POSITION_MOVE, operaPosition, data, null);
    }

    public void reqQueryStatusAndJudge(int operaItem) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_QUERY_STATUS_AND_JUDGE);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_QUERY_STATUS_AND_JUDGE, operaItem, -1, null);
    }

    public void reMachineSlefTest() {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SELF_INSPECTION);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_SELF_INSPECTION, -1, -1, null);
    }

    public void reTestDischarge(int testProject, int testPosition) {
        TcnUtility.removeMessages(m_cmunicatHandler, TcnProtoDef.REQ_CMD_TEST_DISCHARGE);
        TcnUtility.sendMsg(m_cmunicatHandler, TcnProtoDef.REQ_CMD_TEST_DISCHARGE, testProject, testPosition, null);

    }
    /***********************  冰淇淋 end *******************************************/


    private void saveDeviceID() {
        if (createFile(TCN_FOLDER_KEY,VendProtoControl.TCN_DEVID_KEY)) {
            String DeviceID = getDeviceID(m_context);
//            TcnLog.getInstance().LoggerInfoForce(TAG, "saveDeviceID DeviceID: " + DeviceID);
            if (!TextUtils.isEmpty(DeviceID)) {
                m_deviceUuid = DeviceID;
                writeFileByLine(DeviceID,TCN_FOLDER_KEY,VendProtoControl.TCN_DEVID_KEY);
            }

        }
    }

    /**
     * 写入内容到txt文本中
     * str为内容
     */
    private void writeFileByLine(String data,String filePath,String fileName) {
        String mStrRootPath = TcnUtility.getExternalStorageDirectory();
        String mDirPath = filePath;
        if (!filePath.startsWith(mStrRootPath)) {
            mDirPath = mStrRootPath + "/" + filePath;
        }
        String mFilePath = mDirPath  + "/"+ fileName;
        FileWriter fw = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            fw = new FileWriter(mFilePath, false);
            fw.write(data);
            fw.close();
        } catch (Exception e) {

        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getDeviceID(Context aContext) {

//        String[] mPermissionArry = new String[1];
//        mPermissionArry[0] = Manifest.permission.READ_PHONE_STATE;
//        ActivityCompat.requestPermissions(m_context,mPermissionArry,128);
        String readDeviceID = null;
      /*  try {
            if (ActivityCompat.checkSelfPermission(m_context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            String androidId = Settings.Secure.getString(aContext.getContentResolver(), Settings.System.ANDROID_ID);

            UUID deviceUuid = null;

            if (!"9774d56d682e549c".equals(androidId)) {    //无需任何许可
                deviceUuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else {
                final String deviceId = ((TelephonyManager) m_context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                deviceUuid = deviceId!=null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            }
            ret = deviceUuid.toString();
        } catch (Exception var7) {
            var7.printStackTrace();
        }*/

        try {
            String folderPath = TcnUtility.getExternalStorageDirectory() + "/" +TCN_FOLDER_KEY;
            //获取保存在sd中的 设备唯一标识符
            readDeviceID = GetDeviceId.getDeviceId(m_context,folderPath,VendProtoControl.TCN_DEVID_KEY);
            //左后再次更新app 的缓存
            if (!TextUtils.isEmpty(readDeviceID)) {
                TcnShareUseData.getInstance().setDeviceID(readDeviceID);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return readDeviceID;
    }

    private boolean isUnlock() {
        boolean bRet = false;
        if (createFoldersAndExist(TCN_FOLDER_KEY)) {
            if (createFile(TCN_FOLDER_KEY,VendProtoControl.TCN_FILE_KEY)) {
                String tcn_password = readFile(TCN_FOLDER_KEY,VendProtoControl.TCN_FILE_KEY);

                if (TextUtils.isEmpty(tcn_password)) {
                    return bRet;
                }
                String mKey = getKeyString();
                if (tcn_password.equals(mKey)) {
                    bRet = true;
                }
            }

        }
        return bRet;
    }

    private String getKeyString() {
        String deviceUuid = getDeviceID(m_context);
        if (TextUtils.isEmpty(deviceUuid)) {
            deviceUuid = m_deviceUuid;
        }
        String keyData = Utility.tcnLock(Utility.tcnLock(VendProtoControl.YS_LOG + deviceUuid));
        if (!TextUtils.isEmpty(keyData)) {
            keyData = Utility.tcnLock("sdk"+keyData);

            if (!TextUtils.isEmpty(keyData)) {
                keyData = keyData.trim();
            }
        }

        return keyData;
    }

    private String getKeyString(String deviceUuid) {
        String keyData = Utility.tcnLock(Utility.tcnLock(VendProtoControl.YS_LOG + deviceUuid));
        if (!TextUtils.isEmpty(keyData)) {
            keyData = Utility.tcnLock("sdk"+keyData);

            if (!TextUtils.isEmpty(keyData)) {
                keyData = keyData.trim();
            }
        }

        return keyData;
    }

    /**
     * 读取文本文件
     *
     * @param fileName
     * @return
     */
    public String readFile(String filePath,String fileName) {
        if ((null == filePath) || (null == fileName)) {
            return null;
        }
        String mStrRootPath = TcnUtility.getExternalStorageDirectory();
        if (!filePath.startsWith(mStrRootPath)) {
            filePath = mStrRootPath + "/" + filePath;
        }
        String mfile = filePath + "/"+fileName;
        StringBuffer sb = new StringBuffer();
        File file = new File(mfile);
        if (file == null || !file.exists() || file.isDirectory()) {
            Log.e("file", "readFile return.");
            return null;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while((line = br.readLine()) != null){
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString().trim();
    }

    private boolean createFile(String filePath, String fileName) {
        boolean bRet = false;
        String mDirPath = filePath;
        try {
            String mStrRootPath = TcnUtility.getExternalStorageDirectory();
            if (!filePath.startsWith(mStrRootPath)) {
                mDirPath = mStrRootPath+"/"+filePath;
            }

            File mDir = new File(mDirPath.trim());
            if (!mDir.exists()) {
                mDir.mkdir();
            }
            String mFilePath = mDirPath  + "/"+ fileName;
            File mFile = new File(mFilePath.trim());
            if (!mFile.exists()) {
                mFile.createNewFile();
            }
            if((mFile.exists()) && (mFile.isFile())) {
                bRet = true;
            }
            return bRet;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bRet;
    }

    private boolean createFoldersAndExist(String filePath) {
        boolean bRet = false;

        if ((filePath == null) || (filePath.length() < 1)) {
            return bRet;
        }
        try {
            String mStrRootPath = TcnUtility.getExternalStorageDirectory();
            String tmpPath = filePath;
            if (!filePath.startsWith(mStrRootPath)) {
                tmpPath = mStrRootPath+"/"+filePath;
            }

            File dir = new File(tmpPath);

            if((dir.exists()) && (dir.isDirectory())) {
                bRet = true;
                return bRet;
            }

            dir.mkdirs();

            if((dir.exists()) && (dir.isDirectory())) {
                bRet = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bRet;
    }

    private void setComMessageCopy(Message message,int what,int arg1, int arg2, int obj) {
        if (null == message) {
           return;
        }
        message.what = what;
        message.arg1 = arg1;
        message.arg2 = arg2;
        message.obj = obj;
    }

    private class VendHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case TcnVendCMDDef.HANDLER_THREAD_VEND_STARED:
                    break;
                case TcnVendCMDDef.HANDLER_THREAD_VEND_STARED_DELAY:
                    if (!m_bIsUnlock) {
                        saveDeviceID();
                        m_bIsUnlock = isUnlock();
                        TcnLog.getInstance().LoggerInfoForce(TAG, "HANDLER_THREAD_VEND_STARED_DELAY m_bIsUnlock: "+m_bIsUnlock);
                        VendProtoControl.getInstance().setUnlock(m_bIsUnlock);
                    }
                    break;
                case TcnVendCMDDef.VEND_LOOP_MSG:
                    break;
                case TcnVendCMDDef.CMD_REQ_PERMISSION:
                    if (!TcnVendIF.getInstance().isHasPermission()) {
	                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_REQ_PERMISSION, -1, -1, -1, TcnVendIF.getInstance().getPermission());
                        m_vendHandler.removeMessages(TcnVendCMDDef.CMD_REQ_PERMISSION);
                        m_vendHandler.sendEmptyMessageDelayed(TcnVendCMDDef.CMD_REQ_PERMISSION, 10000);
                    } else {
                        saveDeviceID();
                        m_bIsUnlock = isUnlock();
                        VendProtoControl.getInstance().setUnlock(m_bIsUnlock);
                    }
                    break;
                case TcnVendCMDDef.END_EFFECTIVETIME:
                    OnEndEffectiveTime();
                    break;
                case TcnVendCMDDef.WRITE_DATA_SHIP_TEST:
                    String icecparm = (String) msg.obj;
                    if ((icecparm != null) && (icecparm.contains("|"))) {
                        String[] strarr = icecparm.split("\\|");
                        if (strarr.length == 6) {
                            String zhuliao = strarr[0];
                            String dingliao = strarr[1];
                            String guojiang = strarr[2];
                            String zhuQ = strarr[3];
                            String dingQ = strarr[4];
                            String guoQ = strarr[5];
                            VendProtoControl.getInstance().reqWriteDataShipTest(zhuliao,dingliao,guojiang,zhuQ,dingQ,guoQ);
                        }
                    }

                    break;
                case TcnVendCMDDef.CLOSE_TRADE:
                    break;
                case TcnVendCMDDef.SELECT_SLOTNO:
                    OnSelectSlotNo(msg.arg1);
                    break;
                case TcnVendCMDDef.UPDATE_TIME:
                    break;
                case TcnVendCMDDef.TEMPERATURE_INFO:
                    String temper = (String) msg.obj;
                    if ((temper != null) && (temper.length() > 0)) {
                        temper = m_context.getString(R.string.board_current_temperature) + temper + "℃";
	                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.TEMPERATURE_INFO, -1, -1, -1, temper);
                    } else {
	                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.TEMPERATURE_INFO, -1, -1, -1, "");
                    }
                    if ((temper != null) && (temper.length() > 0) && (!temper.equals(m_strTemp))) {
                        // TcnVendIF.getInstance().sendMessageTempServer(temper);
                    }
                    m_strTemp = (String) msg.obj;
                    break;
                case TcnVendCMDDef.REQ_SHIP:
                    String methodAmountTradeNo = (String) msg.obj;
                    TcnLog.getInstance().LoggerInfoForce(TAG, "REQ_SHIP arg1: " + msg.arg1 + " methodAmountTradeNo: " + methodAmountTradeNo);
                    if ((methodAmountTradeNo != null) && (methodAmountTradeNo.contains("|"))) {
                        String[] strarr = methodAmountTradeNo.split("\\|");
                        if (strarr.length == 9) {
                            String method = strarr[0];
                            String amount = strarr[1];
                            String tradeNo = strarr[2];
                            String zhuliao = strarr[3];
                            String dingliao = strarr[4];
                            String guojiang = strarr[5];
                            String zhuQ = strarr[6];
                            String dingQ = strarr[7];
                            String guoQ = strarr[8];
                            ship(msg.arg1,msg.arg2, method, amount, tradeNo,zhuliao,dingliao,guojiang,zhuQ,dingQ,guoQ);
                        }
                    }
                    break;
                case TcnVendCMDDef.REQ_SHIP_MUTI:

                    break;
                case TcnVendCMDDef.REQ_SHIP_MUTI_CONTIN_AUTO:
                    break;
                default:
                    break;
            }
            TcnVendIF.getInstance().handleVendMessage(msg);
        }
    }

    private class TCNCommunicationHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case TcnProtoDef.SERIAL_PORT_CONFIG_ERROR:
	                TcnLog.getInstance().LoggerInfoForce(TAG, "SERIAL_PORT_CONFIG_ERROR");
	                TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.SERIAL_PORT_CONFIG_ERROR, -1, -1, -1, null);
                    break;
                case TcnProtoDef.SERIAL_PORT_SECURITY_ERROR:
	                TcnLog.getInstance().LoggerInfoForce(TAG, "SERIAL_PORT_SECURITY_ERROR");
	                TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.SERIAL_PORT_SECURITY_ERROR, -1, -1, -1, null);
                    break;
                case TcnProtoDef.SERIAL_PORT_UNKNOWN_ERROR:
	                TcnLog.getInstance().LoggerInfoForce(TAG, "SERIAL_PORT_UNKNOWN_ERROR");
	                TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.SERIAL_PORT_UNKNOWN_ERROR, -1, -1, -1, null);
                    break;
                case TcnProtoDef.CMD_LOOP:
                    break;
                case TcnVendCMDDef.SELECT_SLOTNO:
                    OnSelectSlotNo(msg.arg1);
                    break;
                case TcnProtoDef.COMMAND_SELECT_SLOTNO:
                    OnSelectedSlotNo(msg.arg1);
                    break;
                case TcnProtoDef.COMMAND_INVALID_SLOTNO:
                    OnInvalidSlotNo(msg.arg1);
                    break;
                case TcnProtoDef.COMMAND_SELECT_FAIL:
                    OnSelectFail(msg.arg1, msg.arg2, (Integer) msg.obj);
                    break;
                case TcnProtoDef.COMMAND_SHIPMENT_CASHPAY:
                    MsgToSend mMsgCash = (MsgToSend) msg.obj;
                    OnShipWithMethod(msg.what, msg.arg1, msg.arg2, mMsgCash);
                    if (mMsgCash != null) {
                        MsgTrade msgTrade = new MsgTrade(msg.arg1,mMsgCash.getErrCode(),mMsgCash.getTradeNo(),mMsgCash.getAmount());
                        msg.obj = msgTrade;
                    }
                    break;
                case TcnProtoDef.COMMAND_SHIPMENT_WECHATPAY:
                    MsgToSend mMsgWx = (MsgToSend) msg.obj;
                    OnShipWithMethod(msg.what, msg.arg1, msg.arg2, mMsgWx);
                    if (mMsgWx != null) {
                        MsgTrade msgTrade = new MsgTrade(msg.arg1,mMsgWx.getErrCode(),mMsgWx.getTradeNo(),mMsgWx.getAmount());
                        msg.obj = msgTrade;
                    }
                    break;
                case TcnProtoDef.COMMAND_SHIPMENT_ALIPAY:
                    MsgToSend mMsgAli = (MsgToSend) msg.obj;
                    OnShipWithMethod(msg.what, msg.arg1, msg.arg2, mMsgAli);
                    if (mMsgAli != null) {
                        MsgTrade msgTrade = new MsgTrade(msg.arg1,mMsgAli.getErrCode(),mMsgAli.getTradeNo(),mMsgAli.getAmount());
                        msg.obj = msgTrade;
                    }
                    break;
                case TcnProtoDef.COMMAND_SHIPMENT_GIFTS:
                    MsgToSend mMsgGift = (MsgToSend) msg.obj;
                    OnShipWithMethod(msg.what, msg.arg1, msg.arg2, mMsgGift);
                    if (mMsgGift != null) {
                        MsgTrade msgTrade = new MsgTrade(msg.arg1,mMsgGift.getErrCode(),mMsgGift.getTradeNo(),mMsgGift.getAmount());
                        msg.obj = msgTrade;
                    }
                    break;
                case TcnProtoDef.COMMAND_SHIPMENT_REMOTE:
                    MsgToSend mMsgRemote = (MsgToSend) msg.obj;
                    OnShipWithMethod(msg.what, msg.arg1, msg.arg2, mMsgRemote);
                    if (mMsgRemote != null) {
                        MsgTrade msgTrade = new MsgTrade(msg.arg1,mMsgRemote.getErrCode(),mMsgRemote.getTradeNo(),mMsgRemote.getAmount());
                        msg.obj = msgTrade;
                    }
                    break;
                case TcnProtoDef.COMMAND_SHIPMENT_VERIFY:
                    MsgToSend mMsgVrf = (MsgToSend) msg.obj;
                    OnShipWithMethod(msg.what, msg.arg1, msg.arg2, mMsgVrf);
                    if (mMsgVrf != null) {
                        MsgTrade msgTrade = new MsgTrade(msg.arg1,mMsgVrf.getErrCode(),mMsgVrf.getTradeNo(),mMsgVrf.getAmount());
                        msg.obj = msgTrade;
                    }
                    break;
                case TcnProtoDef.COMMAND_SHIPMENT_BANKCARD_ONE:
                    MsgToSend mMsgBankOne = (MsgToSend) msg.obj;
                    OnShipWithMethod(msg.what, msg.arg1, msg.arg2, mMsgBankOne);
                    if (mMsgBankOne != null) {
                        MsgTrade msgTrade = new MsgTrade(msg.arg1,mMsgBankOne.getErrCode(),mMsgBankOne.getTradeNo(),mMsgBankOne.getAmount());
                        msg.obj = msgTrade;
                    }
                    break;
                case TcnProtoDef.COMMAND_SHIPMENT_BANKCARD_TWO:
                    MsgToSend mMsgBankTwo = (MsgToSend) msg.obj;
                    OnShipWithMethod(msg.what, msg.arg1, msg.arg2, mMsgBankTwo);
                    if (mMsgBankTwo != null) {
                        MsgTrade msgTrade = new MsgTrade(msg.arg1,mMsgBankTwo.getErrCode(),mMsgBankTwo.getTradeNo(),mMsgBankTwo.getAmount());
                        msg.obj = msgTrade;
                    }
                    break;
                case TcnProtoDef.COMMAND_SHIPMENT_TCNCARD_OFFLINE:
                    MsgToSend mMsgOfl = (MsgToSend) msg.obj;
                    OnShipWithMethod(msg.what, msg.arg1, msg.arg2, mMsgOfl);
                    if (mMsgOfl != null) {
                        MsgTrade msgTrade = new MsgTrade(msg.arg1,mMsgOfl.getErrCode(),mMsgOfl.getTradeNo(),mMsgOfl.getAmount());
                        msg.obj = msgTrade;
                    }
                    break;
                case TcnProtoDef.COMMAND_SHIPMENT_TCNCARD_ONLINE:
                    MsgToSend mMsgOnLine = (MsgToSend) msg.obj;
                    OnShipWithMethod(msg.what, msg.arg1, msg.arg2, mMsgOnLine);
                    if (mMsgOnLine != null) {
                        MsgTrade msgTrade = new MsgTrade(msg.arg1,mMsgOnLine.getErrCode(),mMsgOnLine.getTradeNo(),mMsgOnLine.getAmount());
                        msg.obj = msgTrade;
                    }
                    break;
                case TcnProtoDef.COMMAND_SHIPMENT_OTHER_PAY:
                    MsgToSend mMsgOtherPay = (MsgToSend) msg.obj;
                    OnShipWithMethod(msg.what, msg.arg1, msg.arg2, mMsgOtherPay);
                    if (mMsgOtherPay != null) {
                        MsgTrade msgTrade = new MsgTrade(msg.arg1,mMsgOtherPay.getErrCode(),mMsgOtherPay.getTradeNo(),mMsgOtherPay.getAmount());
                        msg.obj = msgTrade;
                    }
                    break;
                case TcnProtoDef.COMMAND_SLOTNO_INFO:
                    break;
                case TcnProtoDef.COMMAND_SLOTNO_INFO_SINGLE:
                    break;
                case TcnProtoDef.COMMAND_BUSY:
                    OnSystemBusy();
                    break;
                case TcnProtoDef.REQ_CMD_TEST_SLOT:
                    if (m_bIsUnlock) {
                        VendProtoControl.getInstance().reqShipTest(msg.arg1,null);
                    }
                    break;
                case TcnProtoDef.CMD_TEST_SLOT:
                    OnShipForTestSlot(msg.arg1, msg.arg2, (Integer) msg.obj);
                    break;
                case TcnProtoDef.REQ_QUERY_SLOT_STATUS:
                    if (m_bIsUnlock) {
//                        VendProtoControl.getInstance().reqQuerySlotExists(msg.arg1);
                    } else {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.PROMPT_INFO, -1, -1, -1, "没有授权！");
                    }
                    break;
                case TcnProtoDef.QUERY_SLOT_STATUS:
                    if (m_bIsUnlock) {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_QUERY_SLOT_STATUS, msg.arg1, msg.arg2, -1, getErrCodeMessageSpring(false, msg.arg2));
                    }

                    break;
                case TcnProtoDef.REQ_SET_TEMP_CONTROL_OR_NOT:
                    break;
                case TcnProtoDef.SET_TEMP_CONTROL_OR_NOT:
                    break;
                case TcnProtoDef.REQ_CMD_READ_CURRENT_TEMP:
                    break;
                case TcnProtoDef.CMD_READ_CURRENT_TEMP:
                    String temper = (String) msg.obj;
                    if (TextUtils.isEmpty(m_strTotalTemp)) {
                        if ((temper != null) && (temper.length() > 0)) {
                            StringBuffer msgBf = new StringBuffer();
                            msgBf.append(m_context.getString(R.string.board_current_temperature));
                            msgBf.append(temper);
                            msgBf.append("℃");
                            TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.TEMPERATURE_INFO, msg.arg1, msg.arg2, -1, msgBf.toString());
                            // TcnVendIF.getInstance().sendMsgToUIDelay(TcnVendEventID.TEMPERATURE_INFO, msg.arg1, msg.arg2, -1, 300000, "");

                        } else {
	                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.TEMPERATURE_INFO, msg.arg1, msg.arg2, -1, "");
                          //  TcnVendIF.getInstance().sendMsgToUIDelay(TcnVendEventID.TEMPERATURE_INFO, msg.arg1, msg.arg2, -1, 300000, "");
                        }
                    }

                    if ((msg.arg2 > -10) && (msg.arg2 < 90)) {
                        m_strTemp = (String) msg.obj;
                    } else {
                        m_strTemp = "";
                    }
                    break;
                case TcnProtoDef.CMD_READ_TEMP:
                    m_strTotalTemp = (String) msg.obj;
                    if (!TextUtils.isEmpty(m_strTotalTemp)) {
                        StringBuffer msgBf = new StringBuffer();
                        msgBf.append(m_context.getString(R.string.board_current_temperature));
                        msgBf.append(m_strTotalTemp);
                        msgBf.append("℃");
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.TEMPERATURE_INFO, msg.arg1, msg.arg2, -1, msgBf.toString());
                        //   TcnVendIF.getInstance().sendMsgToUIDelay(TcnVendEventID.TEMPERATURE_INFO, msg.arg1, msg.arg2, -1, 300000, "");
                    }
                    break;
                case TcnProtoDef.CMD_SET_COOL_HEAT_CLOSE:
	                TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SET_COOL_HEAT_CLOSE, msg.arg1, msg.arg2, -1, getErrCodeMessageSpring(true, msg.arg1));
                    break;
                case TcnProtoDef.REQ_CMD_SET_LIGHT_OPEN:
//                    VendProtoControl.getInstance().setLightControl(msg.arg1, true);
                    break;
                case TcnProtoDef.CMD_SET_LIGHT_OPEN:
	                TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SET_LIGHT_OPEN, msg.arg1, msg.arg2, -1, getErrCodeMessageSpring(true, msg.arg1));
                    break;
                case TcnProtoDef.REQ_CMD_SET_LIGHT_CLOSE:
//                    VendProtoControl.getInstance().setLightControl(msg.arg1, false);
                    break;
                case TcnProtoDef.CMD_SET_LIGHT_CLOSE:
	                TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SET_LIGHT_CLOSE, msg.arg1, msg.arg2, -1, getErrCodeMessageSpring(true, msg.arg1));
                    break;
                case TcnProtoDef.REQ_CMD_SET_BUZZER_OPEN:
//                    VendProtoControl.getInstance().setBuzzerControl(msg.arg1, true);
                    break;
                case TcnProtoDef.CMD_SET_BUZZER_OPEN:
	                TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SET_BUZZER_OPEN, msg.arg1, msg.arg2, -1, getErrCodeMessageSpring(true, msg.arg1));
                    break;
                case TcnProtoDef.REQ_CMD_SET_BUZZER_CLOSE:
//                    VendProtoControl.getInstance().setBuzzerControl(msg.arg1, false);
                    break;
                case TcnProtoDef.CMD_SET_BUZZER_CLOSE:
	                TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SET_BUZZER_CLOSE, msg.arg1, msg.arg2, -1, getErrCodeMessageSpring(true, msg.arg1));
                    break;
                case TcnProtoDef.REQ_CMD_READ_DOOR_STATUS:
                    break;
                case TcnProtoDef.CMD_READ_DOOR_STATUS:
	                TcnLog.getInstance().LoggerInfoForce(TAG, "CMD_READ_DOOR_STATUS msg.arg1: " + msg.arg1+" msg.arg2: "+msg.arg2);
                    if (TcnProtoResultDef.DOOR_CLOSE == msg.arg1) {
	                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_READ_DOOR_STATUS, TcnVendEventResultID.DO_CLOSE, msg.arg2, -1, null);
                    } else {
	                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_READ_DOOR_STATUS, TcnVendEventResultID.DO_OPEN, msg.arg2, -1, null);
                    }
                    break;
                case TcnProtoDef.CMD_CHECK_SERIPORT:
                    if (TcnProtoResultDef.SUCCESS == msg.arg1) {
	                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_CHECK_SERIPORT, TcnVendEventResultID.SUCCESS, msg.arg2, -1, null);
                    } else {
                        String tips = (String) msg.obj;
                        if (TextUtils.isEmpty(tips)) {
	                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_CHECK_SERIPORT, TcnVendEventResultID.FAIL, msg.arg2, -1, "请检查串口");
                        } else {
	                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_CHECK_SERIPORT, TcnVendEventResultID.FAIL, msg.arg2, -1, "请检查串口 tips: "+tips);
                        }

                    }
                    break;
                case TcnProtoDef.REQ_CMD_QUERY_STATUS:
                    VendProtoControl.getInstance().reqQueryStatus(msg.arg1);
                    break;
                case TcnProtoDef.CMD_QUERY_STATUS:
	                if (TcnProtoResultDef.STATUS_HEATING_START == msg.arg1) {
		                TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_QUERY_STATUS_LIFTER, TcnVendEventResultID.STATUS_HEATING_START, msg.arg2, -1, m_context.getString(R.string.board_tip_heating));

	                } else if (TcnProtoResultDef.STATUS_HEATING_END == msg.arg1) {
		                TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_QUERY_STATUS_LIFTER, TcnVendEventResultID.STATUS_HEATING_END, msg.arg2, -1, m_context.getString(R.string.board_notify_shipping));

	                } else {
//		                TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_QUERY_STATUS_LIFTER, getStatusLifter(msg.arg1), msg.arg2, -1, getErrCodeMessageLifter(false,VendProtoControl.BOARD_LIFT_HEFAN_ZP, msg.arg2));
	                }
                    break;
                case TcnProtoDef.CMD_QUERY_STATUS_ICEC:
                    // TcnBoardIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_QUERY_STATUS_ICEC, getStatusIcec(msg.arg1), msg.arg2, -1, getErrCodeMessageC1Icec(msg.arg2),
                    //                            null, null, null, null);
                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_QUERY_STATUS_ICEC, getStatusIcec(msg.arg1), msg.arg2, -1, getErrCodeMessageC1Icec(msg.arg2));
//                        sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_ICEC, TcnProtoResultDef.STATUS_FREE, msg.arg2, null);
                    break;
                case TcnProtoDef.REQ_CMD_ICE_SET_WORK_MODE:
                    VendProtoControl.getInstance().reqSetWorkMode(msg.arg1, msg.arg2);
                    break;
                case TcnProtoDef.CMD_ICE_SET_WORK_MODE:
                    if (TcnProtoResultDef.SUCCESS == msg.arg1) {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SET_WORK_MODE, msg.arg1, msg.arg2, TcnVendEventResultID.SUCCESS, "设置成功");

                    } else {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SET_WORK_MODE, msg.arg1, msg.arg2, TcnVendEventResultID.FAIL, "设置失败，可能指令不合法！");

                    }
                    break;
                case TcnProtoDef.REQ_CMD_PARAM_ICE_MAKE_SET:
                    MsgSend mMakeSetMsgSend = (MsgSend) msg.obj;
                    if (mMakeSetMsgSend != null) {
                        VendProtoControl.getInstance().reqSetParamIceMake(msg.arg1, msg.arg2, mMakeSetMsgSend.getData1(), mMakeSetMsgSend.getData2(), mMakeSetMsgSend.getData3(), mMakeSetMsgSend.getData4());
                    }
                    break;
                case TcnProtoDef.CMD_PARAM_ICE_MAKE_SET:
                    if (TcnProtoResultDef.SUCCESS == msg.arg1) {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_PARAM_ICE_MAKE_SET, msg.arg1, msg.arg2, TcnVendEventResultID.SUCCESS, "设置成功");

                    } else {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_PARAM_ICE_MAKE_SET, msg.arg1, msg.arg2, TcnVendEventResultID.FAIL, "设置失败，可能指令不合法！");

                    }
                    break;
                case TcnProtoDef.CMD_SELF_INSPECTION:
                    if (TcnProtoResultDef.SUCCESS == msg.arg1) {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SELF_INSPECTION, msg.arg1, msg.arg2, TcnVendEventResultID.SUCCESS, "自检成功");

                    } else {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SELF_INSPECTION, msg.arg1, msg.arg2, TcnVendEventResultID.FAIL, "自检失败，可能指令不合法！");

                    }
                    break;
                case TcnProtoDef.REQ_CMD_PARAM_ICE_MAKE_QUERY:
                    VendProtoControl.getInstance().reqQueryParamIceMake();
                    break;
                case TcnProtoDef.REQ_CMD_TEST_DISCHARGE:
                    VendProtoControl.getInstance().testDischarge(msg.arg1, msg.arg2);
                    break;
                case TcnProtoDef.CMD_PARAM_ICE_MAKE_QUERY:
//                    upDataDB((String) msg.obj);
                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_PARAM_ICE_MAKE_QUERY, msg.arg1, msg.arg2, TcnVendEventResultID.FAIL, (String) msg.obj);
                    break;
                case TcnProtoDef.REQ_CMD_PARAM_QUERY:
                    VendProtoControl.getInstance().reqQueryParam(msg.arg1, msg.arg2);
                    break;
                case TcnProtoDef.REQ_CMD_PARAM_AUERY_ALL:
                    VendProtoControl.getInstance().reqQueryAllParam(msg.arg1, msg.arg2);
                    break;
                case TcnProtoDef.REQ_CMD_PARAM_SET:
                    VendProtoControl.getInstance().reqParamSet(msg.arg1, msg.arg2, (int) msg.obj);
                    break;
                case TcnProtoDef.REQ_CMD_POSITION_MOVE:
                    VendProtoControl.getInstance().reqMove(msg.arg1, msg.arg2);
                    break;
                case TcnProtoDef.CMD_POSITION_MOVE:
                    if (TcnProtoResultDef.SUCCESS == msg.arg1) {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_POSITION_MOVE, msg.arg1, msg.arg2, TcnVendEventResultID.SUCCESS, (String) msg.obj);
                    } else {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_POSITION_MOVE, msg.arg1, msg.arg2, TcnVendEventResultID.FAIL, "设置失败，可能指令不合法！");
                    }

                    break;
                case TcnProtoDef.REQ_CMD_QUERY_STATUS_AND_JUDGE:
                    VendProtoControl.getInstance().reqQueryStatusAndJudge();
                    break;
                case TcnProtoDef.CMD_QUERY_STATUS_AND_JUDGE:
                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_QUERY_STATUS_AND_JUDGE, msg.arg1, msg.arg2, -1, (String) msg.obj);
                    break;
                case TcnProtoDef.REQ_CMD_SELF_INSPECTION:
                    VendProtoControl.getInstance().reqMachineSelf_test();
                    break;
                case TcnProtoDef.REQ_CMD_TAKE_GOODS_DOOR:
                    if ((Boolean) msg.obj != null) {
//                        VendProtoControl.getInstance().reqSetTakeGoodsDoorControl(msg.arg1, ((Boolean) msg.obj).booleanValue());
                    }
                    break;
                case TcnProtoDef.CMD_TAKE_GOODS_DOOR:
                    int iDoor = (Integer) msg.obj;
                    if (TcnProtoResultDef.DOOR_TAKE_GOODS_OPEN == iDoor) {
                        iDoor = TcnVendEventResultID.DO_OPEN;
                    } else if (TcnProtoResultDef.DOOR_TAKE_GOODS_CLOSE == iDoor) {
                        iDoor = TcnVendEventResultID.DO_CLOSE;
                    } else {
                        iDoor = TcnVendEventResultID.DO_INVALID;
                    }
//                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_TAKE_GOODS_DOOR, getStatusStartEndLifter(msg.arg1), msg.arg2, iDoor, getErrCodeMessageLifter(false,VendProtoControl.BOARD_LIFT_HEFAN_ZP, msg.arg2));
                    break;
                case TcnProtoDef.REQ_CMD_LIFTER_UP:
//                    VendProtoControl.getInstance().reqLifterUp(msg.arg1, msg.arg2);
                    break;
                case TcnProtoDef.CMD_LIFTER_UP:     //arg2:m_iFloor
                    int errCode = -1;
                    if ((Integer) msg.obj != null) {
                        errCode = ((Integer) msg.obj).intValue();
                    }
//                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_LIFTER_UP, getStatusStartEndLifter(msg.arg1), msg.arg2, errCode, getErrCodeMessageLifter(false,VendProtoControl.BOARD_LIFT_HEFAN_ZP, msg.arg2));
                    break;
                case TcnProtoDef.REQ_CMD_LIFTER_BACK_HOME:
//                    VendProtoControl.getInstance().reqLifterBackHome(msg.arg1);
                    break;
                case TcnProtoDef.CMD_LIFTER_BACK_HOME:

//                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_LIFTER_BACK_HOME, getStatusStartEndLifter(msg.arg1), msg.arg2, -1, getErrCodeMessageLifter(false,VendProtoControl.BOARD_LIFT_HEFAN_ZP, msg.arg2));
                    break;
                case TcnProtoDef.REQ_CMD_CLAPBOARD_SWITCH:
                    if ((Boolean) msg.obj != null) {
//                        VendProtoControl.getInstance().reqSetClapboardSwitch(msg.arg1, ((Boolean) msg.obj).booleanValue());
                    }
                    break;
                case TcnProtoDef.CMD_CLAPBOARD_SWITCH_LIFT:
                    int iClapBoard = (Integer) msg.obj;
                    if (TcnProtoResultDef.CLAPBOARD_OPEN == iClapBoard) {
                        iClapBoard = TcnVendEventResultID.DO_OPEN;
                    } else if (TcnProtoResultDef.CLAPBOARD_CLOSE == iClapBoard) {
                        iClapBoard = TcnVendEventResultID.DO_CLOSE;
                    } else {
                        iClapBoard = TcnVendEventResultID.DO_INVALID;
                    }
//                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_CLAPBOARD_SWITCH, getStatusStartEndLifter(msg.arg1), msg.arg2, iClapBoard, getErrCodeMessageLifter(false,VendProtoControl.BOARD_LIFT_HEFAN_ZP, msg.arg2));
                    break;
                case TcnProtoDef.CMD_CLOSE_COOL_HEAT:
//                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_CLOSE_COOL_HEAT, getStatusStartEndLifter(msg.arg1), msg.arg2, -1, getErrCodeMessageLifter(false,VendProtoControl.BOARD_LIFT_HEFAN_ZP, msg.arg2));
                    break;
                case TcnProtoDef.REQ_CMD_CLEAN_FAULTS:
                    VendProtoControl.getInstance().reqClearFaults(msg.arg1);
                    break;
                case TcnProtoDef.CMD_CLEAN_FAULTS:
                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_CLEAN_FAULTS, getStatusLifter(msg.arg1), msg.arg2, -1, getErrCodeMessageLifter(true,(Integer) msg.obj, msg.arg2));
                    break;
                case TcnProtoDef.REQ_CMD_QUERY_PARAMETERS:
//                    VendProtoControl.getInstance().reqQueryParameters(msg.arg1, msg.arg2);
                    break;
                case TcnProtoDef.CMD_QUERY_PARAMETERS:
                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_QUERY_PARAMETERS, msg.arg1, msg.arg2, -1, null);
                    break;
                case TcnProtoDef.REQ_CMD_QUERY_DRIVER_CMD:
                    //VendProtoControl.getInstance().reqD(msg.arg1);
                    break;
                case TcnProtoDef.CMD_QUERY_DRIVER_CMD:
                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_QUERY_DRIVER_CMD, getStatusLifter(msg.arg1), msg.arg2, -1, null);
                    break;
                case TcnProtoDef.REQ_CMD_SET_SWITCH_OUTPUT_STATUS:
//                    VendProtoControl.getInstance().reqSetSwitchOutPutStatus(msg.arg1,msg.arg2,(Integer)msg.obj);
                    break;
                case TcnProtoDef.CMD_SET_SWITCH_OUTPUT_STATUS:
                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SET_SWITCH_OUTPUT_STATUS, getStatusLifter(msg.arg1), msg.arg2, -1, null);
                    break;
                case TcnProtoDef.REQ_CMD_SET_ID:
//                    VendProtoControl.getInstance().reqSetID(msg.arg1, msg.arg2);
                    break;
                case TcnProtoDef.CMD_SET_ID:
                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SET_ID, msg.arg1, -1, -1, null);
                    break;
                case TcnProtoDef.REQ_CMD_SET_LIGHT_OUT_STEP:
//                    VendProtoControl.getInstance().reqSetLightOutStep(msg.arg1, msg.arg2);
                    break;
                case TcnProtoDef.CMD_SET_LIGHT_OUT_STEP:    //msg.arg1:升降机高出光检步数    //msg.arg2:故障代码
                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SET_LIGHT_OUT_STEP, msg.arg1, msg.arg2, -1, getErrCodeMessageLifter(true,(Integer) msg.obj, msg.arg2));
                    break;
                case TcnProtoDef.REQ_CMD_SET_PARAMETERS:
//                    VendProtoControl.getInstance().reqSetParameters(msg.arg1, msg.arg2, (String) msg.obj);
                    break;
                case TcnProtoDef.CMD_SET_PARAMETERS:    //msg.arg1 地址, msg.arg2 故障, (Integer)msg.obj 值
//                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_SET_PARAMETERS, msg.arg1, msg.arg2, (Integer) msg.obj, getErrCodeMessageLifter(true,VendProtoControl.BOARD_LIFT_HEFAN_ZP, msg.arg2));
                    break;
                case TcnProtoDef.REQ_CMD_FACTORY_RESET:
//                    VendProtoControl.getInstance().reqFactoryReset(msg.arg1);
                    break;
                case TcnProtoDef.CMD_FACTORY_RESET: //msg.arg1 故障
                    TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_FACTORY_RESET, msg.arg1, msg.arg2, -1, getErrCodeMessageLifter(true,(Integer) msg.obj, msg.arg1));
                    break;
                case TcnProtoDef.REQ_CMD_DETECT_LIGHT:
//                    VendProtoControl.getInstance().reqLightDetect(msg.arg1, (String) msg.obj);
                    break;
                case TcnProtoDef.CMD_DETECT_LIGHT:
                    String strLight = "";
                    if (TcnProtoResultDef.CMD_DETECT_LIGHT_BLOCKED == msg.arg1) {
                        strLight = m_context.getString(R.string.board_drive_light_blocked);
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_DETECT_LIGHT, TcnVendEventResultID.CMD_DETECT_LIGHT_BLOCKED, msg.arg1, (Integer) msg.obj, strLight);
                    } else if (TcnProtoResultDef.CMD_DETECT_LIGHT_NOT_BLOCKED == msg.arg1) {
                        strLight = m_context.getString(R.string.board_drive_light_not_blocked);
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_DETECT_LIGHT, TcnVendEventResultID.CMD_DETECT_LIGHT_NOT_BLOCKED, msg.arg1, (Integer) msg.obj, strLight);
                    } else if (TcnProtoResultDef.CMD_NO_DATA_RECIVE == msg.arg1) {
                        strLight = m_context.getString(R.string.board_drive_check_seriport);
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_DETECT_LIGHT, TcnVendEventResultID.CMD_NO_DATA_RECIVE, msg.arg1, (Integer) msg.obj, strLight);
                    } else {
                        strLight = m_context.getString(R.string.board_drive_unknow_err);
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_DETECT_LIGHT, TcnVendEventResultID.CMD_DETECT_LIGHT_INVALID, msg.arg1, (Integer) msg.obj, strLight);
                    }
                    break;
                case TcnProtoDef.REQ_CMD_DETECT_SHIP:
//                    VendProtoControl.getInstance().reqShipDetect(msg.arg1);
                    break;
                case TcnProtoDef.CMD_DETECT_SHIP:
                    String strDetShip = "";
                    if (TcnProtoResultDef.CMD_DETECT_SHIP_HAVE_GOODS == msg.arg1) {
                        strDetShip = m_context.getString(R.string.board_drive_have_goods);
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_DETECT_SHIP, TcnVendEventResultID.CMD_DETECT_SHIP_HAVE_GOODS, -1, -1, strDetShip);
                    } else if (TcnProtoResultDef.CMD_DETECT_SHIP_NO_GOODS == msg.arg1) {
                        strDetShip = m_context.getString(R.string.board_drive_no_goods);
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_DETECT_SHIP, TcnVendEventResultID.CMD_DETECT_SHIP_NO_GOODS, -1, -1, strDetShip);
                    } else if (TcnProtoResultDef.CMD_NO_DATA_RECIVE == msg.arg1) {
                        strDetShip = m_context.getString(R.string.board_drive_check_seriport);
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_DETECT_SHIP, TcnVendEventResultID.CMD_NO_DATA_RECIVE, -1, -1, strDetShip);
                    } else {
                        strDetShip = m_context.getString(R.string.board_drive_unknow_err);
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_DETECT_SHIP, TcnVendEventResultID.CMD_DETECT_SHIP_INVALID, -1, -1, strDetShip);
                    }
                    break;
                case TcnProtoDef.REQ_CMD_DETECT_SWITCH_INPUT:
//                    VendProtoControl.getInstance().reqSwitchInPutDetect(msg.arg1, msg.arg2);
                    break;
                case TcnProtoDef.CMD_DETECT_SWITCH_INPUT:
                    if (TcnProtoResultDef.DETECT_SWITCH_INPUT_CONNECT == msg.arg1) {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_DETECT_SWITCH_INPUT, TcnVendEventResultID.DETECT_SWITCH_INPUT_CONNECT, -1, -1, null);
                    } else if (TcnProtoResultDef.DETECT_SWITCH_INPUT_DISCONNECT == msg.arg1) {
                        TcnVendIF.getInstance().sendMsgToUI(TcnVendEventID.CMD_DETECT_SWITCH_INPUT, TcnVendEventResultID.DETECT_SWITCH_INPUT_DISCONNECT, -1, -1, null);
                    } else {

                    }
                    break;
                default:
                    break;
            }
	        TcnVendIF.getInstance().handleCommunicationMessage(msg);
        }
    }

    private int getStatusIcec(int status) {
        int iRetStatus = TcnVendEventResultID.STATUS_INVALID;
        if (TcnProtoResultDef.STATUS_FREE == status) {
            iRetStatus = TcnVendEventResultID.STATUS_FREE;
        } else if (TcnProtoResultDef.STATUS_SHIPING == status) {
            iRetStatus = TcnVendEventResultID.STATUS_SHIPING;
        } else if (TcnProtoResultDef.STATUS_WAIT_TAKE_GOODS == status) {
            iRetStatus = TcnVendEventResultID.STATUS_WAIT_TAKE_GOODS;
        } else if (TcnProtoResultDef.STATUS_CLEAN == status) {
            iRetStatus = TcnVendEventResultID.STATUS_CLEAN;
        } else if (TcnProtoResultDef.STATUS_FAULT == status) {
            iRetStatus = TcnVendEventResultID.STATUS_FAULT;
        } else {

        }
        return iRetStatus;
    }
    private String getErrCodeMessageC1Icec(int errCode) {
        TcnLog.getInstance().LoggerInfo(TAG, "getErrCodeMessageIcec() errCode: " + errCode);
        StringBuffer errMsg = new StringBuffer();

        if (VendProtoControl.ERROR_CODE_SHIPING == errCode) {
            errMsg.append(m_context.getString(R.string.board_notify_shipping));
            return errMsg.toString();
        }

        if (VendProtoControl.ERROR_CODE_WAIT_TAKE_GOODS == errCode) {
            errMsg.append(m_context.getString(R.string.board_notify_receive_goods_first));
            return errMsg.toString();
        }

        if (VendProtoControl.ERROR_CODE_CLEAN == errCode) {
            errMsg.append("清洗中！");
            return errMsg.toString();
        }

        if (0 == errCode) {
            errMsg.append(m_context.getString(R.string.board_drive_errcode_normal));
            return errMsg.toString();
        }

        errMsg.append(m_context.getString(R.string.board_drive_errcode));
        errMsg.append(errCode);
        errMsg.append(" ");

        switch (errCode) {
            case 1:
                errMsg.append("冰淇淋缺少浆料！");
                break;
            case 2:
                errMsg.append("制冰机无响应！");
                break;
            case 3:
                errMsg.append("制冰系统未启动！");
                break;
            case 4:
                errMsg.append("冰淇淋未成型！");
                break;
            case 5:
                errMsg.append("冰淇淋机其它故障！");
                break;
            case 6:
                errMsg.append("保护罩不能复位！");
                break;
            case 7:
                errMsg.append("Y无法找到起始点！");
                break;
            case 8:
                errMsg.append("Y电机开路！");
                break;
            case 9:
                errMsg.append("Y电机过流！");
                break;
            case 10:
                errMsg.append("Y电机无法定位！");
                break;
            case 11:
                errMsg.append("X电机无法找到起始点！");
                break;
            case 12:
                errMsg.append("X电机开路！");
                break;
            case 13:
                errMsg.append("X电机过流！");
                break;
            case 14:
                errMsg.append("X电机无法定位！");
                break;
            case 15:
                errMsg.append("Z电机无法找到起始点！");
                break;
            case 16:
                errMsg.append("Z电机开路！");
                break;
            case 17:
                errMsg.append("Z电机过流！");
                break;
            case 18:
                errMsg.append("Z电机无法定位！");
                break;
            case 19:
                errMsg.append("杯光检不发射也正常！");
                break;
            case 20:
                errMsg.append("杯光检有档住！");
                break;
            case 21:
                errMsg.append("落杯器无通迅！");
                break;
            case 22:
                errMsg.append("出杯电机开路！");
                break;
            case 23:
                errMsg.append("出杯电机过流！");
                break;
            case 24:
                errMsg.append("出杯后未检测到杯！");
                break;
            case 25:
                errMsg.append("冰淇淋电机开路！");
                break;
            case 26:
                errMsg.append("冰淇淋电机过流！");
                break;
            case 27:
                errMsg.append("冰淇淋电机无法定位！");
                break;
            case 28:
                errMsg.append("门电机开路！");
                break;
            case 29:
                errMsg.append("门电机过流！");
                break;
            case 30:
                errMsg.append("果酱电机开路！");
                break;
            case 31:
                errMsg.append("果酱电机过流！");
                break;
            case 32:
                errMsg.append("果酱缺料！");
                break;
            case 33:
                errMsg.append("顶料电机开路！");
                break;
            case 34:
                errMsg.append("顶料电机过流！");
                break;
            case 35:
                errMsg.append("顶料缺料或故障！");
                break;
            case 36:
                errMsg.append("防夹手光检不发射也正常！");
                break;
            case 37:
                errMsg.append("防夹手光检有挡住！");
                break;
            case 38:
                errMsg.append("酱料光检不发射也正常！");
                break;
            case 39:
                errMsg.append("酱料光检有挡住！");
                break;
            case 40:
                errMsg.append("震动电机故障！");
                break;
            case 41:
                errMsg.append("清洗电机异常！");
                break;
            case 42:
                errMsg.append("称重1异常！");
                break;
            case 43:
                errMsg.append("称重2异常！");
                break;
            case 44:
                errMsg.append("称重3异常！");
                break;
            case 45:
                errMsg.append("称重4异常！");
                break;
            case 46:
                errMsg.append("称重5异常！");
                break;
            case 47:
                errMsg.append("门未关！");
                break;
            case 48:
                errMsg.append("其它异常！");
                break;
            case 49:
                errMsg.append("！");
                break;
            case 50:
                errMsg.append("！");
                break;
            case 51:
                errMsg.append("！");
                break;
            case 52:
                errMsg.append("！");
                break;
            case 53:
                errMsg.append("！");
                break;
            case 54:
                errMsg.append("！");
                break;
            case 55:
                errMsg.append("！");
                break;
            case 56:
                errMsg.append("！");
                break;
            case 57:
                errMsg.append("！");
                break;
            case 58:
                errMsg.append("！");
                break;
            case 59:
                errMsg.append("！");
                break;
            case 60:
                errMsg.append("！");
                break;
            case 61:
                errMsg.append("！");
                break;
            case 62:
                errMsg.append("！");
                break;
            case 63:
                errMsg.append("！");
                break;
            case 64:
                errMsg.append("！");
                break;
            case 65:
                errMsg.append("！");
                break;
            case 66:
                errMsg.append("！");
                break;
            case 67:
                errMsg.append("！");
                break;
            case 68:
                errMsg.append("！");
                break;
            case 69:
                errMsg.append("！");
                break;
            case 70:
                errMsg.append("！");
                break;
            case 71:
                errMsg.append("！");
                break;
            case 100:
                errMsg.append("！");
                break;
            case 101:
                errMsg.append("！");
                break;
            case 102:
                errMsg.append("！");
                break;
            case 103:
                errMsg.append("！");
                break;
            default:
                break;
        }

        return errMsg.toString();
    }
}
