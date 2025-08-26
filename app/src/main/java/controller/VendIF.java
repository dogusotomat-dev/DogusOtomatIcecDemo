package controller;

import android.os.Message;

import com.tcn.icecboard.control.MsgTrade;
import com.tcn.icecboard.control.TcnComDef;
import com.tcn.icecboard.control.TcnComResultDef;
import com.tcn.icecboard.control.TcnVendIF;


/**
 * Created by Administrator on 2016/6/30.
 */
public class VendIF {
    private static final String TAG = "VendIF";
    private static VendIF m_Instance = null;

    /**************************  故障代码表 ****************************
     1   升降电机断线
     2   升降电机过流
     3   升降超时
     4   平移电机断线
     5   平移电机过流
     6   平移电机超时
     7   平台电机断线
     8   平台电机过流
     9   平台电机超时
     10  移动平台有商品
     11  运行过程中跌落
     12  微波加热顶不到位
     13  货掉不出
     14  升降电机堵转
     15  平移电机堵转
     16  升降电机超出最大步数
     17  平移电机超出最大步数
     18  加热平台升降电机断线
     19  加热平台升降电机过流
     20  加热平台升降电机超时
     21 磁控管1完全没有工作
     22 磁控管2完全没有工作
     23 磁控管1和磁控管2完全没有工作
     24 磁控管3完全没有工作
     25 磁控管1和磁控管3完全没有工作
     26 磁控管2和磁控管3完全没有工作
     27 磁控管1磁控管2和磁控管3完全没有工作

     28 磁控管1工作一定时间后停止工作（可能过热保护/电池保护）
     29 磁控管2工作一定时间后停止工作（可能过热保护/电池保护）
     30 磁控管1和磁控管2工作一定时间后停止工作（可能过热保护/电池保护）
     31 磁控管3工作一定时间后停止工作（可能过热保护/电池保护）
     32 磁控管1和磁控管3工作一定时间后停止工作（可能过热保护/电池保护）
     33 磁控管2和磁控管3工作一定时间后停止工作（可能过热保护/电池保护）
     34 磁控管1磁控管2和磁控管3工作一定时间后停止工作（可能过热保护/电池保护）

     35 磁控管1控制继电器粘连
     36 磁控管2控制继电器粘连
     37 磁控管3控制继电器粘连

     40 货道故障/没有找到对应货道层

     51  取货门电机断线
     52  取货门电机过流
     53  取货门电机超时
     54  防夹手光检被挡住
     55	取货口推货电机断线
     56	取货口推货电机过流
     57	取货口推货电机超时
     58  取货口推货电机堵转
     59	出货皮带电机断线
     60  出货皮带电机过流

     64  无效货道

     71  转盘电机断线
     72  转盘电机过流
     73  转盘电机超时
     74  转盘电机堵转


     80  转动超时

     101 取货口有商品
     102 加热平台没有检测到商品（可能卡在侧门）

     240 存储芯片故障
     255 通信故障

    ********************************************************************************/


    public static synchronized VendIF getInstance() {
        if (null == m_Instance) {
            m_Instance = new VendIF();
        }
        return m_Instance;
    }

    public void initialize() {
        registerListener ();
    }


    public void deInitialize() {
        unregisterListener();
    }

    public void registerListener () {
        TcnVendIF.getInstance().setOnCommunicationListener(m_CommunicationListener);
    }

    public void unregisterListener() {
        TcnVendIF.getInstance().setOnCommunicationListener(null);
    }

    private void OnSelectedSlotNo(int slotNo) {

    }

    //驱动板上报过来的数据 slotNo:货道号     status:0 货道状态正常     4：没有检测到掉货      255：货道号不存在（检测不到该货道）
    public void OnUploadSlotNoInfo(boolean finish, int slotNo, int status) {

    }

    //驱动板上报过来的数据 slotNo:货道号     status:0 货道状态正常     4：没有检测到掉货      255：货道号不存在（检测不到该货道）
    public void OnUploadSlotNoInfoSingle(boolean finish, int slotNo, int status) {
        TcnVendIF.getInstance().LoggerInfoForce(TAG, "OnUploadSlotNoInfoSingle finish: " + finish + " slotNo: " + slotNo + " status: " + status);
    }

    //出货状态返回    slotNo： 货道号    shipStatus： 出货状态    status: 货道状态正常    支付订单号（出货接口传入，原样返回） amount：支付金额（出货接口传入，原样返回）
    private void OnShipWithMethod(int slotNo, int shipStatus,int errCode, String tradeNo, String amount) {
        TcnVendIF.getInstance().LoggerInfoForce(TAG, "OnShipWithMethod slotNo: " + slotNo + " shipStatus: " + shipStatus+" errCode: "+errCode
                + " tradeNo: " + tradeNo+" amount: "+amount);

        if (TcnComResultDef.SHIP_SHIPING == shipStatus) {   //出货中

        } else if (TcnComResultDef.SHIP_SUCCESS == shipStatus) {   //出货成功

        } else if (TcnComResultDef.SHIP_FAIL == shipStatus) {    //出货失败

        } else {

        }
    }

    private void OnDoorSwitch(int door) {

    }

    private void OnSelectedGoods(int slotNoOrKey, String price) {

    }

    private void OnShipForTestSlot(int slotNo, int errCode, int shipStatus) {
        TcnVendIF.getInstance().LoggerInfoForce(TAG, "OnShipForTestSlot slotNo: " + slotNo + " errCode: " + errCode + " shipStatus: " + shipStatus);
    }

//    private void OnUploadGoodsInfo(int slotNo, int finish, Coil_info slotInfo) {
//
//    }

    /*
     * 此处监听底层发过来的数据，此处接收数据位于线程内
     */
    private VendCommunicationListener m_CommunicationListener = new VendCommunicationListener();
    private class VendCommunicationListener implements TcnVendIF.CommunicationListener {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TcnComDef.COMMAND_SELECT_SLOTNO:
                    OnSelectedSlotNo(msg.arg1);
                    break;
//                case TcnComDef.COMMAND_SLOTNO_INFO: //底层上报上来的货道状态,会连续上报所有货道，开机或者重启程序就会上报一次
//                    OnUploadSlotNoInfo((boolean) msg.obj, msg.arg1, msg.arg2);
//                    break;
                case TcnComDef.COMMAND_SLOTNO_INFO_SINGLE:
                    OnUploadSlotNoInfoSingle((boolean) msg.obj, msg.arg1, msg.arg2); //底层上报上来的货道状态
                    break;
                case TcnComDef.COMMAND_SHIPMENT_CASHPAY:
	                MsgTrade mMsgToSendcash = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2,mMsgToSendcash.getErrCode(), mMsgToSendcash.getTradeNo(),mMsgToSendcash.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_WECHATPAY:
	                MsgTrade mMsgToSendWx = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendWx.getErrCode(),mMsgToSendWx.getTradeNo(),mMsgToSendWx.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_ALIPAY:
	                MsgTrade mMsgToSendAli = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendAli.getErrCode(),mMsgToSendAli.getTradeNo(),mMsgToSendAli.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_GIFTS:
                    MsgTrade mMsgToSendGifts = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendGifts.getErrCode(),mMsgToSendGifts.getTradeNo(),mMsgToSendGifts.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_REMOTE:
                    MsgTrade mMsgToSendRemote = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendRemote.getErrCode(),mMsgToSendRemote.getTradeNo(),mMsgToSendRemote.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_VERIFY:
                    MsgTrade mMsgToSendVerfy = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendVerfy.getErrCode(),mMsgToSendVerfy.getTradeNo(),mMsgToSendVerfy.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_BANKCARD_ONE:
                    MsgTrade mMsgToSendBankcard = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2,mMsgToSendBankcard.getErrCode(), mMsgToSendBankcard.getTradeNo(),mMsgToSendBankcard.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_BANKCARD_TWO:
                    MsgTrade mMsgToSendBankcardTwo = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendBankcardTwo.getErrCode(),mMsgToSendBankcardTwo.getTradeNo(),mMsgToSendBankcardTwo.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_TCNCARD_OFFLINE:
                    MsgTrade mMsgToSendBankcardOffLine = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendBankcardOffLine.getErrCode(),mMsgToSendBankcardOffLine.getTradeNo(),mMsgToSendBankcardOffLine.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_TCNCARD_ONLINE:
                    MsgTrade mMsgToSendBankcardOnLine = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendBankcardOnLine.getErrCode(),mMsgToSendBankcardOnLine.getTradeNo(),mMsgToSendBankcardOnLine.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_OTHER_PAY:
                    MsgTrade mMsgToSendBankcardPay = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendBankcardPay.getErrCode(),mMsgToSendBankcardPay.getTradeNo(),mMsgToSendBankcardPay.getAmount());
                    break;
                case TcnComDef.CMD_TEST_SLOT:
                    OnShipForTestSlot(msg.arg1, msg.arg2, (Integer) msg.obj);
                    break;
//                case TcnComDef.CMD_READ_DOOR_STATUS:
//                    TcnVendIF.getInstance().LoggerInfoForce(TAG, "CMD_READ_DOOR_STATUS msg.arg1: " + msg.arg1+" msg.arg2: "+msg.arg2);
//                    if (TcnComResultDef.DOOR_CLOSE == msg.arg1) {   //关门
//
//                    } else if (TcnComResultDef.DOOR_OPEN == msg.arg1) {   //开门
//
//                    }
//                    else {
//
//                    }
//                    break;
//                case TcnComDef.CMD_READ_CURRENT_TEMP:   //单个柜子温度上报，msg.arg1：柜子编号0,1,2    msg.arg2：温度值
//                    String temper = (String) msg.obj;  //温度描述
//                    break;
//                case TcnComDef.CMD_READ_TEMP:     //所有柜子温度描述  (String) msg.obj: 主柜和副柜温度描述
//                    String temperAll = (String) msg.obj;
//                    break;
                default:
                    break;
            }
        }
    }
}
