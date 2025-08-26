package com.tcn.icecboard.DriveControl;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.tcn.icecboard.DriveControl.icec.DriveIcec;
import com.tcn.icecboard.TcnConstant;
import com.tcn.icecboard.TcnUtility;
import com.tcn.icecboard.control.Coil_info;
import com.tcn.icecboard.control.GroupInfo;
import com.tcn.icecboard.control.PayMethod;
import com.tcn.icecboard.control.TcnShareUseData;
import com.tcn.icecboard.def.TcnProtoDef;
import com.tcn.icecboard.def.TcnProtoResultDef;
import com.tcn.icecboard.vend.TcnLog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android_serialport_api.SerialPortController;

/**
 * Created by Administrator on 2017/6/2.
 */
public class VendProtoControl {
    private static final String TAG = "VendProtoControl";
    private static VendProtoControl m_Instance = null;
    public static final String TCN_FILE_KEY = "tcn_sdk_password.txt";
    public static final String TCN_DEVID_KEY = "tcn_sdk_device_id.txt";
    public static final String YS_LOG             = "YUNS";

    public static final int BOARD_SPRING               = 5;    //弹簧机
    public static final int BOARD_ICE               = 20;  //冰淇淋

    public static final int ERROR_CODE_BUSY = -10;

    public static final int ERROR_CODE_SHIPING = -11;
    public static final int ERROR_CODE_WAIT_TAKE_GOODS = -12;
    public static final int ERROR_CODE_CLEAN = -13;

    public static final int ERROR_SPRING_CODE_0 = 0;   //正常
    public static final int ERROR_SPRING_CODE_1 = 1;   //光电开关在没有发射的情况下也有信号输出
    public static final int ERROR_SPRING_CODE_2 = 2;    //发射有改变的时候 也没有信号输出
    public static final int ERROR_SPRING_CODE_3 = 3;     //出货时一直有输出信号 不能判断好坏
    public static final int ERROR_SPRING_CODE_4 = 4;    //没有检测到出货
    public static final int ERROR_CODE_22 = 22;   //P型MOS管有短路
    public static final int ERROR_SPRING_CODE_23 = 23;    //P型MOS管有短路; 光电开关在没有发射的情况下也有信号输出;
    public static final int ERROR_SPRING_CODE_24 = 24;    //P型MOS管有短路; 发射有改变的时候 也没有信号输出;
    public static final int ERROR_SPRING_CODE_25 = 25;    //P型MOS管有短路; 出货时一直有输出信号 不能判断好坏;
    public static final int ERROR_SPRING_CODE_50 = 50;    //N型MOS管有短路;
    public static final int ERROR_SPRING_CODE_51 = 51;    //N型MOS管有短路; 光电开关在没有发射的情况下也有信号输出;
    public static final int ERROR_SPRING_CODE_52 = 52;    //N型MOS管有短路; 发射有改变的时候 也没有信号输出;
    public static final int ERROR_SPRING_CODE_53 = 53;    //N型MOS管有短路; 出货时一直有输出信号 不能判断好坏;
    public static final int ERROR_SPRING_CODE_72 = 72;    //电机短路;
    public static final int ERROR_SPRING_CODE_73 = 73;    //电机短路; 光电开关在没有发射的情况下也有信号输出;
    public static final int ERROR_SPRING_CODE_80 = 80;    //电机短路; 发射有改变的时候 也没有信号输出;
    public static final int ERROR_SPRING_CODE_81 = 81;    //电机短路;出货时一直有输出信号 不能判断好坏;
    public static final int ERROR_SPRING_CODE_100 = 100;    //电机断路;
    public static final int ERROR_SPRING_CODE_101 = 101;    //电机断路; 光电开关在没有发射的情况下也有信号输出;
    public static final int ERROR_SPRING_CODE_102 = 102;    //电机断路; 发射有改变的时候 也没有信号输出;
    public static final int ERROR_SPRING_CODE_103 = 103;    //电机断路;出货时一直有输出信号 不能判断好坏;
    public static final int ERROR_SPRING_CODE_128 = 128;    //RAM出错,电机转动超时。
    public static final int ERROR_SPRING_CODE_129 = 129;    //在规定时间内没有接收到回复数据 表明驱动板工作不正常或者与驱动板连接有问题。
    public static final int ERROR_SPRING_CODE_130 = 130;    //接收到数据不完整。
    public static final int ERROR_SPRING_CODE_131 = 131;    //校验不正确。
    public static final int ERROR_SPRING_CODE_132 = 133;    //地址不正确。
    public static final int ERROR_SPRING_CODE_134 = 134;    //货道不存在。
    public static final int ERROR_SPRING_CODE_135 = 135;    //返回故障代码有错超出范围。
    public static final int ERROR_SPRING_CODE_144 = 144;    //连续多少次转动正常但没检测到商品售出。
    public static final int ERROR_SPRING_CODE_145 = 145;    //其它故障小于。


    public static final int ERROR_LIFT_CODE_WAIT_TAKE_GOODS = -2;
    public static final int ERROR_LIFT_CODE_0 = 0;    //正常
    public static final int ERROR_LIFT_CODE_1 = 1;    //锁门时锁开关没检测到位
    public static final int ERROR_LIFT_CODE_2 = 2;    //锁门时门开关没检测到位
    public static final int ERROR_LIFT_CODE_3 = 3;    //升降电机电流过大
    public static final int ERROR_LIFT_CODE_4 = 4;    //超过极限步数还没到底
    public static final int ERROR_LIFT_CODE_5 = 5;    //检测到的最大层数比现在要出货的层数还少
    public static final int ERROR_LIFT_CODE_6 = 6;    //回原点运行超时
    public static final int ERROR_LIFT_CODE_7 = 7;    //正常运行时超时
    public static final int ERROR_LIFT_CODE_8 = 8;    //下降正常运行时超时
    public static final int ERROR_LIFT_CODE_9 = 9;    //开门时锁开关没检测到位
    public static final int ERROR_LIFT_CODE_10 = 10;    //等待离开层检测光检超时
    public static final int ERROR_LIFT_CODE_10i = 11;    //升降机光检被挡住
    public static final int ERROR_LIFT_CODE_20 = 20;    //升降机光检被挡住
    public static final int ERROR_LIFT_CODE_20i = 21;    //升降机光检不发送也有接收
    public static final int ERROR_LIFT_CODE_30 = 30;    //往上移动了一段距离，但原点开关仍然没放开
    public static final int ERROR_LIFT_CODE_31 = 31;    //推板运行超时
    public static final int ERROR_LIFT_CODE_32 = 32;    //推板电流过大
    public static final int ERROR_LIFT_CODE_33 = 33;    //推板从来没有电流
    public static final int ERROR_LIFT_CODE_34 = 34;    //取货口没有货物
    public static final int ERROR_LIFT_CODE_35 = 35;    //售货前货斗里面有货
    public static final int ERROR_LIFT_CODE_36 = 36;    //货在货道口被卡住
    public static final int ERROR_LIFT_CODE_37 = 37;    //升降电机开路
    public static final int ERROR_LIFT_CODE_40 = 40;    //货道驱动板故障
    public static final int ERROR_LIFT_CODE_41 = 41;    //FLASH檫除错误
    public static final int ERROR_LIFT_CODE_42 = 42;    //FLASH写错误
    public static final int ERROR_LIFT_CODE_43 = 43;    //错误命令
    public static final int ERROR_LIFT_CODE_44 = 44;    //校验错误
    public static final int ERROR_LIFT_CODE_45 = 45;    //柜门没关
    public static final int ERROR_LIFT_CODE_46 = 46;    //第二次购买到履带货道

    public static final int ERROR_LIFT_CODE_47 = 47;    //1层超时
    public static final int ERROR_LIFT_CODE_48 = 48;    //1层过流
    public static final int ERROR_LIFT_CODE_49 = 49;    //1层断线（正反都没有电流）
    public static final int ERROR_LIFT_CODE_50 = 50;    //2层超时
    public static final int ERROR_LIFT_CODE_51 = 51;    //2层过流
    public static final int ERROR_LIFT_CODE_52 = 52;    //2层断线（正反都没有电流）
    public static final int ERROR_LIFT_CODE_53 = 53;    //3层超时
    public static final int ERROR_LIFT_CODE_54 = 54;    //3层过流
    public static final int ERROR_LIFT_CODE_55 = 55;    //3层断线（正反都没有电流）
    public static final int ERROR_LIFT_CODE_56 = 56;    //4层超时
    public static final int ERROR_LIFT_CODE_57 = 57;    //4层过流
    public static final int ERROR_LIFT_CODE_58 = 58;    //4层断线（正反都没有电流）
    public static final int ERROR_LIFT_CODE_59 = 59;    //5层超时
    public static final int ERROR_LIFT_CODE_60 = 60;    //5层过流
    public static final int ERROR_LIFT_CODE_61 = 61;    //5层断线（正反都没有电流）

    public static final int ERROR_LIFT_CODE_65 = 65;    //卡货 弹夹式饮料机

    public static final int ERROR_LIFT_CODE_64 = 64;    //无效电机
    public static final int ERROR_LIFT_CODE_80 = 80;    //转动超时
    public static final int ERROR_LIFT_CODE_100 = 100;    //取货口有货未取出
    public static final int ERROR_LIFT_CODE_101 = 101;    //光检故障
    public static final int ERROR_LIFT_CODE_127 = 127;    //驱动板不回复命令


    public static final int ERR_COFF_CODE_0 = 0; //操作完成，没有出现故障
    public static final int ERR_COFF_CODE_1 = 1; //缺水
    public static final int ERR_COFF_CODE_2 = 2; //水温没有达到要求
    public static final int ERR_COFF_CODE_3 = 3; //缺杯
    public static final int ERR_COFF_CODE_4 = 4; //配料数据校验出错
    public static final int ERR_COFF_CODE_5 = 5; //出杯超时
    public static final int ERR_COFF_CODE_6 = 6; //检查掉杯超时
    public static final int ERR_COFF_CODE_7 = 7; //出料电机断路
    public static final int ERR_COFF_CODE_8 = 8; //出料电机短路
    public static final int ERR_COFF_CODE_9 = 9; //搅拌电机断路
    public static final int ERR_COFF_CODE_10 = 10;    //搅拌电机短路
    public static final int ERR_COFF_CODE_11 = 11;    //热水电磁阀断路
    public static final int ERR_COFF_CODE_12 = 12;    //热水电磁阀短路
    public static final int ERR_COFF_CODE_13 = 13;    //冷水电磁阀断路
    public static final int ERR_COFF_CODE_14 = 14;    //冷水电磁阀短路

    public static final int ERR_CODE_50 = 50;    //出杯测试时出杯电机转动超时
    public static final int ERR_CODE_51 = 51;    //出杯测试时掉杯检测超时

    public static final int ERR_CODE_100 = 100;   //与驱动板通讯超时
    public static final int ERR_CODE_101 = 101;   //按键板发往驱动板的命令和校验错误
    public static final int ERR_CODE_102 = 102;   //按键板发往驱动板的命令异或校验错误
    public static final int ERR_CODE_103 = 103;   //按键板发往驱动板的命令不存在


    public static final int ERROR_CODE_255 = 255;    //货道号不存在

    public static final int KEY_COFF_STATUS_INVALID = -1;
    public static final int KEY_COFF_STATUS_NORMAL = 0;
    public static final int KEY_COFF_STATUS_LACK_WATER = 1;
    public static final int KEY_COFF_STATUS_LACK_CUP = 2;
    public static final int KEY_COFF_STATUS_LACK_WATER_CUP = 3;   //缺水和缺杯
    public static final int KEY_COFF_STATUS_HEATING = 4;
    public static final int KEY_COFF_STATUS_COOLING = 8;


    public static final int ERR_SNAKE_CODE_0 = 0;
    public static final int ERR_SNAKE_CODE_1 = 1; //蒸发器风扇 1 信号丢失
    public static final int ERR_SNAKE_CODE_2 = 2; //蒸发器风扇 2 信号丢失
    public static final int ERR_SNAKE_CODE_4 = 4; //蒸发器风扇 3 信号丢失
    public static final int ERR_SNAKE_CODE_8 = 8; //蒸发器风扇 4 信号丢失
    public static final int ERR_SNAKE_CODE_16 = 16; //温度探头 1 故障
    public static final int ERR_SNAKE_CODE_32 = 32; //温度探头 2 故障
    public static final int ERR_SNAKE_CODE_64 = 64; //温度探头 3 故障
    public static final int ERR_SNAKE_CODE_128 = 128; //温度探头 4 故障
    public static final int ERR_SNAKE_CODE_256 = 256; //温度探头 5 故障
    public static final int ERR_SNAKE_CODE_512 = 512; //压缩机通电但没有工作
    public static final int ERR_SNAKE_CODE_1024 = 1024; //货道电磁铁电流太小，可能 2A 保险（F2）或货道电磁铁损坏
    public static final int ERR_SNAKE_CODE_2048 = 2048; //货道出货故障：检测缺货
    public static final int ERR_SNAKE_CODE_4096 = 4096; //货道出货故障 : K16 ~ K19 继电器有短路现象


    public static final byte BUTTON_EMPTY = (byte) 0x01;   //售空
    public static final byte BUTTON_PERPARE = (byte) 0x04;
    public static final byte BUTTON_NORMAL = (byte) 0x02;  // 有货
    public static final byte BUTTON_PRESS = (byte) 0x08;

    public static final int BUTTON_COOL = 0;   //冷
    public static final int BUTTON_HOT = 1;  //热
    public static final int BUTTON_NORMAL_TEMP = 2;  // 常温


    public static final int TEMP_CONTROL_CLOSE = 0;
    public static final int TEMP_CONTROL_COOL = 1;
    public static final int TEMP_CONTROL_HEAT = 2;

    private volatile boolean m_bShiping = false;
    private volatile boolean m_bIsCannotShipNext = false;
    private volatile boolean m_isTestingSlotNo = false;
    private volatile Handler m_ReceiveHandler = null;
    private volatile Handler m_SendHandler = null;

//    private volatile String m_strDataType = TcnConstant.DATA_TYPE[1];

    private volatile long m_lCurrentShipTime = 0;

    private volatile String m_strTemp1 = null;
    private volatile String m_strTemp2 = null;
    private volatile String m_strTemp3 = null;

    private CopyOnWriteArrayList<Integer> m_slotNoTestList = null;

    private volatile boolean m_bIsUnlock = false;

    public static synchronized VendProtoControl getInstance() {
        if (null == m_Instance) {
            m_Instance = new VendProtoControl();
        }
        return m_Instance;
    }

    public void initialize(String board1, String board2, String board3, String board4, String group1, String group2, String group3, String group4,  Handler sendHandler) {
        TcnLog.getInstance().LoggerDebug(TAG, "initialize board1: " + board1 + " board2: " + board2 + " board3: " + board3 + " board4: " + board4 + " group1: " + group1
                + " group2: " + group2 + " group3: " + group3 + " group4: " + group4 );
        m_SendHandler = sendHandler;
        m_ReceiveHandler = new CommunicationHandler();
//        m_strDataType = TcnShareUseData.getInstance().getTcnDataType();

        BoardGroupControl.getInstance().initialize(board1, board2, board3, board4, group1, group2, group3, group4);
        if ((TcnConstant.DEVICE_CONTROL_TYPE[1]).equals(board1)) {   //冰淇淋
            DriveIcec.getInstance().init(m_ReceiveHandler);
        }

        reqSlotNoInfoOpenSerialPort();
    }

    public void deInit() {

    }
    public void setUnlock(boolean unlock) {
        m_bIsUnlock = unlock;
    }
    public boolean isDoorOpen() {
        boolean bRet = false;
        GroupInfo mDriveDoorGroupInfo = BoardGroupControl.getInstance().getGroupInfoFirst();
        if ((mDriveDoorGroupInfo != null) && (mDriveDoorGroupInfo.getID() >= 0)) {
            if (BoardGroupControl.BOARD_ICE == (mDriveDoorGroupInfo.getBoardType())) {
//                bRet = DriveIcec.getInstance().isDoorOpen();
            }
        }
        return bRet;
    }

    public void setDoorOpen(boolean open) {
        GroupInfo mDriveDoorGroupInfo = BoardGroupControl.getInstance().getGroupInfoFirst();
        if ((mDriveDoorGroupInfo != null) && (mDriveDoorGroupInfo.getID() >= 0)) {
            if (BoardGroupControl.BOARD_ICE == (mDriveDoorGroupInfo.getBoardType())) {
//                DriveIcec.getInstance().setDoorOpen(open);
            }
        }
    }

    public Handler getReceiveHandler() {
        return m_ReceiveHandler;
    }

    public int getStartSlotNo(int grpId) {
        return BoardGroupControl.getInstance().getStartSlotNo(grpId);
    }

    public String[] getBoardGroupNumberArr() {
        return BoardGroupControl.getInstance().getBoardGroupNumberArr();
    }

    public String[] getBoardLatticeGroupNumberArr() {
        return BoardGroupControl.getInstance().getBoardLatticeGroupNumberArr();
    }


    public void setShiping(boolean shiping) {
        m_bShiping = shiping;
    }

    public boolean isShiping() {
        if ((Math.abs(System.currentTimeMillis() - m_lCurrentShipTime)) > 60000) {
            TcnLog.getInstance().LoggerDebug(TAG, "isShiping m_bShiping: " + m_bShiping);
            m_bShiping = false;
        }
        return m_bShiping;
    }

    public boolean isCannotShipNext(int slotNo) {
        boolean bRet = false;
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNo);
        if (null == mGroupInfo) {
            return bRet;
        }
        bRet = DriveIcec.getInstance().isCannotShipNext();
        return bRet;
    }

    public void setSendHandler(Handler handler) {
        m_SendHandler = handler;
    }

    private void openSerialPort() {
        SerialPortController.getInstance().setHandler(m_ReceiveHandler);
        int iFirstType = BoardGroupControl.getInstance().getGroupFirstType();
        TcnLog.getInstance().LoggerDebug(TAG, "openSerialPort getBoardSerPortFirst: " + TcnShareUseData.getInstance().getBoardSerPortFirst()
                + " getBoardBaudRate: " + TcnShareUseData.getInstance().getBoardBaudRate()+" iFirstType: "+iFirstType);
        SerialPortController.getInstance().openSerialPort(iFirstType, "MAINDEVICE", "MAINBAUDRATE");

    }

    public void reqSlotNoInfo() {
        TcnLog.getInstance().LoggerDebug(TAG, "reqSlotNoInfo");
        if (BoardGroupControl.getInstance().hasIceMachine()) {
            TcnLog.getInstance().LoggerDebug(TAG, "reqSlotNoInfo hasIceMachine");
            sendReceiveData(TcnProtoDef.COMMAND_SLOTNO_INFO, -1, -1, true);
        }
    }

    private void reqSlotNoInfoOpenSerialPort() {
        openSerialPort();
        reqSlotNoInfo();
    }

    public void reqShipTest(int slotNo, CopyOnWriteArrayList<Integer> slotNoList) {

        if ((null == slotNoList) || (slotNoList.size() < 1)) {
            if (slotNo > 0) {
                if (m_slotNoTestList != null) {
                    m_slotNoTestList.clear();
                }
                reqWriteDataShipTest(slotNo);
            }
            return;
        }
        reqWriteDataShipTest(-1, -1, -1, -1, slotNoList);
    }

    public void reqShipTest(int slotNo, int heatTime, int row, int column, int back, CopyOnWriteArrayList<Integer> slotNoList) {

        if ((null == slotNoList) || (slotNoList.size() < 1)) {
            if (slotNo > 0) {
                if (m_slotNoTestList != null) {
                    m_slotNoTestList.clear();
                }
                reqWriteDataShipTest(slotNo, heatTime, row, column, back);
            }
            return;
        }
        reqWriteDataShipTest(heatTime, row, column, back, slotNoList);
    }

    private void cleanShipTestList() {
        m_isTestingSlotNo = false;
        if (m_slotNoTestList != null) {
            m_slotNoTestList.clear();
        }
    }

    private void reqWriteDataShipTest(int slotNo) {
        if (!m_bIsUnlock) {
            return;
        }
        m_isTestingSlotNo = true;
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNo);
        if (null == mGroupInfo) {
            return;
        }
        if (mGroupInfo.getBoardType() == BoardGroupControl.BOARD_ICE) {
//            Coil_info info = TcnBoardIF.getInstance().getCoilInfo(slotNo);
//            DriveIcec.getInstance().reqShipTest(mGroupInfo.getSerGrpNo(), slotNo, addrSlotNo, bBoardGrpNo,
//                    info.getRow(), info.getColumn(), info.getBack(), info.getCloseStatus(),info.getSlotOrder(),info.getHeatTime());
        }
    }

    private void reqWriteDataShipTest(int slotNo, int heatTime, int row, int column, int back) {
        if (!m_bIsUnlock) {
            return;
        }
        m_isTestingSlotNo = true;
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNo);
        if (null == mGroupInfo) {
            return;
        }
        if (mGroupInfo.getBoardType() == BoardGroupControl.BOARD_ICE) {
//            Coil_info info = TcnBoardIF.getInstance().getCoilInfo(slotNo);
//            DriveIcec.getInstance().reqShipTest(mGroupInfo.getSerGrpNo(), slotNo, addrSlotNo, bBoardGrpNo,
//                    info.getRow(), info.getColumn(), info.getBack(), info.getCloseStatus(),info.getSlotOrder(),info.getHeatTime());
        }
    }


    private void reqWriteDataShipTest(int heatTime, int row, int column, int back, CopyOnWriteArrayList<Integer> slotNoList) {
        if ((null == slotNoList) || (slotNoList.size() < 1)) {
            return;
        }
        if (!m_bIsUnlock) {
            return;
        }
        m_slotNoTestList = slotNoList;
        m_isTestingSlotNo = true;
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNoList.get(0));
        if (null == mGroupInfo) {
            return;
        }
        if (mGroupInfo.getBoardType() == BoardGroupControl.BOARD_ICE) {
//            Coil_info info = TcnBoardIF.getInstance().getCoilInfo(slotNoList.get(0));
//            DriveIcec.getInstance().reqShipTest(mGroupInfo.getSerGrpNo(), slotNoList.get(0), addrSlotNo, bBoardGrpNo,
//                    info.getRow(), info.getColumn(), info.getBack(), info.getCloseStatus(),info.getSlotOrder(),info.getHeatTime());
        }
    }
    public void reqWriteDataShipTest(String zhuliao,String dingliao,String guojiang,String zhuQ,String dingQ,String guoQ) {
        if (!m_bIsUnlock) {
            return;
        }
        m_isTestingSlotNo = true;
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(1);
        if (null == mGroupInfo) {
            return;
        }
        int addrSlotNo = BoardGroupControl.getInstance().getAddrSlotNo(mGroupInfo.getBoardType(), 1);
        byte bBoardGrpNo = BoardGroupControl.getInstance().getGroup(mGroupInfo.getBoardGrpNo());
        int zhu = 0;
        int ding = 0;
        int guo = 0;
        int zhuliaoQuantity = 0;
        int guojiangQuantity = 0;
        int dingliaoQuantity = 0;
        if (!TextUtils.isEmpty(zhuliao)) {
            try {
                zhu = Integer.parseInt(zhuliao);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(dingliao)) {
            try {
                ding  = Integer.parseInt(dingliao);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(guojiang)) {
            try {
                guo  = Integer.parseInt(guojiang);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(zhuQ)) {
            try {
                zhuliaoQuantity  = Integer.parseInt(zhuQ);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(dingQ)) {
            try {
                dingliaoQuantity  = Integer.parseInt(dingQ);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(guoQ)) {
            try {
                guojiangQuantity  = Integer.parseInt(guoQ);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mGroupInfo.getBoardType() == BoardGroupControl.BOARD_ICE) {
            DriveIcec.getInstance().reqShipTest(mGroupInfo.getSerGrpNo(), 1, addrSlotNo, bBoardGrpNo,
                    zhu,guo,ding,zhuliaoQuantity,guojiangQuantity,dingliaoQuantity);
        }
    }
    public void reqSelectSlotNo(int slotNo) {
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNo);
        if (null == mGroupInfo) {
            return;
        }
        if (mGroupInfo.getBoardType() == BoardGroupControl.BOARD_ICE) {
//            DriveIcec.getInstance().reqSelectSlotNo(mGroupInfo.getSerGrpNo(), slotNo, addrSlotNo, bBoardGrpNo);
        }
    }

    public List<GroupInfo> getGroupListAll() {
        return BoardGroupControl.getInstance().getGroupListAll();
    }

    public GroupInfo getMachineGroupInfo(int grpId) {
        return BoardGroupControl.getInstance().getMachineGroupInfo(grpId);
    }

    /************************************************ 出货方式 start ************************************************/

    public void shipWithCashPay(int slotNo, String tradeNo,Coil_info info) {
        if (slotNo < 1) {
            return;
        }
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNo);
        if (null == mGroupInfo) {
            shipFail(slotNo, PayMethod.PAYMETHED_CASH, tradeNo);
            return;
        }
//        ship(slotNo, mGroupInfo, PayMethod.PAYMETHED_CASH, tradeNo, -1, -1, -1, -1,info);
    }


    public void ship(int slotNo, String payMedthod, String tradeNo,Coil_info info) {
        if (slotNo < 1) {
            return;
        }

        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNo);
        if (null == mGroupInfo) {
            shipFail(slotNo, payMedthod, tradeNo);
            return;
        }

        /*if (isBusy(mGroupInfo)) {
            TcnBoardIF.getInstance().LoggerError(TAG, "ship isBusy slotNo: "+slotNo+" tradeNo: "+tradeNo);
            shipFail(slotNo,payMedthod,tradeNo);
            return;
        }*/

//        ship(slotNo, mGroupInfo, payMedthod, tradeNo, -1, -1, -1, 0,info);
    }

    public void shipWithCashPay(int slotNo, String tradeNo, int heatTime,Coil_info info) {
        if (slotNo < 1) {
            return;
        }
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNo);
        if (null == mGroupInfo) {
            shipFail(slotNo, PayMethod.PAYMETHED_CASH, tradeNo);
            return;
        }
        if (heatTime < 0) {
            heatTime = 45;
        }
//        ship(slotNo, mGroupInfo, PayMethod.PAYMETHED_CASH, tradeNo, heatTime, -1, -1, -1,info);
    }


    public void ship(int slotNo, String payMedthod, String tradeNo, int heatTime,Coil_info info) {
        if (slotNo < 1) {
            return;
        }

        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNo);
        if (null == mGroupInfo) {
            shipFail(slotNo, payMedthod, tradeNo);
            return;
        }

      /*  if (isBusy(mGroupInfo)) {
            TcnBoardIF.getInstance().LoggerError(TAG, "ship isBusy slotNo: "+slotNo+" tradeNo: "+tradeNo);
            shipFail(slotNo,payMedthod,tradeNo);
            return;
        }*/

        if (heatTime < 0) {
            heatTime = 45;
        }
//        ship(slotNo, mGroupInfo, payMedthod, tradeNo, heatTime, -1, -1, 0,info);
    }

    public void shipWithCashPay(int slotNo, String tradeNo, int row, int column, int back,Coil_info info) {
        if (slotNo < 1) {
            return;
        }
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNo);
        if (null == mGroupInfo) {
            shipFail(slotNo, PayMethod.PAYMETHED_CASH, tradeNo);
            return;
        }
//        ship(slotNo, mGroupInfo, PayMethod.PAYMETHED_CASH, tradeNo, -1, row, column, back,info);
    }


    public void ship(int slotNo, String payMedthod, String tradeNo, int row, int column, int back, Coil_info info) {
        if (slotNo < 1) {
            return;
        }

        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNo);
        if (null == mGroupInfo) {
            shipFail(slotNo, payMedthod, tradeNo);
            return;
        }

       /* if (isBusy(mGroupInfo)) {
            TcnBoardIF.getInstance().LoggerError(TAG, "ship isBusy slotNo: "+slotNo+" tradeNo: "+tradeNo);
            shipFail(slotNo,payMedthod,tradeNo);
            return;
        }*/

//        ship(slotNo, mGroupInfo, payMedthod, tradeNo, -1, row, column, back,info);
    }
    public void ship(int slotNo, String payMedthod, String tradeNo, int heatTime, String amount, String zhuliao,String dingliao,String guojiang,String zhuQ,String dingQ,String guoQ) {
        if (!m_bIsUnlock) {
            return;
        }
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfo(slotNo);
        if (null == mGroupInfo) {
            shipFail(slotNo, payMedthod, tradeNo);
            return;
        }
        m_lCurrentShipTime = System.currentTimeMillis();
        if (null == mGroupInfo) {
            return;
        }

        cleanShipTestList();
        m_bShiping = true;
        m_bIsCannotShipNext = true;

        int addrSlotNo = BoardGroupControl.getInstance().getAddrSlotNo(mGroupInfo.getBoardType(), slotNo);
        byte bBoardGrpNo = BoardGroupControl.getInstance().getGroup(mGroupInfo.getBoardGrpNo());

        if (mGroupInfo.getBoardType() == BoardGroupControl.BOARD_ICE) {
            int zhu = 0;
            int ding = 0;
            int guo = 0;
            int zhuliaoQuantity = 0;
            int guojiangQuantity = 0;
            int dingliaoQuantity = 0;
            if (!TextUtils.isEmpty(zhuliao)) {
                try {
                    zhu = Integer.parseInt(zhuliao);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(dingliao)) {
                try {
                    ding  = Integer.parseInt(dingliao);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(guojiang)) {
                try {
                    guo  = Integer.parseInt(guojiang);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(zhuQ)) {
                try {
                    zhuliaoQuantity  = Integer.parseInt(zhuQ);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(dingQ)) {
                try {
                    dingliaoQuantity  = Integer.parseInt(dingQ);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(guoQ)) {
                try {
                    guojiangQuantity  = Integer.parseInt(guoQ);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            DriveIcec.getInstance().reqShip(mGroupInfo.getSerGrpNo(), slotNo, addrSlotNo, bBoardGrpNo, payMedthod, tradeNo,
                    zhu, guo, ding, zhuliaoQuantity, guojiangQuantity, dingliaoQuantity);
        }
    }

    private boolean isBusy(GroupInfo groupInfo) {
        boolean bRet = false;
        if (null == groupInfo) {
            return bRet;
        }
        bRet = DriveIcec.getInstance().isBusy();
        return bRet;
    }

    /************************************************ 出货方式 end ************************************************/
    public void reqQueryStatus(int grpId) {
        if (grpId <= 0) {
            reqQueryStatus();
            return;
        }
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getMachineGroupInfo(grpId);
        if (null == mGroupInfo) {
            return;
        }
        byte bBoardGrpNo = BoardGroupControl.getInstance().getGroup(mGroupInfo.getBoardGrpNo());
        if (mGroupInfo.getBoardType() == BoardGroupControl.BOARD_ICE) {
            DriveIcec.getInstance().reqQueryStatus(mGroupInfo.getSerGrpNo(), bBoardGrpNo);
        }
    }

    public void reqQueryStatus() {
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfoFirst(BoardGroupControl.BOARD_ICE);
        if (null != mGroupInfo) {
            byte bBoardGrpNo = BoardGroupControl.getInstance().getGroup(mGroupInfo.getBoardGrpNo());
            if (mGroupInfo.getBoardType() == BoardGroupControl.BOARD_ICE) {
                DriveIcec.getInstance().reqQueryStatus(mGroupInfo.getSerGrpNo(), bBoardGrpNo);
            }
        }
    }

    public void reqClearFaults(int grpId) {
        if (grpId <= 0) {
            reqClearFaults();
            return;
        }

        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getMachineGroupInfo(grpId);
        if (null == mGroupInfo) {
            return;
        }
        byte bBoardGrpNo = BoardGroupControl.getInstance().getGroup(mGroupInfo.getBoardGrpNo());
        if (mGroupInfo.getBoardType() == BoardGroupControl.BOARD_ICE) {
            DriveIcec.getInstance().reqClearFaults(mGroupInfo.getSerGrpNo(), bBoardGrpNo);
        }

    }

    public void reqClearFaults() {
        GroupInfo mGroupInfo = BoardGroupControl.getInstance().getGroupInfoFirst(BoardGroupControl.BOARD_ICE);
        if (null != mGroupInfo) {
            byte bBoardGrpNo = BoardGroupControl.getInstance().getGroup(mGroupInfo.getBoardGrpNo());
            if (mGroupInfo.getBoardType() == BoardGroupControl.BOARD_ICE) {
                DriveIcec.getInstance().reqClearFaults(mGroupInfo.getSerGrpNo(), bBoardGrpNo);
            }
        }
    }


    /***********************  冰淇淋 start *******************************************/

    public void reqSetWorkMode(int workModeLeft, int workModeRight) {
        DriveIcec.getInstance().reqSetWorkMode(workModeLeft,workModeRight);
    }

    public void reqSetParamIceMake(int positionCoolLeft, int positionCoolRight,int coolTempLeft, int coolTempRight,int coolStorage,int coolFree) {
        DriveIcec.getInstance().reqSetParamIceMake(positionCoolLeft,positionCoolRight,coolTempLeft,coolTempRight,coolStorage,coolFree);
    }

    public void reqQueryParamIceMake() {
        DriveIcec.getInstance().reqQueryParamIceMake();
    }

    public void testDischarge(int testPorject,int testPosition){
        DriveIcec.getInstance().TestDischarge(testPorject,testPosition);
    }

    public void reqQueryParam(int operaItem,int operaPosition) {
        DriveIcec.getInstance().reqQueryParam(operaItem,operaPosition);
    }

    public void reqQueryAllParam(int operaItem, int operaPosition) {
        DriveIcec.getInstance().reqDataAllQuery(operaItem, operaPosition);
    }

    public void reqParamSet(int operaItem,int operaPosition,int data) {
        DriveIcec.getInstance().reqParamSet(operaItem,operaPosition,data);
    }

    public void reqMove(int operaPosition,int data) {
        DriveIcec.getInstance().reqMove(operaPosition,data);
    }

    public void reqQueryStatusAndJudge() {
        DriveIcec.getInstance().reqQueryStatusAndJudge();
    }

    public void reqMachineSelf_test(){
        DriveIcec.getInstance().reqMachineSelf_test();
    }
    /***********************  冰淇淋 end *******************************************/

    private void OnAnalyseProtocolData(int bytesCount, int boardType, byte[] bytesData) {
        if ((null == bytesData) || (bytesData.length < 1)) {
            return;
        }
//	    TcnBoardIF.getInstance().LoggerDebug(TAG, "OnAnalyseProtocolData bytesCount: " + bytesCount + " boardType: " + boardType + " m_strLiftMode: " + m_strLiftMode
//	    +" m_strDataType: "+m_strDataType+" isUpdating:"+DriveUpgrade.getInstance().isUpdating());

        String hexData = TcnUtility.bytesToHexString(bytesData, bytesCount);
        DriveIcec.getInstance().protocolAnalyse(hexData);

    }

    private void OnAnalyseProtocolDataNew(int bytesCount, int boardType, byte[] bytesData) {
        if ((null == bytesData) || (bytesData.length < 1)) {
            return;
        }
        String hexData = TcnUtility.bytesToHexString(bytesData, bytesCount);
        if (handleAnalyseData(boardType, hexData)) {
            return;
        }

    }

    private void OnAnalyseProtocolDataThird(int bytesCount, int boardType, byte[] bytesData) {
        if ((null == bytesData) || (bytesData.length < 1)) {
            return;
        }
        String hexData = TcnUtility.bytesToHexString(bytesData, bytesCount);

    }

    private void OnAnalyseProtocolDataFourth(int bytesCount, int boardType, byte[] bytesData) {
        if ((null == bytesData) || (bytesData.length < 1)) {
            return;
        }
        String hexData = TcnUtility.bytesToHexString(bytesData, bytesCount);

    }

    public int getPayShipMedthod(String payMethod) {
        int sendWhat = -1;
        int iPayMethod = -1;
        if (TcnUtility.isDigital(payMethod)) {
            iPayMethod = Integer.valueOf(payMethod);
            if (Integer.valueOf(PayMethod.PAYMETHED_CASH) == iPayMethod) {
                sendWhat = TcnProtoDef.COMMAND_SHIPMENT_CASHPAY;
            } else if (Integer.valueOf(PayMethod.PAYMETHED_MDB_CARD) == iPayMethod) {
                sendWhat = TcnProtoDef.COMMAND_SHIPMENT_BANKCARD_ONE;
            } else if (Integer.valueOf(PayMethod.PAYMETHED_TCNICCARD) == iPayMethod) {
                sendWhat = TcnProtoDef.COMMAND_SHIPMENT_TCNCARD_ONLINE;
            } else if (Integer.valueOf(PayMethod.PAYMETHED_BANKPOSCARD) == iPayMethod) {
                sendWhat = TcnProtoDef.COMMAND_SHIPMENT_BANKCARD_TWO;
            } else if (Integer.valueOf(PayMethod.PAYMETHED_WECHAT) == iPayMethod) {
                sendWhat = TcnProtoDef.COMMAND_SHIPMENT_WECHATPAY;
            } else if (Integer.valueOf(PayMethod.PAYMETHED_ALI) == iPayMethod) {
                sendWhat = TcnProtoDef.COMMAND_SHIPMENT_ALIPAY;
            } else if (Integer.valueOf(PayMethod.PAYMETHED_GIFTS) == iPayMethod) {
                sendWhat = TcnProtoDef.COMMAND_SHIPMENT_GIFTS;
            } else if (Integer.valueOf(PayMethod.PAYMETHED_REMOUT) == iPayMethod) {
                sendWhat = TcnProtoDef.COMMAND_SHIPMENT_REMOTE;
            } else if (Integer.valueOf(PayMethod.PAYMETHED_VERIFY) == iPayMethod) {
                sendWhat = TcnProtoDef.COMMAND_SHIPMENT_VERIFY;
            } else {
                sendWhat = TcnProtoDef.COMMAND_SHIPMENT_OTHER_PAY;
            }
        } else {
            sendWhat = TcnProtoDef.COMMAND_SHIPMENT_OTHER_PAY;
        }

        return sendWhat;
    }

    private int getShipStatus(int boardType, int shipStatus) {
        int iArg2 = -1;
        if (BoardGroupControl.BOARD_ICE == boardType) {
            if (DriveIcec.SHIP_STATUS_SHIPING == shipStatus) {
                iArg2 = TcnProtoResultDef.SHIP_SHIPING;
                m_bShiping = true;
            } else if (DriveIcec.SHIP_STATUS_SUCCESS == shipStatus) {
                m_bShiping = false;
                iArg2 = TcnProtoResultDef.SHIP_SUCCESS;
            } else {
                m_bShiping = false;
                iArg2 = TcnProtoResultDef.SHIP_FAIL;
            }
        }

        return iArg2;

    }

    private void shipForTestSlot(int boardType, int slotNo, int shipStatus, MsgToSend msgToSend) {
        if (null == msgToSend) {
            TcnLog.getInstance().LoggerError(TAG, "shipForTestSlot msgToSend is null");
            return;
        }
        TcnLog.getInstance().LoggerDebug(TAG, "shipForTestSlot() boardType: " + boardType + " slotNo: " + slotNo + " shipStatus: " + shipStatus + " getErrCode: " + msgToSend.getErrCode());
        int errCode = msgToSend.getErrCode();
        if (BoardGroupControl.BOARD_ICE == boardType) {
            if (DriveIcec.SHIP_STATUS_SHIPING == shipStatus) {
                sendReceiveData(TcnProtoDef.CMD_TEST_SLOT, slotNo, errCode, TcnProtoResultDef.SHIP_SHIPING);
            } else if (DriveIcec.SHIP_STATUS_SUCCESS == shipStatus) {
                sendReceiveData(TcnProtoDef.CMD_TEST_SLOT, slotNo, errCode, TcnProtoResultDef.SHIP_SUCCESS);
            } else if (DriveIcec.SHIP_STATUS_FAIL == shipStatus) {
                sendReceiveData(TcnProtoDef.CMD_TEST_SLOT, slotNo, errCode, TcnProtoResultDef.SHIP_FAIL);
            } else {

            }
        }

    }


    private void shipFail(int slotNo, String payMethod, String tradeNo) {
        TcnLog.getInstance().LoggerDebug(TAG, "shipFail slotNo: " + slotNo + " payMethod: " + payMethod + " tradeNo: " + tradeNo);
        int sendWhat = getPayShipMedthod(payMethod);
        MsgToSend msgToSend = new MsgToSend();
        msgToSend.setSlotNo(slotNo);
        msgToSend.setPayMethod(payMethod);
        msgToSend.setTradeNo(tradeNo);
        sendReceiveData(sendWhat, slotNo, TcnProtoResultDef.SHIP_FAIL, msgToSend);
    }

    private void handShipData(int boardType, int slotNo, int shipStatus, MsgToSend msgToSend) {
        if (null == msgToSend) {
            TcnLog.getInstance().LoggerError(TAG, "handShipData msgToSend is null");
            return;
        }

        if (BoardGroupControl.BOARD_ICE == boardType) {
            msgToSend.setBoardType(BoardGroupControl.BOARD_ICE);
        }

        TcnLog.getInstance().LoggerDebug(TAG, "handShipData slotNo: " + slotNo + " shipStatus: " + shipStatus + " errCode: " + msgToSend.getErrCode() + " payMethod: " + msgToSend.getPayMethod());

        int sendWhat = getPayShipMedthod(msgToSend.getPayMethod());

        int iShipStatus = getShipStatus(boardType, shipStatus);

        int errCode = msgToSend.getErrCode();

        sendReceiveData(sendWhat, slotNo, iShipStatus, msgToSend);
    }

    private void sendReceiveData(int what, int arg1, int arg2, Object data) {
        if (null == m_SendHandler) {
            return;
        }
        Message message = m_SendHandler.obtainMessage();
        message.what = what;
        message.arg1 = arg1;
        message.arg2 = arg2;
        message.obj = data;
        m_SendHandler.sendMessage(message);
    }

    private void sendMessageDelay(Handler handler, int what, int arg1, int arg2, long delayTime, Object data) {
        if (null == handler) {
            return;
        }
        Message message = handler.obtainMessage();
        message.what = what;
        message.arg1 = arg1;
        message.arg2 = arg2;
        message.obj = data;
        handler.sendMessageDelayed(message, delayTime);
    }

    private void sendNoDataCmd(int cmdType, int arg2) {
        TcnLog.getInstance().LoggerDebug(TAG, "sendNoDataCmd cmdType: " + cmdType + " arg2: " + arg2);

        sendReceiveData(TcnProtoDef.CMD_NO_DATA_RECIVE, cmdType, -1, -1);

        if ((DriveIcec.CMD_SHIP == cmdType) || DriveIcec.CMD_SHIP_TEST == cmdType) {
            m_bIsCannotShipNext = false;
            m_bShiping = false;
        }
    }

    private AnalyseDataListener m_AnalyseDataListener = null;

    public void setOnAnalyseDataListener(AnalyseDataListener listener) {
        m_AnalyseDataListener = listener;
    }

    public boolean handleAnalyseData(int boardType, String data) {
        boolean bRet = false;
        if (m_AnalyseDataListener != null) {
            bRet = true;
            m_AnalyseDataListener.OnAnalyseData(boardType, data);
        }

        return bRet;
    }

    public interface AnalyseDataListener {
        public void OnAnalyseData(int boardType, String data);
    }


    private MainBaudrateListener m_MainBaudrateListener = null;

    public void setOnMainBaudrateListener(MainBaudrateListener listener) {
        m_MainBaudrateListener = listener;
    }

    public boolean handleMainBaudrate(int seriIndex, int boardType) {
        boolean bRet = false;
        if (m_MainBaudrateListener != null) {
            bRet = true;
            m_MainBaudrateListener.OnMainBaudrate(seriIndex, boardType);
        }

        return bRet;
    }

    public interface MainBaudrateListener {
        public void OnMainBaudrate(int seriIndex, int boardType);
    }

    private class CommunicationHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SerialPortController.SERIAL_PORT_CONFIG_ERROR:
                    TcnLog.getInstance().LoggerDebug(TAG, "SERIAL_PORT_CONFIG_ERROR");
                    sendReceiveData(TcnProtoDef.SERIAL_PORT_CONFIG_ERROR, msg.arg1, msg.arg2, null);
                    break;
                case SerialPortController.SERIAL_PORT_SECURITY_ERROR:
                    TcnLog.getInstance().LoggerDebug(TAG, "SERIAL_PORT_SECURITY_ERROR");
                    sendReceiveData(TcnProtoDef.SERIAL_PORT_SECURITY_ERROR, msg.arg1, msg.arg2, null);
                    break;
                case SerialPortController.SERIAL_PORT_UNKNOWN_ERROR:
                    TcnLog.getInstance().LoggerDebug(TAG, "SERIAL_PORT_UNKNOWN_ERROR");
                    sendReceiveData(TcnProtoDef.SERIAL_PORT_UNKNOWN_ERROR, msg.arg1, msg.arg2, null);
                    break;
                case SerialPortController.SERIAL_PORT_RECEIVE_DATA:
                    OnAnalyseProtocolData(msg.arg1, msg.arg2, (byte[]) msg.obj);
                    break;
                case SerialPortController.SERIAL_PORT_RECEIVE_DATA_NEW:
                    OnAnalyseProtocolDataNew(msg.arg1, msg.arg2, (byte[]) msg.obj);
                    break;
                case SerialPortController.SERIAL_PORT_RECEIVE_DATA_THIRD:
                    OnAnalyseProtocolDataThird(msg.arg1, msg.arg2, (byte[]) msg.obj);
                    break;
                case SerialPortController.SERIAL_PORT_RECEIVE_DATA_FOURTH:
                    OnAnalyseProtocolDataFourth(msg.arg1, msg.arg2, (byte[]) msg.obj);
                    break;

                case DriveIcec.CMD_BUSY:
                    if (msg.arg2 >= 0) {
                        DriveIcec.getInstance().handleBusyMessage((MsgToSend) msg.obj);
                    } else {
                        sendReceiveData(TcnProtoDef.COMMAND_BUSY, msg.arg1, msg.arg2, null);
                    }
                    break;
                case DriveIcec.CMD_QUERY_STATUS:
                    if (DriveIcec.STATUS_FREE == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_ICEC, TcnProtoResultDef.STATUS_FREE, msg.arg2, null);
                    } else if (DriveIcec.STATUS_SHIPING == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_ICEC, TcnProtoResultDef.STATUS_SHIPING, ERROR_CODE_SHIPING, null);
                    } else if (DriveIcec.STATUS_WAIT_TAKE_GOODS == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_ICEC, TcnProtoResultDef.STATUS_WAIT_TAKE_GOODS, ERROR_CODE_WAIT_TAKE_GOODS, null);
                    } else if (DriveIcec.STATUS_CLEAN == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_ICEC, TcnProtoResultDef.STATUS_CLEAN, ERROR_CODE_CLEAN, null);
                    } else if (DriveIcec.STATUS_FAULT == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_ICEC, TcnProtoResultDef.STATUS_FAULT, msg.arg2, null);
                    } else {

                    }

                    break;
                case DriveIcec.CMD_SELECT_SLOTNO:      //msg.arg1:货道号   msg.arg2:故障代码
                    if (DriveIcec.STATUS_FREE == msg.arg2) {
                        m_bIsCannotShipNext = false;
                        sendReceiveData(TcnProtoDef.COMMAND_SELECT_SLOTNO, msg.arg1, msg.arg2, Integer.valueOf(TcnProtoResultDef.BOARD_ICE));
                    } else if (DriveIcec.STATUS_SHIPING == msg.arg2) {
                        sendReceiveData(TcnProtoDef.COMMAND_SELECT_FAIL, msg.arg1, ERROR_CODE_SHIPING, Integer.valueOf(TcnProtoResultDef.BOARD_ICE));
                    }
                    else if (DriveIcec.STATUS_CLEAN == msg.arg2) {
                        sendReceiveData(TcnProtoDef.COMMAND_SELECT_FAIL, msg.arg1, ERROR_CODE_CLEAN, Integer.valueOf(TcnProtoResultDef.BOARD_ICE));
                    }
                    else if (DriveIcec.STATUS_WAIT_TAKE_GOODS == msg.arg2) {
                        sendReceiveData(TcnProtoDef.COMMAND_SELECT_FAIL, msg.arg1, ERROR_CODE_WAIT_TAKE_GOODS, Integer.valueOf(TcnProtoResultDef.BOARD_ICE));
                    } else {
                        MsgToSend ms = (MsgToSend) msg.obj;
                        sendReceiveData(TcnProtoDef.COMMAND_SELECT_FAIL, msg.arg1, ms.getErrCode(), Integer.valueOf(TcnProtoResultDef.BOARD_ICE));
                    }
                    break;
                case DriveIcec.CMD_QUERY_STATUS_GOODS_TAKE:
                    if (DriveIcec.STATUS_FREE == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_GOODS_TAKE, TcnProtoResultDef.STATUS_FREE, msg.arg2, null);
                    } else if (DriveIcec.STATUS_SHIPING == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_GOODS_TAKE, TcnProtoResultDef.STATUS_SHIPING, ERROR_CODE_SHIPING, null);
                    } else if (DriveIcec.STATUS_WAIT_TAKE_GOODS == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_GOODS_TAKE, TcnProtoResultDef.STATUS_WAIT_TAKE_GOODS, ERROR_CODE_WAIT_TAKE_GOODS, null);
                    } else if (DriveIcec.STATUS_CLEAN == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_GOODS_TAKE, TcnProtoResultDef.STATUS_CLEAN, ERROR_CODE_CLEAN, null);
                    } else if (DriveIcec.STATUS_FAULT == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_GOODS_TAKE, TcnProtoResultDef.STATUS_FAULT, msg.arg2, null);
                    }
                    else {

                    }

                    break;
                case DriveIcec.CMD_QUERY_STATUS_GOODS_TAKE_LOOP:
                    DriveIcec.getInstance().reqQueryTakeGoodsStatusDelay();
                    break;
                case DriveIcec.CMD_PARAM_ICE_MAKE_QUERY_LOOP:
                    DriveIcec.getInstance().reqQueryParamIceMakeStatusDelay();
                    break;
                case DriveIcec.CMD_QUERY_STATUS_SHIP_RESULT:
                    handShipData(BoardGroupControl.BOARD_ICE, msg.arg1, msg.arg2, (MsgToSend) msg.obj);
                    break;
                case DriveIcec.CMD_QUERY_STATUS_SHIP_TEST_RESULT:
                    shipForTestSlot(BoardGroupControl.BOARD_ICE, msg.arg1, msg.arg2, (MsgToSend) msg.obj);
                    break;
                case DriveIcec.CMD_QUERY_STATUS_SHIP_RESULT_LOOP:
                    DriveIcec.getInstance().reqQueryShipStatusDelay();
                    break;
                case DriveIcec.CMD_QUERY_STATUS_SHIP_TEST_RESULT_LOOP:
                    DriveIcec.getInstance().reqQueryShipTestStatusDelay();
                    break;
                case DriveIcec.CMD_SELF_INSPECTION_STATUS_LOOP:
//                    TcnLog.getInstance().LoggerError("ComponentBoard", TAG, "commondAnalyse", "DriveIcec.CMD_SELF_INSPECTION_STATUS_LOOP" );
                    DriveIcec.getInstance().reqQuerySelefInspectionStatusDelay();
                    break;
                case DriveIcec.CMD_SET_WORK_MODE:
                    if (DriveIcec.SUCCESS == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_ICE_SET_WORK_MODE, TcnProtoResultDef.SUCCESS, msg.arg2, msg.obj);
                    } else {
                        sendReceiveData(TcnProtoDef.CMD_ICE_SET_WORK_MODE, TcnProtoResultDef.FAIL, msg.arg2, msg.obj);
                    }
                    break;
                case DriveIcec.CMD_PARAM_ICE_MAKE_SET:
                    if (DriveIcec.SUCCESS == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_PARAM_ICE_MAKE_SET, TcnProtoResultDef.SUCCESS, msg.arg2, msg.obj);
                    } else {
                        sendReceiveData(TcnProtoDef.CMD_PARAM_ICE_MAKE_SET, TcnProtoResultDef.FAIL, msg.arg2, msg.obj);
                    }
                    break;
                case DriveIcec.CMD_SELF_INSPECTION:
                    if (DriveIcec.SUCCESS == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_SELF_INSPECTION, TcnProtoResultDef.SUCCESS, msg.arg2, msg.obj);
                    } else {
                        sendReceiveData(TcnProtoDef.CMD_SELF_INSPECTION, TcnProtoResultDef.FAIL, msg.arg2, msg.obj);
                    }
                    break;
                case DriveIcec.CMD_PARAM_ICE_MAKE_QUERY:
                    sendReceiveData(TcnProtoDef.CMD_PARAM_ICE_MAKE_QUERY, msg.arg1, msg.arg2, msg.obj);
                    break;
                case DriveIcec.CMD_PARAM_QUERY:
                    TcnLog.getInstance().LoggerDebug(TAG, "handleMessage,CMD_PARAM_QUERY,msg.arg1=" + msg.arg1);
                    if (DriveIcec.SUCCESS == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_PARAM_QUERY, TcnProtoResultDef.SUCCESS, msg.arg2, msg.obj);
                    } else {
                        sendReceiveData(TcnProtoDef.CMD_PARAM_QUERY, TcnProtoResultDef.FAIL, msg.arg2, msg.obj);
                    }

                    break;
                case DriveIcec.CMD_PARAM_SET:
                    if (DriveIcec.SUCCESS == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_PARAM_SET, TcnProtoResultDef.SUCCESS, msg.arg2, msg.obj);
                    } else {
                        sendReceiveData(TcnProtoDef.CMD_PARAM_SET, TcnProtoResultDef.FAIL, msg.arg2, msg.obj);
                    }

                    break;
                case DriveIcec.CMD_POSITION_MOVE:
                    if (DriveIcec.SUCCESS == msg.arg1) {
                        sendReceiveData(TcnProtoDef.CMD_POSITION_MOVE, TcnProtoResultDef.SUCCESS, msg.arg2, msg.obj);
                    } else {
                        sendReceiveData(TcnProtoDef.CMD_POSITION_MOVE, TcnProtoResultDef.FAIL, msg.arg2, msg.obj);
                    }

                    break;
                case DriveIcec.CMD_QUERY_STATUS_AND_JUDGE:
                    sendReceiveData(TcnProtoDef.CMD_QUERY_STATUS_AND_JUDGE, msg.arg1, msg.arg2, msg.obj);
                    break;
                case DriveIcec.CMD_TEST_DISCHARGE:
                    sendReceiveData(TcnProtoDef.CMD_TEST_DISCHARGE, msg.arg1, msg.arg2, msg.obj);
                    break;
                case DriveIcec.CMD_SELF_INSPECTION_STATUS:
                    sendReceiveData(TcnProtoDef.CMD_SELF_INSPECTION_STATUS, msg.arg1, msg.arg2, msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}
